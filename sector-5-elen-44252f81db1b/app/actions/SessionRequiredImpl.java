package actions;

import org.pac4j.play.store.PlaySessionStore;
import play.mvc.Http;
import play.mvc.Result;
import security.Role;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * WithAccountModelImpl.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
public class SessionRequiredImpl extends SessionBehavior<SessionRequired> {

    /**
     * Instantiates a new Session required.
     *
     * @param playSessionStore the play session store
     */
    @Inject
    public SessionRequiredImpl(final PlaySessionStore playSessionStore) {
        super(playSessionStore, true);
    }

    @Override
    public CompletionStage<Result> call(final Http.Request request) {
        final Role role = this.configuration.value();
        return this.underlyingCall(request, role.equals(Role.NONE) ? null : role);
    }
}
