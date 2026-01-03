package org.dows.setting;

import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VertxDeploy {

    private final Vertx vertx;

    private final RfaUploadVerticle rfaUploadVerticle;
    private final RfaAcceptVerticle rfaAcceptVerticle;
    private final RfaAnalysisVerticle rfaAnalysisVerticle;

    @PostConstruct
    public void deploy() {
        vertx.deployVerticle(rfaUploadVerticle);
        vertx.deployVerticle(rfaAcceptVerticle);
        vertx.deployVerticle(rfaAnalysisVerticle);
    }
}
