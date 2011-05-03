package com.fernferret.wolfpound;

import java.util.logging.Logger;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class WPPluginListener extends ServerListener {
	private WolfPound plugin;
	public static final Logger log = Logger.getLogger("Minecraft");
	public WPPluginListener(WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		// Let AllPay handle all econ plugin loadings
		plugin.allPay.loadEconPlugin();
		plugin.bank = plugin.allPay.getEconPlugin();
	}
}
