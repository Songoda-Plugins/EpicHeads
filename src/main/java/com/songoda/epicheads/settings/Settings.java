package com.songoda.epicheads.settings;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.configuration.ConfigSetting;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.epicheads.EpicHeads;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Settings {
    static final Config CONFIG = EpicHeads.getPlugin(EpicHeads.class).getCoreConfig();

    public static final ConfigSetting AUTOSAVE = new ConfigSetting(CONFIG, "Main.Auto Save Interval In Seconds", 15,
            "The amount of time in between saving to file.",
            "This is purely a safety function to prevent against unplanned crashes or",
            "restarts. With that said it is advised to keep this enabled.",
            "If however you enjoy living on the edge, feel free to turn it off.");

    public static final ConfigSetting FREE_IN_CREATIVE = new ConfigSetting(CONFIG, "Main.Heads Free In Creative Mode", false,
            "Enabling this will make it so that a player can get all heads",
            "for free as long as they are in the creative game mode.");

    public static final ConfigSetting DROP_MOB_HEADS = new ConfigSetting(CONFIG, "Main.Drop Mob Heads", true,
            "Should heads drop after a monster is killed?");

    public static final ConfigSetting DROP_PLAYER_HEADS = new ConfigSetting(CONFIG, "Main.Drop Player Heads", true,
            "Should a players drop their head on death?");

    public static final ConfigSetting DROP_CHANCE = new ConfigSetting(CONFIG, "Main.Head Drop Chance", "25%",
            "When a player or monster is killed what should be",
            "the chance that their head drops?");

    public static final ConfigSetting ECONOMY_PLUGIN = new ConfigSetting(CONFIG, "Economy.Economy", "Vault",
            "Which economy plugin should be used?");

    public static final ConfigSetting HEAD_COST = new ConfigSetting(CONFIG, "Economy.Head Cost", 24.99,
            "The cost the of the head. If you wan't to use PlayerPoints",
            "or item tokens you need to use whole numbers.");

    public static final ConfigSetting ITEM_TOKEN_TYPE = new ConfigSetting(CONFIG, "Economy.Item.Type", "PLAYER_HEAD",
            "Which item material type should be used?",
            "You can use any of the materials from the following link:",
            "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");

    public static final ConfigSetting ITEM_TOKEN_ID = new ConfigSetting(CONFIG, "Economy.Item.Head ID", 14395,
            "If a player head is used as the token which head ID should be used?",
            "This can be any head from the global database.");

    public static final ConfigSetting ITEM_TOKEN_NAME = new ConfigSetting(CONFIG, "Economy.Item.Name", "&6Player Head Token",
            "What should the token be named?");

    public static final ConfigSetting ITEM_TOKEN_LORE = new ConfigSetting(CONFIG, "Economy.Item.Lore", Arrays.asList("&8Use in /Heads!"),
            "What should the tokens lore be?");

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(CONFIG, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(CONFIG, "Interfaces.Glass Type 1", "GRAY_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(CONFIG, "Interfaces.Glass Type 2", "BLUE_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(CONFIG, "Interfaces.Glass Type 3", "LIGHT_BLUE_STAINED_GLASS_PANE");

    /**
     * In order to set dynamic economy comment correctly, this needs to be
     * called after EconomyManager load
     */
    public static void setupConfig() {
        CONFIG.load();
        CONFIG.setAutoremove(true).setAutosave(true);

        // convert glass pane settings
        int color;
        if ((color = GLASS_TYPE_1.getInt(-1)) != -1) {
            CONFIG.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            CONFIG.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            CONFIG.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneForColor(color).name());
        }

        CONFIG.setDefault("Economy.Economy",
                EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName());
        CONFIG.setComment("Economy.Economy", null,
                "Which economy plugin should be used?",
                "Supported plugins you have installed: \"" + EconomyManager.getManager().getRegisteredPlugins().stream().filter(p -> !p.equals("EpicHeads")).collect(Collectors.joining("\", \"")) + "\", \"Item\".");

        // convert economy settings
        if (CONFIG.getBoolean("Economy.Use Vault Economy") && EconomyManager.getManager().isEnabled("Vault")) {
            CONFIG.set("Economy.Economy", "Vault");
        } else if (CONFIG.getBoolean("Economy.Use Reserve Economy") && EconomyManager.getManager().isEnabled("Reserve")) {
            CONFIG.set("Economy.Economy", "Reserve");
        } else if (CONFIG.getBoolean("Economy.Use Player Points Economy") && EconomyManager.getManager().isEnabled("PlayerPoints")) {
            CONFIG.set("Economy.Economy", "PlayerPoints");
        } else if (CONFIG.getBoolean("Economy.Use Item Economy")) {
            CONFIG.set("Economy.Economy", "Item");
        }

        CONFIG.saveChanges();
    }
}
