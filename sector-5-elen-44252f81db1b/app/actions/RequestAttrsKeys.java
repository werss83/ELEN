package actions;

import models.account.AccountModel;
import play.libs.typedmap.TypedKey;

/**
 * RequestAttrsKeys.
 *
 * @author Pierre Adam
 * @since 20.06.04
 */
public class RequestAttrsKeys {

    /**
     * The account currently logged in or the account being impersonate.
     */
    public static final TypedKey<AccountModel> ACCOUNT_MODEL = TypedKey.create("ACCOUNT_MODEL");

    /**
     * The real account currently logged in.
     */
    public static final TypedKey<AccountModel> REAL_ACCOUNT = TypedKey.create("REAL_ACCOUNT");

    /**
     * The account currently logged in.
     */
    public static final TypedKey<String> PROFILE_PICTURE = TypedKey.create("PROFILE_PICTURE");
}
