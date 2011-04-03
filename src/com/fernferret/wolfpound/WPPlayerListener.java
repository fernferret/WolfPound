package com.fernferret.wolfpound;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;

public class WPPlayerListener extends PlayerListener {
	private final WolfPound plugin;
	
	public WPPlayerListener(final WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (plugin.blockIsValidWolfSign(event.getClickedBlock()) && plugin.hasPermission(p, "wolfpound.use")) {
				// We have a valid pound!
				double price = getPriceFromBlock(event.getClickedBlock(), 1);
				// If the price is 0 or no econ plugin
				if(WPBankAdapter.hasMoney(p, price)) {
					WPBankAdapter.payForWolf(p, price);
					WPBankAdapter.showRecipt(p, price);
				} else {
					WPBankAdapter.userIsTooPoor(p);
				}
			}
		}
	}
	private Double getPriceFromBlock(Block b, int i) {
		
		try {
			Sign s = new CraftSign(b);
			return Double.parseDouble(s.getLine(i).replaceAll("\\D", ""));
		} catch (NumberFormatException e) {
			// We'll return the default
		}
		return 0.0;
		
	}
}
