package com.songoda.epicheads.database.migrations;

import com.songoda.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_InitialMigration extends DataMigration {
    public _1_InitialMigration() {
        super(1);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        // Create player profiles
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "players (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "favorites MEDIUMTEXT NOT NULL" +
                    ")");
        }

        // Create local heads table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "local_heads (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "category VARCHAR(48) NOT NULL, " +
                    "name VARCHAR(64) NOT NULL," +
                    "url VARCHAR(256) " +
                    ")");
        }

        // Create disabled heads table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "disabled_heads (" +
                    "id INTEGER PRIMARY KEY" +
                    ")");
        }
    }
}
