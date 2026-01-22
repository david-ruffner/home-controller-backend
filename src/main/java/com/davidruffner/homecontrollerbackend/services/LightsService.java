package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.HueGroupResponseDto.GetBulbsForGroupResponse;
import com.davidruffner.homecontrollerbackend.HueGroupResponseDto.HueGroupResponse;
import com.davidruffner.homecontrollerbackend.entities.*;
import com.davidruffner.homecontrollerbackend.repositories.LightBulbTrackRepository;
import com.davidruffner.homecontrollerbackend.services.ColorConversionService.RgbToXyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Map.entry;

@Service
public class LightsService {

    @Autowired
    @Qualifier("HueRestClient")
    RestClient restClient;

    @Autowired
    ColorConversionService colorConversionService;

    @Autowired
    LightBulbTrackRepository lightBulbTrackRepo;

    public record ServiceDto(
        String rid,
        String rtype
    ) {}

    public record DeviceDto(
        String id,
        String id_v1,
        List<ServiceDto> services,
        String type
    ) {}

    public record HueDevicesResponse(
        List<Object> errors,
        List<DeviceDto> data
    ) {}

    public record LightMetadataDto(
        String name
    ) {}

    public record LightOnDto(
        Boolean on
    ) {}

    public record LightDimmingDto(
        Double brightness
    ) {}

    public record LightColorXYDto(
        Double x,
        Double y
    ) {}

    public record LightColorDto(
        LightColorXYDto xy
    ) {}

    public record LightDto(
        String id,
        LightMetadataDto metadata,
        LightOnDto on,
        LightDimmingDto dimming,
        LightColorDto color
    ) {}

    public record HueLightResponse(
        List<Object> errors,
        List<LightDto> data
    ) {}

    public List<LightBulb> getAllLightBulbs() {
        List<LightBulb> lightBulbs = new ArrayList<>();

        HueDevicesResponse response = restClient
            .get()
            .uri("/clip/v2/resource/device")
            .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
            .retrieve()
            .body(HueDevicesResponse.class);

        this.lightBulbTrackRepo.deleteAll();

        response.data.forEach(device -> {
            String deviceId = device.id();
            Optional<ServiceDto> serviceDto = device.services().stream()
                .filter(service -> {
                    return service.rtype.equals("light");
                }).findFirst();

            if (serviceDto.isPresent()) {
                String lightId = serviceDto.get().rid();
                LightBulb lightBulb = new LightBulb(deviceId, lightId);

                HueLightResponse lightResponse = restClient
                    .get()
                    .uri("/clip/v2/resource/light/" + lightId)
                    .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                    .retrieve()
                    .body(HueLightResponse.class);

                if (lightResponse != null && !lightResponse.data.isEmpty()) {
                    LightDto light = lightResponse.data.getFirst();
                    Double colorX = light.color().xy().x();
                    Double colorY = light.color().xy().y();
                    Double colorZ = (light.dimming().brightness() / 100);
                    RGB color = colorConversionService.xyToRGB(colorX, colorY, colorZ);
                    Boolean lightStatus = light.on().on();

                    lightBulb.setName(light.metadata().name());
                    lightBulb.setLightStatus(light.on().on());
                    lightBulb.setColor(color);
                    lightBulb.setBrightness(light.dimming().brightness());

                    LightBulbTrack track = new LightBulbTrack();
                    track.setLightId(lightId);
                    track.setDeviceId(deviceId);
                    track.setBrightness(light.dimming.brightness());
                    track.setRed(color.getRed());
                    track.setBlue(color.getBlue());
                    track.setGreen(color.getGreen());
                    track.setIsOn(lightStatus);
                    track.setName(light.metadata().name());
                    this.lightBulbTrackRepo.save(track);

                    lightBulbs.add(lightBulb);
                }
            }
        });

        return lightBulbs;
    }

    public record HueRoomChildDTO(
        String rid,
        String rtype
    ) {}

    public record HueRoomMetadataDTO(
        String name
    ) {}

    public record HueRoomServiceDTO(
        String rid,
        String rtype
    ) {}

    public record HueRoomDataDTO(
        String id,
        List<HueRoomServiceDTO> services,
        List<HueRoomChildDTO> children,
        HueRoomMetadataDTO metadata
    ) {}

    public record HueRoomResponse(
        List<Object> errors,
        List<HueRoomDataDTO> data
    ) {}

//    public record HueRoom(
//        String name,
//        String groupToggleId,
//        List<LightBulb> lightBulbs
//    ) {}

