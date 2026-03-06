package com.auction.platform.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Канал для рассылки от сервера к клиентам (подписка)
        config.enableSimpleBroker("/topic");
        // Префикс для сообщений от клиента к серверу (если понадобятся)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Точка входа для подключения фронтенда
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Разрешаем подключения с любых доменов (для разработки)
                .withSockJS(); // Поддержка SockJS для старых браузеров
    }
}