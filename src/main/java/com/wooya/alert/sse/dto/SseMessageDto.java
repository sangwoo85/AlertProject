package com.wooya.alert.sse.dto;

import java.time.Instant;

public record SseMessageDto(
        String message
        ,String at
) {
    public SseMessageDto(String message){
        this(message, Instant.now().toString());
    }
}
