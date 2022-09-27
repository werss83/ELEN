/*
 * Copyright (C) 2014 - 2019 PayinTech, SAS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package security.password;

/**
 * PasswordProviderEnum.
 *
 * @author Pierre Adam
 * @since 20.06.14
 */
public enum PasswordProviderEnum {

    /**
     * The Pbkdf2 provider.
     */
    PBKDF2(new PBKDF2PasswordProvider());

    /**
     * The password provider.
     */
    private final IPasswordProvider provider;

    /**
     * Instantiates a new Password provider enum.
     *
     * @param provider the provider
     */
    PasswordProviderEnum(final IPasswordProvider provider) {
        this.provider = provider;
    }

    /**
     * Gets provider.
     *
     * @return the provider
     */
    public IPasswordProvider getProvider() {
        return this.provider;
    }
}
