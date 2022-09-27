package controllers;

import actions.SessionRequired;
import form.profile.ChangePasswordForm;
import form.profile.ProfileForm;
import org.pac4j.play.store.PlaySessionStore;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Html;
import security.Role;
import services.GoogleService;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * ProfileController.
 *
 * @author Pierre Adam
 * @since 20.06.26
 */
public class ProfileController extends AController {

    /**
     * The Google service.
     */
    private final GoogleService googleService;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param googleService    the google service
     */
    @Inject
    public ProfileController(final MessagesApi messagesApi,
                             final FormFactory formFactory,
                             final PlaySessionStore playSessionStore,
                             final GoogleService googleService) {
        super(messagesApi, formFactory, playSessionStore);
        this.googleService = googleService;
    }

    /**
     * Get admin profile result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public Result GET_AdminProfile(final Http.Request request) {
        return this.internalGetProfile(request,
            AbsProfileRender.forge(views.html.back.admin.common.UserProfileView::render,
                routes.ProfileController.POST_AdminProfile(), routes.ProfileController.POST_AdminPassword()));
    }

    /**
     * Post admin profile result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public CompletionStage<Result> POST_AdminProfile(final Http.Request request) {
        return this.internalPostProfile(request,
            AbsProfileRender.forge(views.html.back.admin.common.UserProfileView::render,
                routes.ProfileController.POST_AdminProfile(), routes.ProfileController.POST_AdminPassword()),
            routes.ProfileController.GET_AdminProfile());
    }

    /**
     * Post admin password completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    @SessionRequired(Role.ROLE_ADMIN)
    public CompletionStage<Result> POST_AdminPassword(final Http.Request request) {
        return this.internalPostPassword(request,
            AbsProfileRender.forge(views.html.back.admin.common.UserProfileView::render,
                routes.ProfileController.POST_AdminProfile(), routes.ProfileController.POST_AdminPassword()),
            routes.ProfileController.GET_AdminProfile());
    }

    /**
     * Get admin profile result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public Result GET_ParentProfile(final Http.Request request) {
        return this.internalGetProfile(request,
            AbsProfileRender.forge(views.html.back.parent.common.UserProfileView::render,
                routes.ProfileController.POST_ParentProfile(), routes.ProfileController.POST_ParentPassword()));
    }

    /**
     * Post admin profile result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_PARENT)
    public CompletionStage<Result> POST_ParentProfile(final Http.Request request) {
        return this.internalPostProfile(request,
            AbsProfileRender.forge(views.html.back.parent.common.UserProfileView::render,
                routes.ProfileController.POST_ParentProfile(), routes.ProfileController.POST_ParentPassword()),
            routes.ProfileController.GET_ParentProfile());
    }

    /**
     * Post parent password completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    @SessionRequired(Role.ROLE_PARENT)
    public CompletionStage<Result> POST_ParentPassword(final Http.Request request) {
        return this.internalPostPassword(request,
            AbsProfileRender.forge(views.html.back.parent.common.UserProfileView::render,
                routes.ProfileController.POST_ParentProfile(), routes.ProfileController.POST_ParentPassword()),
            routes.ProfileController.GET_ParentProfile());
    }

    /**
     * Get admin profile result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_SITTER)
    public Result GET_SitterProfile(final Http.Request request) {
        return this.internalGetProfile(request,
            AbsProfileRender.forge(views.html.back.sitter.common.UserProfileView::render,
                routes.ProfileController.POST_SitterProfile(), routes.ProfileController.POST_SitterPassword()));
    }

    /**
     * Post admin profile result.
     *
     * @param request the request
     * @return the result
     */
    @SessionRequired(Role.ROLE_SITTER)
    public CompletionStage<Result> POST_SitterProfile(final Http.Request request) {
        return this.internalPostProfile(request,
            AbsProfileRender.forge(views.html.back.sitter.common.UserProfileView::render,
                routes.ProfileController.POST_SitterProfile(), routes.ProfileController.POST_SitterPassword()),
            routes.ProfileController.GET_SitterProfile());
    }

    /**
     * Post sitter password completion stage.
     *
     * @param request the request
     * @return the completion stage
     */
    @SessionRequired(Role.ROLE_SITTER)
    public CompletionStage<Result> POST_SitterPassword(final Http.Request request) {
        return this.internalPostPassword(request,
            AbsProfileRender.forge(views.html.back.sitter.common.UserProfileView::render,
                routes.ProfileController.POST_SitterProfile(), routes.ProfileController.POST_SitterPassword()),
            routes.ProfileController.GET_SitterProfile());
    }

