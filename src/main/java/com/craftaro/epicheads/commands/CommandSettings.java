package com.craftaro.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.configuration.editor.PluginConfigGui;
import com.songoda.core.gui.GuiManager;
import com.craftaro.epicheads.EpicHeads;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSettings extends AbstractCommand {
    private final EpicHeads epicHeads;
    private final GuiManager guiManager;

    public CommandSettings(EpicHeads epicHeads, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "settings");
        this.epicHeads = epicHeads;
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        this.guiManager.showGUI((Player) sender, new PluginConfigGui(this.epicHeads));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.admin";
    }

    @Override
    public String getSyntax() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Edit EpicHeads Settings.";
    }
}
