package org.dows.setting.config;

public final class EventAddressMapper {
    private EventAddressMapper() {
    }

    public static String map(DomainEvent event) {
        if (event instanceof AttachmentDownloadedEvent) {
            return "order.created.v1";
        }
        throw new IllegalArgumentException("Unknown event: " + event.getClass());
    }
}