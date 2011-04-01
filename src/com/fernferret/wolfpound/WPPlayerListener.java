package com.fernferret.wolfpound;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.block.Sign;

import com.nijiko.coelho.iConomy.iConomy;

public class WPPlayerListener extends PlayerListener {
	private final WolfPound plugin;
	
	public WPPlayerListener(final WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if (event.getClickedBlock().getState() instanceof Sign && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign s = (Sign) event.getClickedBlock().getState();
			if (s.getLine(0).equalsIgnoreCase("[WolfPound]")) {
				// There is no cost!
				if (s.getLine(1) == null || s.getLine(1).equals("")) {
					plugin.spawnWolf(p);
					p.sendMessage("There was a problem reading the price. Please remove the sign and try again.");
				} else if (s.getLine(1) != null && !s.getLine(1).equals("")) {
					double price = 0;
					try {
						price = Double.parseDouble(s.getLine(1));
					} catch (NumberFormatException e) {
						p.sendMessage("There was a problem reading the price. Please remove the sign and try again.");
						return;
					}
					if (WolfPound.useiConomy) {
						
						if (iConomy.getBank().getAccount(p.getName()).hasEnough(price)) {
							iConomy.getBank().getAccount(p.getName()).subtract(price);
							p.sendMessage(ChatColor.RED + "[WolfPound]"
									+ " You have been charged " + price + " "
									+ iConomy.getBank().getCurrency());
							plugin.spawnWolf(p);
						} else {
								p.sendMessage("Sorry but you do not have the required funds for a wolf");
							return;
						}
					} else if (WolfPound.useEssentials) {
						
					}
				}
				
			}
		}
	}
}
