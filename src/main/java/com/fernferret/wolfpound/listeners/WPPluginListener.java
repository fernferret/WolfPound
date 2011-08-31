package com.fernferret.wolfpound.listeners;

import java.util.Arrays;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.fernferret.allpay.AllPay;
import com.fernferret.wolfpound.WolfPound;

public class WPPluginListener extends ServerListener {
	private WolfPound plugin;
	
	public WPPluginListener(WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		// Let AllPay handle all econ plugin loadings, only go for econ plugins we support
		if (Arrays.asList(AllPay.validEconPlugins).contains(event.getPlugin().getDescription().getName())) {
            this.plugin.setBank(this.plugin.getBanker().loadEconPlugin());
        }
	}
}
