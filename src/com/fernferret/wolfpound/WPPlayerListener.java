package com.fernferret.wolfpound;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;

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
			Sign s = new CraftSign(event.getClickedBlock());
			if (s.getLine(0).equals("¤1[WolfPound]") && plugin.hasPermission(p, "wolfpound.use")) {
				// We have a valid pound!
				double price = getPriceFromLine(s, 1);
				if (price == 0) {
					plugin.spawnWolf(p);
				} else if (WolfPound.useiConomy) {
					if (iConomy.getBank().getAccount(p.getName()).hasEnough(price)) {
						iConomy.getBank().getAccount(p.getName()).subtract(price);
						p.sendMessage(ChatColor.RED + "[WolfPound]"
								+ " You have been charged " + price + " "
								+ iConomy.getBank().getCurrency());
						plugin.spawnWolf(p);
					} else {
						userIsTooPoor(p);
						return;
					}
				} else if (WolfPound.useEssentials) {
					User user = User.get(event.getPlayer());
					if (user.getMoney() >= price) {
						user.takeMoney(price);
						p.sendMessage(ChatColor.RED + "[WolfPound]"
								+ " You have been charged $" + price);
					} else {
						userIsTooPoor(p);
						return;
					}
				}
			}
		}
	}
	
	private Double getPriceFromLine(Sign s, int i) {
		try {
			return Double.parseDouble(s.getLine(i));
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
	
	private void userIsTooPoor(Player p) {
		p.sendMessage("Sorry but you do not have the required funds for a wolf");
	}
}
