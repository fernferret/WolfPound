package com.fernferret.wolfpound;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.fernferret.allpay.AllPay;

public class WPPluginListener extends ServerListener {
	private WolfPound plugin;
	
	public WPPluginListener(WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		// Let AllPay handle all econ plugin loadings, only go for econ plugins we support
		if (event.getPlugin().getDescription().getName().equals("WolfPound")) {
			AllPay banker = new AllPay(plugin, WolfPound.logPrefix + " ");
			plugin.bank = banker.getEconPlugin();
		}
	}
}
