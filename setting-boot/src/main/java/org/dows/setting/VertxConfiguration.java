package org.dows.setting;

import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxConfiguration {
    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @PostConstruct
    public void deploy() {
        vertx().deployVerticle(new RfaVerticle());
    }
}
