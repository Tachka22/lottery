package org.lottery.repository;

import com.google.inject.Inject;
import org.lottery.model.Draw;
import org.lottery.model.enums.DrawStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrawRepositoryImpl implements DrawRepository {
    private final DataSource dataSource;

    @Inject
    public DrawRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Draw save(Draw draw) {
        var sql = "INSERT INTO draws (name, lottery_type, status, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, draw.getName());
            stmt.setString(2, draw.getLotteryType());
            stmt.setString(3, draw.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(draw.getCreatedAt()));

            stmt.executeUpdate();

            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    draw.setId(rs.getInt(1));
                }
            }

            return draw;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during save", e);
        }
    }

    @Override
    public List<Draw> findAll() {
        var sql = "SELECT id, name, lottery_type, status, winning_combination, created_at, finished_at FROM draws ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            List<Draw> draws = new ArrayList<>();
            while (rs.next()) {
                draws.add(mapRow(rs));
            }
            return draws;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all draws", e);
        }
    }

    private Draw mapRow(ResultSet rs) throws SQLException {
        Draw draw = new Draw();
        draw.setId(rs.getInt("id"));
        draw.setName(rs.getString("name"));
        draw.setLotteryType(rs.getString("lottery_type"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            draw.setStatus(DrawStatus.valueOf(statusStr));
        }

        draw.setWinningCombination(rs.getString("winning_combination"));

        draw.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp finishedAt = rs.getTimestamp("finished_at");
        if (finishedAt != null) {
            draw.setFinishedAt(finishedAt.toLocalDateTime());
        }

        return draw;
    }


}
