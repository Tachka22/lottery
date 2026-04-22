package org.lottery.model.repository;

import org.lottery.config.DatabaseConfig;
import org.lottery.model.Ticket;

import java.sql.*;

public class TicketRepositoryImpl implements TicketRepository {

    private final DatabaseConfig dbConfig;

    public TicketRepositoryImpl(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public Ticket save(Ticket ticket) {
        String sql = "INSERT INTO tickets (draw_id, user_id, numbers, bonus, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING id";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getDrawId());
            stmt.setInt(2, ticket.getUserId());
            stmt.setString(3, ticket.getNumbers());

            // Обработка бонусного числа (может быть null)
            if (ticket.getBonus() != null) {
                stmt.setInt(4, ticket.getBonus());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, ticket.getStatus());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ticket.setId(rs.getInt(1));
            }
            return ticket;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean existsByDrawIdAndNumbers(int drawId, String numbers) {
        String sql = "SELECT 1 FROM tickets WHERE draw_id = ? AND numbers = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, drawId);
            stmt.setString(2, numbers);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}