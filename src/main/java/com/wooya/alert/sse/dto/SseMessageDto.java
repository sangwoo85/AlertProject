package com.wooya.alert.sse.dto;

import java.time.Instant;

public record SseMessageDto(
        String topic,
        String from,
        String message
        ,String at
) {
    public SseMessageDto(String message){
        this(null, null, message, Instant.now().toString());
    }

    public SseMessageDto(String message, String at) {
        this(null, null, message, at);
    }

    public SseMessageDto(String topic, String from, String message) {
        this(topic, from, message, Instant.now().toString());
    }
}
