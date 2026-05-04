package org.notification.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import org.notification.NotificationService;
import org.notification.model.Channel;
import org.notification.strategy.EmailSender;
import org.notification.strategy.NotificationSender;

public class NotificationModule extends AbstractModule {
    @Override
    protected void configure() {
        MapBinder<Channel, NotificationSender> binder =
                MapBinder.newMapBinder(binder(), Channel.class, NotificationSender.class);

        binder.addBinding(Channel.EMAIL).to(EmailSender.class).in(Singleton.class);

        bind(NotificationService.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    EmailConfig provideEmailConfig() {
        return new EmailConfig(
                System.getenv().getOrDefault("SMTP_HOST", "smtp.gmail.com"),
                Integer.parseInt(System.getenv().getOrDefault("SMTP_PORT", "587")),
                Boolean.parseBoolean(System.getenv().getOrDefault("SMTP_TLS", "true")),
                System.getenv().get("SMTP_USERNAME"),
                System.getenv().get("SMTP_PASSWORD"),
                System.getenv().getOrDefault("SMTP_FROM_EMAIL", "noreply@lottery.com"),
                System.getenv().getOrDefault("SMTP_FROM_NAME", "Lottery Service")
        );
    }
}
