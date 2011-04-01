package com.fernferret.wolfpound;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

public class WPBlockListener extends BlockListener {
	private final WolfPound plugin;
	
	public WPBlockListener(final WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onSignChange(SignChangeEvent event) {
		Player p = event.getPlayer();
		Location l = event.getBlock().getLocation();
		boolean createPermissions = plugin.hasPermission(p, "wolfpound.create");
		plugin.log.info("You are messing with: " + l);
		plugin.log.info("You are messing with: " + event.getBlock().getTypeId());
		plugin.log.info("You are messing with: " + event.getBlock().getType());
		
		if (event.getLine(0).equalsIgnoreCase("[WolfPound]")) {
			if (createPermissions) {
				
				event.setLine(0, "¤1[WolfPound]");
				addToPoundList(event);
			} else {
				event.setLine(0, "¤4[WolfPound]");
				event.setCancelled(true);
			}
		}
		// TODO: Make wolves assigned to people
		// TODO: Color Signs
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST) {
			Sign sign = new CraftSign(event.getBlock());
			// Don't let the user make this an auth'd sign
			if (sign.getLine(0).matches("¤1\\[[a-zA-Z]+\\]")) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Location l = event.getBlock().getLocation();
		p.sendMessage("You are messing with: " + l);
		// If the block we're breaking is in our hash
		if (WolfPound.pounds.get(event.getBlock()) != null) {
			if (!plugin.hasPermission(event.getPlayer(), "wolfpound.create")) {
				event.setCancelled(true);
			} else {
				if (WolfPound.pounds.remove(event.getBlock()) != null) {
					event.getPlayer().sendMessage("Destroying Wolf Pound");
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
				WolfPound.pounds.put(event.getBlock(), new Pound(event.getBlock().getLocation(), event.getPlayer(), price));
			}
		} catch (NumberFormatException e) {
			WolfPound.pounds.put(event.getBlock(), new Pound(event.getBlock().getLocation(), event.getPlayer()));
			
		}
		event.setLine(0, "¤1[WolfPound]");
		plugin.log.info("Number of Pounds: " + WolfPound.pounds.size() + ", Current: (" + event.getBlock());
		
	}
}
