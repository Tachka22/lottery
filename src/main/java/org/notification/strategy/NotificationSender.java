package org.notification.strategy;

import org.notification.model.NotificationEvent;

public interface NotificationSender {
    void send(NotificationEvent event);
}