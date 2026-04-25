package org.lottery.repository;

import com.google.inject.Inject;
import org.lottery.model.LotteryType;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LotteryTypeRepositoryImpl implements LotteryTypeRepository {
    private final DataSource dataSource;

    @Inject
    public LotteryTypeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean existsByName(String name) {
        var sql = """
                SELECT 1 FROM lottery_types WHERE name = ?
                """;
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось проверить наличие типа лотереи", e);
        }
    }

    @Override
    public Optional<LotteryType> findByName(String name) {
        String sql = """
                    SELECT name, numbers_count, min_number, max_number, has_bonus, bonus_min, bonus_max, description 
                    FROM lottery_types WHERE name = ?
                    """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LotteryType type = new LotteryType();
                    type.setName(rs.getString("name"));
                    type.setNumbersCount(rs.getInt("numbers_count"));
                    type.setMinNumber(rs.getInt("min_number"));
                    type.setMaxNumber(rs.getInt("max_number"));
                    type.setHasBonus(rs.getBoolean("has_bonus"));
                    type.setBonusMin(rs.getInt("bonus_min"));
                    type.setBonusMax(rs.getInt("bonus_max"));
                    type.setDescription(rs.getString("description"));

                    return Optional.of(type);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения лотереи по имени: " + name, e);
        }
    }


    @Override
    public List<LotteryType> findAll() {
        var sql = "SELECT name, numbers_count, min_number, max_number, has_bonus, bonus_min, bonus_max, description FROM lottery_types ORDER BY name";
        List<LotteryType> list = new ArrayList<>();
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("ошибка при взаимодействии с базой данных при выгрузке типов лотерейных билетов", e);
        }
        return list;
    }

    @Override
    public LotteryType save(LotteryType type) {
        var sql = """
            INSERT INTO lottery_types (name, numbers_count, min_number, max_number, has_bonus, bonus_min, bonus_max, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.getName());
            stmt.setInt(2, type.getNumbersCount());
            stmt.setInt(3, type.getMinNumber());
            stmt.setInt(4, type.getMaxNumber());
            stmt.setBoolean(5, type.isHasBonus());
            setNullableInt(stmt, 6, type.getBonusMin());
            setNullableInt(stmt, 7, type.getBonusMax());
            stmt.setString(8, type.getDescription());
            stmt.executeUpdate();
            return type;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("тип лотереи '" + type.getName() + "' уже существует");
            }
            throw new RuntimeException("ошибка при взаимодействии с базой данных при сохранении типа лотереи", e);
        }
    }

    private void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) stmt.setInt(index, value);
        else stmt.setNull(index, Types.INTEGER);
    }

    private LotteryType mapRow(ResultSet rs) throws SQLException {
        LotteryType t = new LotteryType();
        t.setName(rs.getString("name"));
        t.setNumbersCount(rs.getInt("numbers_count"));
        t.setMinNumber(rs.getInt("min_number"));
        t.setMaxNumber(rs.getInt("max_number"));
        t.setHasBonus(rs.getBoolean("has_bonus"));

        int bMin = rs.getInt("bonus_min");
        if (!rs.wasNull()) t.setBonusMin(bMin);

        int bMax = rs.getInt("bonus_max");
        if (!rs.wasNull()) t.setBonusMax(bMax);

        t.setDescription(rs.getString("description"));
        return t;
    }
}
