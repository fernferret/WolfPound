package com.fernferret.wolfpound.listeners;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

import com.fernferret.wolfpound.WolfPound;

public class WPWorldListener extends WorldListener {
    private WolfPound plugin;
    public WPWorldListener(WolfPound plugin) {
        this.plugin = plugin;
    }
    @Override
    public void onWorldLoad(WorldLoadEvent event) {
        super.onWorldLoad(event);
    }
}
