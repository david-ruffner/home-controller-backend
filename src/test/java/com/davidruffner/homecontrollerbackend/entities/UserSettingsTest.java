package com.davidruffner.homecontrollerbackend.entities;

import com.davidruffner.homecontrollerbackend.enums.AccountType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSettingsTest {

    @Test
    public void testJSON() {
        UserSettings userSettings = new UserSettings();
        userSettings.setControlDeviceId("1");
        userSettings.setAddress("1");
        userSettings.setLat("1");
        userSettings.setLon("1");
        userSettings.setWeatherApiKey("1");
        userSettings.setHueApiKey("1");
        userSettings.setTodoistApiKey("1");
        userSettings.setGeoapifyKey("1");
        userSettings.setCity("1");
        userSettings.setState("1");
        userSettings.setStateCode("1");
        userSettings.setTimeZone("1");
        userSettings.setName("1");
        userSettings.setPinNumber("1");
        userSettings.setAccountType(AccountType.ADMIN);

        String jsonStr = userSettings.toJSONStr();
        System.out.printf("JSON String: %s", jsonStr);
        UserSettings actual = UserSettings.fromJSONStr(jsonStr);
        
        assertEquals(userSettings.getControlDeviceId(), actual.getControlDeviceId());
        assertEquals(userSettings.getAddress(), actual.getAddress());
        assertEquals(userSettings.getLat(), actual.getLat());
        assertEquals(userSettings.getLon(), actual.getLon());
        assertEquals(userSettings.getWeatherApiKey(), actual.getWeatherApiKey());
        assertEquals(userSettings.getHueApiKey(), actual.getHueApiKey());
        assertEquals(userSettings.getTodoistApiKey(), actual.getTodoistApiKey());
        assertEquals(userSettings.getGeoapifyKey(), actual.getGeoapifyKey());
        assertEquals(userSettings.getCity(), actual.getCity());
        assertEquals(userSettings.getState(), actual.getState());
        assertEquals(userSettings.getStateCode(), actual.getStateCode());
        assertEquals(userSettings.getTimeZone(), actual.getTimeZone());
        assertEquals(userSettings.getName(), actual.getName());
        assertEquals(userSettings.getPinNumber(), actual.getPinNumber());
        assertEquals(userSettings.getAccountType(), actual.getAccountType());
    }
}
