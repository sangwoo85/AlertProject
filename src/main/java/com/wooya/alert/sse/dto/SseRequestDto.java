package com.wooya.alert.sse.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @title Sse로 보내기 위한 Message Dto
 * */
public record SseRequestDto(
        @NotBlank String topic,
        @NotBlank String message,
        String from
) {
}
