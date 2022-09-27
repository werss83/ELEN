package controllers;

import actions.WithoutSession;
import form.accountservice.ForgotPasswordForm;
import form.accountservice.ResetPasswordForm;
import interfaces.ResetPassword;
import models.account.AccountModel;
import org.pac4j.play.store.PlaySessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.html.front.account.ForgotPasswordView;
import views.html.front.account.ResetPasswordView;

import javax.inject.Inject;
import java.util.Optional;

/**
 * ForgotPasswordController.
 *
 * @author Pierre Adam
 * @since 20.09.06
 */
public class ForgotPasswordController extends AController {

    /**
     * The LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardLoginController.class);

    /**
     * The Reset password.
     */
    private final ResetPassword resetPassword;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param resetPassword    the reset password
     */
    @Inject
    public ForgotPasswordController(final MessagesApi messagesApi,
                                    final FormFactory formFactory,
                                    final PlaySessionStore playSessionStore,
                                    final ResetPassword resetPassword) {
        super(messagesApi, formFactory, playSessionStore);
        this.resetPassword = resetPassword;
    }

    /**
     * Get forgot password result.
     *
     * @param request the request
     * @return the result
     */
    @WithoutSession
    public Result GET_ForgotPassword(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<ForgotPasswordForm> boundForm = this.formFactory.form(ForgotPasswordForm.class);
            return Results.ok(ForgotPasswordView.render(boundForm, request, messages));
        });
    }

    /**
     * Get forgot password result.
     *
     * @param request the request
     * @return the result
     */
    @WithoutSession
    public Result POST_ForgotPassword(final Http.Request request) {
        return this.withMessages(request, messages -> {
            final Form<ForgotPasswordForm> boundForm = this.formFactory.form(ForgotPasswordForm.class).bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return Results.ok(ForgotPasswordView.render(boundForm, request, messages));
            }

            final ForgotPasswordForm form = boundForm.get();
            final AccountModel account = AccountModel.find.query()
                .where()
                .eq("email", form.getEmail())
                .findOne();

            if (account != null) {
                try {
                    this.resetPassword.sendResetPasswordEmail(request, messages, account);
                } catch (final Exception e) {
                    this.logger.error("Unable to send reset password email.", e);
                }
            }

            return Results.redirect(routes.StandardLoginController.GET_Login())
                .flashing("success", "controllers.forgotPassword.requestReset");
        });
    }

    /**
     * Get reset password result.
     *
     * @param request the request
     * @param token   the token
     * @return the result
     */
    @WithoutSession
    public Result GET_ResetPassword(final Http.Request request, final String token) {
        return this.withMessages(request, messages -> {
            final Optional<AccountModel> optionalAccount = this.resetPassword.fromToken(token);

            if (!optionalAccount.isPresent()) {
                return Results.redirect(routes.HomeController.GET_Home())
                    .flashing("warning", "controllers.forgotPassword.invalidLink");
            }

            final Form<ResetPasswordForm> boundForm = this.formFactory.form(ResetPasswordForm.class);

            return Results.ok(ResetPasswordView.render(boundForm, token, request, messages));
        });
    }

    /**
     * Post reset password result.
     *
     * @param request the request
     * @param token   the token
     * @return the result
     */
    @WithoutSession
    public Result POST_ResetPassword(final Http.Request request, final String token) {
        return this.withMessages(request, messages -> {
            final Optional<AccountModel> optionalAccount = this.resetPassword.fromToken(token);

            if (!optionalAccount.isPresent()) {
                return Results.redirect(routes.HomeController.GET_Home())
                    .flashing("warning", "controllers.forgotPassword.invalidLink");
            }

            final Form<ResetPasswordForm> boundForm = this.formFactory.form(ResetPasswordForm.class).bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return Results.ok(ResetPasswordView.render(boundForm, token, request, messages));
            }

            final AccountModel accountModel = optionalAccount.get();
            final ResetPasswordForm form = boundForm.get();

            this.safeDB(() -> {
                accountModel.setPassword(form.getPassword());
                accountModel.save();
            });

            this.resetPassword.removeToken(token);

            return Results.redirect(routes.StandardLoginController.GET_Login())
                .flashing("success", "controllers.forgotPassword.passwordUpdated");
        });
    }
}
