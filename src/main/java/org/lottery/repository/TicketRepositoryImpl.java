package org.lottery.repository;

import com.google.inject.Inject;
import org.lottery.model.ParticipantNotificationInfo;
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

  @Override
  public int markWinners(int drawId, String winningNumbers, Integer winningBonus) {
    try (Connection conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);
      int winners = 0;

      var winSql = "UPDATE tickets SET status = 'WIN' WHERE draw_id = ? AND numbers = ?";
      if (winningBonus != null) {
        winSql += " AND bonus = ?";
      } else {
        winSql += " AND bonus IS NULL";
      }

      try (var stmt = conn.prepareStatement(winSql)) {
        stmt.setInt(1, drawId);
        stmt.setString(2, winningNumbers);
        if (winningBonus != null)
          stmt.setInt(3, winningBonus);

        winners = stmt.executeUpdate();
      }

      var loseSql = "UPDATE tickets SET status = 'LOSE' WHERE draw_id = ? AND status = 'PENDING'";
      try (var stmt = conn.prepareStatement(loseSql)) {
        stmt.setInt(1, drawId);
        stmt.executeUpdate();
      }

      conn.commit();

      return winners;
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка установки результата билетов для тиража: " + drawId, e);
    }
  }

  @Override
  public int cancelTickets(int drawId) {
    var sql = "UPDATE tickets SET status = 'LOSE' WHERE draw_id = ? AND status != 'LOSE'";
    try (var conn = dataSource.getConnection();
         var stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, drawId);
      int updated = stmt.executeUpdate();

      return updated;
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка отмены билетов для тиража: " + drawId, e);
    }
  }

  @Override
  public Ticket save(Ticket ticket) {
    String sql = """
                INSERT INTO tickets (draw_id, user_id, numbers, bonus, status, created_at)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING id
                """;

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, ticket.getDrawId());
      stmt.setInt(2, ticket.getUserId());
      stmt.setString(3, ticket.getNumbers());

      if (ticket.getBonus() != null) {
        stmt.setInt(4, ticket.getBonus());
      } else {
        stmt.setNull(4, Types.INTEGER);
      }

      stmt.setString(5, ticket.getStatus().name());

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        ticket.setId(rs.getInt(1));
      }
      return ticket;
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка сохранения билета", e);
    }
  }

  @Override
  public boolean existsByDrawIdAndNumbers(int drawId, String numbers) {
    String sql = "SELECT 1 FROM tickets WHERE draw_id = ? AND numbers = ?";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, drawId);
      stmt.setString(2, numbers);
      ResultSet rs = stmt.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка получение состояния комбинации билета", e);
    }
  }

  @Override
  public List<ParticipantNotificationInfo> findParticipantsByDrawId(int drawId) {
    String sql = """
        SELECT DISTINCT u.email, t.status 
        FROM tickets t 
        JOIN users u ON t.user_id = u.id 
        WHERE t.draw_id = ?
        """;

    List<ParticipantNotificationInfo> result = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, drawId);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          result.add(new ParticipantNotificationInfo(
                  rs.getString("email"),
                  rs.getString("status")
          ));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при получении списка участников для рассылки", e);
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