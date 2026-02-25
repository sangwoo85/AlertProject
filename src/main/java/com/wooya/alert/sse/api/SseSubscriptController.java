package com.wooya.alert.sse.api;

import com.wooya.alert.sse.dto.SseMessageDto;
import com.wooya.alert.sse.repo.SseEmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * @title Sse 연결을 위한 Controller
 * */
@Slf4j
@RestController
public class SseSubscriptController {

    private final SseEmitterRepository repo;

    public SseSubscriptController(SseEmitterRepository repo){
        this.repo = repo;
    }

    @GetMapping(value="/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscript(
            @RequestParam String clientId,
            @RequestParam String topic
    ){
        //log.info("START [ {} ] [ {} ]", clientId, topic);

        //여기서 0L 은 무제한
        SseEmitter emitter = new SseEmitter(0L);

        String emitterId= clientId +  "-" + UUID.randomUUID();
        repo.add(topic, emitterId, emitter);

        //Emiiter 생명 주기
        // Emitter 연결  종료/에러/타임아웃시 정리 하는것
        emitter.onCompletion(() ->repo.remove(topic,emitterId));
        emitter.onTimeout(() ->repo.remove(topic,emitterId));
        emitter.onError((e) -> repo.remove(topic,emitterId));

        repo.sendToTopic(topic,"connected",new SseMessageDto("connect"));

        return emitter;
    }
}
