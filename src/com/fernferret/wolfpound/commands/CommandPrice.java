package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandPrice extends WolfPoundCommand {
	private static final String[] PRICE_KEYWORDS = {"all"};
	public CommandPrice(WolfPound plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Someone tried just /wpprice
		if(args.length == 0) {
			if(sender instanceof Player) {
				Player p = (Player)sender;
				plugin.sendWolfPrice(p, p.getWorld().getName());
				return true;
			} else {
				WolfPound.log.info("Please use: /wpprice {w:WORLD|all} from the console");
			}
		} else if(args.length == 1) {
			if(checkKeyword(args[0], PRICE_KEYWORDS) == "all") {
				if(sender instanceof Player) {
					this.plugin.sendWolfPrice((Player)sender, "all");
					return true;
				}
				// TODO: Fix for console
			} else if(isValidWorld(args[0])) {
				this.plugin.sendWolfPrice((Player)sender, getWorldName(args[0]));
				return true;
				// TODO: Fix for console
			} else {
				if(!(sender instanceof Player)) {
					WolfPound.log.info("Please use: /wpprice {w:WORLD|all}");
				}
			}
		}
		return false;
	}
	
}
