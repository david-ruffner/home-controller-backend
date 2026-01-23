package com.davidruffner.homecontrollerbackend.entities;

import com.davidruffner.homecontrollerbackend.enums.AccountType;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Id
    @Column(name = "control_device_id", nullable = false)
    private String controlDeviceId;

    @Column(name = "address")
    private String address;

    @Column(name = "lat")
    private String lat;

    @Column(name = "lon")
    private String lon;

    @Column(name = "weather_api_key")
    private String weatherApiKey;

    @Column(name = "hue_api_key")
    private String hueApiKey;

    @Column(name = "todoist_api_key")
    private String todoistApiKey;

    @Column(name = "geoapify_key")
    private String geoapifyKey;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "state_code")
    private String stateCode;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pin_number", nullable = false)
    private String pinNumber;
    
    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "inbox_project_id")
    private String inboxProjectId;

    public String getControlDeviceId() {
        return controlDeviceId;
    }

    public void setControlDeviceId(String controlDeviceId) {
        this.controlDeviceId = controlDeviceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getWeatherApiKey() {
        return weatherApiKey;
    }

    public void setWeatherApiKey(String weatherApiKey) {
        this.weatherApiKey = weatherApiKey;
    }

    public String getHueApiKey() {
        return hueApiKey;
    }

    public void setHueApiKey(String hueApiKey) {
        this.hueApiKey = hueApiKey;
    }

    public String getTodoistApiKey() {
        return todoistApiKey;
    }

    public void setTodoistApiKey(String todoistApiKey) {
        this.todoistApiKey = todoistApiKey;
    }

    public String getGeoapifyKey() {
        return geoapifyKey;
    }

    public void setGeoapifyKey(String geoapifyKey) {
        this.geoapifyKey = geoapifyKey;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public AccountType getAccountType() {
        return AccountType.fromStrVal(this.accountType)
            .orElseThrow(() -> new ControllerException(String.format("AccountType '%s' is invalid",
                this.accountType), ResponseCode.SYSTEM_EXCEPTION));
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType.getValue();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Optional<String> getInboxProjectId() {
        return Optional.ofNullable(inboxProjectId);
    }

    public void setInboxProjectId(String inboxProjectId) {
        this.inboxProjectId = inboxProjectId;
    }

    public static UserSettings fromJSONStr(String jsonStr) {
        Map<String, Object> map = mapper.readValue(jsonStr, new TypeReference<>() {});
        UserSettings userSettings = new UserSettings();

        userSettings.setControlDeviceId((String) map.get("controlDeviceId"));
        userSettings.setName((String) map.get("name"));
        userSettings.setPinNumber((String) map.get("pinNumber"));
        String accountTypeStr = (String) map.get("accountType");
        userSettings.setAccountType(AccountType.fromStrVal(accountTypeStr).orElseThrow(() ->
            new ControllerException(String.format("Account Type '%s' is invalid",
                accountTypeStr), ResponseCode.SYSTEM_EXCEPTION)));
        userSettings.setTimeZone((String) map.get("timeZone"));

        Map<String, Object> location = (Map<String, Object>) map.get("location");
        userSettings.setAddress((String) location.get("address"));
        userSettings.setLat((String) location.get("latitude"));
        userSettings.setLon((String) location.get("longitude"));
        userSettings.setCity((String) location.get("city"));
        userSettings.setState((String) location.get("state"));
        userSettings.setStateCode((String) location.get("stateCode"));

        Map<String, Object> apiKeys = (Map<String, Object>) map.get("apiKeys");
        userSettings.setWeatherApiKey((String) apiKeys.get("weatherApiKey"));
        userSettings.setHueApiKey((String) apiKeys.get("hueApiKey"));
        userSettings.setTodoistApiKey((String) apiKeys.get("todoistApiKey"));
        userSettings.setGeoapifyKey((String) apiKeys.get("geoapifyKey"));

        return userSettings;
    }

    public String toJSONStr() {
        ObjectNode root = mapper.createObjectNode();

        root.put("controlDeviceId", this.controlDeviceId);
        root.put("name", this.name);
        root.put("accountType", this.accountType);
        root.put("timeZone", this.timeZone);
        root.put("pinNumber", this.pinNumber);

        ObjectNode location = root.putObject("location");
        location.put("address", this.address);
        location.put("latitude", this.lat);
        location.put("longitude", this.lon);
        location.put("city", this.city);
        location.put("state", this.state);
        location.put("stateCode", this.stateCode);

        ObjectNode apiKeys = root.putObject("apiKeys");
        apiKeys.put("weatherApiKey", this.weatherApiKey);
        apiKeys.put("hueApiKey", this.hueApiKey);
        apiKeys.put("todoistApiKey", this.todoistApiKey);
        apiKeys.put("geoapifyKey", this.geoapifyKey);

        return mapper.writeValueAsString(root);
    }
}
