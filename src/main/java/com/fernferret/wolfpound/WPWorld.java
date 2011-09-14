package com.fernferret.wolfpound;

import org.bukkit.util.config.Configuration;

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
    private Configuration config;
    private boolean canSave = false;
    private String worldString = "adopt.worlds.";

    public WPWorld(String w, Configuration config) {
        this.config = config;
        if (w == null) {
            worldString = "adopt.";
        } else {
            worldString += w + ".";
        }
        // Initialize variables
        this.setPrice(this.config.getDouble(worldString + "price", 0.0));
        this.setCurrency(this.config.getInt(worldString + "type", -1));
        this.setAggro(this.config.getString(worldString + "aggro", "neutral"));
        this.setLimit(this.config.getInt(worldString + "limit", 1));
        this.canSave = true;
        this.saveConfig();
    }

    public double getPrice() {
        return price;
    }

    public boolean setPrice(double price) {
        if(price < 0) {
            return false;
        }
        this.price = price;
        this.config.setProperty(worldString + "price", this.price);
        this.saveConfig();
        return true;
    }

    public int getCurrency() {
        return currency;
    }

    public boolean setCurrency(int currency) {
        if(currency < -1) {
            return false;
        }
        this.currency = currency;
        this.config.setProperty(worldString + "type", this.price);
        this.saveConfig();
        return true;
    }

    public WolfAggro getAggro() {
        return aggro;
    }

    public boolean setAggro(WolfAggro aggro) {
        this.aggro = aggro;
        this.config.setProperty(worldString + "aggro", this.aggro.toString());
        this.saveConfig();
        return true;
    }

    public boolean setAggro(String aggro) {
        try {
            this.aggro = WolfAggro.valueOf(aggro.toUpperCase());
        } catch (Exception e) {
            return false;
        }
        this.config.setProperty(worldString + "aggro", this.aggro.toString());
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
        this.config.setProperty(worldString + "limit", this.limit);
        this.saveConfig();
        return true;
    }

    private void saveConfig() {
        if (this.canSave) {
            this.config.save();
        }
    }

    public void removeFromConfig() {
        this.config.removeProperty(worldString);
    }
}
