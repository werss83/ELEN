package controllers.admin;

import actions.SessionRequired;
import datatable.EffectiveCareDatatable;
import entities.dto.admin.OneTimeCareDTO;
import models.care.EffectiveCareModel;
import models.care.enumeration.BookedCareType;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import views.html.back.admin.cares.onetime.CaresListView;

import javax.inject.Inject;

/**
 * CareController.
 *
 * @author Pierre Adam
 * @since 20.07.24
 */
public class OneTimeCareController extends ACareController {

    /**
     * The Account data tables.
     */
    private final EffectiveCareDatatable caresDataTables;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    @Inject
    public OneTimeCareController(final MessagesApi messagesApi, final FormFactory formFactory, final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);

        this.caresDataTables = new EffectiveCareDatatable(messagesApi, formFactory);
        this.caresDataTables.setWhere(query -> query.eq("bookedCare.type", BookedCareType.ONE_TIME));
    }

    /**
     * Get effective cares result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_Cares(final Http.Request request) {
        return this.withMessages(request, messages ->
            Results.ok(CaresListView.render(new OneTimeCareDTO(), request, messages))
        );
    }

    /**
     * Post effective cares ajax list result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_CaresAjaxList(final Http.Request request) {
        return this.dataTablesAjaxRequest(request,
            boundForm -> Results.badRequest(),
            form -> Results.ok(this.caresDataTables.getAjaxResult(request, form.getParameters()))
        );
    }

    /**
     * Post update care result.
     *
     * @param request       the request
     * @param effectiveCare the effective care
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_UpdateCare(final Http.Request request, final EffectiveCareModel effectiveCare) {
        return this.updateCare(request, effectiveCare,
            (oneTimeCareDTO, messages) -> CaresListView.render(oneTimeCareDTO, request, messages),
            routes.OneTimeCareController.GET_Cares());
    }

    /**
     * Post update care result.
     *
     * @param request       the request
     * @param effectiveCare the effective care
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result POST_UpdateCareStatus(final Http.Request request, final EffectiveCareModel effectiveCare) {
        return this.updateCareStatus(request, effectiveCare, routes.OneTimeCareController.GET_Cares());
    }
}
