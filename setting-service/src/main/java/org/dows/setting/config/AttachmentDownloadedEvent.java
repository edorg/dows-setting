//package org.dows.setting.config;
//
//import io.vertx.core.json.JsonObject;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//@EqualsAndHashCode(callSuper = true)
//@Data
//@EventAddress()
//public class AttachmentDownloadedEvent extends DomainEvent {
//    public Long orderId;
//    public Long userId;
//    public Long amount;
//
//    public JsonObject toJson() {
//        return new JsonObject()
//                .put("orderId", orderId)
//                .put("userId", userId)
//                .put("amount", amount);
//    }
//
//
//    public String getEventAddress() {
//        return "attachment.downloaded";
//    }
//
//    public static AttachmentDownloadedEvent fromJson(JsonObject json) {
//
//        return null;
//    }
//}