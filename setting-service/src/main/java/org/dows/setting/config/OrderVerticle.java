//package org.dows.setting.config;
//
//import io.vertx.core.AbstractVerticle;
//
//public class OrderVerticle extends AbstractVerticle {
//
//
//    public static final String ADDRESS_ORDER_CREATED = "order.created";
//    @Override
//    public void start() {
//        vertx.setTimer(1000, id -> {
//            FileDownloadedEvent event = new FileDownloadedEvent();
//            vertx.eventBus().publish(ADDRESS_ORDER_CREATED, event);
//            System.out.println("[Order] published OrderCreatedEvent");
//        });
//    }
//}