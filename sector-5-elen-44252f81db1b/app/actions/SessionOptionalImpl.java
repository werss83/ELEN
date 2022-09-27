package actions;

import org.pac4j.play.store.PlaySessionStore;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * WithAccountModelImpl.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
public class SessionOptionalImpl extends SessionBehavior<SessionOptional> {

    /**
     * Instantiates a new Session optional.
     *
     * @param playSessionStore the play session store
     */
    @Inject
    public SessionOptionalImpl(final PlaySessionStore playSessionStore) {
        super(playSessionStore, false);
    }

    @Override
    public CompletionStage<Result> call(final Http.Request request) {
        return this.underlyingCall(request, null);
    }
}
