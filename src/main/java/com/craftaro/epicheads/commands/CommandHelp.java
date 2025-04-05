package com.craftaro.epicheads.commands;

import com.songoda.core.chat.AdventureUtils;
import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.third_party.net.kyori.adventure.text.Component;
import com.songoda.core.utils.TextUtils;
import com.craftaro.epicheads.EpicHeads;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandHelp extends AbstractCommand {
    private final EpicHeads plugin;

    public CommandHelp(EpicHeads plugin) {
        super(CommandType.CONSOLE_OK, "help");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        sender.sendMessage("");
        Component component = AdventureUtils.formatComponent(String.format("<color:#ff8080>&l%s &8» &7Version %s Created with <3 by <gradient:#ec4e74:#f4c009><b><o>Songoda</o></b></gradient>",
                this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion()));
        AdventureUtils.sendMessage(this.plugin, component, sender);
        sender.sendMessage("");
        sender.sendMessage(TextUtils.formatText("&7Welcome to EpicHeads! To get started try using the command /heads to access the heads panel."));
        sender.sendMessage("");
        sender.sendMessage(TextUtils.formatText("&6Commands:"));
        for (AbstractCommand command : this.plugin.getCommandManager().getAllCommands()) {
            if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.YELLOW + command.getSyntax() + ChatColor.GRAY + " - " + command.getDescription());
            }
        }
        sender.sendMessage("");

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays this page.";
    }
}
