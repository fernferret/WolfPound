package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandAdoptWolf extends WolfPoundCommand {
	public CommandAdoptWolf(WolfPound plugin) {
		super(plugin);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO: Allow players to buy multiple wolves
		if(!(sender instanceof Player)) {
			log.info("Only Players can adopt wolves!");
			return true;
		}
		Player player = (Player)sender;
		if (args.length == 0) {
			// Adopt a wolf with no params
			this.plugin.adoptWolf(player, 1);
			return true;
		} else if(args.length == 1) {
			try{
				int wolves = Integer.parseInt(args[0]);
				this.plugin.adoptWolf(player, wolves);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
//		Player player = (Player) sender;
//		switch (ifargs.length) {
//			case 1:
//				} else if (args[0].equalsIgnoreCase("limit")) {
//					this.plugin.sendWolfLimit(player, player.getWorld().getName());
//					return true;
//				}
//			case 2:
//				// change a setting!,
//				if (plugin.hasPermission(player, WolfPound.PERM_ADMIN) && args[0].equalsIgnoreCase("remove")) {
//					this.plugin.removeWorld(args[1]);
//					return true;
//				}
//				if (this.plugin.hasPermission(player, WolfPound.PERM_ADOPT) && args[0].equalsIgnoreCase("limit") && args[1].equalsIgnoreCase("all")) {
//					this.plugin.sendWolfLimit(player, "");
//					return true;
//				}
//				if (!this.plugin.hasPermission(player, WolfPound.PERM_ADMIN)) {
//					return false;
//				}
//				return (this.plugin.changeSetting(args[0], args[1], player.getWorld().getName(), player));
//			case 3:
//				// change a setting!,
//				if (!this.plugin.hasPermission(player, WolfPound.PERM_ADMIN)) {
//					return false;
//				}
//				return (this.plugin.changeSetting(args[0], args[1], args[2], player));
//			default:
//				return false;
//		}
	}
}
