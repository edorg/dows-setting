package org.dows.setting;

import cn.hutool.json.JSONUtil;
import io.vertx.core.AbstractVerticle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.event.TraceContext;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class RfaUploadVerticle extends AbstractVerticle {


    @Override
    public void start() {
        vertx.eventBus().consumer("setting.mail.readed", message -> {
            try {
                String traceId = message.headers().get("traceId");
                TraceContext.set(traceId);


                Object body = message.body();
                System.out.println("RfaUploadVerticle==================body:"+ JSONUtil.toJsonStr(body));

                log.info("body:{}", body);
            } finally {
                TraceContext.clear();
            }
        });
    }

}
