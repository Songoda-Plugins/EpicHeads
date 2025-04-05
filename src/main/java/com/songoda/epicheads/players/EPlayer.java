package com.songoda.epicheads.players;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EPlayer {

    private final UUID uuid;

    private List<String> favorites = new ArrayList<>();

    public EPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public EPlayer(UUID uuid, List<String> favorites) {
        this.uuid = uuid;
        if (favorites != null)
            this.favorites = favorites;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<String> getFavorites() {
        return new ArrayList<>(favorites);
    }

    public List<Head> getFavoritesAsHeads() {
        return EpicHeads.getInstance().getHeadManager().getHeads().stream()
                .filter(head -> favorites.contains(head.getUrl())).collect(Collectors.toList());
    }

    public void addFavorite(String url) {
        favorites.add(url);
    }

    public void removeFavorite(String url) {
        favorites.remove(url);
    }

}
