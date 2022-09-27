package services;

import behavior.PacProfileForger;
import behavior.RequestBehavior;
import com.sendgrid.helpers.mail.objects.Email;
import com.typesafe.config.Config;
import controllers.routes;
import form.accountservice.LoginForm;
import form.accountservice.RegistrationForm;
import interfaces.EmailValidation;
import io.ebean.DB;
import models.account.AccountModel;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.pac4j.oidc.profile.OidcProfile;
import org.pac4j.play.store.PlaySessionStore;
import org.postgis.Point;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * AccountService.
 *
 * @author Pierre Adam
 * @since 20.06.21
 */
@Singleton
public class AccountService extends Service implements RequestBehavior, PacProfileForger {

    /**
     * The Messages api.
     */
    private final MessagesApi messagesApi;

    /**
     * The Form factory.
     */
    private final FormFactory formFactory;

    /**
     * The Server Config.
     */
    private final Config config;

    /**
     * The Email service.
     */
    private final EmailService emailService;

    /**
     * The Email validation.
     */
    private final EmailValidation emailValidation;

    /**
     * The Play session store.
     */
    private final PlaySessionStore playSessionStore;

    /**
     * The Google service.
     */
    private final GoogleService googleService;

    /**
     * Instantiates a new Account service.
     *
     * @param messagesApi      the messages api
     * @param formFactory      the form factory
     * @param config           the config
     * @param emailService     the email service
     * @param emailValidation  the email validation
     * @param playSessionStore the play session store
     * @param googleService    the google service
     */
    @Inject
    public AccountService(final MessagesApi messagesApi,
                          final FormFactory formFactory,
                          final Config config,
                          final EmailService emailService,
                          final EmailValidation emailValidation,
                          final PlaySessionStore playSessionStore,
                          final GoogleService googleService) {
        super();
        this.messagesApi = messagesApi;
        this.formFactory = formFactory;
        this.config = config;
        this.emailService = emailService;
        this.emailValidation = emailValidation;
        this.playSessionStore = playSessionStore;
        this.googleService = googleService;
    }

    /**
     * Post login form result.
     *
     * @param request         the request
     * @param loginPageRender the login page render
     * @return the result
     */
    public Result GetLoginForm(final Http.Request request,
                               final BiFunction<Messages, Form<LoginForm>, Result> loginPageRender) {
        return this.internalWithMessages(this.messagesApi, request, messages -> {
            final Form<LoginForm> boundForm = this.formFactory.form(LoginForm.class);
            return loginPageRender.apply(messages, boundForm);
        });
    }

    /**
     * Post login form result.
     *
     * @param request               the request
     * @param loginPageRender       the login page render
     * @param successResultSupplier the success result supplier
     * @return the result
     */
    public Result PostLoginForm(final Http.Request request,
                                final BiFunction<Messages, Form<LoginForm>, Result> loginPageRender,
                                final Function<AccountModel, Result> successResultSupplier) {
        return this.internalWithMessages(this.messagesApi, request, messages -> {
            final Form<LoginForm> boundForm = this.formFactory.form(LoginForm.class).bindFromRequest(request);

            if (boundForm.hasErrors()) {
                return loginPageRender.apply(messages, boundForm);
            } else {
                final LoginForm form = boundForm.get();
                final AccountModel account = form.getResolvedAccount();

                // Initialize the context from the session store and forge a new pac4j profile for the user.
                // This part will log the user in without any other verification.
                return this.internalProfileManager(this.playSessionStore, request, CommonProfile.class,
                    (context, profileManager) -> {
                        final CommonProfile profile = this.forgeProfileFromAccount(account);
                        return this.updateProfileAndSession(request, context, profileManager, profile, account, successResultSupplier);
                    });
            }
        });
    }

    /**
     * Get registration form result.
     *
     * @param request    the request
     * @param pageRender the registration page render
     * @return the result
     */
    public Result GetRegistrationForm(final Http.Request request,
                                      final PageRender pageRender) {
        return this.internalWithMessages(this.messagesApi, request, messages -> {
            final Form<RegistrationForm> form = this.formFactory.form(RegistrationForm.class);
            return pageRender.apply(messages, form);
        });
    }

