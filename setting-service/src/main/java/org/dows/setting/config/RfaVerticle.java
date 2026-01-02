package org.dows.setting.config;

import io.vertx.core.AbstractVerticle;

public class RfaVerticle extends AbstractVerticle {


    @Override
    public void start() {
        vertx.eventBus().consumer("setting.mail.read", message -> {
            try {
                String traceId = message.headers().get("traceId");
                TraceContext.set(traceId);


                Object body = message.body();

            } finally {
                TraceContext.clear();
            }
        });
    }
}