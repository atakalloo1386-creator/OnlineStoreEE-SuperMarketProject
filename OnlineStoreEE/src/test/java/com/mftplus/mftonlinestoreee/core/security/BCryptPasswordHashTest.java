package com.mftplus.mftonlinestoreee.core.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordHashTest {

    private final BCryptPasswordHash passwordHash = new BCryptPasswordHash();

    @Test
    void verifiesTheRightPasswordOnly() {
        String hash = passwordHash.generate("secret123".toCharArray());

        assertTrue(hash.startsWith("$2a$10$"));
        assertTrue(passwordHash.verify("secret123".toCharArray(), hash));
        assertFalse(passwordHash.verify("wrong".toCharArray(), hash));
    }

    @Test
    void sameInputGivesDifferentHashes() {
        String first = passwordHash.generate("secret123".toCharArray());
        String second = passwordHash.generate("secret123".toCharArray());

        assertNotEquals(first, second);   // a new random salt every time
    }

    @Test
    void userWithoutPasswordNeverMatches() {
        assertFalse(passwordHash.verify("secret123".toCharArray(), null));
    }

    @Test
    void plainTextInTheDatabaseNeverMatches() {
        assertFalse(passwordHash.verify("secret123".toCharArray(), "secret123"));
        assertFalse(passwordHash.verify("secret123".toCharArray(), ""));
    }
}
