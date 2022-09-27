package security.password;

import org.apache.commons.codec.binary.Base64;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * PasswordHandler.
 *
 * @author Pierre Adam
 * @since 20.06.14
 */
public class PasswordHandler {

    /**
     * The Secure random.
     */
    static private final SecureRandom SECURE_RANDOM;

    /**
     * The Providers.
     */
    static private final Map<String, IPasswordProvider> PROVIDERS;

    /**
     * The Default provider.
     */
    static private final IPasswordProvider DEFAULT_PROVIDER = PasswordProviderEnum.PBKDF2.getProvider();

    static {
        SECURE_RANDOM = new SecureRandom();
        PROVIDERS = new HashMap<>();
        for (final PasswordProviderEnum providerEnum : PasswordProviderEnum.values()) {
            final IPasswordProvider provider = providerEnum.getProvider();
            PROVIDERS.put(provider.getPrefix(), provider);
        }
    }

    /**
     * The Provider.
     */
    private final IPasswordProvider provider;

    /**
     * The Salt.
     */
    private String salt;

    /**
     * The Hashed password.
     */
    private String hashedPassword;

    /**
     * Instantiates a new Password handler.
     *
     * @param data the data
     */
    public PasswordHandler(final String data) {
        if (data == null || data.isEmpty()) {
            this.provider = DEFAULT_PROVIDER;
            this.generateSalt();
            this.hashedPassword = null;
        } else {
            final String[] split = data.split(":");
            if (split.length != 3) {
                throw new RuntimeException("Malformed password data.");
            }
            if (PROVIDERS.containsKey(split[0])) {
                this.provider = PROVIDERS.get(split[0]);
            } else {
                throw new RuntimeException(String.format("No provider was found for \"%s\"", split[0]));
            }
            this.salt = split[1];
            this.hashedPassword = split[2];
        }
    }

    /**
     * Instantiates a new Password handler.
     */
    public PasswordHandler() {
        this.provider = DEFAULT_PROVIDER;
        this.generateSalt();
        this.hashedPassword = null;
    }

    /**
     * To base 64 string.
     *
     * @param array the array
     * @return the string
     */
    private static String toBase64(final byte[] array) {
        return Base64.encodeBase64String(array);
    }

    /**
     * Generate salt.
     */
    private void generateSalt() {
        final byte[] saltBytes = new byte[20];
        SECURE_RANDOM.nextBytes(saltBytes);
        this.salt = toBase64(saltBytes);
    }

    /**
     * New password string.
     *
     * @param password the password
     * @return the string
     */
    public String newPassword(final String password) {
        this.generateSalt();
        this.hashedPassword = this.provider.getHash(password, this.salt);
        return String.format("%s:%s:%s", this.provider.getPrefix(), this.salt, this.hashedPassword);
    }

    /**
     * Check password boolean.
     *
     * @param password the password
     * @return the boolean
     */
    public boolean checkPassword(final String password) {
        final String hash = this.provider.getHash(password, this.salt);
        return hash.equals(this.hashedPassword);
    }
}
