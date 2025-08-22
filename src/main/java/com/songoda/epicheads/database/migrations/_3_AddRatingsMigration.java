package com.songoda.epicheads.database.migrations;

import com.songoda.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _3_AddRatingsMigration extends DataMigration {
    public _3_AddRatingsMigration() {
        super(3);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        // Create head ratings table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "head_ratings (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "head_id INTEGER NOT NULL, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5), " +
                    "rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE KEY unique_rating (head_id, player_uuid)" +
                    ")");
        }

        // Create index for faster queries
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE INDEX IF NOT EXISTS idx_head_ratings_head_id ON " + tablePrefix + "head_ratings (head_id)");
        }
    }
}