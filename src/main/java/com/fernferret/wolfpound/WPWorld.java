package com.fernferret.wolfpound;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Represents a world that contains values for WolfPound.
 *
 * @author fernferret
 */

public class WPWorld {
    private double price;
    private int currency;
    private WolfAggro aggro;
    private int limit;
    private FileConfiguration config;
    private ConfigurationSection configSection;
    private boolean canSave = false;
    private WolfPound plugin;
    private String worldString;

    public WPWorld(String w, FileConfiguration config, WolfPound plugin) {
        this.worldString = w;
        this.config = config;
        this.plugin = plugin;
        if (w == null) {
            this.configSection = config.getConfigurationSection("adopt");
        } else {
            this.configSection = config.getConfigurationSection(w);
            if (this.configSection == null) {
                this.configSection = config.createSection(w);
            }
        }
        // Initialize variables
        this.setPrice(this.configSection.getDouble("price", 0.0));
        this.setCurrency(this.configSection.getInt("type", -1));
        this.setAggro(this.configSection.getString("aggro", "neutral"));
        this.setLimit(this.configSection.getInt("limit", 1));
        this.canSave = true;
        this.saveConfig();
    }

    public double getPrice() {
        return price;
    }

    public boolean setPrice(double price) {
        if (price < 0) {
            return false;
        }
        this.price = price;
        this.configSection.set("price", this.price);
        this.saveConfig();
        return true;
    }

    public int getCurrency() {
        return currency;
    }

    public boolean setCurrency(int currency) {
        if (currency < -1) {
            return false;
        }
        this.currency = currency;
        this.configSection.set("type", this.price);
        this.saveConfig();
        return true;
    }

    public WolfAggro getAggro() {
        return aggro;
    }

    public boolean setAggro(WolfAggro aggro) {
        this.aggro = aggro;
        this.configSection.set("aggro", this.aggro.toString());
        this.saveConfig();
        return true;
    }

    public boolean setAggro(String aggro) {
        try {
            this.aggro = WolfAggro.valueOf(aggro.toUpperCase());
        } catch (Exception e) {
            return false;
        }
        this.configSection.set("aggro", this.aggro.toString());
        this.saveConfig();
        return true;
    }

    public int getLimit() {
        return limit;
    }

    public boolean setLimit(int limit) {
        if (limit < 0) {
            return false;
        }
        this.limit = limit;
        this.configSection.set("limit", this.limit);
        this.saveConfig();
        return true;
    }

    private void saveConfig() {
        this.plugin.saveConfig();
    }

    public void removeFromConfig() {
        this.config.set(this.worldString, null);
    }
}
