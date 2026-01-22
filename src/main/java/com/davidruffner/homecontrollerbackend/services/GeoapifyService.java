package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.davidruffner.homecontrollerbackend.utils.Utils.strEmpty;

@Service
public class GeoapifyService {

    @Autowired
    @Qualifier("GeoapifyRestClient")
    RestClient restClient;

    public record UnpackAddressPropertiesResponse(
        @JsonProperty("state_code")
        String stateCode,
        String state,
        String city,
        String lat,
        String lon
    ) {}

    public record UnpackAddressFeatureResponse(
        UnpackAddressPropertiesResponse properties
    ) {}

    public record UnpackAddressResponseDTO(
        List<UnpackAddressFeatureResponse> features
    ) {}

    public static class UnpackAddressResponse {
        private final Optional<String> stateCode;
        private final Optional<String> state;
        private final Optional<String> city;
        private final Optional<String> lat;
        private final Optional<String> lon;
        private final Optional<String> errMsg;
        private final boolean status;

        public UnpackAddressResponse(String stateCode, String state, String city,
            String lat, String lon) {

            this.stateCode = Optional.of(stateCode);
            this.state = Optional.of(state);
            this.city = Optional.of(city);
            this.lat = Optional.of(lat);
            this.lon = Optional.of(lon);
            this.errMsg = Optional.empty();
            this.status = true;
        }

        public UnpackAddressResponse(String errMsg) {
            this.stateCode = Optional.empty();
            this.state = Optional.empty();
            this.city = Optional.empty();
            this.lat = Optional.empty();
            this.lon = Optional.empty();
            this.errMsg = Optional.of(errMsg);
            this.status = false;
        }

        public Optional<String> getStateCode() {
            return stateCode;
        }

        public Optional<String> getState() {
            return state;
        }

        public Optional<String> getCity() {
            return city;
        }

        public Optional<String> getLat() {
            return lat;
        }

        public Optional<String> getLon() {
            return lon;
        }

        public Optional<String> getErrMsg() {
            return errMsg;
        }

        public boolean isStatus() {
            return status;
        }
    }

    public UnpackAddressResponse unpackAddress(UserSettings userSettings) {
        if (strEmpty(userSettings.getAddress()) || strEmpty(userSettings.getGeoapifyKey())) {
            return new UnpackAddressResponse("Address and/or geoapify key was empty in user settings.");
        }

        URI uri = UriComponentsBuilder
            .fromPath("/v1/geocode/search")
            .queryParam("text", userSettings.getAddress())
            .queryParam("apiKey", userSettings.getGeoapifyKey())
            .build()
            .toUri();

        UnpackAddressResponseDTO response = this.restClient
            .get()
            .uri(uri)
            .retrieve()
            .body(UnpackAddressResponseDTO.class);

        return new UnpackAddressResponse(response.features.get(0).properties.stateCode,
            response.features.get(0).properties.state, response.features.get(0).properties.city,
            response.features.get(0).properties.lat, response.features.get(0).properties.lon);
    }
}
