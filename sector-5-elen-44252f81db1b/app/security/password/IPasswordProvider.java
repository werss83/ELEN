/*
 * Copyright (C) 2014 - 2019 PayinTech, SAS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package security.password;

/**
 * IPasswordProvider.
 *
 * @author Pierre Adam
 * @since 20.06.14
 */
public interface IPasswordProvider {

    /**
     * Gets prefix.
     *
     * @return the prefix
     */
    String getPrefix();

    /**
     * Gets hash.
     *
     * @param password the password
     * @param salt     the salt
     * @return the hash
     */
    String getHash(final String password, final String salt);
}
