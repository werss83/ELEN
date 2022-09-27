package redis;

import com.typesafe.config.Config;
import com.zero_x_baadf00d.play.module.redis.PlayRedis;
import interfaces.EmailValidation;
import services.EmailService;

import javax.inject.Inject;

/**
 * RedisEmailValidation.
 *
 * @author Pierre Adam
 * @since 20.06.08
 */
public class RedisEmailValidation extends TokenToAccountImpl implements EmailValidation {

    /**
     * Instantiates a new Redis email validation.
     *
     * @param playRedis    the play redis
     * @param emailService the email service
     * @param config       the config
     */
    @Inject
    public RedisEmailValidation(final PlayRedis playRedis, final EmailService emailService, final Config config) {
        super(playRedis, emailService, config);
    }

    @Override
    public String getKeyFromToken(final String token) {
        return String.format(RedisKeys.emailVerificationCode, token);
    }
}