    /**
     * Get registration form result.
     *
     * @param request                       the request
     * @param pageRender                    the registration page render
     * @param creationSuccessExtraActions   the success extra actions
     * @param creationSuccessResultSupplier the success result supplier
     * @return the result
     */
    public CompletionStage<Result> PostRegistrationForm(final Http.Request request,
                                                        final PageRender pageRender,
                                                        final Consumer<AccountModel> creationSuccessExtraActions,
                                                        final Function<AccountModel, Result> creationSuccessResultSupplier) {
        return this.internalWithMessages(this.messagesApi, request, messages -> {
            final Form<RegistrationForm> boundForm = this.formFactory.form(RegistrationForm.class).bindFromRequest(request);

            return this.validateForm(messages, boundForm, pageRender,
                (form, coordinate) -> {
                    final AccountModel account;
                    try {
                        DB.beginTransaction();
                        // Build the account, save it and allow for extra actions from the calling controller.
                        account = form.buildAccount();
                        account.setCoordinate(coordinate);
                        account.save();

                        creationSuccessExtraActions.accept(account);
                        DB.commitTransaction();
                    } catch (final Exception e) {
                        return pageRender.apply(messages, boundForm)
                            .flashing("danger", "services.account.error");
                    } finally {
                        DB.endTransaction();
                    }

                    // Send the validation email.
                    try {
                        this.emailValidation.sendValidationEmail(request, messages, account);
                        this.sendWelcomeEmail(request, messages, account);
                    } catch (final IOException e) {
                        this.logger.error("Unable to send the validation email", e);
                    }

                    // Initialize the context from the session store and forge a new pac4j profile for the user.
                    // This part will log the user in without any other verification.
                    return this.internalProfileManager(this.playSessionStore, request, CommonProfile.class,
                        (context, profileManager) -> {
                            final CommonProfile profile = this.forgeProfileFromAccount(account);
                            return this.updateProfileAndSession(request, context, profileManager, profile, account, creationSuccessResultSupplier);
                        });
                });
        });
    }

    /**
     * Get facebook auth result.
     *
     * @param request                    the request
     * @param pageRender                 the register page render
     * @param loginSuccessExtraActions   the login success extra actions
     * @param loginSuccessResultSupplier the login success result supplier
     * @return the result
     */
    public Result GetFacebookAuth(final Http.Request request,
                                  final PageRender pageRender,
                                  final Consumer<AccountModel> loginSuccessExtraActions,
                                  final Function<AccountModel, Result> loginSuccessResultSupplier) {
        return this.GetFacebookAuth(
            request,
            new RegistrationForm(),
            pageRender,
            loginSuccessExtraActions,
            loginSuccessResultSupplier
        );
    }

