package org.dows.setting;

import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VertxDeploy {

    private final Vertx vertx;

    @PostConstruct
    public void deploy() {
        vertx.deployVerticle(new RfaVerticle());
    }
}
