package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandSetType extends WolfPoundCommand {
	// TODO: Make a new subclass that SetType, SetPrice, SetAggro and SetLimit can inherit from
	private static final String[] TYPE_KEYWORDS = { "global" };
	public CommandSetType(WolfPound plugin) {
		super(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = null;
		if(sender instanceof Player) {
			p = (Player) sender;
		}
		if(args.length == 1) {
			if(p != null) {
				this.plugin.changeSetting("type", args[0], p.getWorld().getName(), p);
				return true;
			}
		} else if(args.length == 2) {
			if(p != null) {
				if(isAKeyword(args[1], TYPE_KEYWORDS)) {
					// Change the global type
					this.plugin.changeSetting("typeglobal", args[0], "", p);
				} else if(isValidWorld(args[1])) {
					this.plugin.changeSetting("type", args[0], getWorldName(args[1]), p);
				} else {
					// Not global or a world, something is wrong...
					return false;
				}
				return true;
			}
			// TODO: Implement console commands here
		}
		return false;
	}
	
}
