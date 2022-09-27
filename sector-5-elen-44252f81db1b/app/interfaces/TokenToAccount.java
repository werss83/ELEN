package interfaces;

import com.typesafe.config.Config;
import models.account.AccountModel;
import services.EmailService;

import java.util.Optional;

/**
 * TokenToAccount.
 *
 * @author Pierre Adam
 * @since 20.09.06
 */
public interface TokenToAccount {

    /**
     * Generate token for the give account.
     *
     * @param account  the account
     * @param tokenTTL the token ttl
     * @return the string
     */
    String generateToken(final AccountModel account, int tokenTTL);

    /**
     * Return an account or an empty.
     *
     * @param token the token
     * @return the optional
     */
    Optional<AccountModel> fromToken(final String token);

    /**
     * Remove token data.
     *
     * @param token the token
     */
    void removeToken(final String token);

    /**
     * Gets email service.
     *
     * @return the email service
     */
    EmailService getEmailService();

    /**
     * Gets config.
     *
     * @return the config
     */
    Config getConfig();
}