    /**
     * Internal get profile result.
     *
     * @param request the request
     * @param render  the render
     * @return the result
     */
    private Result internalGetProfile(final Http.Request request, final AbsProfileRender render) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Form<ProfileForm> boundForm = this.formFactory
                    .form(ProfileForm.class, ProfileForm.Groups.smartGroup(account))
                    .fill(new ProfileForm(account));
                final Form<ChangePasswordForm> boundPasswordForm = this.formFactory
                    .form(ChangePasswordForm.class, ChangePasswordForm.Groups.smartGroup(account));
                return Results.ok(render.apply(boundForm, boundPasswordForm, request, messages));
            })
        );
    }

    /**
     * Internal post profile result.
     *
     * @param request         the request
     * @param render          the render
     * @param successRedirect the success redirect
     * @return the result
     */
    private CompletionStage<Result> internalPostProfile(final Http.Request request,
                                                        final AbsProfileRender render,
                                                        final Call successRedirect) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Form<ProfileForm> boundForm = this.requestAddAttrs(request, ProfileForm.Attrs.ACCOUNT.bindValue(account),
                    requestWithAttrs -> this.formFactory
                        .form(ProfileForm.class, ProfileForm.Groups.smartGroup(account))
                        .bindFromRequest(requestWithAttrs));
                final Form<ChangePasswordForm> boundPasswordForm = this.formFactory
                    .form(ChangePasswordForm.class, ChangePasswordForm.Groups.smartGroup(account));

                if (boundForm.hasErrors()) {
                    return CompletableFuture.completedFuture(Results.ok(render.apply(boundForm, boundPasswordForm, request, messages)));
                }

                // The form is valid. Update the account and save.
                final ProfileForm form = boundForm.get();

                return this.googleService.addressToCoordinate(account, form)
                    .handle((point, throwable) -> {
                        if (throwable != null) {
                            if (throwable.getCause() instanceof GoogleService.NoResultException) {
                                final Form<ProfileForm> formWithError = boundForm.withError("address", "error.invalid");
                                return Results.ok(render.apply(formWithError, boundPasswordForm, request, messages));
                            }
                            throw new RuntimeException(throwable);
                        }
                        form.updateAccount(account);
                        account.setCoordinate(point);
                        account.save();

                        return Results.redirect(successRedirect);
                    });
            })
        );
    }

    /**
     * Internal post profile result.
     *
     * @param request         the request
     * @param render          the render
     * @param successRedirect the success redirect
     * @return the result
     */
    private CompletionStage<Result> internalPostPassword(final Http.Request request,
                                                         final AbsProfileRender render,
                                                         final Call successRedirect) {
        return this.withAccount(request, account ->
            this.withMessages(request, messages -> {
                final Form<ChangePasswordForm> boundForm = this.requestAddAttrs(request, ChangePasswordForm.Attrs.ACCOUNT.bindValue(account),
                    requestWithAttrs -> this.formFactory
                        .form(ChangePasswordForm.class, ChangePasswordForm.Groups.smartGroup(account))
                        .bindFromRequest(requestWithAttrs));

                if (boundForm.hasErrors()) {
                    final Form<ProfileForm> boundProfileForm = this.formFactory
                        .form(ProfileForm.class, ProfileForm.Groups.smartGroup(account))
                        .fill(new ProfileForm(account));
                    return CompletableFuture.completedFuture(Results.ok(render.apply(boundProfileForm, boundForm, request, messages)));
                }

                final ChangePasswordForm changePasswordForm = boundForm.get();
                account.setPassword(changePasswordForm.getNewPassword());
                account.save();

                return CompletableFuture.completedFuture(Results.redirect(successRedirect)
                    .flashing("success", "controllers.profile.passwordSuccessfullyUpdated"));
            })
        );
    }

    /**
     * The interface Abs profile render.
     */
    @FunctionalInterface
    public interface AbsProfileRender {
        /**
         * Forge abs profile render.
         *
         * @param innerCall the inner call
         * @param c1        the c 1
         * @param c2        the c 2
         * @return the abs profile render
         */
        static AbsProfileRender forge(final ProfileRender innerCall, final Call c1, final Call c2) {
            return (p1, p2, req, msg) -> innerCall.apply(p1, p2, c1, c2, req, msg);
        }

        /**
         * Apply html.
         *
         * @param p1  the p 1
         * @param p2  the p 2
         * @param req the req
         * @param msg the msg
         * @return the html
         */
        Html apply(Form<ProfileForm> p1, Form<ChangePasswordForm> p2, Http.Request req, Messages msg);

        /**
         * The interface Profile render.
         */
        @FunctionalInterface
        interface ProfileRender {
            /**
             * Apply html.
             *
             * @param p1  the p 1
             * @param p2  the p 2
             * @param c1  the c 1
             * @param c2  the c 2
             * @param req the req
             * @param msg the msg
             * @return the html
             */
            Html apply(Form<ProfileForm> p1, Form<ChangePasswordForm> p2, Call c1, Call c2, Http.Request req, Messages msg);
        }
    }
}
