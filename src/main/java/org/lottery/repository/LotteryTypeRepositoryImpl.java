package org.lottery.repository;

import com.google.inject.Inject;

import javax.sql.DataSource;
import java.sql.SQLException;

public class LotteryTypeRepositoryImpl implements LotteryTypeRepository {
    private final DataSource dataSource;

    @Inject
    public LotteryTypeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean existsByName(String name) {
        var sql = "SELECT 1 FROM lottery_types WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check lottery type existence", e);
        }
    }
}
