package com.wooya.alert.sse.repo;

import com.wooya.alert.sse.dto.SseMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class SseEmitterRepository {

    private final Map<String, Map<String, SseEmitter>> store = new ConcurrentHashMap<>();
    private final Executor sseTaskExecutor;

    public SseEmitterRepository(@Qualifier("sseTaskExecutor") Executor sseTaskExecutor) {
        this.sseTaskExecutor = sseTaskExecutor;
    }


    /*
    * store 에 현재 접속한 Emitter를 저장 한다.
    * */
    public SseEmitter add(String topic, String emitterId, SseEmitter emitter) {
        store.computeIfAbsent(topic, t -> new ConcurrentHashMap<>()).put(emitterId,emitter);
        return emitter;
    }

    public void remove(String topic, String emitterId) {
        log.info("remove topic: {} emitterId:{}",topic,emitterId);
        Map<String, SseEmitter> emitter = store.get(topic);
        if(emitter != null){
            emitter.remove(emitterId);
            if(emitter.isEmpty())
                store.remove(topic);
        }
    }

    public int topicSize(String topic){
        return store.get(topic) == null ? 0 : store.get(topic).size();
    }

    public Set<String> topics() {
        return store.keySet();
    }

    public void sendToTopic(String topic, String eventName, SseMessageDto data) {
        sseTaskExecutor.execute(() -> sendToTopicInternal(topic, eventName, data));
    }

    /**
     * @title 현재 접속 되어있는 모든 Emitter에게 메시지를 전송 한다.
     * */
    public void broadcast(String eventName, SseMessageDto message) {
        sseTaskExecutor.execute(() ->
                store.keySet().forEach(topic -> sendToTopicInternal(topic, eventName, message))
        );
    }

    private void sendToTopicInternal(String topic, String eventName, SseMessageDto data) {
        log.info("Topic [{}] MessageDto [{}]", topic, data);
        Map<String, SseEmitter> emitters = store.get(topic);
        if (emitters == null) return;

        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .id(String.valueOf(Instant.now().toEpochMilli()))
                        .data(data));
            } catch (IOException e) {
                remove(topic, id);
            }
        });
    }
}
