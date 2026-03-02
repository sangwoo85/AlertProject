package com.wooya.notification.application.port.in;

public interface NotificationCommandUseCase {

    void sendToTopic(String topic, String from, String message);

    void broadcast(String message);

    void broadcastTimer(int intervalMs, int count);
}
