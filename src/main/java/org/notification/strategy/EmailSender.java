package org.notification.strategy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.notification.config.EmailConfig;
import org.notification.model.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class EmailSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final EmailConfig config;
    private final Session session;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Inject
    public EmailSender(EmailConfig config) {
        this.config = Objects.requireNonNull(config, "EmailConfig не может быть null");
        this.session = createSession(config);
    }

    @Override
    public void send(NotificationEvent event) {
        validateRecipient(event.recipient());

        CompletableFuture.runAsync(() -> sendSync(event), executor).exceptionally(ex -> {
            log.error("Критическая ошибка при отправке email на {}", event.recipient(), ex);
            return null;
        });
    }

    private void sendSync(NotificationEvent event) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(config.fromEmail(), config.fromName()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(event.recipient()));
            msg.setSubject("Уведомление от лотерейного сервиса");
            msg.setText(event.message());
            msg.setSentDate(new java.util.Date());

            Transport.send(msg);
            log.info("Email успешно отправлен на {}", event.recipient());
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Ошибка SMTP", e);
        }
    }

    private void validateRecipient(String email) {
        try {
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            throw new IllegalArgumentException("Некорректный адрес: " + email, e);
        }
    }

    private Session createSession(EmailConfig cfg) {
        Properties props = new Properties();
        props.put("mail.smtp.host", cfg.smtpHost());
        props.put("mail.smtp.port", String.valueOf(cfg.smtpPort()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        if (cfg.useTls()) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(cfg.username(), cfg.password());
            }
        });
    }
}