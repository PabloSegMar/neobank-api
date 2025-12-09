package com.example.neo_bank.api.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleNotificationService.class);

    @Override
    public void sendNotification(String email, String message) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("[NOTIFICACIÓN] Enviando email a: {} | Mensaje: {}", email, message);
    }
}
