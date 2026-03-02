package com.wooya.notification.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationSendRequest(
        @NotBlank String topic,
        @NotBlank String message,
        String from
) {
}