    /**
     * Get facebook auth result.
     *
     * @param request                    the request
     * @param form                       the registration form
     * @param pageRender                 the register page render
     * @param loginSuccessExtraActions   the login success extra actions
     * @param loginSuccessResultSupplier the login success result supplier
     * @return the result
     */
    public Result GetFacebookAuth(final Http.Request request,
                                  final RegistrationForm form,
                                  final PageRender pageRender,
                                  final Consumer<AccountModel> loginSuccessExtraActions,
                                  final Function<AccountModel, Result> loginSuccessResultSupplier) {
        return this.internalAlterableProfile(this.playSessionStore, request, FacebookProfile.class,
            (context, profileManager, profile) -> {
                // Try to resolve the account using the facebook ID (cause the email could potentially be different)
                AccountModel account = AccountModel.find
                    .query()
                    .where()
                    .eq("facebook_id", profile.getId())
                    .findOne();

                if (account == null && profile.getEmail() != null) {
                    // Try to resolve the account using the email.
                    account = AccountModel.find
                        .query()
                        .where()
                        .eq("email", profile.getEmail())
                        .findOne();

                    if (account != null) {
                        account.setFacebookId(profile.getId());
                        account.setEmailVerified(true);
                        account.save();
                    }
                }

                // If the account exists. Continue by applying extra actions if needed
                // then continuing with the success result.
                if (account != null) {
                    return this.continueWithActiveAccountOrLogout(account,
                        (validAccount) -> {
                            // Apply extra actions to the account.
                            if (loginSuccessExtraActions != null) {
                                loginSuccessExtraActions.accept(validAccount);
                            }

                            return this.updateProfileAndSession(request, context, profileManager, profile, validAccount, loginSuccessResultSupplier);
                        });
                }

                // If the account does not exists. Continuing to the registration process.
                Class<?> group = RegistrationForm.Groups.SocialFormWithEmail.class;

                form.setFirstName(profile.getFirstName());
                form.setLastName(profile.getFamilyName());

                // If the email is on the profile, the user can't change it. So we use the SocialForm.class
                if (profile.getEmail() != null) {
                    form.setEmail(profile.getEmail());
                    group = RegistrationForm.Groups.SocialForm.class;
                }

                final Form<RegistrationForm> boundForm = this.formFactory.form(RegistrationForm.class, group).fill(form);

                return this.internalWithMessages(this.messagesApi, request,
                    messages -> pageRender.apply(messages, boundForm));
            });
    }

    /**
     * Post facebook auth result.
     *
     * @param request                       the request
     * @param pageRender                    the registration page render
     * @param creationSuccessExtraActions   the creation success extra actions
     * @param creationSuccessResultSupplier the creation success result supplier
     * @return the result
     */
    public CompletionStage<Result> PostFacebookAuth(final Http.Request request,
                                                    final PageRender pageRender,
                                                    final Consumer<AccountModel> creationSuccessExtraActions,
                                                    final Function<AccountModel, Result> creationSuccessResultSupplier) {
        return this.internalAlterableProfile(this.playSessionStore, request, FacebookProfile.class,
            (context, profileManager, profile) ->
                this.internalWithMessages(this.messagesApi, request, messages -> {
                    final Form<RegistrationForm> boundForm;
                    if (profile.getEmail() != null) {
                        boundForm = this.internalRequestAddAttrs(request, RegistrationForm.Attrs.EMAIL.bindValue(profile.getEmail()),
                            requestWithAttr -> this.formFactory
                                .form(RegistrationForm.class, RegistrationForm.Groups.SocialForm.class)
                                .bindFromRequest(requestWithAttr));
                    } else {
                        boundForm = this.formFactory
                            .form(RegistrationForm.class, RegistrationForm.Groups.SocialFormWithEmail.class)
                            .bindFromRequest(request);
                    }

                    return this.validateForm(messages, boundForm, pageRender, (form, coordinate) -> {
                        final String email = profile.getEmail() == null ? form.getEmail() : profile.getEmail();

                        // Test if an account exists with that ID.
                        AccountModel account = AccountModel.find
                            .query()
                            .where()
                            .eq("facebook_id", profile.getId())
                            .findOne();

                        if (account != null) {
                            // If an account exists, redirecting to the standard login and ignore the registration.
                            return Results.redirect(routes.StandardLoginController.GET_LoginFacebook());
                        }

                        try {
                            DB.beginTransaction();
                            // Build the account, save it and allow for extra actions from the calling controller.
                            account = form.buildAccount(email);
                            if (profile.getEmail() != null) {
                                account.setEmailVerified(true);
                            }
                            account.setCoordinate(coordinate);
                            account.setFacebookId(profile.getId());
                            account.save();

                            creationSuccessExtraActions.accept(account);
                            DB.commitTransaction();
                        } catch (final Exception e) {
                            this.logger.error("An exception occurred !", e);
                            return pageRender.apply(messages, boundForm)
                                .flashing("danger", "services.account.error");
                        } finally {
                            DB.endTransaction();
                        }

                        if (!account.getEmailVerified()) {
                            // Send the validation email.
                            try {
                                this.emailValidation.sendValidationEmail(request, messages, account);
                            } catch (final IOException e) {
                                this.logger.error("Unable to send the validation email", e);
                            }
                        }
                        // Send the welcome email.
                        try {
                            this.sendWelcomeEmail(request, messages, account);
                        } catch (final IOException e) {
                            this.logger.error("Unable to send the validation email", e);
                        }

                        return this.updateProfileAndSession(request, context, profileManager, profile, account, creationSuccessResultSupplier);
                    });
                })
        );
    }

