package com.songoda.epicheads.gui;

import com.songoda.core.chat.AdventureUtils;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.input.ChatPrompt;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.database.DataHelper;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.players.EPlayer;
import com.songoda.epicheads.settings.Settings;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUIHeads extends Gui {
    private final EpicHeads plugin;
    private final Player player;

    private List<Head> heads;

    private String query;
    private final QueryTypes type;

    public GUIHeads(EpicHeads plugin, Player player, String query, QueryTypes type, List<Head> heads) {
        this.plugin = plugin;
        this.player = player;
        this.query = query;
        this.type = type;

        List<String> favorites = plugin.getPlayerManager().getPlayer(player).getFavorites();
        this.heads = heads.stream()
                .sorted(Comparator.comparingInt(head -> (favorites.contains(head.getUrl()) ? 0 : 1)))
                .collect(Collectors.toList());

        this.setDefaultItem(null);
        this.setRows(6);
        this.setOnPage((event) -> showPage());
        showPage();
    }

    private void updateTitle() {
        int numHeads = this.heads.size();
        if (numHeads == 0) {
            this.plugin.getLocale().getMessage("general.search.nonefound").sendPrefixedMessage(this.player);
            return;
        }
        Category category = this.heads.get(0).getCategory();

        String name = null;

        switch (this.type) {
            case SEARCH:
                name = this.plugin.getLocale().getMessage("general.word.query").toText() + ": " + this.query;
                break;
            case CATEGORY:
                name = category.getName();
                break;
            case FAVORITES:
                name = this.plugin.getLocale().getMessage("general.word.favorites").toText();
                break;
        }

        this.pages = (int) Math.ceil(numHeads / 45.0);

        this.setTitle(name + " (" + numHeads + ")");
    }

    private void showPage() {
        if (!isOpen())
            updateTitle();
        // Sort heads by rating (highest first), then by favorites
        List<Head> sortedHeads = this.heads.stream()
                .sorted(Comparator.<Head>comparingDouble(head -> head.getAverageRating()).reversed())
                .collect(Collectors.toList());
        
        List<Head> pageHeads = sortedHeads.stream().skip((this.page - 1) * (this.rows - 1) * 9).limit((this.rows - 1) * 9)
                .collect(Collectors.toList());

        if (this.page - 3 >= 1) {
            setButton(0, GuiUtils.createButtonItem(XMaterial.ARROW, this.page - 3,
                            ChatColor.RED.toString() + this.plugin.getLocale().getMessage("general.word.page").toText() + " " + (this.page - 3)),
                    (event) -> changePage(-3));
        } else {
            clearActions(0);
            setItem(0, null);
        }

        if (this.page - 2 >= 1) {
            setButton(1, GuiUtils.createButtonItem(XMaterial.ARROW, this.page - 2,
                            ChatColor.RED.toString() + this.plugin.getLocale().getMessage("general.word.page").toText() + " " + (this.page - 2)),
                    (event) -> changePage(-2));
        } else {
            clearActions(1);
            setItem(1, null);
        }

        if (this.page > 1) {
            setButton(2, GuiUtils.createButtonItem(XMaterial.ARROW, this.page - 1,
                            ChatColor.RED.toString() + this.plugin.getLocale().getMessage("general.word.page").toText() + " " + (this.page - 1)),
                    (event) -> changePage(-1));
        } else {
            clearActions(2);
            setItem(2, null);
        }

        setButton(3, GuiUtils.createButtonItem(XMaterial.COMPASS,
                        this.plugin.getLocale().getMessage("gui.heads.search").getMessage()),
                (event) -> doSearch(this.plugin, this, this.guiManager, event.player));

        setButton(4, GuiUtils.createButtonItem(XMaterial.MAP, this.page,
                this.plugin.getLocale().getMessage("gui.heads.categories").toText()), (event) -> this.guiManager.showGUI(this.player, new GUIOverview(event.player)));

        if (pageHeads.size() > 1) {
            setButton(5, GuiUtils.createButtonItem(XMaterial.COMPASS,
                            this.plugin.getLocale().getMessage("gui.heads.refine").getMessage()),
                    (event) -> {
                        exit();
                        ChatPrompt.showPrompt(this.plugin, event.player, AdventureUtils.toLegacy(this.plugin.getLocale().getMessage("general.search.refine").getPrefixedMessage()), promptEvent -> {
                            this.page = 1;
                            this.heads = this.heads.stream().filter(head -> head.getName().toLowerCase()
                                    .contains(promptEvent.getMessage().toLowerCase())).collect(Collectors.toList());
                            if (this.query == null) {
                                this.query = promptEvent.getMessage();
                            } else {
                                this.query += ", " + promptEvent.getMessage();
                            }
                        }).setOnClose(() -> {
                            showPage();
                            this.guiManager.showGUI(event.player, this);
                        }).setOnCancel(() -> {
                            this.plugin.getLocale().getMessage("general.search.canceled").sendPrefixedMessage(event.player);
                        });
                    });
        }

        if (this.page + 1 <= this.pages) {
            setButton(6, GuiUtils.createButtonItem(XMaterial.ARROW, this.page + 1,
                            ChatColor.RED.toString() + this.plugin.getLocale().getMessage("general.word.page").toText() + " " + (this.page + 1)),
                    (event) -> changePage(+1));
        } else {
            clearActions(6);
            setItem(6, null);
        }

        if (this.page + 2 <= this.pages) {
            setButton(7, GuiUtils.createButtonItem(XMaterial.ARROW, this.page + 2,
                            ChatColor.RED.toString() + this.plugin.getLocale().getMessage("general.word.page").toText() + " " + (this.page + 2)),
                    (event) -> changePage(+2));
        } else {
            clearActions(7);
            setItem(7, null);
        }

        if (this.page + 3 <= this.pages) {
            setButton(8, GuiUtils.createButtonItem(XMaterial.ARROW, this.page + 3,
                            ChatColor.RED.toString() + this.plugin.getLocale().getMessage("general.word.page").toText() + " " + (this.page + 3)),
                    (event) -> changePage(+3));
        } else {
            clearActions(8);
            setItem(8, null);
        }

        List<String> favorites = this.plugin.getPlayerManager().getPlayer(this.player).getFavorites();

        double cost = Settings.HEAD_COST.getDouble();
        boolean free = this.player.hasPermission("epicheads.bypasscost")
                || (Settings.FREE_IN_CREATIVE.getBoolean() && this.player.getGameMode() == GameMode.CREATIVE);
        int i = 0;
        for (; i < pageHeads.size(); i++) {
            Head head = pageHeads.get(i);

            if (head.getName() == null) {
                continue;
            }

            ItemStack item = head.asItemStack(favorites.contains(head.getUrl()), free);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().getLore();
            lore.add("");
            lore.add(this.plugin.getLocale().getMessage("gui.heads.leftclick").toText());
            lore.add(this.plugin.getLocale().getMessage("gui.heads.rightclick").toText());
            lore.add(this.plugin.getLocale().getMessage("gui.heads.delete").toText());
            meta.setLore(lore);
            item.setItemMeta(meta);

            setButton(i + 9, item, (event) -> {
                if (event.clickType == ClickType.MIDDLE && this.player.hasPermission("epicheads.delete")) {
                    this.plugin.getHeadManager().disableHead(head);
                    DataHelper.disableHead(head);
                    this.heads.remove(head);
                    showPage();
                    return;
                } else if (event.clickType == ClickType.SHIFT_LEFT || event.clickType == ClickType.SHIFT_RIGHT) {
                    EPlayer ePlayer = this.plugin.getPlayerManager().getPlayer(this.player);
                    boolean isFav = ePlayer.getFavorites().contains(head.getUrl());
                    if (isFav) {
                        ePlayer.removeFavorite(head.getUrl());
                    } else {
                        ePlayer.addFavorite(head.getUrl());
                    }
                    showPage();
                    return;
                } else if (event.clickType == ClickType.RIGHT) {
                    // Right-click to rate head
                    if (!event.player.hasPermission("epicheads.rate")) {
                        this.plugin.getLocale().getMessage("event.rating.nopermission").sendPrefixedMessage(event.player);
                        return;
                    }
                    exit();
                    ChatPrompt.showPrompt(this.plugin, event.player, 
                        this.plugin.getLocale().getMessage("event.rating.prompt").toText(), 
                        promptEvent -> {
                            try {
                                int rating = Integer.parseInt(promptEvent.getMessage().trim());
                                if (rating >= 1 && rating <= 5) {
                                    DataHelper.addHeadRating(head.getId(), event.player.getUniqueId(), rating);
                                    DataHelper.updateHeadRatingStats(head);
                                    String plural = rating != 1 ? "s" : "";
                                    this.plugin.getLocale().getMessage("event.rating.success")
                                        .processPlaceholder("head", head.getName())
                                        .processPlaceholder("rating", String.valueOf(rating))
                                        .processPlaceholder("plural", plural)
                                        .sendPrefixedMessage(event.player);
                                } else {
                                    this.plugin.getLocale().getMessage("event.rating.invalid").sendPrefixedMessage(event.player);
                                }
                            } catch (NumberFormatException e) {
                                this.plugin.getLocale().getMessage("event.rating.invalidnumber").sendPrefixedMessage(event.player);
                            }
                        }).setOnClose(() -> {
                            showPage();
                            this.guiManager.showGUI(event.player, this);
                        }).setOnCancel(() -> {
                            this.plugin.getLocale().getMessage("event.rating.canceled").sendPrefixedMessage(event.player);
                            showPage();
                            this.guiManager.showGUI(event.player, this);
                        });
                    return;
                }
                if (!free) {
                    if (EconomyManager.isEnabled()) {
                        if (EconomyManager.hasBalance(this.player, cost)) {
                            EconomyManager.withdrawBalance(this.player, cost);
                        } else {
                            this.player.sendMessage(this.plugin.getLocale().getMessage("event.buyhead.cannotafford").toText());
                            return;
                        }
                    } else {
                        this.player.sendMessage("Economy plugin not setup correctly...");
                        return;
                    }
                }

                ItemStack headItem = item.clone();
                meta.setLore(new ArrayList<>());
                headItem.setItemMeta(meta);

                this.player.getInventory().addItem(headItem);
            });
        }
        if (this.inventory != null) {
            i += 9;
            for (; i < this.inventory.getSize(); ++i) {
                clearActions(i);
                setItem(i, null);
            }
        }
    }

    public static void doSearch(EpicHeads plugin, Gui activeGui, GuiManager guiManager, Player player) {
        if (activeGui != null) {
            activeGui.exit();
        }
        ChatPrompt.showPrompt(plugin, player, AdventureUtils.toLegacy(plugin.getLocale().getMessage("general.search.global").getPrefixedMessage()), response -> {
            List<Head> searchHeads = plugin.getHeadManager().getHeads().stream()
                    .filter(head -> head.getName().toLowerCase().contains(response.getMessage().toLowerCase()))
                    .filter(head -> player.hasPermission("epicheads.category." + head.getCategory().getName().replace(" ", "_")))
                    .collect(Collectors.toList());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> guiManager.showGUI(player, new GUIHeads(plugin, player, response.getMessage(), QueryTypes.SEARCH, searchHeads)), 0);
        }).setOnCancel(() -> {
            plugin.getLocale().getMessage("general.search.canceled").sendPrefixedMessage(player);
        });
    }

    public enum QueryTypes {
        SEARCH, CATEGORY, FAVORITES
    }
}
