package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandAdoptWolf extends WolfPoundCommand {
	public CommandAdoptWolf(WolfPound plugin) {
		super.setPlugin(plugin);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		switch (args.length) {
			case 0:
				// Adopt a wolf with no params
				plugin.adoptWolf(player, 1);
				return true;
			case 1:
				// Display the wolf price
				if (args[0].equalsIgnoreCase("price")) {
					plugin.sendWolfPrice(player, player.getWorld().getName());
					return true;
				} else if (args[0].equalsIgnoreCase("limit")) {
					plugin.sendWolfLimit(player, player.getWorld().getName());
					return true;
				}
				// Adopt X wolves
				plugin.adoptWolf(player, plugin.getWolfInt(args[0], player, "I didn't understand how many wolves you wanted!"));
				return true;
			case 2:
				// change a setting!,
				if (plugin.hasPermission(player, WolfPound.PERM_ADMIN) && args[0].equalsIgnoreCase("remove")) {
					plugin.removeWorld(args[1]);
					return true;
				}
				if (plugin.hasPermission(player, WolfPound.PERM_ADOPT) && args[0].equalsIgnoreCase("price") && args[1].equalsIgnoreCase("all")) {
					plugin.sendWolfPrice(player, "");
					return true;
				}
				if (plugin.hasPermission(player, WolfPound.PERM_ADOPT) && args[0].equalsIgnoreCase("limit") && args[1].equalsIgnoreCase("all")) {
					plugin.sendWolfLimit(player, "");
					return true;
				}
				if (!plugin.hasPermission(player, WolfPound.PERM_ADMIN)) {
					return false;
				}
				return (plugin.changeSetting(args[0], args[1], player.getWorld().getName(), player));
			case 3:
				// change a setting!,
				if (!plugin.hasPermission(player, WolfPound.PERM_ADMIN)) {
					return false;
				}
				return (plugin.changeSetting(args[0], args[1], args[2], player));
			default:
				return false;
		}
	}
	
}
