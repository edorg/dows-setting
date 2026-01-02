package org.dows.setting.config;

import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class VerticleBootstrap {

    /**
     * 创建并配置Vertx实例
     *
     * @return Vertx实例
     */
    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @PostConstruct
    public void deploy() {
        vertx().deployVerticle(new RfaVerticle());
    }
}