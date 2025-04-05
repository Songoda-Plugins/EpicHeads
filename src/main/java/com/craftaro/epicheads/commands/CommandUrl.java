package com.craftaro.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.core.utils.ItemUtils;
import com.craftaro.epicheads.EpicHeads;
import com.songoda.third_party.com.cryptomorin.xseries.profiles.builder.XSkull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class CommandUrl extends AbstractCommand {
    private final EpicHeads plugin;

    public CommandUrl(EpicHeads plugin) {
        super(CommandType.PLAYER_ONLY, "url");
        this.plugin = plugin;
    }

    @Override
    protected AbstractCommand.ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        ItemStack item = CompatibleHand.MAIN_HAND.getItem(player);

        if (!item.hasItemMeta() || !(item.getItemMeta() instanceof SkullMeta)) {
            return ReturnType.FAILURE;
        }

        String encodedStr = XSkull.of(item).getProfileValue();
        if (encodedStr == null) {
            return ReturnType.FAILURE;
        }

        String url = ItemUtils.getDecodedTexture(encodedStr);

        this.plugin.getLocale().newMessage("http://textures.minecraft.net/texture/" + url).sendPrefixedMessage(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.url";
    }

    @Override
    public String getSyntax() {
        return "url";
    }

    @Override
    public String getDescription() {
        return "Gives you the texture url for the head you are holding.";
    }
}
