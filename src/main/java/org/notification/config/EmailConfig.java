package org.notification.config;

public record EmailConfig(
        String smtpHost,
        int smtpPort,
        boolean useTls,
        String username,
        String password,
        String fromEmail,
        String fromName
) {}