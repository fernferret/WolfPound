package com.fernferret.wolfpound;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.block.Sign;

import com.earth2me.essentials.User;
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
			Pound pound = WolfPound.pounds.get(s.getBlock().getLocation()); 
			if(pound != null && plugin.hasPermission(p, "wolfpound.use")) {
				// We have a valid pound!
				if(pound.getPrice() == 0) {
					plugin.spawnWolf(p);
				} else if(WolfPound.useiConomy) {
					if (iConomy.getBank().getAccount(p.getName()).hasEnough(pound.getPrice())) {
						iConomy.getBank().getAccount(p.getName()).subtract(pound.getPrice());
						p.sendMessage(ChatColor.RED + "[WolfPound]"
								+ " You have been charged " + pound.getPrice() + " "
								+ iConomy.getBank().getCurrency());
						plugin.spawnWolf(p);
					} else {
							userIsTooPoor(p);
						return;
					}
				} else if(WolfPound.useEssentials) {
					User user = User.get(event.getPlayer());
					if (user.getMoney() >= pound.getPrice()) {
						user.takeMoney(pound.getPrice());
						p.sendMessage(ChatColor.RED + "[WolfPound]"
								+ " You have been charged $" + pound.getPrice());
					} else {
						userIsTooPoor(p);
						return;
					}
				}
			}
		}
	}

	private void userIsTooPoor(Player p) {
		p.sendMessage("Sorry but you do not have the required funds for a wolf");
	}
}
