package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.dtos.CommonDTOS.CommonResponse;
import com.davidruffner.homecontrollerbackend.dtos.GetUserSettingsResponseDTO;
import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.repositories.UserSettingsRepository;
import com.davidruffner.homecontrollerbackend.services.GeoapifyService;
import com.davidruffner.homecontrollerbackend.services.GeoapifyService.UnpackAddressResponse;
import com.davidruffner.homecontrollerbackend.utils.AuthUtil;
import com.davidruffner.homecontrollerbackend.utils.AuthUtil.JWTTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.davidruffner.homecontrollerbackend.utils.Utils.strNotEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/userSettings")
public class UserSettingsController {

    @Autowired
    UserSettingsRepository userSettingsRepo;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    GeoapifyService geoapifyService;

    public record GetUserRequestDto(
        String pinNumber,
        String username
    ) {}

    @PostMapping("/getUser")
    public ResponseEntity<JWTTokenResponse> getUser(@RequestBody GetUserRequestDto body) {
        return ResponseEntity.ok(this.authUtil.createJWTToken(body.pinNumber(), body.username()));
    }

    @GetMapping("/verifyToken")
    public ResponseEntity<JWTTokenResponse> verifyToken(@RequestParam String token) {
        return ResponseEntity.ok(this.authUtil.verifyToken(token));
    }

    public static class UserAndName {
        private final String username;
        private final String name;
        private final String nameFirstLetter;

        public UserAndName(String username, String name) {
            this.username = username;
            this.name = name;
            // First letter as uppercase (used for the icon)
            this.nameFirstLetter = this.name.substring(0, 1).toUpperCase(Locale.US);
        }

        public String getUsername() {
            return username;
        }

        public String getName() {
            return name;
        }

        public String getNameFirstLetter() {
            return nameFirstLetter;
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserAndName>> getAllUsers() {
        return ResponseEntity.ok(this.userSettingsRepo.getAllUsers());
    }

    @GetMapping("/getUserSettings/{controlDeviceId}")
    public ResponseEntity<GetUserSettingsResponseDTO> getUserSettings(@PathVariable String controlDeviceId) {
        Optional<UserSettings> userSettings = this.userSettingsRepo
            .getUserSettingsByControlDeviceId(controlDeviceId);

        if (userSettings.isEmpty()) {
            return new ResponseEntity<>(new GetUserSettingsResponseDTO("Invalid control device ID"),
                BAD_REQUEST);
        }

        return new ResponseEntity<>(new GetUserSettingsResponseDTO(userSettings.get()),
            HttpStatus.OK);
    }

    public record UpdateUserSettingsRequest(
        String controlDeviceId,
        String address,
        String weatherApiKey,
        String hueApiKey,
        String todoistApiKey,
        String geoapifyApiKey
    ) {}

    @PostMapping("/updateUserSettings")
    public ResponseEntity<CommonResponse> updateUserSettings(@RequestBody UpdateUserSettingsRequest body) {
        if (body.controlDeviceId() == null) {
            return new ResponseEntity<>(new CommonResponse("Must provide a controlDeviceId"),
                BAD_REQUEST);
        }

        Optional<UserSettings> userSettingsOpt = this.userSettingsRepo
            .getUserSettingsByControlDeviceId(body.controlDeviceId());
        if (userSettingsOpt.isEmpty()) {
            return new ResponseEntity<>(new CommonResponse("Given controlDeviceId is invalid."), BAD_REQUEST);
        }
        UserSettings userSettings = userSettingsOpt.get();

        if (strNotEmpty(body.address())) {
            if (!body.address.equals(userSettings.getAddress())) {
                userSettings.setAddress(strNotEmpty(body.address()) ? body.address() : null);

                // Set the lat/lon from the new address
                UnpackAddressResponse response = this.geoapifyService.unpackAddress(userSettings);
                response.getCity().ifPresent(userSettings::setCity);
                response.getState().ifPresent(userSettings::setState);
                response.getStateCode().ifPresent(userSettings::setStateCode);
                response.getLat().ifPresent(userSettings::setLat);
                response.getLon().ifPresent(userSettings::setLon);
            }
        }
        if (strNotEmpty(body.weatherApiKey())) {
            userSettings.setWeatherApiKey(strNotEmpty(body.weatherApiKey()) ? body.weatherApiKey() : null);
        }
        if (strNotEmpty(body.hueApiKey())) {
            userSettings.setHueApiKey(strNotEmpty(body.hueApiKey) ? body.hueApiKey : null);
        }
        if (strNotEmpty(body.todoistApiKey)) {
            userSettings.setTodoistApiKey(strNotEmpty(body.todoistApiKey) ? body.todoistApiKey : null);
        }
        if (strNotEmpty(body.geoapifyApiKey)) {
            userSettings.setGeoapifyKey(strNotEmpty(body.geoapifyApiKey) ? body.geoapifyApiKey : null);
        }

        this.userSettingsRepo.save(userSettings);

        return new ResponseEntity<>(new CommonResponse(), OK);
    }
}
