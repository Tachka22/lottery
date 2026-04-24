package org.lottery.model.repository;

import org.lottery.config.DatabaseConfig;
import org.lottery.model.Draw;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrawRepositoryImpl implements DrawRepository {

    private final DatabaseConfig dbConfig;

    public DrawRepositoryImpl(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public List<Draw> findActiveDraws() {
        List<Draw> draws = new ArrayList<>();
        String sql = "SELECT id, name, lottery_type_name, status, winning_numbers, " +
                "winning_bonus, created_at, finished_at, description " +
                "FROM draws WHERE status = 'ACTIVE'";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                draws.add(mapResultSetToDraw(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return draws;
    }

    @Override
    public Draw findById(int id) {
        String sql = "SELECT id, name, lottery_type_name, status, winning_numbers, " +
                "winning_bonus, created_at, finished_at, description " +
                "FROM draws WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDraw(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Draw mapResultSetToDraw(ResultSet rs) throws SQLException {
        Draw draw = new Draw();
        draw.setId(rs.getInt("id"));
        draw.setName(rs.getString("name"));
        draw.setLotteryTypeName(rs.getString("lottery_type_name"));
        draw.setStatus(rs.getString("status"));
        draw.setWinningNumbers(rs.getString("winning_numbers"));

        int bonus = rs.getInt("winning_bonus");
        draw.setWinningBonus(rs.wasNull() ? null : bonus);

        draw.setCreatedAt(rs.getTimestamp("created_at") != null ?
                rs.getTimestamp("created_at").toLocalDateTime() : null);
        draw.setFinishedAt(rs.getTimestamp("finished_at") != null ?
                rs.getTimestamp("finished_at").toLocalDateTime() : null);
        draw.setDescription(rs.getString("description"));
        return draw;
    }
}