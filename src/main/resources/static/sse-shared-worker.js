/**
 * 설명용 단순 버전
 * - 탭(port)마다 topic별 EventSource를 가진다.
 * - 같은 탭에서 같은 topic 재연결은 무시한다.
 * - 새 탭에서 같은 topic을 연결하면 서버에는 새 emitter가 추가된다.
 */
const sourcesByPort = new Map(); // Map<MessagePort, Map<topic, EventSource>>

function connectTopic(port, clientId, topic) {
    let topicSources = sourcesByPort.get(port);
    if (!topicSources) {
        topicSources = new Map();
        sourcesByPort.set(port, topicSources);
    }

    if (topicSources.has(topic)) return;

    const es = new EventSource(`/sse/subscribe?clientId=${clientId}&topic=${topic}`);
    topicSources.set(topic, es);

    function forward(eventName, event) {
        let parsed;
        try {
            parsed = JSON.parse(event.data);
        } catch (_) {
            parsed = null;
        }

        port.postMessage({
            type: "message",
            eventName,
            topic: parsed?.topic ?? topic,
            data: parsed?.message ?? event.data
        });
    }

    es.onmessage = (event) => forward("message", event);
    es.addEventListener("connected", (event) => forward("connected", event));
    es.addEventListener("broadcast", (event) => forward("broadcast", event));

    es.onerror = () => {
        port.postMessage({ type: "error", data: `SSE error (topic=${topic})` });
        es.close();
        topicSources.delete(topic);
    };
}

function disconnectAll(port) {
    const topicSources = sourcesByPort.get(port);
    if (!topicSources) return;

    topicSources.forEach((es) => es.close());
    topicSources.clear();
}

onconnect = function (e) {
    const port = e.ports[0];

    port.onmessage = function (event) {
        const { type, clientId, topic } = event.data;

        if (type === "connect") {
            connectTopic(port, clientId, topic);
        } else if (type === "disconnectAll") {
            disconnectAll(port);
        }
    };

    // 설명용 예제라 최소 정리만 수행
    port.onmessageerror = function () {
        disconnectAll(port);
        sourcesByPort.delete(port);
    };

    port.start();
};
