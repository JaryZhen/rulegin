package com.github.rulegin.common.data.id;

import com.github.rulegin.common.data.EntityType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 *
 */
public class DeviceId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public DeviceId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static DeviceId fromString(String deviceId) {
        return new DeviceId(UUID.fromString(deviceId));
    }

    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.DEVICE;
    }
}
