package org.dows.setting.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class DomainEvent implements Serializable {

    public DomainEvent() {
    }
    @JsonProperty
    private final String eventId = UUID.randomUUID().toString();
    @JsonProperty
    private final Instant occurredAt = Instant.now();
    @JsonProperty
    private final String traceId = TraceContext.getOrCreate();
    @JsonProperty("address")
    private String address;
    @JsonProperty("data")
    private Object data;

    public String address() {
        return address;
    }

    public static DomainEvent address(String address) {
        DomainEvent event = new DomainEvent();
        event.address = address;
        return event;
    }

    public DomainEvent data(Object data) {
        this.data = data;
        return this;
    }

    public String toJson() {
        return Json.encode(this);
    }

    public static DomainEvent fromJson(String json) {
        return Json.decodeValue(json, DomainEvent.class);
    }


    public <T> T data(Class<T> clazz) {
        JsonObject json = JsonObject.mapFrom(this).getJsonObject("data");
        return json.mapTo(clazz);
    }


    public String eventId() {
        return eventId;
    }

    public Instant occurredAt() {
        return occurredAt;
    }

    public String traceId() {
        return traceId;
    }
}