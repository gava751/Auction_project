import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const createStompClient = (onMessageReceived: (message: any) => void, lotId: number) => {
    const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        onConnect: () => {
            console.log('Connected to WebSocket');
            // Подписываемся на конкретный лот
            client.subscribe(`/topic/lot/${lotId}`, (message) => {
                onMessageReceived(JSON.parse(message.body));
            });
        },
        onStompError: (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
        },
    });

    return client;
};