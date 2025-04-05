package com.songoda.epicheads.database;

import com.songoda.core.database.DataManager;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.core.database.DatabaseType;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.players.EPlayer;
import com.songoda.third_party.org.jooq.Query;
import com.songoda.third_party.org.jooq.impl.DSL;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class DataHelper {

    private static DataManager dataManager;
    private static DatabaseConnector databaseConnector;

    public static void init(DataManager dataManager) {
        DataHelper.dataManager = dataManager;
        DataHelper.databaseConnector = dataManager.getDatabaseConnector();
    }

    private static String getTablePrefix() {
        return dataManager.getTablePrefix();
    }

    public static void updatePlayer(EPlayer ePlayer) {
        dataManager.getAsyncPool().submit(() -> {
            Gson gson = new Gson();
            try (Connection connection = databaseConnector.getConnection()) {
                String updatePlayer = "UPDATE " + getTablePrefix() + "players SET favorites = ? WHERE uuid = ?";
                try (PreparedStatement statement = connection.prepareStatement(updatePlayer)) {
                    statement.setString(1, gson.toJson(ePlayer.getFavorites()));
                    statement.setString(2, ePlayer.getUuid().toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void getPlayer(Player player, Consumer<EPlayer> callback) {
        Gson gson = new Gson();
        dataManager.getAsyncPool().submit(() -> {
            try (Connection connection = databaseConnector.getConnection()) {
                String sql;
                boolean isH2 = databaseConnector.getType().equals(DatabaseType.H2);

                if (!isH2) {
                    sql = "INSERT INTO " + getTablePrefix() + "players (uuid, favorites) VALUES (?, ?) ON DUPLICATE KEY UPDATE favorites = ?";
                } else {
                    sql = "MERGE INTO " + getTablePrefix() + "players (uuid, favorites) KEY(uuid) VALUES (?, ?)";
                }

                String selectPlayers = "SELECT * FROM " + getTablePrefix() + "players WHERE uuid = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql);
                     PreparedStatement selectStatement = connection.prepareStatement(selectPlayers)) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setString(2, gson.toJson(new ArrayList<>()));
                    if (!isH2) {
                        statement.setString(3, gson.toJson(new ArrayList<>()));
                    }
                    statement.execute();

                    selectStatement.setString(1, player.getUniqueId().toString());
                    ResultSet result = selectStatement.executeQuery();
                    if (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        List<String> favorites = gson.fromJson(result.getString("favorites"), new TypeToken<List<String>>() {
                        }.getType());

                        EPlayer ePlayer = new EPlayer(uuid, favorites);
                        callback.accept(ePlayer);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void migratePlayers(List<EPlayer> players) {
        Gson gson = new Gson();
        dataManager.getAsyncPool().submit(() -> {
            try (Connection connection = databaseConnector.getConnection()) {
                String insertPlayer = "REPLACE INTO " + getTablePrefix() + "players (uuid, favorites) VALUES (?, ?)";
                try (PreparedStatement insert = connection.prepareStatement(insertPlayer)) {
                    for (EPlayer player : players) {
                        insert.setString(1, player.getUuid().toString());
                        insert.setString(2, gson.toJson(player.getFavorites()));
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void createLocalHead(Head head) {
        dataManager.getAsyncPool().submit(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                dslContext.insertInto(DSL.table(getTablePrefix() + "local_heads"),
                        DSL.field("category"),
                        DSL.field("name"),
                        DSL.field("url"))
                        .values(head.getCategory().getName(), head.getName(), head.getUrl())
                        .execute();
                head.setId(dslContext.lastID().intValue());
            });
        });
    }

    public static void getLocalHeads(Consumer<List<Head>> callback) {
        dataManager.getAsyncPool().submit(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                List<Head> heads = new ArrayList<>();
                dslContext.select()
                        .from(DSL.table(getTablePrefix() + "local_heads"))
                        .orderBy(DSL.field("id asc"))
                        .fetch()
                        .forEach(record -> {
                            int id = record.get(DSL.field("id", Integer.class));
                            String categoryString = record.get(DSL.field("category", String.class));
                            String name = record.get(DSL.field("name", String.class));
                            String url = record.get(DSL.field("url", String.class));

                            Head head = new Head(id,
                                    name,
                                    url,
                                    EpicHeads.getInstance().getHeadManager().getOrCreateCategoryByName(categoryString),
                                    true);

                            heads.add(head);
                        });
                callback.accept(heads);
            });
        });
    }

    public static void updateLocalHead(Head head) {
        dataManager.getAsyncPool().submit(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                dslContext.update(DSL.table(getTablePrefix() + "local_heads"))
                        .set(DSL.field("name"), head.getName())
                        .set(DSL.field("url"), head.getUrl())
                        .where(DSL.field("id").eq(head.getId()))
                        .execute();
            });
        });
    }

    public static void disableHead(Head head) {
        dataManager.getAsyncPool().submit(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                dslContext.insertInto(DSL.table(getTablePrefix() + "disabled_heads"),
                        DSL.field("id"))
                        .values(head.getId())
                        .execute();
            });
        });
    }

    public static void migrateDisabledHead(List<Integer> heads) {
        dataManager.getAsyncPool().submit(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                dslContext.insertInto(DSL.table(getTablePrefix() + "disabled_heads"),
                        DSL.field("id"))
                        .values(heads)
                        .execute();
            });
        });
    }

    public static void getDisabledHeads(Consumer<List<Integer>> callback) {
        dataManager.getAsyncPool().submit(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                List<Integer> heads = new ArrayList<>();
                dslContext.select()
                        .from(DSL.table(getTablePrefix() + "disabled_heads"))
                        .fetch()
                        .forEach(record -> {
                            int id = record.get(DSL.field("id", Integer.class));
                            heads.add(id);
                        });
                callback.accept(heads);
            });
        });
    }

    public static void saveAllPlayers() {
        Gson gson = new Gson();
        dataManager.getAsyncPool().submit(() -> {
            dataManager.getDatabaseConnector().connectDSL(dslContext -> {
                List<Query> queries = new ArrayList<>();
                for (EPlayer player : EpicHeads.getInstance().getPlayerManager().getPlayers()) {
                    queries.add(dslContext.update(DSL.table(getTablePrefix() + "players"))
                            .set(DSL.field("favorites"), gson.toJson(player.getFavorites()))
                            .where(DSL.field("uuid").eq(player.getUuid().toString())));
                }
            });
        });
    }

    public static boolean isInitialized() {
        return dataManager != null;
    }
}
