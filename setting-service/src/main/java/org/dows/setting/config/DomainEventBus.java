package org.dows.setting.config;

public interface DomainEventBus {
    void publish(DomainEvent event);
}