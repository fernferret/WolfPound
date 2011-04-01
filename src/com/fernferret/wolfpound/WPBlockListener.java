package com.fernferret.wolfpound;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

public class WPBlockListener extends BlockListener {
	private final WolfPound plugin;
	public WPBlockListener(final WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onSignChange(SignChangeEvent event) {
		Player p = event.getPlayer();
		if(event.getLine(0).equalsIgnoreCase("[WolfPound]")) {
			p.sendMessage("Setting up your Wolf Pound!");
			plugin.spawnWolf(p);
			
			//TODO: Make wolves assigned to people
			//TODO: Color Signs
		}
		
	}
}
