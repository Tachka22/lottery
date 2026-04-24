package org.lottery.service;

import com.google.inject.Inject;
import org.lottery.model.UserActionEvent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

public class DatabaseAuditListener implements Consumer<UserActionEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuditListener.class);

    private final DataSource dataSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public DatabaseAuditListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void accept(UserActionEvent event) {
        String sql = "INSERT INTO user_actions (user_id, action_type, details, created_at) VALUES (?, ?, ?::jsonb, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, event.userId());
            stmt.setString(2, event.actionCode());
            stmt.setString(3, objectMapper.writeValueAsString(event.params()));
            stmt.setTimestamp(4, new Timestamp(event.timestamp()));

            stmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Ошибка сохранения истории '{}' для пользователя {}. Событие: {}", event.actionCode(), event.userId(), event.params(), e);
        }
    }
}
