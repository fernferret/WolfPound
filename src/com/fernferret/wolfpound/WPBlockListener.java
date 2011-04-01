package com.fernferret.wolfpound;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
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
		boolean createPermissions = plugin.hasPermission(p, "wolfpound.create");
		
		if (event.getLine(0).equalsIgnoreCase("[WolfPound]")) {
			if (createPermissions) {
				addToPoundList(event);
			} else {
				event.setCancelled(true);
			}
		}
		// TODO: Make wolves assigned to people
		// TODO: Color Signs
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		// If the block we're breaking is in our hash
		if (WolfPound.pounds.get(event.getBlock().getLocation()) != null) {
			if (!plugin.hasPermission(event.getPlayer(), "wolfpound.create")) {
				event.setCancelled(true);
			} else {
				if (WolfPound.pounds.remove(event.getBlock().getLocation()) != null) {
				}
			}
		}
	}
	
	private void addToPoundList(SignChangeEvent event) {
		event.getPlayer().sendMessage("Setting up your Wolf Pound!");
		Double price;
		try {
			if (event.getLine(1) != null) {
				price = Double.parseDouble(event.getLine(1).replaceAll("\\D", ""));
				WolfPound.pounds.put(event.getBlock().getLocation(), new Pound(event.getBlock().getLocation(), event.getPlayer(), price));
			}
		} catch (NumberFormatException e) {
			WolfPound.pounds.put(event.getBlock().getLocation(), new Pound(event.getBlock().getLocation(), event.getPlayer()));
			
		}
		event.getPlayer().sendMessage("Number of Pounds: " + WolfPound.pounds.size() + ", Current: (" + event.getBlock().getLocation() +  ")");
		
	}
}
