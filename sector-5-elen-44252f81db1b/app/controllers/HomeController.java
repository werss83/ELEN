package controllers;

import actions.SessionOptional;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;

/**
 * Home controller.
 *
 * @author Pierre Adam
 * @since 20.06.02
 */
public class HomeController extends AController {

    /**
     * Instantiates a new Home controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     */
    @Inject
    public HomeController(final MessagesApi messagesApi,
                          final FormFactory formFactory,
                          final PlaySessionStore playSessionStore) {
        super(messagesApi, formFactory, playSessionStore);
    }

    /**
     * Get home result.
     *
     * @param request the request
     * @return the result
     */
    @SessionOptional
    public Result GET_Home(final Http.Request request) {
        return this.withOptionalAccount(request, optionalAccount ->
//            this.withMessages(request, messages -> Results.ok(HomeView.render(request, messages)))
                this.withMessages(request, messages -> Results.redirect(controllers.routes.OnboardingController.GET_ParentOnboarding()))
        );
    }

    /**
     * Get policy result.
     *
     * @param request the request
     * @return the result
     */
    @SessionOptional
    public Result GET_Policy(final Http.Request request) {
        return Results.ok("Policy...");
    }

    /**
     * Get terms and conditions result.
     *
     * @param request the request
     * @return the result
     */
    @SessionOptional
    public Result GET_TermsAndConditions(final Http.Request request) {
        return Results.ok("Terms and Conditions...");
    }

}
