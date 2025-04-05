package com.craftaro.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.gui.GuiManager;
import com.craftaro.epicheads.EpicHeads;
import com.craftaro.epicheads.gui.GUIHeads;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSearch extends AbstractCommand {
    private final EpicHeads epicHeads;
    private final GuiManager guiManager;

    public CommandSearch(EpicHeads epicHeads, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "search");
        this.epicHeads = epicHeads;
        this.guiManager = guiManager;
    }

    @Override
    protected AbstractCommand.ReturnType runCommand(CommandSender sender, String... args) {
        GUIHeads.doSearch(this.epicHeads, null, this.guiManager, (Player) sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.search";
    }

    @Override
    public String getSyntax() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "Opens a gui displaying your search results.";
    }
}
