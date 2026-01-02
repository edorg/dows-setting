//package org.dows.setting.config;
//
//import io.vertx.core.buffer.Buffer;
//import io.vertx.core.eventbus.MessageCodec;
//
//public class Codec implements MessageCodec<AttachmentDownloadedEvent, AttachmentDownloadedEvent> {
//    @Override
//    public void encodeToWire(Buffer buffer, AttachmentDownloadedEvent event) {
//        buffer.appendBuffer(event.toJson().toBuffer());
//    }
//
//    @Override
//    public AttachmentDownloadedEvent decodeFromWire(int pos, Buffer buffer) {
//        return AttachmentDownloadedEvent.fromJson(buffer.toJsonObject());
//    }
//
//
//    @Override
//    public AttachmentDownloadedEvent transform(AttachmentDownloadedEvent event) {
//        return event; // JVM 内可直接传引用
//    }
//
//
//    @Override
//    public String name() {
//        return "OrderCreatedEventCodec";
//    }
//
//
//    @Override
//    public byte systemCodecID() {
//        return -1;
//    }
//}