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
 * EmailValidation.
 *
 * @author Pierre Adam
 * @since 20.06.08
 */
public interface EmailValidation extends TokenToAccount {

    /**
     * Send validation email.
     *
     * @param request  the request
     * @param messages the messages
     * @param account  the account
     * @throws IOException the io exception
     */
    default void sendValidationEmail(final Http.Request request,
                                     final Messages messages,
                                     final AccountModel account) throws IOException {
        final EmailService emailService = this.getEmailService();
        final Config config = this.getConfig();
        final String token = this.generateToken(account, config.getInt("email.validation.tokenTtl"));

        emailService.sendMail(
            new Email(config.getString("email.validation.from.email"), config.getString("email.validation.from.name")),
            null,
            account.getEmail(),
            messages.at("email.validation.subject"),
            null,
            views.html.emailviews.EmailValidationEmailView.render(
                routes.EmailValidationController.GET_ValidateEmail(token).absoluteURL(request),
                request,
                messages
            )
        );
    }
}
