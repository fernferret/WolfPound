package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandLimit extends WolfPoundCommand {
	private static final String[] LIMIT_KEYWORDS = { "all" };
	public CommandLimit(WolfPound plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Someone tried just /wplimit
		Player p = null;
		if (sender instanceof Player) {
			p = (Player) sender;
		}
		if (args.length == 0) {
			if (p != null) {
				plugin.sendWolfLimit(p, p.getWorld().getName());
				return true;
			} else {
				WolfPound.log.info("Please use: wplimit [w:WORLD | all]");
				return true;
			}
		} else if (args.length == 1) {
			if (isAKeyword(args[0], LIMIT_KEYWORDS)) {
				if (p != null) {
					this.plugin.sendWolfLimit((Player) sender, "all");
				} else {
					// TODO: Add console support
					WolfPound.log.info("This command is currently not supported from the console.");
				}
			} else if (isValidWorld(args[0])) {
				if (p != null) {
					this.plugin.sendWolfLimit((Player) sender, getWorldName(args[0]));
				} else {
					// TODO: Add console support
					WolfPound.log.info("This command is currently not supported from the console.");
				}
			} else if (p == null) {
				WolfPound.log.info("Please use: wplimit [w:WORLD | all]");
			} else {
				return false;
			}
			return true;
		}
		return false;
	}
	
}
