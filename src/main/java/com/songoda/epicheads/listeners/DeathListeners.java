package com.songoda.epicheads.listeners;

import com.songoda.core.nms.Nms;
import com.songoda.core.utils.HeadType;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.settings.Settings;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Optional;

public class DeathListeners implements Listener {
    private final EpicHeads plugin;

    public DeathListeners(EpicHeads plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        double dropChance = Double.parseDouble(Settings.DROP_CHANCE.getString().replace("%", ""));
        double rand = Math.random() * 100;
        if (rand - dropChance < 0 || dropChance == 100) {

            ItemStack itemNew = null;
            if (event.getEntity() instanceof Player) {
                if (!Settings.DROP_PLAYER_HEADS.getBoolean()) {
                    return;
                }

                String encodedStr = Nms.getImplementations().getPlayer().getProfile((Player) event.getEntity()).getTextureValue();
                if (encodedStr == null) {
                    itemNew = XMaterial.PLAYER_HEAD.parseItem();

                    ItemMeta meta = itemNew.getItemMeta();
                    meta.setDisplayName(TextUtils.formatText("&9" + ((Player) event.getEntity()).getDisplayName()));
                    itemNew.setItemMeta(meta);
                } else {
                    String url = ItemUtils.getDecodedTexture(encodedStr);

                    Optional<Head> optional = this.plugin.getHeadManager().getHeads().stream()
                            .filter(head -> url.equals(head.getUrl())).findFirst();

                    if (optional.isPresent()) {
                        itemNew = optional.get().asItemStack();
                    }
                }
            } else {
                if (!Settings.DROP_MOB_HEADS.getBoolean() || event.getEntity() instanceof ArmorStand) {
                    return;
                }

                Head head = new Head(-1, TextUtils.formatText(event.getEntity().getType().name().toLowerCase()
                        .replace("_", " "), true),
                        HeadType.valueOf(event.getEntity().getType().name()).getUrl(),
                        null, true);
                itemNew = head.asItemStack();
            }
            if (itemNew == null) {
                return;
            }

            ItemMeta meta = itemNew.getItemMeta();
            meta.setLore(new ArrayList<>());
            itemNew.setItemMeta(meta);

            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemNew);
        }
    }
}
