package com.fernferret.wolfpound;

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
    private World w;

    public WPWorld(World w) {
        this.config = plugin.getConfig();

        // Initialize variables
        this.setPrice(this.config.getDouble("adopt.worlds." + w.getName() + ".price", 0.0));
        this.setCurrency(this.config.getInt("adopt.worlds." + w.getName() + ".type", -1));
        this.setAggro(this.config.getString("adopt.worlds." + w.getName() + ".aggro", "neutral"));
        this.setPrice(this.config.getDouble("adopt.worlds." + w.getName() + ".limit", 0.0));
        this.saveConfig();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.config.setProperty("adopt.worlds." + w.getName() + ".price", this.price);
        this.saveConfig();
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
        this.config.setProperty("adopt.worlds." + w.getName() + ".price", this.price);
        this.saveConfig();
    }

    public WolfAggro getAggro() {
        return aggro;
    }

    public void setAggro(WolfAggro aggro) {
        this.aggro = aggro;
        this.config.setProperty("adopt.worlds." + w.getName() + ".aggro", this.aggro.toString());
        this.saveConfig();
    }
    
    public void setAggro(String aggro) {
        this.aggro = WolfAggro.valueOf(aggro.toUpperCase());
        this.config.setProperty("adopt.worlds." + w.getName() + ".aggro", this.aggro.toString());
        this.saveConfig();
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        this.config.setProperty("adopt.worlds." + w.getName() + ".limit", this.limit);
        this.saveConfig();
    }

    private void saveConfig() {
        if (this.canSave) {
            this.config.save();
        }
    }
}
