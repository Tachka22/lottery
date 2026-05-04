package org.notification;

import com.google.inject.Inject;
import org.notification.model.Channel;
import org.notification.model.NotificationEvent;
import org.notification.strategy.NotificationSender;

import java.util.Map;

public class NotificationService {
    private final Map<Channel, NotificationSender> senders;

    @Inject
    public NotificationService(Map<Channel, NotificationSender> senders) {
        this.senders = senders;
    }

    public void notify(NotificationEvent notification, Channel channel) throws IllegalArgumentException {
        NotificationSender sender = senders.get(channel);

        if (sender == null) {
            throw new IllegalArgumentException("Нет обработчика для канала: " + channel.toString());
        }
        sender.send(notification);
    }
}
