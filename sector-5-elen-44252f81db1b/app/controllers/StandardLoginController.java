package controllers;

import actions.WithoutSession;
import models.account.AccountModel;
import models.account.RoleModel;
import models.parent.ParentCriteriaModel;
import models.parent.ParentModel;
import org.pac4j.play.java.Secure;
import org.pac4j.play.store.PlaySessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.mvc.Call;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;
import services.AccountService;
import views.html.front.account.LoginView;
import views.html.front.account.RegistrationView;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * LoginController.
 *
 * @author Pierre Adam
 * @since 20.05.29
 */
public class StandardLoginController extends AController {

    /**
     * The LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardLoginController.class);

    /**
     * The account service.
     */
    private final AccountService accountService;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param accountService   the account service
     */
    @Inject
    public StandardLoginController(final MessagesApi messagesApi,
                                   final FormFactory formFactory,
                                   final PlaySessionStore playSessionStore,
                                   final AccountService accountService) {
        super(messagesApi, formFactory, playSessionStore);
        this.accountService = accountService;
    }

    /**
     * Get login result.
     *
     * @param request the request
     * @return the result
     */
    @WithoutSession
    public Result GET_Login(final Http.Request request) {
        return this.accountService.GetLoginForm(request,
            (messages, form) -> Results.ok(LoginView.render(form, request, messages)));
    }

    /**
     * Post login result.
     *
     * @param request the request
     * @return the result
     */
    @WithoutSession
    public Result POST_Login(final Http.Request request) {
        return this.accountService.PostLoginForm(request,
            (messages, form) -> Results.ok(LoginView.render(form, request, messages)),
            this::redirectOnSuccess);
    }

    /**
     * Get login google result.
     *
     * @param request the request
     * @return the result
     */
    @Secure(clients = "GoogleClient")
    public Result GET_LoginGoogle(final Http.Request request) {
        return this.accountService.GetGoogleAuth(request,
            (messages, form) ->
                Results.ok(RegistrationView.render(form, routes.StandardLoginController.POST_LoginGoogle().url(), false, request, messages)),
            null,
            this::redirectOnSuccess);
    }

    /**
     * Post login google result.
     *
     * @param request the request
     * @return the result
     */
    public CompletionStage<Result> POST_LoginGoogle(final Http.Request request) {
        return this.accountService.PostGoogleAuth(request,
            (messages, form) ->
                Results.ok(RegistrationView.render(form, routes.StandardLoginController.POST_LoginGoogle().url(), false, request, messages)),
            this::checkOrGiveParentRole,
            this::redirectOnSuccess);
    }

    /**
     * Get login facebook result.
     *
     * @param request the request
     * @return the result
     */
    @Secure(clients = "FacebookClient")
    public Result GET_LoginFacebook(final Http.Request request) {
        return this.accountService.GetFacebookAuth(request,
            (messages, form) ->
                Results.ok(RegistrationView.render(form, routes.StandardLoginController.POST_LoginFacebook().url(), false, request, messages)),
            null,
            this::redirectOnSuccess);
    }

    /**
     * Post login facebook result.
     *
     * @param request the request
     * @return the result
     */
    public CompletionStage<Result> POST_LoginFacebook(final Http.Request request) {
        return this.accountService.PostFacebookAuth(request,
            (messages, form) ->
                Results.ok(RegistrationView.render(form, routes.StandardLoginController.POST_LoginFacebook().url(), false, request, messages)),
            this::checkOrGiveParentRole,
            this::redirectOnSuccess);
    }

    /**
     * Get registration result.
     *
     * @param request the request
     * @return the result
     */
    @WithoutSession
    public Result GET_Registration(final Http.Request request) {
        final String formUrl = routes.StandardLoginController.POST_Registration().url();
        return this.accountService.GetRegistrationForm(request,
            (messages, form) -> Results.ok(RegistrationView.render(form, formUrl, true, request, messages))
        );
    }

    /**
     * Post registration result.
     *
     * @param request the request
     * @return the result
     */
    @WithoutSession
    public CompletionStage<Result> POST_Registration(final Http.Request request) {
        final String formUrl = routes.StandardLoginController.POST_Registration().url();
        return this.accountService.PostRegistrationForm(request,
            (messages, form) -> Results.ok(RegistrationView.render(form, formUrl, true, request, messages)),
            this::checkOrGiveParentRole,
            this::redirectOnSuccess
        );
    }

    /**
     * Check or give parent role result.
     *
     * @param account the account
     */
    private void checkOrGiveParentRole(final AccountModel account) {
        RoleModel parentRole = RoleModel.find
            .query()
            .where()
            .eq("account", account)
            .eq("role", Role.ROLE_PARENT)
            .findOne();

        if (parentRole == null) {
            // The parent role is missing. Adding one !
            parentRole = new RoleModel(account, Role.ROLE_PARENT);
            parentRole.save();
        }

        if (account.getParent() == null) {
            // The account does not have any parent preferences in database.
            final ParentModel parent = new ParentModel(account, new ParentCriteriaModel());
            parent.save();
        }
    }

    /**
     * Redirect on success result.
     *
     * @param account the account
     * @return the result
     */
    private Result redirectOnSuccess(final AccountModel account) {
        final Call target;

        if (account.hasRole(Role.ROLE_ADMIN)) {
            target = controllers.admin.routes.AdminController.GET_AdminDashboard();
        } else if (account.hasRole(Role.ROLE_SITTER)) {
            target = controllers.sitter.routes.SitterController.GET_SitterDashboard();
        } else if (account.hasRole(Role.ROLE_PARENT)) {
            target = controllers.parent.routes.ParentController.GET_ParentDashboard();
        } else {
            target = routes.HomeController.GET_Home();
        }
        return Results.redirect(target);
    }
}
