package com.fernferret.wolfpound.commands;

import org.bukkit.command.CommandExecutor;

import com.fernferret.wolfpound.WolfPound;

public abstract class WolfPoundCommand implements CommandExecutor{
	private WolfPound plugin;
	protected void setPlugin(WolfPound plugin) {
		this.plugin = plugin;
	}
	protected WolfPound getPlugin() {
		return this.plugin;
	}
}
