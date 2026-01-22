package com.davidruffner.homecontrollerbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.davidruffner.homecontrollerbackend.config.HashUtilConfig;
import com.davidruffner.homecontrollerbackend.config.UserSettingsConfig;
import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.AccountType;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.repositories.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
public class AuthUtil {

    @Autowired
    HashUtilConfig hashUtilConfig;

    @Autowired
    UserSettingsConfig userSettingsConfig;

    @Autowired
    UserSettingsRepository userSettingsRepo;

    public static class JWTTokenResponse {
        private final String address;
        private final String city;
        private final String state;
        private final String stateCode;
        private final String timeZone;
        private final String name;
        private final AccountType accountType;
        private final List<String> allowedApps;
        private final String jwtToken;
        private final String errMsg;
        private final ShortCode shortCode;

        public JWTTokenResponse(String jwtToken, UserSettings userSettings,
            UserSettingsConfig userSettingsConfig) {

            this.jwtToken = jwtToken;
            this.address = userSettings.getAddress();
            this.city = userSettings.getCity();
            this.state = userSettings.getState();
            this.stateCode = userSettings.getStateCode();
            this.timeZone = userSettings.getTimeZone();
            this.name = userSettings.getName();
            this.accountType = userSettings.getAccountType();
            this.allowedApps = userSettingsConfig
                .getAllowedAppsByAccountType(userSettings.getAccountType());
            this.errMsg = null;
            this.shortCode = ShortCode.SUCCESS;
        }

        public JWTTokenResponse(String errMsg, ShortCode shortCode) {
            this.errMsg = errMsg;
            this.shortCode = shortCode;
            this.jwtToken = null;
            this.address = null;
            this.city = null;
            this.state = null;
            this.stateCode = null;
            this.timeZone = null;
            this.name = null;
            this.accountType = null;
            this.allowedApps = null;
        }

        public String getJwtToken() {
            return jwtToken;
        }

        public String getAddress() {
            return address;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getStateCode() {
            return stateCode;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public String getName() {
            return name;
        }

        public AccountType getAccountType() {
            return accountType;
        }

        public List<String> getAllowedApps() {
            return allowedApps;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public ShortCode getShortCode() {
            return shortCode;
        }
    }

    public JWTTokenResponse createJWTToken(String pinNumber, String username) {
        // Lookup user by hashed PIN
        String hashedPin = toSha256(pinNumber);
        Optional<UserSettings> userSettingsOpt = this.userSettingsRepo
            .getUserSettingsByPinHashAndUsername(hashedPin, username);

        if (userSettingsOpt.isEmpty()) {
            return new JWTTokenResponse("Couldn't find user with the given PIN number",
                ShortCode.NON_EXISTENT_USER);
        }

        UserSettings userSettings = userSettingsOpt.get();
        byte[] secret = Base64.getDecoder().decode(this.hashUtilConfig.getJwtKey());
        Algorithm alg = Algorithm.HMAC256(secret);

        String token = JWT.create()
            .withClaim("userSettings", userSettings.toJSONStr())
            .withNotBefore(Instant.now())
            .withExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
            .sign(alg);

        return new JWTTokenResponse(token, userSettings, this.userSettingsConfig);
    }

    // Used for the controller method of the same name
    public JWTTokenResponse verifyToken(String token) {
        token = token.replace("Bearer ", "");

        UserSettings userSettings = verifyJWTToken(token);

        return new JWTTokenResponse(token, userSettings, this.userSettingsConfig);
    }

    public UserSettings verifyJWTToken(String jwtToken) {
        byte[] secret = Base64.getDecoder().decode(this.hashUtilConfig.getJwtKey());
        Algorithm alg = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(alg).build();

        jwtToken = jwtToken.replace("Bearer ", "");

        DecodedJWT jwt = verifier.verify(jwtToken);
        String userSettingsJson = jwt.getClaim("userSettings").asString();

        return UserSettings.fromJSONStr(userSettingsJson);
    }

    public String toSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(this.hashUtilConfig.getDigestType());
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // convert bytes to hex
            StringBuilder hexString = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new ControllerException(String.format("Digest Type '%s' is not available",
                this.hashUtilConfig.getDigestType()), ResponseCode.SYSTEM_EXCEPTION);
        }
    }
}
