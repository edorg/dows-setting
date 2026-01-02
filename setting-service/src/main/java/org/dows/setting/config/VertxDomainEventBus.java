package org.dows.setting.config;

import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VertxDomainEventBus implements DomainEventBus {

    private final Vertx vertx;
    @Override
    public void publish(DomainEvent event) {
        //String address = EventAddressMapper.map(event);
        vertx.eventBus().publish(event.address(), event);
    }
}