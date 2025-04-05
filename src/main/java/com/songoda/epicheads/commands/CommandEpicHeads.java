package com.songoda.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.songoda.epicheads.gui.GUIOverview;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandEpicHeads extends AbstractCommand {
    private final GuiManager guiManager;

    public CommandEpicHeads(GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "EpicHeads");
        this.guiManager = guiManager;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        this.guiManager.showGUI((Player) sender, new GUIOverview((Player) sender));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.menu";
    }

    @Override
    public String getSyntax() {
        return "/heads";
    }

    @Override
    public String getDescription() {
        return "Displays heads overview.";
    }
}
