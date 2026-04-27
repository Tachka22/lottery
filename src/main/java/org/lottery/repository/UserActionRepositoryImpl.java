package org.lottery.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.lottery.model.UserAction;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserActionRepositoryImpl implements UserActionRepository {
    private final DataSource dataSource;
    private final ObjectMapper objectMapper;

    @Inject
    public UserActionRepositoryImpl(DataSource dataSource, ObjectMapper objectMapper) {
        this.dataSource = dataSource;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<UserAction> getAll(int userId) {
        var sql = """
            SELECT id, user_id, action_type, details, created_at FROM user_actions WHERE user_id = ? ORDER BY created_at DESC
            """;

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            var rs = stmt.executeQuery();

            List<UserAction> actions = new ArrayList<>();
            while (rs.next()) {
                actions.add(mapToUserAction(rs));
            }
            return actions;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка чтения истории пользователя", e);
        }
    }

    @Override
    public void save(UserAction userAction) {
        var sql = "INSERT INTO user_actions (user_id, action_type, details) VALUES (?, ?, ?::jsonb)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userAction.getUserId());
            stmt.setString(2, userAction.getActionType());
            stmt.setString(3, objectMapper.writeValueAsString(userAction.getDetails()));

            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException("Ошибка сохранения действия", e);
        }
    }

    private UserAction mapToUserAction(ResultSet rs) throws SQLException {
        try {
            UserAction action = new UserAction();
            action.setId(rs.getInt("id"));
            action.setUserId(rs.getInt("user_id"));
            action.setActionType(rs.getString("action_type"));
            action.setCreatedAt(rs.getTimestamp("created_at"));

            String detailsJson = rs.getString("details");
            if (detailsJson != null) {
                action.setDetails(objectMapper.readTree(detailsJson));
            }

            return action;
        } catch (JsonProcessingException e) {
            throw new SQLException("Ошибка парсинга JSONB поля details", e);
        }
    }
}
