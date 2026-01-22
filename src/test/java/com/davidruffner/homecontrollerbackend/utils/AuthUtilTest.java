package com.davidruffner.homecontrollerbackend.utils;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"local", "secure"})
public class AuthUtilTest {

    @Autowired
    AuthUtil authUtil;

    @Test
    public void testJWTUsage() {
        AuthUtil.JWTTokenResponse response = this.authUtil.createJWTToken("9244", "dave");

        assertNull(response.getErrMsg());
        assertNull(response.getShortCode());
        assertNotNull(response.getJwtToken());

        String jwtToken = response.getJwtToken();
        UserSettings userSettings = this.authUtil.verifyJWTToken(jwtToken);
        assertEquals("America/Detroit", userSettings.getTimeZone());
    }
}
