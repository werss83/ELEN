package actions;

import controllers.routes;
import models.account.AccountModel;
import models.account.RoleModel;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.store.PlaySessionStore;
import org.slf4j.LoggerFactory;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import security.Role;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * SessionBehavior.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 20.06.04
 */
abstract class SessionBehavior<T> extends Action<T> {

    /**
     * The constant LOGGER.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SessionBehavior.class);

    /**
     * The Play session store.
     */
    protected final PlaySessionStore playSessionStore;

    /**
     * Is the session required.
     */
    protected final boolean sessionRequired;

    /**
     * Instantiates a new Session behavior.
     *
     * @param playSessionStore the play session store
     * @param sessionRequired  the session required
     */
    public SessionBehavior(final PlaySessionStore playSessionStore, final boolean sessionRequired) {
        this.playSessionStore = playSessionStore;
        this.sessionRequired = sessionRequired;
    }

    /**
     * Underlying call completion stage.
     *
     * @param request      the request
     * @param requiredRole the required role
     * @return the completion stage
     */
    public CompletionStage<Result> underlyingCall(final Http.Request request, final Role requiredRole) {
        final PlayWebContext context = new PlayWebContext(request, this.playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager<>(context);
        final Optional<CommonProfile> profileOptional = profileManager.get(true);

        final Optional<String> optionalUserUid = request.session().get("u");
        final Optional<String> optionalProfilePicture = request.session().get("p");
        final Optional<String> optionalRealUserUid = request.session().get("ru");

        if (!optionalUserUid.isPresent()) {
            // There is no profiles. Meaning that the user is not logged in.
            if (this.sessionRequired || requiredRole != null) {
                // If the session is required or if a specific role is needed
                // redirecting the user to the login page.
                return CompletableFuture.completedFuture(
                    Results.redirect(routes.StandardLoginController.GET_Login())
                        .flashing("info", "actions.sessionBehavior.mustBeLogged")
                );
            } else {
                return this.delegate.call(request);
            }
        }

        final UUID accountUid;
        try {
            accountUid = UUID.fromString(optionalUserUid.get());
        } catch (final Exception e) {
            LOGGER.info("Session in a weird state ! Cleaning ! (Missing LinkedId from profile)");
            if (profileOptional.isPresent()) {
                final CommonProfile profile = profileOptional.get();
                profile.removeLoginData();
                profileManager.remove(true);
            }
            return CompletableFuture.completedFuture(context.supplementResponse(Results.redirect(request.path()))
                .withFlash(request.flash())
                .withNewSession());
        }

        final AccountModel account = AccountModel.find
            .query()
            .where()
            .eq("uid", accountUid)
            .findOne();

        // In case of a profile currently logged in but a missing profile, a log out need to be done and the profile cleaned.
        if (account == null) {
            LOGGER.info("Session in a weird state ! Cleaning ! (The account does not exists)");
            if (profileOptional.isPresent()) {
                final CommonProfile profile = profileOptional.get();
                profile.removeLoginData();
                profileManager.remove(true);
            }
            return CompletableFuture.completedFuture(context.supplementResponse(Results.redirect(request.path()))
                .withFlash(request.flash())
                .withNewSession());
        }

        if (requiredRole != null) {
            // Check for the role.
            final RoleModel role = RoleModel.find
                .query()
                .where()
                .eq("account", account)
                .eq("role", requiredRole)
                .findOne();

            if (role == null) {
                // The account doesn't have the proper role.
                throw new RuntimeException("401");
            }
        }

        final AccountModel realAccount = optionalRealUserUid.map(s ->
            AccountModel.find.query()
                .where()
                .eq("uid", UUID.fromString(s))
                .findOne())
            .orElse(null);

        final String profilePicture;
        // Retrieving the profile picture.
        profilePicture = optionalProfilePicture.orElseGet(() -> routes.Assets.at("assets/img/patients/patient1.jpg").url());

        // The account is authorized to access the resource. Adding the account and profile picture to the request.
        Http.Request newRequest = request
            .addAttr(RequestAttrsKeys.ACCOUNT_MODEL, account)
            .addAttr(RequestAttrsKeys.PROFILE_PICTURE, profilePicture);

        if (realAccount != null) {
            newRequest = newRequest.addAttr(RequestAttrsKeys.REAL_ACCOUNT, realAccount);
        }

        return this.delegate.call(newRequest);
    }
}
