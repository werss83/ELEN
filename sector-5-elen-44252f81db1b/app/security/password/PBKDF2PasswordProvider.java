/*
 * Copyright (C) 2014 - 2019 PayinTech, SAS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package security.password;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * PBKDF2PasswordProvider.
 *
 * @author Pierre Adam
 * @since 20.06.14
 */
public class PBKDF2PasswordProvider implements IPasswordProvider {

    @Override
    public String getPrefix() {
        return "pbkdf2";
    }

    @Override
    public String getHash(final String password, final String salt) {
        try {
            final int iterations = 1000;
            final char[] chars = password.toCharArray();
            final byte[] bSalt = salt.getBytes();

            final PBEKeySpec spec = new PBEKeySpec(chars, bSalt, iterations, 64 * 8);
            SecretKeyFactory skf = null;
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.encodeBase64String(hash);
            //return iterations + ":" + toHex(bSalt) + ":" + toHex(hash);
        } catch (final InvalidKeySpecException | NoSuchAlgorithmException e) {
            return null;
        }
    }
}
