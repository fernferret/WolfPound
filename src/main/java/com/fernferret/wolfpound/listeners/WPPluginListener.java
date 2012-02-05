package com.fernferret.wolfpound.listeners;

import com.fernferret.allpay.AllPay;
import com.fernferret.wolfpound.WolfPound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import java.util.Arrays;

public class WPPluginListener implements Listener {
    private WolfPound plugin;

    public WPPluginListener(WolfPound plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        // Let AllPay handle all econ plugin loadings, only go for econ plugins we support
        if (Arrays.asList(AllPay.getValidEconPlugins()).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.setBank(this.plugin.getBanker().loadEconPlugin());
        }
    }
}
