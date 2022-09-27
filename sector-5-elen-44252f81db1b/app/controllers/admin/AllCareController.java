package controllers.admin;

import actions.SessionRequired;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import datatable.AllCareDatatable;
import entities.dto.admin.OneTimeCareDTO;
import models.care.EffectiveCareModel;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import views.html.back.admin.cares.all.CaresListView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * CareController.
 *
 * @author Pierre Adam
 * @since 20.07.24
 */
public class AllCareController extends ACareController {

    /**
     * The Account data tables.
     */
    private final AllCareDatatable caresDataTables;

    /**
     * The Actor system.
     */
    private final ActorSystem actorSystem;

    /**
     * The care creator actor.
     */
    private final ActorRef careCreatorActor;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param actorSystem      the actor system
     * @param careCreatorActor the care creator actor
     */
    @Inject
    public AllCareController(final MessagesApi messagesApi,
                             final FormFactory formFactory,
                             final PlaySessionStore playSessionStore,
                             final ActorSystem actorSystem,
                             @Named("CareCreatorActor") final ActorRef careCreatorActor) {
        super(messagesApi, formFactory, playSessionStore);

        this.actorSystem = actorSystem;
        this.careCreatorActor = careCreatorActor;

        this.caresDataTables = new AllCareDatatable(messagesApi, formFactory);
    }

    /**
     * Get effective cares result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_Cares(final Http.Request request) {
        return this.withMessages(request, messages -> Results.ok(CaresListView.render(new OneTimeCareDTO(), request, messages)));
    }

    /**
     * Post cares ajax list result.
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
            (oneTimeCareDTO, messages) -> views.html.back.admin.cares.onetime.CaresListView.render(oneTimeCareDTO, request, messages),
            routes.AllCareController.GET_Cares());
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
        return this.updateCareStatus(request, effectiveCare, routes.AllCareController.GET_Cares());
    }
}
