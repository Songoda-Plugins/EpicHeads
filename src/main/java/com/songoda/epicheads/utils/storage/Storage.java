package com.songoda.epicheads.utils.storage;

import com.songoda.core.configuration.Config;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.players.EPlayer;

import java.util.List;

public abstract class Storage {
    protected final EpicHeads instance;
    protected final Config dataFile;

    public Storage(EpicHeads instance) {
        this.instance = instance;
        this.dataFile = (new Config(instance, "data.yml"))
                .setAutosave(true);
        this.dataFile.load();
    }

    public abstract boolean containsGroup(String group);

    public abstract List<StorageRow> getRowsByGroup(String group);

    public abstract void prepareSaveItem(String group, StorageItem... items);

    public void updateData(EpicHeads plugin) {
        // Save game data
        for (EPlayer player : this.instance.getPlayerManager().getPlayers()) {
            prepareSaveItem("players", new StorageItem("uuid", player.getUuid().toString()),
                    new StorageItem("favorites", player.getFavorites()));
        }

        for (Head head : this.instance.getHeadManager().getLocalHeads()) {
            prepareSaveItem("local", new StorageItem("url", head.getUrl()),
                    new StorageItem("name", head.getName()),
                    new StorageItem("id", head.getId()),
                    new StorageItem("category", head.getCategory().getName()));
        }

        for (Head head : this.instance.getHeadManager().getDisabledHeads()) {
            prepareSaveItem("disabled", new StorageItem("id", String.valueOf(head.getId())));
        }
    }

    public abstract void doSave();

    public abstract void save();

    public abstract void makeBackup();

    public abstract void closeConnection();

}
