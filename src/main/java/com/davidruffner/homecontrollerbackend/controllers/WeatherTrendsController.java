package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.TrendType;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import com.davidruffner.homecontrollerbackend.services.WeatherService;
import com.davidruffner.homecontrollerbackend.services.WeatherService.GetFeelsLikeTrendsResponse;
import com.davidruffner.homecontrollerbackend.services.WeatherService.GetHumidityTrendsResponse;
import com.davidruffner.homecontrollerbackend.services.WeatherService.GetPrecipitationTrendsResponse;
import com.davidruffner.homecontrollerbackend.services.WeatherService.GetTemperatureTrendsResponse;
import com.davidruffner.homecontrollerbackend.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.davidruffner.homecontrollerbackend.enums.ShortCode.INVALID_TREND_TYPE;

@RequestMapping("/weatherTrends")
@RestController
public class WeatherTrendsController {

    @Autowired
    WeatherService weatherService;

    @Autowired
    AuthUtil authUtil;

    @GetMapping("/getPrecipitation")
    public ResponseEntity<GetPrecipitationTrendsResponse> getPrecipitationTrendsResponse(
        @RequestHeader("Authorization") String authHeader, @RequestParam("trendType") String trendTypeStr) {

        UserSettings userSettings = this.authUtil.verifyJWTToken(authHeader);
        TrendType trendType = TrendType.fromStrVal(trendTypeStr)
            .orElseThrow(() -> new ControllerException(String.format("Invalid TrendType '%s'", trendTypeStr),
                ResponseCode.BAD_REQUEST, INVALID_TREND_TYPE.toString()));

        return ResponseEntity.ok(this.weatherService.getPrecipitationTrends(userSettings, trendType));
    }

    @GetMapping("/getTemperature")
    public ResponseEntity<GetTemperatureTrendsResponse> getTemperatureTrends(
        @RequestHeader("Authorization") String authHeader, @RequestParam("trendType") String trendTypeStr) {

        UserSettings userSettings = this.authUtil.verifyJWTToken(authHeader);
        TrendType trendType = TrendType.fromStrVal(trendTypeStr)
            .orElseThrow(() -> new ControllerException(String.format("Invalid TrendType '%s'", trendTypeStr),
                ResponseCode.BAD_REQUEST, INVALID_TREND_TYPE.toString()));

        return ResponseEntity.ok(this.weatherService.getTemperatureTrends(userSettings, trendType));
    }

    @GetMapping("/getHumidity")
    public ResponseEntity<GetHumidityTrendsResponse> getHumidityTrends(
        @RequestHeader("Authorization") String authHeader, @RequestParam("trendType") String trendTypeStr) {

        UserSettings userSettings = this.authUtil.verifyJWTToken(authHeader);
        TrendType trendType = TrendType.fromStrVal(trendTypeStr)
            .orElseThrow(() -> new ControllerException(String.format("Invalid TrendType '%s'", trendTypeStr),
                ResponseCode.BAD_REQUEST, INVALID_TREND_TYPE.toString()));

        return ResponseEntity.ok(this.weatherService.getHumidityTrends(userSettings, trendType));
    }

    @GetMapping("/getFeelsLike")
    public ResponseEntity<GetFeelsLikeTrendsResponse> getFeelsLikeTrends(
        @RequestHeader("Authorization") String authHeader, @RequestParam("trendType") String trendTypeStr) {

        UserSettings userSettings = this.authUtil.verifyJWTToken(authHeader);
        TrendType trendType = TrendType.fromStrVal(trendTypeStr)
            .orElseThrow(() -> new ControllerException(String.format("Invalid TrendType '%s'", trendTypeStr),
                ResponseCode.BAD_REQUEST, INVALID_TREND_TYPE.toString()));

        return ResponseEntity.ok(this.weatherService.getFeelsLikeTrends(userSettings, trendType));
    }
}
