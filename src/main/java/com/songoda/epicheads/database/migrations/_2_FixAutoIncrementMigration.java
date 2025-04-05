package com.songoda.epicheads.database.migrations;

import com.songoda.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _2_FixAutoIncrementMigration extends DataMigration {
    public _2_FixAutoIncrementMigration() {
        super(2);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + tablePrefix + "local_heads MODIFY COLUMN id INTEGER AUTO_INCREMENT");
        }
    }
}
