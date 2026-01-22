package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.repositories.UserSettingsRepository;
import com.davidruffner.homecontrollerbackend.services.WeatherService;
import com.davidruffner.homecontrollerbackend.services.WeatherService.*;
import com.davidruffner.homecontrollerbackend.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.davidruffner.homecontrollerbackend.enums.ResponseCode.SYSTEM_EXCEPTION;
import static com.davidruffner.homecontrollerbackend.enums.ShortCode.INVALID_CONTROLLER_ID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RequestMapping("/dashboard")
@RestController
public class DashboardController {

    @Autowired
    WeatherService weatherService;

    @Autowired
    UserSettingsRepository userSettingsRepo;

    @Autowired
    AuthUtil authUtil;

    @GetMapping("/getForecast/{controllerDeviceId}")
    public ResponseEntity<NWSForecastResponse> getForecast(@PathVariable String controllerDeviceId) {
        UserSettings userSettings = this.userSettingsRepo.getUserSettingsByControlDeviceId(controllerDeviceId)
                .orElseThrow(() -> new ControllerException("Controller Device ID Invalid", ResponseCode.BAD_REQUEST,
                    INVALID_CONTROLLER_ID.toString()));

        return ResponseEntity.ok(this.weatherService.getForecastResponse(
            userSettings.getLat(), userSettings.getLon()));
    }

    public static class GetCurrentConditionsResponse {
        private final String locationStr;
        private final NWSHourlyPeriod nwsHourlyPeriod;

        public GetCurrentConditionsResponse(UserSettings userSettings, NWSHourlyPeriod hourlyPeriod) {
            this.locationStr = userSettings.getCity() + ", " + userSettings.getStateCode();
            this.nwsHourlyPeriod = hourlyPeriod;
        }

        public String getLocationStr() {
            return locationStr;
        }

        public NWSHourlyPeriod getNwsHourlyPeriod() {
            return nwsHourlyPeriod;
        }
    }

    @GetMapping("/getCurrentConditions")
    public ResponseEntity<GetCurrentConditionsResponse> getCurrentConditions(
        @RequestHeader("Authorization") String authHeader) {

        UserSettings userSettings = this.authUtil.verifyJWTToken(authHeader);

        return ResponseEntity.ok(new GetCurrentConditionsResponse(userSettings,
            this.weatherService.getCurrentConditions(userSettings.getLat(), userSettings.getLon())));
    }

    @GetMapping("/getDaytimeForecast")
    public ResponseEntity<GetDaytimeForecastResponse> getDaytimeForecast(
        @RequestHeader("Authorization") String authHeader
    ) {
        UserSettings userSettings = this.authUtil.verifyJWTToken(authHeader);

        return ResponseEntity.ok(this.weatherService.getDaytimeForecast(userSettings));
    }

    @GetMapping("/getNighttimeForecast/{controllerDeviceId}")
    public ResponseEntity<GetNighttimeForecastResponse> getNighttimeForecast(
        @PathVariable String controllerDeviceId) throws Exception {

        UserSettings userSettings = this.userSettingsRepo.getUserSettingsByControlDeviceId(controllerDeviceId)
            .orElseThrow(() -> new ControllerException("Controller Device ID Invalid", ResponseCode.BAD_REQUEST,
                INVALID_CONTROLLER_ID.toString()));

        return ResponseEntity.ok(this.weatherService.getNighttimeForecast(userSettings));
    }

    @GetMapping("/getTodayForecast/{controllerDeviceId}")
    public ResponseEntity<GetTodayForecastResponse> getTodayForecast(
        @PathVariable String controllerDeviceId) throws ControllerException {

        UserSettings userSettings = this.userSettingsRepo.getUserSettingsByControlDeviceId(controllerDeviceId)
            .orElseThrow(() -> new ControllerException("Controller Device ID Invalid", ResponseCode.BAD_REQUEST,
                INVALID_CONTROLLER_ID.toString()));

        return ResponseEntity.ok(this.weatherService.getTodayForecastResponse(userSettings));
    }


    @GetMapping("/getTomorrowForecast/{controllerDeviceId}")
    public ResponseEntity<GetTomorrowForecastResponse> getTomorrowForecast(
        @PathVariable String controllerDeviceId) throws Exception {

        UserSettings userSettings = this.userSettingsRepo.getUserSettingsByControlDeviceId(controllerDeviceId)
            .orElseThrow(() -> new ControllerException("Controller Device ID Invalid", ResponseCode.BAD_REQUEST,
                INVALID_CONTROLLER_ID.toString()));

        return ResponseEntity.ok(this.weatherService.getTomorrowForecastResponse(userSettings));
    }

    @GetMapping("/getThreeDayForecast/{controllerDeviceId}")
    public ResponseEntity<GetThreeDayForecastResponse> getThreeDayForecast(
        @PathVariable String controllerDeviceId) throws ControllerException {

        UserSettings userSettings = this.userSettingsRepo.getUserSettingsByControlDeviceId(controllerDeviceId)
            .orElseThrow(() -> new ControllerException("Controller Device ID Invalid", ResponseCode.BAD_REQUEST,
                INVALID_CONTROLLER_ID.toString()));

        return ResponseEntity.ok(this.weatherService.getThreeDayForecast(userSettings));
    }

    @GetMapping("/getSevenDayForecast/{controllerDeviceId}")
    public ResponseEntity<GetSevenDayForecastResponse> getSevenDayForecast(
        @PathVariable String controllerDeviceId) throws ControllerException {

        UserSettings userSettings = this.userSettingsRepo.getUserSettingsByControlDeviceId(controllerDeviceId)
            .orElseThrow(() -> new ControllerException("Controller Device ID Invalid", ResponseCode.BAD_REQUEST,
                INVALID_CONTROLLER_ID.toString()));

        return ResponseEntity.ok(this.weatherService.getSevenDayForecast(userSettings));
    }

    // TODO: Remove
    @GetMapping("/getException")
    public ResponseEntity<Void> getException() {
        throw new ControllerException("This is a controller exception", SYSTEM_EXCEPTION,
            ShortCode.SYSTEM_EXCEPTION.toString());
    }
}
