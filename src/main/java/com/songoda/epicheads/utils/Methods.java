package com.songoda.epicheads.utils;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.utils.TextUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.settings.Settings;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Methods {
    public static ItemStack createToken(int amount) {
        ItemStack itemStack = CompatibleMaterial.getMaterial(Settings.ITEM_TOKEN_TYPE.getString()).get().parseItem();

        if (XMaterial.PLAYER_HEAD.isSimilar(itemStack)) {
            itemStack = EpicHeads.getInstance()
                    .getHeadManager()
                    .getHeads()
                    .stream()
                    .filter(head -> head.getId() == Settings.ITEM_TOKEN_ID.getInt())
                    .findFirst()
                    .get()
                    .asItemStack();
        }
        itemStack.setAmount(amount);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(TextUtils.formatText(Settings.ITEM_TOKEN_NAME.getString()));
        List<String> lore = new ArrayList<>();
        for (String line : Settings.ITEM_TOKEN_LORE.getStringList()) {
            if (!line.isEmpty()) {
                lore.add(TextUtils.formatText(line));
            }
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