    /**
     * Get Google auth result.
     *
     * @param request                    the request
     * @param pageRender                 the register page render
     * @param loginSuccessExtraActions   the login success extra actions
     * @param loginSuccessResultSupplier the login success result supplier
     * @return the result
     */
    public Result GetGoogleAuth(final Http.Request request,
                                final PageRender pageRender,
                                final Consumer<AccountModel> loginSuccessExtraActions,
                                final Function<AccountModel, Result> loginSuccessResultSupplier) {
        return this.GetGoogleAuth(request, new RegistrationForm(), pageRender, loginSuccessExtraActions, loginSuccessResultSupplier);
    }

    /**
     * Get Google auth result.
     *
     * @param request                    the request
     * @param form                       the registration form
     * @param pageRender                 the register page render
     * @param loginSuccessExtraActions   the login success extra actions
     * @param loginSuccessResultSupplier the login success result supplier
     * @return the result
     */
    public Result GetGoogleAuth(final Http.Request request,
                                final RegistrationForm form,
                                final PageRender pageRender,
                                final Consumer<AccountModel> loginSuccessExtraActions,
                                final Function<AccountModel, Result> loginSuccessResultSupplier) {
        return this.internalAlterableProfile(this.playSessionStore, request, OidcProfile.class,
            (context, profileManager, profile) -> {
                // Try to resolve the account using the Google ID (cause the email could potentially be different)
                AccountModel account = AccountModel.find
                    .query()
                    .where()
                    .eq("google_id", profile.getId())
                    .findOne();

                if (account == null && profile.getEmail() != null) {
                    // Try to resolve the account using the email.
                    account = AccountModel.find
                        .query()
                        .where()
                        .eq("email", profile.getEmail())
                        .findOne();

                    if (account != null) {
                        account.setGoogleId(profile.getId());
                        account.setEmailVerified(true);
                        if (account.getProfilePicture() == null || account.getProfilePicture().isEmpty()) {
                            account.setProfilePicture(profile.getPictureUrl().toString());
                        }
                        account.save();
                    }
                }

                // If the account exists. Continue by applying extra actions if needed
                // then continuing with the success result.
                if (account != null) {
                    return this.continueWithActiveAccountOrLogout(account,
                        validAccount -> {
                            // Apply extra actions to the account.
                            if (loginSuccessExtraActions != null) {
                                loginSuccessExtraActions.accept(validAccount);
                            }

                            return this.updateProfileAndSession(request, context, profileManager, profile, validAccount, loginSuccessResultSupplier);
                        });
                }

                // If the account does not exists. Continuing to the registration process.
                form.setFirstName(profile.getFirstName());
                form.setLastName(profile.getFamilyName());
                form.setEmail(profile.getEmail());

                final Form<RegistrationForm> boundForm = this.formFactory
                    .form(RegistrationForm.class, RegistrationForm.Groups.SocialForm.class)
                    .fill(form);

                return this.internalWithMessages(this.messagesApi, request,
                    messages -> pageRender.apply(messages, boundForm));
            });
    }

