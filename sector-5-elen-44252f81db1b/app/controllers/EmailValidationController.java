package controllers;

import actions.SessionOptional;
import interfaces.EmailValidation;
import models.account.AccountModel;
import org.pac4j.play.store.PlaySessionStore;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.Optional;

/**
 * EmailValidationController.
 *
 * @author Pierre Adam
 * @since 20.06.08
 */
public class EmailValidationController extends AController {

    /**
     * The Email validation.
     */
    private final EmailValidation emailValidation;

    /**
     * Instantiates a new A controller.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param playSessionStore the play session store
     * @param emailValidation  the email validation
     */
    @Inject
    public EmailValidationController(final MessagesApi messagesApi,
                                     final FormFactory formFactory,
                                     final PlaySessionStore playSessionStore,
                                     final EmailValidation emailValidation) {
        super(messagesApi, formFactory, playSessionStore);
        this.emailValidation = emailValidation;
    }

    /**
     * Get validate email result.
     *
     * @param token   the token
     * @param request the request
     * @return the result
     */
    @SessionOptional
    public Result GET_ValidateEmail(final Http.Request request, final String token) {
        return this.withMessages(request, messages ->
            this.withOptionalAccount(request, optionalLoggedAccount -> {
                final Optional<AccountModel> optionalAccount = this.emailValidation.fromToken(token);

                if (!optionalAccount.isPresent()) {
                    return Results.redirect(routes.HomeController.GET_Home())
                        .flashing("danger", "controllers.emailValidation.invalidLink");
                }

                final AccountModel account = optionalAccount.get();

                if (optionalLoggedAccount.isPresent()) {
                    final AccountModel loggerAccount = optionalLoggedAccount.get();

                    if (!loggerAccount.getId().equals(account.getId())) {
                        this.logger.error("The account [{}] tried to validate an email for the account [{}]", loggerAccount.getId(), account.getId());
                        return Results.redirect(routes.HomeController.GET_Home())
                            .flashing("danger", "controllers.emailValidation.invalidLink");
                    }
                }

                account.setEmailVerified(true);
                account.save();

                this.emailValidation.removeToken(token);

                if (optionalLoggedAccount.isPresent()) {
                    return Results.redirect(routes.HomeController.GET_Home())
                        .flashing("success", "controllers.emailValidation.validAndLogged");
                } else {
                    return Results.redirect(routes.StandardLoginController.GET_Login())
                        .flashing("success", "controllers.emailValidation.valid");
                }
            })
        );
    }
}
