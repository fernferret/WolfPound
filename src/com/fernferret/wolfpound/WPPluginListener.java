package com.fernferret.wolfpound;

import java.util.logging.Logger;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.fernferret.allpay.AllPay;


public class WPPluginListener extends ServerListener {
	private WolfPound plugin;
	public static final Logger log = Logger.getLogger("Minecraft");
	public WPPluginListener(WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		// Let AllPay handle all econ plugin loadings
		AllPay banker = new AllPay(plugin, WolfPound.logPrefix);
		plugin.bank = banker.getEconPlugin();
	}
}
