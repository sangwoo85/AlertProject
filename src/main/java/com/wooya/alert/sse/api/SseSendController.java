package com.wooya.alert.sse.api;

import com.wooya.alert.sse.dto.SseMessageDto;
import com.wooya.alert.sse.dto.SseRequestDto;
import com.wooya.alert.sse.repo.SseEmitterRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
public class SseSendController {

    private final SseEmitterRepository repo;

    public SseSendController(SseEmitterRepository repo) {this.repo = repo;}

    // topic 으로 발신 (서버 -> 해당 topic 구독자)
    @PostMapping("/api/send")
    public Map<String, Object> send(@RequestBody @Valid SseRequestDto req) {

        repo.sendToTopic(req.topic(), "message", Map.of(
                "topic", req.topic(),
                "from", req.from() == null ? "anonymous" : req.from(),
                "message", req.message(),
                "at", Instant.now().toString()
        ));

        return Map.of("ok", true);
    }

    // 전체 broadcast (데모용)
    @PostMapping("/api/broadcast")
    public Map<String, Object> broadcast(@RequestParam String message) {
        repo.broadcast("broadcast", new SseMessageDto(
                message,
                Instant.now().toString()
        ));
        return Map.of("ok", true);
    }
    @PostMapping("/api/broadcast/timer")
    public Map<String, Object> broadcastTimer(@RequestParam String timer) {
        int sleepTime =  Integer.valueOf(timer);
        for(int i = 0; i < 1000;i++){
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            repo.broadcast("broadcast",
                        new SseMessageDto(
                    "순번 ["+i+"]",
                    Instant.now().toString()
            ));
        }

        return Map.of("ok", true);
    }

    @GetMapping("/api/topics")
    public Map<String, Object> topics() {
        return Map.of("topics", repo.topics());
    }

}
