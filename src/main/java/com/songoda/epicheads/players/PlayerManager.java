package com.songoda.epicheads.players;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private static final Map<UUID, EPlayer> REGISTERED_HEADS = new HashMap<>();

    public EPlayer getPlayer(UUID uuid) {
        return REGISTERED_HEADS.computeIfAbsent(uuid, u -> new EPlayer(uuid));
    }

    public EPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public EPlayer addPlayer(EPlayer player) {
        REGISTERED_HEADS.put(player.getUuid(), player);
        return player;
    }

    public List<EPlayer> getPlayers() {
        return new ArrayList<>(REGISTERED_HEADS.values());
    }
}
