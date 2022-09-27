package redis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.typesafe.config.Config;
import com.zero_x_baadf00d.play.module.redis.PlayRedis;
import interfaces.TokenToAccount;
import models.account.AccountModel;
import org.apache.commons.lang3.RandomStringUtils;
import services.EmailService;

import java.util.Optional;

/**
 * TokenToAccountImpl.
 *
 * @author Pierre Adam
 * @since 20.09.06
 */
public abstract class TokenToAccountImpl implements TokenToAccount {

    /**
     * The Play redis.
     */
    private final PlayRedis playRedis;

    /**
     * The Email service.
     */
    private final EmailService emailService;

    /**
     * The Config.
     */
    private final Config config;

    /**
     * Instantiates a new Redis email validation.
     *
     * @param playRedis    the play redis
     * @param emailService the email service
     * @param config       the config
     */
    public TokenToAccountImpl(final PlayRedis playRedis, final EmailService emailService, final Config config) {
        this.playRedis = playRedis;
        this.emailService = emailService;
        this.config = config;
    }

    @Override
    public String generateToken(final AccountModel account, final int tokenTTL) {
        final String token = RandomStringUtils.random(8, true, true) + "-" +
            RandomStringUtils.random(8, true, true) + "-" +
            RandomStringUtils.random(8, true, true);
        final RedisEmailValidation.TokenData tokenData = new RedisEmailValidation.TokenData(account.getId(), account.getEmail());
        final String key = this.getKeyFromToken(token);

        this.playRedis.set(key, RedisEmailValidation.TokenData.class, tokenData, tokenTTL);

        return token;
    }

    @Override
    public Optional<AccountModel> fromToken(final String token) {
        final TokenData tokenData = this.playRedis.get(this.getKeyFromToken(token), TokenData.class);

        if (tokenData == null) {
            return Optional.empty();
        }

        final AccountModel accountModel = AccountModel.find.byId(tokenData.accountId);

        // Validate that the account exists and that the email hasn't changed.
        if (accountModel == null || !accountModel.getEmail().equals(tokenData.email)) {
            return Optional.empty();
        }

        return Optional.of(accountModel);
    }

    @Override
    public void removeToken(final String token) {
        this.playRedis.remove(this.getKeyFromToken(token));
    }

    @Override
    public EmailService getEmailService() {
        return this.emailService;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    /**
     * Gets key.
     *
     * @param token the token
     * @return the key
     */
    abstract protected String getKeyFromToken(final String token);

    /**
     * The type Token data.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenData {

        /**
         * The Account id.
         */
        private final Long accountId;

        /**
         * The Email.
         */
        private final String email;

        /**
         * Instantiates a new Token data.
         *
         * @param accountId the account id
         * @param email     the email
         */
        @JsonCreator
        public TokenData(@JsonProperty(value = "account_id", required = true) final Long accountId,
                         @JsonProperty(value = "email", required = true) final String email) {
            this.accountId = accountId;
            this.email = email;
        }

        /**
         * Gets account id.
         *
         * @return the account id
         */
        @JsonGetter("account_id")
        public Long getAccountId() {
            return this.accountId;
        }

        /**
         * Gets email.
         *
         * @return the email
         */
        @JsonGetter("email")
        public String getEmail() {
            return this.email;
        }
    }
}