    public Map<String, HueRoom> getLightBulbsMappedByRoom() {
        List<LightBulb> lightBulbs = getAllLightBulbs();
        Map<String, HueRoom> mappedLightBulbs = new HashMap<>();

        HueRoomResponse response = restClient
            .get()
            .uri("/clip/v2/resource/room")
            .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
            .retrieve()
            .body(HueRoomResponse.class);

        response.data().forEach(room -> {
            if (mappedLightBulbs.containsKey(room.id())) {
                HueRoom hueRoom = mappedLightBulbs.get(room.id());

                Optional<HueRoomServiceDTO> groupService = room.services()
                    .stream().filter(s -> s.rtype().equals("grouped_light")).findFirst();
                groupService.ifPresent(hueRoomServiceDTO ->
                    hueRoom.setGroupToggleId(hueRoomServiceDTO.rid()));

                room.children().forEach(child -> {
                    Optional<LightBulb> lightBulb = lightBulbs.stream()
                        .filter(bulb -> {
                            return bulb.getDeviceId().equals(child.rid());
                        }).findFirst();

                    lightBulb.ifPresent(hueRoom::addLightBulb);
                });
            } else {
                HueRoom hueRoom = new HueRoom(room.metadata().name());

                Optional<HueRoomServiceDTO> groupService = room.services()
                    .stream().filter(s -> s.rtype().equals("grouped_light")).findFirst();
                groupService.ifPresent(hueRoomServiceDTO ->
                    hueRoom.setGroupToggleId(hueRoomServiceDTO.rid()));

                room.children().forEach(child -> {
                    Optional<LightBulb> lightBulb = lightBulbs.stream()
                        .filter(bulb -> {
                            return bulb.getDeviceId().equals(child.rid());
                        }).findFirst();

                    lightBulb.ifPresent(hueRoom::addLightBulb);
                });
                mappedLightBulbs.put(room.id(), hueRoom);
            }
        });

        return mappedLightBulbs;
    }

    public record ToggleLightGroupResponseDTO(
        boolean status,
        List<HueRoomChildDTO> children
    ) {}

    public ToggleLightGroupResponseDTO toggleLightGroup(String groupId, Boolean newStatus) {
        try {
            restClient
                .put()
                .uri("/clip/v2/resource/grouped_light/" + groupId)
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .body(Map.of("on", Map.of("on", newStatus)))
                .retrieve()
                .toBodilessEntity();

            // Get all devices affected
            HueRoomResponse roomResponse = restClient
                .get()
                .uri("/clip/v2/resource/room")
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .retrieve()
                .body(HueRoomResponse.class);

            List<HueRoomChildDTO> matchingChildren =
                roomResponse.data.stream()
                    .filter(room ->
                        room.services().stream()
                            .anyMatch(service -> service.rid().equals(groupId))
                    )
                    .flatMap(room -> room.children().stream())
                    .toList();

            matchingChildren.forEach(child -> {
                this.lightBulbTrackRepo.updateIsOn((newStatus ? 1 : 0), child.rid());
            });


            return new ToggleLightGroupResponseDTO(true, matchingChildren);
        } catch (Exception ex) {
            return new ToggleLightGroupResponseDTO(false, null);
        }
    }

