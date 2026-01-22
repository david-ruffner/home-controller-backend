package com.davidruffner.homecontrollerbackend.entities;

import java.util.Optional;

public class ModifyLightRequest {
    public record RGBDTO(
        Double red,
        Double green,
        Double blue,
        Double alpha
    ) {}

    public record ModifyLightRequestDTO(
        Double dimPercent,
        RGBDTO rgb,
        String lightId,
        String groupId
    ) {}

    private final Optional<Double> dimPercent;
    private final Optional<RGB> rgb;
    private final Optional<String> lightId;
    private final Optional<String> groupId;

    public ModifyLightRequest(ModifyLightRequestDTO dto) throws Exception {
        this.dimPercent = Optional.ofNullable(dto.dimPercent);

        if (dto.rgb != null) {
            this.rgb = Optional.of(new RGB(dto.rgb.red, dto.rgb.green,
                dto.rgb.blue, dto.rgb.alpha));
        } else {
            this.rgb = Optional.empty();
        }

        this.lightId = Optional.ofNullable(dto.lightId);
        this.groupId = Optional.ofNullable(dto.groupId);

        if (this.dimPercent.isEmpty() && this.rgb.isEmpty()) {
            throw new Exception("Both Dim Percentage and RGB Value cannot be null.");
        } else if (this.groupId.isEmpty() && this.lightId.isEmpty()) {
            throw new Exception("Both group ID and light ID cannot be null.");
        }
    }

    public Optional<Double> getDimPercent() {
        return dimPercent;
    }

    public Optional<RGB> getRgb() {
        return rgb;
    }

    public Optional<String> getLightId() {
        return lightId;
    }

    public Optional<String> getGroupId() {
        return groupId;
    }
}
