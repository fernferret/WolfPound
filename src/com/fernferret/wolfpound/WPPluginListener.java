package com.fernferret.wolfpound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.fernferret.allpay.AllPay;

public class WPPluginListener extends ServerListener {
	private WolfPound plugin;
	String elements[] = { "iConomy", "BOSEconomy", "Essentials", "RealBank" };
	HashSet<String> set = new HashSet<String>(Arrays.asList(elements));
	public static final Logger log = Logger.getLogger("Minecraft");
	
	public WPPluginListener(WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		// Let AllPay handle all econ plugin loadings, only go for econ plugins we support
		
		if (event.getPlugin().getDescription().getName().equals("WolfPound")) {
			AllPay banker = new AllPay(plugin, WolfPound.logPrefix + " ");
			plugin.bank = banker.getEconPlugin();
		} else {
			log.warning("ALLPAY - Did not find an econ plugin this time: " + event.getPlugin().getDescription().getName());
		}
	}
}
