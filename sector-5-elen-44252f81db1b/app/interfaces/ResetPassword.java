package interfaces;

import com.sendgrid.helpers.mail.objects.Email;
import com.typesafe.config.Config;
import controllers.routes;
import models.account.AccountModel;
import play.i18n.Messages;
import play.mvc.Http;
import services.EmailService;

import java.io.IOException;

/**
 * ResetPassword.
 *
 * @author Pierre Adam
 * @since 20.06.08
 */
public interface ResetPassword extends TokenToAccount {

    /**
     * Send validation email.
     *
     * @param request  the request
     * @param messages the messages
     * @param account  the account
     * @throws IOException the io exception
     */
    default void sendResetPasswordEmail(final Http.Request request,
                                        final Messages messages,
                                        final AccountModel account) throws IOException {
        final EmailService emailService = this.getEmailService();
        final Config config = this.getConfig();
        final String token = this.generateToken(account, config.getInt("email.resetPassword.tokenTtl"));

        emailService.sendMail(
            new Email(config.getString("email.resetPassword.from.email"), config.getString("email.resetPassword.from.name")),
            null,
            account.getEmail(),
            messages.at("email.resetPassword.subject"),
            null,
            views.html.emailviews.ResetPasswordEmail.render(
                routes.ForgotPasswordController.GET_ResetPassword(token).absoluteURL(request),
                request,
                messages
            )
        );
    }
}
