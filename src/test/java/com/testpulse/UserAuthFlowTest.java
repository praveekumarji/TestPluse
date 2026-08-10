package com.testpulse;

import com.testpulse.model.SubscriptionStatus;
import com.testpulse.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAuthFlowTest {

    @Test
    void shouldStoreMobileNumberAndDefaultToFreeSubscription() {
        User user = User.builder()
                .mobileNumber("9876543210")
                .fullName("Demo User")
                .passwordHash("123456")
                .preferredLanguage("en")
                .build();

        assertEquals("9876543210", user.getMobileNumber());
        assertEquals(SubscriptionStatus.FREE, user.getSubscriptionStatus());
    }
}
