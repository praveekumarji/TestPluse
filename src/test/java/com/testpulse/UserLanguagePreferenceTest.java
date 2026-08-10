package com.testpulse;

import com.testpulse.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserLanguagePreferenceTest {

    @Test
    void shouldStorePreferredLanguage() {
        User user = User.builder()
                .email("demo@testpulse.com")
                .fullName("Demo User")
                .passwordHash("hashed")
                .preferredLanguage("hi")
                .build();

        assertEquals("hi", user.getPreferredLanguage());
    }
}
