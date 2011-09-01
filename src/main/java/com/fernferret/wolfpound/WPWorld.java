package com.fernferret.wolfpound;

import java.util.List;

import org.bukkit.World;
import org.bukkit.util.config.Configuration;

/**
 * Represents a world that contains values for WolfPound.
 * 
 * @author fernferret
 */

public class WPWorld {
    private WolfPound plugin;
    private double price;
    private int currency;
    private WolfAggro aggro;
    private int limit;
    private Configuration config;
    private boolean canSave = false;
    private String worldString = "adopt.worlds.";

    public WPWorld(String w) {
        this.config = plugin.getConfig();
        if(w == null) {
            worldString = "adopt.";
        } else {
            worldString += w + ".";
        }
        // Initialize variables
        this.setPrice(this.config.getDouble(worldString + "price", 0.0));
        this.setCurrency(this.config.getInt(worldString + "type", -1));
        this.setAggro(this.config.getString(worldString + "aggro", "neutral"));
        this.setPrice(this.config.getDouble(worldString + "limit", 0.0));
        this.saveConfig();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.config.setProperty(worldString + "price", this.price);
        this.saveConfig();
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
        this.config.setProperty(worldString + "price", this.price);
        this.saveConfig();
    }

    public WolfAggro getAggro() {
        return aggro;
    }

    public void setAggro(WolfAggro aggro) {
        this.aggro = aggro;
        this.config.setProperty(worldString + "aggro", this.aggro.toString());
        this.saveConfig();
    }
    
    public void setAggro(String aggro) {
        this.aggro = WolfAggro.valueOf(aggro.toUpperCase());
        this.config.setProperty(worldString + "aggro", this.aggro.toString());
        this.saveConfig();
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        this.config.setProperty(worldString + "limit", this.limit);
        this.saveConfig();
    }

    private void saveConfig() {
        if (this.canSave) {
            this.config.save();
        }
    }
}
