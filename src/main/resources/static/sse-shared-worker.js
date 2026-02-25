let eventSource = null;
const ports = [];

function connect(topic, clientId) {
    if (eventSource) return; // 이미 연결되어 있으면 재사용

    eventSource = new EventSource(
        `/sse/subscribe?clientId=${clientId}&topic=${topic}`
    );

    eventSource.onmessage = (event) => {
        ports.forEach(port => port.postMessage({
            type: "message",
            data: event.data
        }));
    };

    eventSource.onerror = () => {
        ports.forEach(port => port.postMessage({
            type: "error",
            data: "SSE error"
        }));
    };
}

onconnect = function (e) {
    const port = e.ports[0];
    ports.push(port);

    port.onmessage = function (event) {
        const { type, topic, clientId } = event.data;

        if (type === "connect") {
            connect(topic, clientId);
        }
    };

    port.start();
};