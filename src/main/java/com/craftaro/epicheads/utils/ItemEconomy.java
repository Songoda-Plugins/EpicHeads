package com.craftaro.epicheads.utils;

import com.songoda.core.hooks.economies.Economy;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.third_party.com.cryptomorin.xseries.profiles.builder.XSkull;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemEconomy extends Economy {
    public static boolean isItem(ItemStack itemStack) {
        if (itemStack == null || XMaterial.AIR.isSimilar(itemStack)) {
            return false;
        }

        if (XMaterial.PLAYER_HEAD.isSimilar(itemStack)) {
            return Objects.equals(XSkull.of(itemStack).getProfileValue(), XSkull.of(Methods.createToken(1)).getProfileValue());
        }
        return itemStack.isSimilar(Methods.createToken(1));
    }

    private int convertAmount(double amount) {
        return (int) Math.ceil(amount);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        int amount = 0;
        for (ItemStack item : player.getPlayer().getInventory().getContents()) {
            if (!isItem(item)) {
                continue;
            }
            amount += item.getAmount();
        }
        return amount;
    }

    @Override
    public boolean hasBalance(OfflinePlayer player, double cost) {
        int amount = convertAmount(cost);
        for (ItemStack item : player.getPlayer().getInventory().getContents()) {
            if (!isItem(item)) {
                continue;
            }
            if (amount <= item.getAmount()) {
                return true;
            }
            amount -= item.getAmount();
        }
        return false;
    }

    @Override
    public boolean withdrawBalance(OfflinePlayer player, double cost) {
        int amount = convertAmount(cost);
        ItemStack[] contents = player.getPlayer().getInventory().getContents();
        for (int index = 0; index < contents.length; ++index) {
            ItemStack item = contents[index];
            if (!isItem(item)) {
                continue;
            }
            if (amount >= item.getAmount()) {
                amount -= item.getAmount();
                contents[index] = null;
            } else {
                item.setAmount(item.getAmount() - amount);
                amount = 0;
            }
            if (amount == 0) {
                break;
            }
        }
        if (amount != 0) {
            return false;
        }
        player.getPlayer().getInventory().setContents(contents);

        return true;
    }

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
