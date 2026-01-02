package org.dows.setting.config;

public final class EventAddressMapper {
    private EventAddressMapper() {
    }

    // todo 动态维护在数据库中
    public static String map(DomainEvent event) {
        String address = event.address();
        if (address != null) {
            return address;
        }
        throw new IllegalArgumentException("No address found for event: " + event.getClass().getName());
    }

}