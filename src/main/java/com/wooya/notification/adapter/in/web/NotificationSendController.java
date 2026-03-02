package com.wooya.notification.adapter.in.web;

import com.wooya.notification.adapter.in.web.dto.NotificationSendRequest;
import com.wooya.notification.application.port.in.NotificationCommandUseCase;
import com.wooya.notification.application.port.in.NotificationSubscriptionUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NotificationSendController {

    private final NotificationCommandUseCase commandUseCase;
    private final NotificationSubscriptionUseCase subscriptionUseCase;

    public NotificationSendController(
            NotificationCommandUseCase commandUseCase,
            NotificationSubscriptionUseCase subscriptionUseCase
    ) {
        this.commandUseCase = commandUseCase;
        this.subscriptionUseCase = subscriptionUseCase;
    }

    @PostMapping("/notification/api/send")
    public Map<String, Object> send(@RequestBody @Valid NotificationSendRequest request) {
        commandUseCase.sendToTopic(request.topic(), request.from(), request.message());
        return Map.of("ok", true);
    }

    @PostMapping("/notification/api/broadcast")
    public Map<String, Object> broadcast(@RequestParam String message) {
        commandUseCase.broadcast(message);
        return Map.of("ok", true);
    }

    @PostMapping("/notification/api/broadcast/timer")
    public Map<String, Object> broadcastTimer(
            @RequestParam int timer,
            @RequestParam(required = false, defaultValue = "1000") int count
    ) {
        commandUseCase.broadcastTimer(timer, count);
        return Map.of("ok", true);
    }

    @GetMapping("/notification/api/topics")
    public Map<String, Object> topics() {
        return Map.of("topics", subscriptionUseCase.topics());
    }
}
