package com.fernferret.wolfpound;

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
			if (plugin.blockIsValidWolfSign(event.getClickedBlock()) && plugin.hasPermission(p, WolfPound.PERM_USE)) {
				// We have a valid pound!
				double price = getPriceFromBlock(event.getClickedBlock(), 1);
				int item = getItemFromBlock(event.getClickedBlock(), 1);
				if (plugin.bank.hasMoney(p, price, item)) {
					plugin.bank.payForWolf(p, price, item);
					plugin.bank.showRecipt(p, price, item);
					plugin.spawnWolf(p);
				}
			}
		}
	}

	/**
	 * Returns a price from a sign
	 * 
	 * @param b The block to parse
	 * @param l The line of the sign to parse
	 * @return The price or 0 if the price was invalid
	 */
	private Double getPriceFromBlock(Block b, int l) {
		try {
			if (b.getState() instanceof Sign) {
				Sign s = new CraftSign(b);
				String line = s.getLine(l);
				String[] items = line.split("(.*):(.*)");
				if(items.length > 0) {
					return Double.parseDouble(items[0].replaceAll("\\D", ""));
				}
			}
		} catch (NumberFormatException e) {
			// We'll return the default
		}
		return 0.0;
	}
	
	private int getItemFromBlock(Block b, int l) {
		try {
			if (b.getState() instanceof Sign) {
				Sign s = new CraftSign(b);
				String line = s.getLine(l);
				String[] items = line.split(":");
				if(items.length > 0) {
					return Integer.parseInt(items[1].replaceAll("\\D", ""));
				}
			}
		} catch (NumberFormatException e) {
			// We'll return the default
		}
		return -1;
	}
}