    public boolean toggleLight(String lightId, Boolean newStatus) {
        try {
            restClient
                .put()
                .uri("/clip/v2/resource/light/" + lightId)
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .body(Map.of("on", Map.of("on", newStatus)))
                .retrieve()
                .toBodilessEntity();

            this.lightBulbTrackRepo.updateIsOn((newStatus ? 1 : 0), lightId);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean modifyLightBrightness(String lightId, Double dimValue) {
        try {
            /**
             * {
             *   "dimming": {
             *      "brightness": 50
             *   }
             * }
             */
            Map<String, Map<String, Double>> body = Map.of(
                "dimming", Map.of("brightness", dimValue)
            );

            HueLightResponse response = restClient
                .put()
                .uri("/clip/v2/resource/light/" + lightId)
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .body(body)
                .retrieve()
                .body(HueLightResponse.class);

            this.lightBulbTrackRepo.updateBrightness(dimValue, lightId);

            return true;
        } catch (Exception ex) {
            // TODO: Log this
            return false;
        }
    }

    public boolean modifyLightColor(String lightId, RgbToXyDto xy) {
        try {
            /**
             * {
             *   "color": {
             *      "xy": {
             *          "x": 0.369,
             *          "y": 0.421
             *      }
             *   }
             * }
             */
            Map<String, Map<String, Map<String, Double>>> body = Map.of(
                "color", Map.of("xy",
                        Map.of("x", xy.x(), "y", xy.y())
                    )
            );

            restClient
                .put()
                .uri("/clip/v2/resource/light/" + lightId)
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .body(body)
                .retrieve()
                .toBodilessEntity();

            RGB rgb = this.colorConversionService.xyToRGB(xy.x(), xy.y(), 1d);
            this.lightBulbTrackRepo.updateColor(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), lightId);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean modifyGroupBrightness(String groupId, Double dimValue) {
        try {
            /**
             * {
             *   "dimming": {
             *      "brightness": 50
             *   }
             * }
             */
            Map<String, Map<String, Double>> body = Map.of(
                "dimming", Map.of("brightness", dimValue)
            );

            restClient
                .put()
                .uri("/clip/v2/resource/grouped_light/" + groupId)
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .body(body)
                .retrieve()
                .toBodilessEntity();

            // Get all devices affected
            HueRoomResponse roomResponse = restClient
                .get()
                .uri("/clip/v2/resource/room")
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .retrieve()
                .body(HueRoomResponse.class);

            List<HueRoomChildDTO> matchingChildren =
                roomResponse.data.stream()
                    .filter(room ->
                        room.services().stream()
                            .anyMatch(service -> service.rid().equals(groupId))
                    )
                    .flatMap(room -> room.children().stream())
                    .toList();

            matchingChildren.forEach(child -> {
                this.lightBulbTrackRepo.updateBrightness(dimValue, child.rid());
            });

            return true;
        } catch (Exception ex) {
            // TODO: Log this
            return false;
        }
    }

    public boolean modifyGroupColor(String groupId, RgbToXyDto xy, RGB rgb) {
        try {
            /**
             * {
             *   "color": {
             *      "xy": {
             *          "x": 0.34,
             *          "y": 0.45
             *      }
             *   }
             * }
             */
            Map<String, Map<String, Map<String, Double>>> body = Map.of(
                "color", Map.of("xy",
                        Map.of("x", xy.x(), "y", xy.y())
                    )
            );

            restClient
                .put()
                .uri("/clip/v2/resource/grouped_light/" + groupId)
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .body(body)
                .retrieve()
                .toBodilessEntity();

            // Get all devices affected
            HueRoomResponse roomResponse = restClient
                .get()
                .uri("/clip/v2/resource/room")
                .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
                .retrieve()
                .body(HueRoomResponse.class);

            List<HueRoomChildDTO> matchingChildren =
                roomResponse.data.stream()
                    .filter(room ->
                        room.services().stream()
                            .anyMatch(service -> service.rid().equals(groupId))
                    )
                    .flatMap(room -> room.children().stream())
                    .toList();

            matchingChildren.forEach(child -> {
                this.lightBulbTrackRepo.updateColor(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), child.rid);
            });

            return true;
        } catch (Exception ex) {
            // TODO: Log this
            return false;
        }
    }

    public enum BulbsForGroupType {
        ROOM("room");

        private final String label;

        private final static Map<String, BulbsForGroupType> strMap = Map.ofEntries(
            entry("room", ROOM)
        );

        BulbsForGroupType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static Optional<BulbsForGroupType> getFromStr(String str) {
            if (strMap.containsKey(str)) {
                return Optional.of(strMap.get(str));
            } else {
                return Optional.empty();
            }
        }
    }

    public ResponseEntity<GetBulbsForGroupResponse> getBulbsForGroup(String groupId) {
        HueGroupResponse response = restClient
            .get()
            .uri("/clip/v2/resource/grouped_light/" + groupId)
            .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
            .retrieve()
            .body(HueGroupResponse.class);

        String roomId = response.data().get(0).owner().rid();

        HueRoomResponse roomResponse = restClient
            .get()
            .uri("/clip/v2/resource/room" + "/" + roomId)
            .header("hue-application-key", "JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ")
            .retrieve()
            .body(HueRoomResponse.class);

        String roomName = roomResponse.data().get(0).metadata().name();

        List<LightBulbTrack> lightBulbs = new ArrayList<>();
        roomResponse.data().get(0).children().forEach(child -> {
            Optional<LightBulbTrack> lightBulb = this.lightBulbTrackRepo.getCachedBulb(child.rid());
            lightBulb.ifPresent(lightBulbs::add);
        });

        // Check if colors between bulbs are different
        String firstBulbColor = lightBulbs.getFirst().getRGBAString();
        AtomicReference<Boolean> isMultiColor = new AtomicReference<>(false);
        Boolean shouldTextBeBlack = false;

        // Color gradient is made up of all colors
        // linear-gradient(135deg, ${firstColorStr}, ${lastColorStr})
        StringBuilder colorGradient = new StringBuilder("linear-gradient(")
            .append("135") // TODO: Make this configurable
            .append("deg");

        // Determine the best color for the string text
        AtomicReference<String> textColor = new AtomicReference<>("rgba(255, 255, 255, 1)"); // white
        AtomicReference<Boolean> isGroupOn = new AtomicReference<>(false);
        AtomicReference<Double> brightness = new AtomicReference<>(0d);
        AtomicReference<Integer> index = new AtomicReference<>(0);

        lightBulbs.forEach(lightBulb -> {
            index.set(index.get() + 1);

            if (!firstBulbColor.equals(lightBulb.getRGBAString())) {
                isMultiColor.set(true);
            }

            colorGradient.append(", ")
                .append(lightBulb.getRGBAString());

            if (lightBulb.getRed() > 175 || lightBulb.getGreen() > 175 || lightBulb.getBlue() > 175) {
                textColor.set("rgba(0, 0, 0, 1)");
            }

            if (lightBulb.getIsOn()) {
                isGroupOn.set(true);
            }

            brightness.set(brightness.get() + lightBulb.getBrightness());
        });
        colorGradient.append(")");

        brightness.set(brightness.get() / index.get());


        return ResponseEntity.ok(new GetBulbsForGroupResponse(groupId, roomName, isMultiColor.get(),
            colorGradient.toString(), textColor.get(), isGroupOn.get(), brightness.get(), lightBulbs));
    }
}
