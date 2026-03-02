package com.wooya.notification.domain.model;

import java.time.Instant;

public record NotificationMessage(
        String topic,
        String from,
        String message,
        String sentAt
) {
    public static NotificationMessage connected(String topic) {
        return new NotificationMessage(topic, "system", "connect", Instant.now().toString());
    }

    public static NotificationMessage of(String topic, String from, String message) {
        String resolvedFrom = (from == null || from.isBlank()) ? "anonymous" : from;
        return new NotificationMessage(topic, resolvedFrom, message, Instant.now().toString());
    }
}
