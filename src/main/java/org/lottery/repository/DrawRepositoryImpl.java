package org.lottery.repository;

import com.google.inject.Inject;
import org.lottery.model.Draw;
import org.lottery.model.enums.DrawStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DrawRepositoryImpl implements DrawRepository {
    private final DataSource dataSource;

    @Inject
    public DrawRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Draw save(Draw draw) {
        var sql = "INSERT INTO draws (name, lottery_type_name, status, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, draw.getName());
            stmt.setString(2, draw.getLotteryTypeName());
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
        var sql = "SELECT id, name, lottery_type_name, status, winning_numbers, winning_bonus, created_at, finished_at FROM draws ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            List<Draw> draws = new ArrayList<>();
            while (rs.next()) {
                draws.add(mapRowToDraw(rs));
            }
            return draws;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all draws", e);
        }
    }

    @Override
    public Optional<Draw> findById(int id) {
        var sql = "SELECT id, name, lottery_type_name, status, winning_numbers, winning_bonus, created_at, finished_at FROM draws WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToDraw(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find draw by id", e);
        }
    }

    @Override
    public void update(Draw draw) {
        var sql = "UPDATE draws SET name = ?, lottery_type_name = ?, status = ?, winning_numbers = ?, winning_bonus = ?, finished_at = ? WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, draw.getName());
            stmt.setString(2, draw.getLotteryTypeName());
            stmt.setString(3, draw.getStatus().name());
            stmt.setString(4, draw.getWinningNumbers());

            if (draw.getWinningBonus() != null)
                stmt.setInt(5, draw.getWinningBonus());
            else
                stmt.setNull(5, Types.INTEGER);

            stmt.setTimestamp(6, draw.getFinishedAt() != null ? Timestamp.valueOf(draw.getFinishedAt()) : null);
            stmt.setInt(7, draw.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update draw", e);
        }
    }

    private Draw mapRowToDraw(ResultSet rs) throws SQLException {
        var draw = new Draw();
        draw.setId(rs.getInt("id"));
        draw.setName(rs.getString("name"));
        draw.setLotteryTypeName(rs.getString("lottery_type_name"));

        var statusStr = rs.getString("status");
        if (statusStr != null) {
            draw.setStatus(DrawStatus.valueOf(statusStr));
        }

        draw.setWinningNumbers(rs.getString("winning_numbers"));
        int winningBonus = rs.getInt("winning_bonus");
        if (!rs.wasNull()) {
            draw.setWinningBonus(winningBonus);
        }

        draw.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        var finishedAt = rs.getTimestamp("finished_at");
        if (finishedAt != null) {
            draw.setFinishedAt(finishedAt.toLocalDateTime());
        }
        return draw;
    }
}
