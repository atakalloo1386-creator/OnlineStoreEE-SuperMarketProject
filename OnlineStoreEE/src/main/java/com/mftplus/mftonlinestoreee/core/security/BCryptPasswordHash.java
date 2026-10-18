package com.mftplus.mftonlinestoreee.core.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.PasswordHash;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import java.security.SecureRandom;

/**
 * BCrypt for the login check ({@link SecurityConfig}) and for storing new passwords:
 * inject it and call {@code generate(password.toCharArray())} before saving a user.
 * BouncyCastle ships with TomEE, so nothing is added to the WAR.
 */
@ApplicationScoped
public class BCryptPasswordHash implements PasswordHash {

    private static final String VERSION = "2a";
    private static final int COST = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generate(char[] password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return OpenBSDBCrypt.generate(VERSION, password, salt, COST);
    }

    @Override
    public boolean verify(char[] password, String hashedPassword) {
        if (hashedPassword == null) {
            return false;
        }
        try {
            return OpenBSDBCrypt.checkPassword(hashedPassword, password);
        } catch (RuntimeException notABCryptHash) {

            return false;
        }
    }
}
