package actions;

import controllers.routes;
import models.account.AccountModel;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.store.PlaySessionStore;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * WithAccountModelImpl.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
public class WithoutSessionImpl extends Action<WithoutSession> {

    /**
     * The Play session store.
     */
    protected final PlaySessionStore playSessionStore;

    /**
     * Instantiates a new Session required.
     *
     * @param playSessionStore the play session store
     */
    @Inject
    public WithoutSessionImpl(final PlaySessionStore playSessionStore) {
        this.playSessionStore = playSessionStore;
    }

    @Override
    public CompletionStage<Result> call(final Http.Request request) {
        final PlayWebContext context = new PlayWebContext(request, this.playSessionStore);
        final ProfileManager<CommonProfile> profileManager = new ProfileManager<>(context);
        final Optional<CommonProfile> profileOptional = profileManager.get(true);

        if (!profileOptional.isPresent()) {
            // There is no profiles. Meaning that the user is not logged in.
            return this.delegate.call(request);
        }

        final CommonProfile profile = profileOptional.get();
        final UUID accountUid;
        try {
            final String linkedId = profile.getLinkedId();
            accountUid = UUID.fromString(linkedId);
        } catch (final Exception e) {
            profile.removeLoginData();
            profileManager.remove(true);
            return CompletableFuture.completedFuture(context.supplementResponse(Results.redirect(request.path())).withFlash(request.flash()));
        }

        final AccountModel account = AccountModel.find
            .query()
            .where()
            .eq("uid", accountUid)
            .findOne();

        // In case of a profile currently logged in but a missing profile, a log out need to be done and the profile cleaned.
        if (account == null) {
            profile.removeLoginData();
            profileManager.remove(true);
            return CompletableFuture.completedFuture(context.supplementResponse(Results.redirect(request.path())).withFlash(request.flash()));
        }

        return CompletableFuture.completedFuture(
            Results.redirect(routes.HomeController.GET_Home()));
    }
}
