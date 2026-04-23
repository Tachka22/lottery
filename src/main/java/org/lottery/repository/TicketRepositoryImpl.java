package org.lottery.repository;

import com.google.inject.Inject;
import org.lottery.model.Ticket;
import org.lottery.model.enums.TicketStatus;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketRepositoryImpl implements TicketRepository {
  private final DataSource dataSource;

  @Inject
  public TicketRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Optional<Ticket> findById(int id) {
    String sql = "SELECT id, draw_id, user_id, numbers, bonus, status, created_at FROM tickets WHERE id = ?";
    try (var conn = dataSource.getConnection();
         var stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      try (var rs = stmt.executeQuery()) {
        return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка поиска билета", e);
    }
  }

  @Override
  public List<Ticket> findAllByUserId(int userId, int limit, int offset) {
    String sql = "SELECT id, draw_id, user_id, numbers, bonus, status, created_at FROM tickets WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
    List<Ticket> result = new ArrayList<>();
    try (var conn = dataSource.getConnection();
         var stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, userId);
      stmt.setInt(2, limit);
      stmt.setInt(3, offset);
      try (var rs = stmt.executeQuery()) {
        while (rs.next()) result.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка получения истории билетов", e);
    }
    return result;
  }

  @Override
  public int countByUserId(int userId) {
    String sql = "SELECT COUNT(*) FROM tickets WHERE user_id = ?";
    try (var conn = dataSource.getConnection();
         var stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, userId);
      try (var rs = stmt.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка подсчёта билетов", e);
    }
  }

  @Override
  public List<Ticket> findAllByDrawId(int drawId) {
    String sql = "SELECT id, draw_id, user_id, numbers, bonus, status, created_at FROM tickets WHERE draw_id = ? ORDER BY id";
    List<Ticket> result = new ArrayList<>();
    try (var conn = dataSource.getConnection();
         var stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, drawId);
      try (var rs = stmt.executeQuery()) {
        while (rs.next()) result.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка получения билетов тиража", e);
    }
    return result;
  }

  private Ticket mapRow(ResultSet rs) throws SQLException {
    Ticket t = new Ticket();
    t.setId(rs.getInt("id"));
    t.setDrawId(rs.getInt("draw_id"));
    t.setUserId(rs.getInt("user_id"));
    t.setNumbers(rs.getString("numbers"));
    int bonus = rs.getInt("bonus");
    if (!rs.wasNull()) t.setBonus(bonus);
    t.setStatus(TicketStatus.valueOf(rs.getString("status")));
    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    return t;
  }
}