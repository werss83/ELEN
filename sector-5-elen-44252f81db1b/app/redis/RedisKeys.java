package redis;

/**
 * common.RedisKeys.
 *
 * @author Pierre Adam
 * @since 20.06.05
 */
public class RedisKeys {

    /**
     * The email verification code key.
     */
    public static String emailVerificationCode = "email-code.%s";

    /**
     * The email verification code key.
     */
    public static String resetPasswordCode = "reset-password.%s";

    /**
     * The constant careCreatorActorLock.
     */
    public static String careCreatorActorLock = "CareCreatorActor.all.lock";


    /**
     * The constant careCreatorActorBookedCareLock.
     */
    public static String careCreatorActorBookedCareLock = "CareCreatorActor.b%d.lock";
}
