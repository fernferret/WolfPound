package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandPrice extends WolfPoundCommand {
	private static final String[] PRICE_KEYWORDS = { "all" };
	
	public CommandPrice(WolfPound plugin) {
		super(plugin);
	}
	
	// TODO: place price message functions in this class!
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Someone tried just /wpprice
		Player p = null;
		if (sender instanceof Player) {
			p = (Player) sender;
		}
		if (args.length == 0) {
			if (p != null) {
				plugin.sendWolfPrice(p, p.getWorld().getName());
				return true;
			} else {
				WolfPound.log.info("Please use: wpprice [w:WORLD | all]");
				return true;
			}
		} else if (args.length == 1) {
			if (isAKeyword(args[0], PRICE_KEYWORDS)) {
				if (p != null) {
					this.plugin.sendWolfPrice((Player) sender, "all");
				} else {
					// TODO: Add console support
					WolfPound.log.info("This command is currently not supported from the console.");
				}
			} else if (isValidWorld(args[0])) {
				if (p != null) {
					this.plugin.sendWolfPrice((Player) sender, getWorldName(args[0]));
				} else {
					// TODO: Add console support
					WolfPound.log.info("This command is currently not supported from the console.");
				}
			} else if (!(sender instanceof Player)) {
				WolfPound.log.info("Please use: wpprice [w:WORLD | all]");
			}
			return true;
		}
		return false;
	}
	
}
