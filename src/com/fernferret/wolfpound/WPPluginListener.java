package com.fernferret.wolfpound;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class WPPluginListener extends ServerListener {
	private WolfPound plugin;
	public WPPluginListener(WolfPound plugin) {
		this.plugin = plugin;
	}
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		if(plugin)
		super.onPluginEnable(event);
	}
}
