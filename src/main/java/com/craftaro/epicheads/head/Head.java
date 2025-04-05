package com.craftaro.epicheads.head;

import com.songoda.core.utils.SkullItemCreator;
import com.songoda.core.utils.TextUtils;
import com.craftaro.epicheads.EpicHeads;
import com.craftaro.epicheads.settings.Settings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Head {
    private int id;
    private String name = null;
    private String url = null;
    private final boolean local;

    private Category category;

    public Head(String name, String url, Category category, boolean local) {
        this.name = name;
        this.url = url;
        this.category = category;
        this.local = local;
    }

    public Head(int id, String name, String url, Category category, boolean local) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
        this.local = local;
    }

    public Head(int id, boolean local) {
        this.id = id;
        this.local = local;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @deprecated Use {@link #setUrl(String)} instead.
     */
    @Deprecated
    public void setURL(String url) {
        setUrl(url);
    }

    /**
     * @deprecated Use {@link #getUrl()} instead.
     */
    @Deprecated
    public String getURL() {
        return getUrl();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        if (this.url == null) {
            return "d23eaefbd581159384274cdbbd576ced82eb72423f2ea887124f9ed33a6872c";
        }
        return this.url;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isLocal() {
        return this.local;
    }

    public ItemStack asItemStack() {
        return asItemStack(false, false);
    }

    public ItemStack asItemStack(boolean favorite) {
        return asItemStack(favorite, false);
    }

    public ItemStack asItemStack(boolean favorite, boolean free) {
        ItemStack skull = createSkullAndAutoDetectInput(getUrl());
        ItemMeta meta = skull.getItemMeta();
        meta.setDisplayName(getHeadItemName(favorite));
        meta.setLore(getHeadItemLore(free));

        skull.setItemMeta(meta);
        return skull;
    }

    public String getHeadItemName(boolean favorite) {
        return TextUtils.formatText((favorite ? "&6⭐ " : "") + "&9" + this.name);
    }

    public List<String> getHeadItemLore(boolean free) {
        EpicHeads plugin = EpicHeads.getInstance();
        double cost = Settings.HEAD_COST.getDouble();
        List<String> lore = new ArrayList<>();
        lore.add(plugin.getLocale().getMessage("general.head.id")
                .processPlaceholder("id", this.id).toText());
        if (!free) {
            String fcost = Settings.ECONOMY_PLUGIN.getString().equalsIgnoreCase("item")
                    ? cost + " " + Settings.ITEM_TOKEN_TYPE.getString()
                    : /* EconomyManager.formatEconomy(cost) */ String.valueOf(cost);  // FIXME: EconomyManager#formatEconomy etc only work in some languages (. vs ,) and only for the currency symbol $
            lore.add(plugin.getLocale().getMessage("general.head.cost")
                    .processPlaceholder("cost", fcost).toText());
        }
        return lore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Head head = (Head) o;
        return this.id == head.id && this.local == head.local;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.local);
    }

    @Override
    public String toString() {
        return "Head{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", URL='" + this.url + '\'' +
                ", local=" + this.local +
                ", category=" + this.category +
                '}';
    }

    private ItemStack createSkullAndAutoDetectInput(String urlThatIsNotInFactAnUrl) {
        if (urlThatIsNotInFactAnUrl.startsWith("http://") || urlThatIsNotInFactAnUrl.startsWith("https://")) {
            return SkullItemCreator.byTextureUrl(urlThatIsNotInFactAnUrl);
        }
        if (urlThatIsNotInFactAnUrl.matches("[A-Za-z0-9+/-]{100,}={0,3}")) {
            return SkullItemCreator.byTextureValue(urlThatIsNotInFactAnUrl);
        }
        return SkullItemCreator.byTextureUrlHash(urlThatIsNotInFactAnUrl);
    }
}
