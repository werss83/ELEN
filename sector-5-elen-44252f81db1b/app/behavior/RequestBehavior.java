package behavior;

import actions.RequestAttrsKeys;
import be.objectify.deadbolt.java.utils.TriFunction;
import models.account.AccountModel;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.store.PlaySessionStore;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.typedmap.TypedEntry;
import play.libs.typedmap.TypedMap;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * RequestBehavior.
 *
 * @author Pierre Adam
 * @since 20.06.21
 */
public interface RequestBehavior {

    /**
     * With messages r.
     *
     * @param <R>         the type parameter
     * @param messagesApi the messages api
     * @param request     the request
     * @param callback    the callback
     * @return the r
     */
    default <R> R internalWithMessages(final MessagesApi messagesApi, final Http.Request request, final Function<Messages, R> callback) {
        return callback.apply(messagesApi.preferred(request));
    }

    /**
     * With account r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param callback the callback
     * @return the r
     */
    default <R> R internalWithAccount(final Http.Request request, final Function<AccountModel, R> callback) {
        return this.internalWithOptionalAccount(request, optionalAccount -> {
            if (!optionalAccount.isPresent()) {
                throw new RuntimeException("401");
            }
            return callback.apply(optionalAccount.get());
        });
    }

    /**
     * With optional account r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param callback the callback
     * @return the r
     */
    default <R> R internalWithOptionalAccount(final Http.Request request,
                                              final Function<Optional<AccountModel>, R> callback) {
        return callback.apply(request.attrs().getOptional(RequestAttrsKeys.ACCOUNT_MODEL));
    }

    /**
     * Internal profile manager.
     *
     * @param <R>              the type parameter
     * @param <T>              the type parameter
     * @param playSessionStore the play session store
     * @param request          the request
     * @param tClass           the t class
     * @param callback         the callback
     * @return the r
     */
    default <R, T extends UserProfile> R internalProfileManager(final PlaySessionStore playSessionStore,
                                                                final Http.Request request,
                                                                final Class<T> tClass,
                                                                final BiFunction<PlayWebContext, ProfileManager<T>, R> callback) {
        final PlayWebContext context = new PlayWebContext(request, playSessionStore);
        final ProfileManager<T> profileManager = new ProfileManager<>(context);

        return callback.apply(context, profileManager);
    }

    /**
     * Internal alterable profile.
     *
     * @param <R>              the type parameter
     * @param <T>              the type parameter
     * @param playSessionStore the play session store
     * @param request          the request
     * @param tClass           the t class
     * @param callback         the callback
     * @return the r
     */
    default <R, T extends UserProfile> R internalAlterableProfile(final PlaySessionStore playSessionStore,
                                                                  final Http.Request request,
                                                                  final Class<T> tClass,
                                                                  final TriFunction<PlayWebContext, ProfileManager<T>, T, R> callback) {
        return this.internalProfileManager(playSessionStore, request, tClass, (context, profileManager) -> {
            final Optional<T> profileOptional = profileManager.get(true);

            if (!profileOptional.isPresent()) {
                throw new RuntimeException("401", new RuntimeException("internalAlterableProfile was called without any profile available."));
            }

            final T profile = profileOptional.get();

            if (!tClass.equals(profile.getClass())) {
                throw new RuntimeException("401",
                    new RuntimeException(
                        String.format(
                            "internalAlterableProfile wrong profile available. Asking for [%s] got [%s]",
                            tClass.getCanonicalName(),
                            profile.getClass().getCanonicalName()
                        )
                    )
                );
            }

            return callback.apply(context, profileManager, profile);
        });
    }

    /**
     * Continue with active account or logout result.
     *
     * @param account        the account
     * @param resultSupplier the result supplier
     * @return the result
     */
    default Result continueWithActiveAccountOrLogout(final AccountModel account, final Function<AccountModel, Result> resultSupplier) {
        if (!account.getActive()) {
            return Results.redirect(org.pac4j.play.routes.LogoutController.logout())
                .flashing("danger", "behavior.requestBehavior.accountSuspended");
        }

        return resultSupplier.apply(account);
    }

    /**
     * Request add attrs r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param attr     the attr
     * @param callback the callback
     * @return the r
     */
    default <R> R internalRequestAddAttrs(final Http.Request request, final TypedEntry attr, final Function<Http.Request, R> callback) {
        final TypedMap typedMap = request.attrs().putAll(attr);
        return callback.apply(request.withAttrs(typedMap));
    }

    /**
     * Request add attrs r.
     *
     * @param <R>      the type parameter
     * @param request  the request
     * @param attrs    the attrs
     * @param callback the callback
     * @return the r
     */
    default <R> R internalRequestAddAttrs(final Http.Request request, final List<TypedEntry> attrs, final Function<Http.Request, R> callback) {
        TypedMap typedMap = request.attrs();
        for (final TypedEntry attr : attrs) {
            typedMap = typedMap.putAll(attr);
        }
        return callback.apply(request.withAttrs(typedMap));
    }
}
