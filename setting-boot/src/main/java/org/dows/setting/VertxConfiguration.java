package org.dows.setting;

import io.vertx.core.Vertx;
import org.dows.rade.event.VertxDomainEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxConfiguration {
    /**
     * 创建并配置Vertx实例
     *
     * @return Vertx实例
     */
    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }


    @Bean
    public VertxDomainEventBus vertxDomainEventBus() {
        return new VertxDomainEventBus(vertx());
    }

}
