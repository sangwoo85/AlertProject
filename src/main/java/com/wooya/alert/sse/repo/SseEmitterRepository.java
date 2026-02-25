package com.wooya.alert.sse.repo;

import com.wooya.alert.sse.dto.SseMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterRepository {

    private final Map<String, Map<String, SseEmitter>> store = new ConcurrentHashMap<>();


    /*
    * store 에 현재 접속한 Emitter를 저장 한다.
    * */
    public SseEmitter add(String topic, String emitterId, SseEmitter emitter) {
        store.computeIfAbsent(topic, t -> new ConcurrentHashMap<>()).put(emitterId,emitter);
        return emitter;
    }

    public void remove(String topic, String emitterId) {
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

    public void sendToTopic(String topic, String eventName, Object data) {
        Map<String,SseEmitter> emitters = store.get(topic);

        if(emitters == null) return;

        emitters.forEach((id,  emitter)->{

            try{

                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .id(String.valueOf(String.valueOf(Instant.now().toEpochMilli())))
                        .data(data));

            }catch(IOException e){
                remove(topic,id);
                //log.error(e.getLocalizedMessage());
            }
        });
    }

    /**
     * @title 현재 접속 되어있는 모든 Emitter에게 메시지를 전송 한다.
     * */
    public void broadcast(String eventName, SseMessageDto message) {
        store.keySet().forEach( topic -> sendToTopic(topic,eventName,message));
    }
}
