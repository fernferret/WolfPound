package com.fernferret.wolfpound;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;

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
				event.getPlayer().sendMessage("Successfully created Wolf Pound!");
				event.setLine(0, "¤1[WolfPound]");
			} else {
				event.setLine(0, "¤4[WolfPound]");
			}
		}
		// TODO: Make wolves assigned to people
		// TODO: Color Signs at Runtime
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
		if (plugin.blockIsValidWolfSign(event.getBlock())) {
			if (!plugin.hasPermission(event.getPlayer(), "wolfpound.create")) {
				event.setCancelled(true);
			} else {
				event.getPlayer().sendMessage("Destroying Wolf Pound");
			}
		}
	}
}
