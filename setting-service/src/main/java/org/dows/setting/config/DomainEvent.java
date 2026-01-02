package org.dows.setting.config;

import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.UUID;

public class DomainEvent {

    public DomainEvent() {
    }

    private final String eventId = UUID.randomUUID().toString();
    private final Instant occurredAt = Instant.now();
    private final String traceId = TraceContext.getOrCreate();
    private String address;

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