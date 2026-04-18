package org.lottery.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/lottery"));
            config.setUsername(System.getenv().getOrDefault("DB_USER", "postgres"));
            config.setPassword(System.getenv().getOrDefault("DB_PASSWORD", "postgres"));
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

//    public static void runMigrations() {
//        var flyway = Flyway.configure()
//                .dataSource(getDataSource())
//                .locations("db/migration")
//                .load();
//        flyway.migrate();
//    }
}
