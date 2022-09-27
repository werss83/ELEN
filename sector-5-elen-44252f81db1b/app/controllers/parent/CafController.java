package controllers.parent;

import actions.SessionRequired;
import controllers.AController;
import entities.dto.parent.CafDTO;
import form.parent.CafInformationForm;
import models.parent.ParentModel;
import org.pac4j.play.store.PlaySessionStore;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import services.CafService;
import views.html.back.parent.caf.CafInformationView;

import javax.inject.Inject;

/**
 * CafController.
 *
 * @author Pierre Adam
 * @since 20.09.04
 */
public class CafController extends AController {

    private final CafService cafService;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    @Inject
    public CafController(final MessagesApi messagesApi, final FormFactory formFactory, final PlaySessionStore playSessionStore, final CafService cafService) {
        super(messagesApi, formFactory, playSessionStore);
        this.cafService = cafService;
    }

    /**
     * Get caf result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result GET_Caf(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final ParentModel parent = account.getParent();
                final Form<CafInformationForm> boundForm = this.formFactory.form(CafInformationForm.class)
                    .fill(new CafInformationForm(parent));
                return Results.ok(CafInformationView.render(new CafDTO(this.cafService, messages, parent), boundForm, request, messages));
            })
        );
    }

    /**
     * Post caf result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result POST_Caf(final Http.Request request) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Form<CafInformationForm> boundForm = this.formFactory.form(CafInformationForm.class).bindFromRequest(request);

                final ParentModel parent = account.getParent();
                if (boundForm.hasErrors()) {
                    return Results.ok(CafInformationView.render(new CafDTO(this.cafService, messages, parent), boundForm, request, messages));
                }

                final CafInformationForm form = boundForm.get();

                this.safeDB(() -> {
                    form.updateParent(parent);
                });

                return Results.redirect(routes.CafController.GET_Caf());
            })
        );
    }
}
