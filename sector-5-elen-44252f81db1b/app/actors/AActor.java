package actors;

import akka.actor.AbstractActor;
import com.zero_x_baadf00d.play.module.redis.PlayRedis;
import interfaces.DbLogic;
import io.ebean.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * AActor.
 *
 * @author Pierre Adam
 * @since 20.07.25
 */
public abstract class AActor extends AbstractActor {

    /**
     * The Logger.
     */
    protected final Logger logger;

    /**
     * The Play redis.
     */
    protected final PlayRedis playRedis;

    /**
     * Instantiates a new A actor.
     *
     * @param playRedis the play redis
     */
    public AActor(final PlayRedis playRedis) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.playRedis = playRedis;
    }

    /**
     * Redis lock or skip t.
     *
     * @param <T>   the type parameter
     * @param key   the key
     * @param ttl   the ttl
     * @param logic the logic
     * @return the t
     */
    protected <T> T redisLockOrSkip(final String key, final int ttl, final Supplier<T> logic) {
        if (this.playRedis.tryLock(key, ttl)) {
            try {
                return logic.get();
            } finally {
                this.playRedis.remove(key);
            }
        }
        return null;
    }

    /**
     * Safe db t.
     *
     * @param <T>   the type parameter
     * @param logic the logic
     * @return the t
     */
    protected void safeDB(final DbLogic logic) {
        try {
            DB.beginTransaction();
            logic.process();
            DB.commitTransaction();
        } finally {
            DB.endTransaction();
        }
    }

    /**
     * Safe db t.
     *
     * @param <T>   the type parameter
     * @param logic the logic
     * @return the t
     */
    protected <T> T safeDB(final Supplier<T> logic) {
        try {
            DB.beginTransaction();
            final T t = logic.get();
            DB.commitTransaction();
            return t;
        } finally {
            DB.endTransaction();
        }
    }
}