    /**
     * Post facebook auth result.
     *
     * @param request                       the request
     * @param pageRender                    the registration page render
     * @param creationSuccessExtraActions   the creation success extra actions
     * @param creationSuccessResultSupplier the creation success result supplier
     * @return the result
     */
    public CompletionStage<Result> PostGoogleAuth(final Http.Request request,
                                                  final PageRender pageRender,
                                                  final Consumer<AccountModel> creationSuccessExtraActions,
                                                  final Function<AccountModel, Result> creationSuccessResultSupplier) {
        return this.internalAlterableProfile(this.playSessionStore, request, OidcProfile.class,
            (context, profileManager, profile) ->
                this.internalWithMessages(this.messagesApi, request, messages -> {
                    final Form<RegistrationForm> boundForm = this
                        .internalRequestAddAttrs(request, RegistrationForm.Attrs.EMAIL.bindValue(profile.getEmail()),
                            requestWithAttr -> this.formFactory
                                .form(RegistrationForm.class, RegistrationForm.Groups.SocialForm.class)
                                .bindFromRequest(requestWithAttr));

                    return this.validateForm(messages, boundForm, pageRender, (form, coordinate) -> {
                        // Test if an account exists with that ID.
                        AccountModel account = AccountModel.find
                            .query()
                            .where()
                            .eq("google_id", profile.getId())
                            .findOne();

                        if (account != null) {
                            // If an account exists, redirecting to the standard login and ignore the registration.
                            return Results.redirect(routes.StandardLoginController.GET_LoginGoogle());
                        }

                        try {
                            DB.beginTransaction();
                            // Build the account, save it and allow for extra actions from the calling controller.
                            account = form.buildAccount(profile.getEmail());
                            account.setEmailVerified(true);
                            account.setCoordinate(coordinate);
                            account.setGoogleId(profile.getId());
                            account.setProfilePicture(profile.getPictureUrl().toString());
                            account.save();

                            creationSuccessExtraActions.accept(account);
                            DB.commitTransaction();
                        } catch (final Exception e) {
                            this.logger.error("An exception occurred !", e);
                            return pageRender.apply(messages, boundForm)
                                .flashing("danger", "services.account.error");
                        } finally {
                            DB.endTransaction();
                        }

                        // Send the welcome email.
                        try {
                            this.sendWelcomeEmail(request, messages, account);
                        } catch (final IOException e) {
                            this.logger.error("Unable to send the validation email", e);
                        }

                        return this.updateProfileAndSession(request, context, profileManager, profile, account, creationSuccessResultSupplier);
                    });
                })
        );
    }

    /**
     * Validate form completion stage.
     *
     * @param messages   the messages
     * @param boundForm  the bound form
     * @param pageRender the page render
     * @param onSuccess  the on success
     * @return the completion stage
     */
    private CompletionStage<Result> validateForm(final Messages messages,
                                                 final Form<RegistrationForm> boundForm,
                                                 final PageRender pageRender,
                                                 final FormSuccess onSuccess) {
        if (boundForm.hasErrors()) {
            return CompletableFuture.completedFuture(pageRender.apply(messages, boundForm));
        }

        final RegistrationForm form = boundForm.get();

        return this.googleService.addressToCoordinate(form)
            .handle((coordinate, throwable) -> {
                if (throwable != null) {
                    if (throwable.getCause() instanceof GoogleService.NoResultException) {
                        final Form<RegistrationForm> formWithError = boundForm.withError("address", "error.invalid");
                        return pageRender.apply(messages, formWithError);
                    }
                    throw new RuntimeException(throwable);
                }

                return onSuccess.apply(form, coordinate);
            });
    }

    /**
     * Send welcome email.
     *
     * @param request  the request
     * @param messages the messages
     * @param account  the account
     * @throws IOException the io exception
     */
    private void sendWelcomeEmail(final Http.Request request,
                                  final Messages messages,
                                  final AccountModel account) throws IOException {
        this.emailService.sendMail(
            new Email(this.config.getString("email.welcome.from.email"), this.config.getString("email.welcome.from.name")),
            new Email(this.config.getString("email.welcome.replyTo.email"), this.config.getString("email.welcome.replyTo.name")),
            account.getEmail(),
            messages.at("email.welcome.subject"),
            null,
            views.html.emailviews.WelcomeEmail.render(account, request, messages)
        );
    }

    /**
     * The interface Page render.
     */
    @FunctionalInterface
    public interface PageRender extends BiFunction<Messages, Form<RegistrationForm>, Result> {
    }

    /**
     * The interface Form success.
     */
    @FunctionalInterface
    public interface FormSuccess extends BiFunction<RegistrationForm, Point, Result> {
    }
}
