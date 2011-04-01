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
		if(WolfPound.usePermissions) {
			if(WolfPound.Permissions.has(p,"wolfpound.create") && event.getLine(0).equalsIgnoreCase("[WolfPound]")) {
				addToPoundList(event);
			}
			else {
				p.sendMessage("You don't have permission(wolfpound.create) to do this!");
			}
		} else {
			addToPoundList(event);
		}
			//TODO: Make wolves assigned to people
			//TODO: Color Signs
	}
	
	private void addToPoundList(SignChangeEvent event) {
		event.getPlayer().sendMessage("Setting up your Wolf Pound!");
		Double price;
		try {
			price = Double.parseDouble(event.getLine(1).replaceAll("\\D", ""));
		} catch (NumberFormatException e) {
			price = 0.0;
		}
		WolfPound.pounds.add(new Pound(event.getBlock().getWorld(), event.getBlock().getLocation(), event.getPlayer(), price));
	}
}
