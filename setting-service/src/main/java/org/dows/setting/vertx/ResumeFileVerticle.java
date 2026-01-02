//package org.dows.setting.vertx;
//
//import io.vertx.core.AbstractVerticle;
//import io.vertx.core.Promise;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.rfa.api.ResumeFileApi;
//import org.dows.rfa.open.PostResumeFileEntityRequest;
//import org.springframework.stereotype.Component;
//
///**
// * 简历文件Verticle，用于处理Event Bus上的简历文件请求
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ResumeFileVerticle extends AbstractVerticle {
//
//    private final ResumeFileApi resumeFileApi;
//
//    // Event Bus地址，与发送方保持一致
//    private static final String RESUME_FILE_EVENT_ADDRESS = "resume.file.entity";
//
//    @Override
//    public void start(Promise<Void> startPromise) throws Exception {
//        // 注册Event Bus消息处理器
//        vertx.eventBus().consumer(RESUME_FILE_EVENT_ADDRESS, message -> {
//            try {
//                // 解析消息体
//                PostResumeFileEntityRequest request = (PostResumeFileEntityRequest) message.body();
//                log.info("Received resume file entity request: {}", request);
//
//                // 调用原有的API方法
//                resumeFileApi.postEntity(request);
//
//                // 回复消息发送成功
//                message.reply("Resume file entity request processed successfully");
//
//            } catch (Exception e) {
//                log.error("Failed to process resume file entity request: {}", e.getMessage());
//                // 回复错误信息
//                message.fail(500, e.getMessage());
//            }
//        });
//
//        log.info("ResumeFileVerticle started and listening on address: {}", RESUME_FILE_EVENT_ADDRESS);
//        startPromise.complete();
//    }
//}
