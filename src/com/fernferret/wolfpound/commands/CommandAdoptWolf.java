package com.fernferret.wolfpound.commands;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandAdoptWolf extends WolfPoundCommand {
	private static final Logger log = Logger.getLogger("Minecraft");
	public CommandAdoptWolf(WolfPound plugin) {
		super(plugin);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		log.info(WolfPound.logPrefix + " Firing adopt command");
		// TODO: Allow players to buy multiple wolves
		if (args.length == 0 && sender instanceof Player) {
			Player player = (Player) sender;
			// Adopt a wolf with no params
			this.plugin.adoptWolf(player, 1);
			return true;
		}
		return false;
//		Player player = (Player) sender;
//		switch (ifargs.length) {
//			case 1:
//				// Display the wolf price
//				if (args[0].equalsIgnoreCase("price")) {
//					this.plugin.sendWolfPrice(player, player.getWorld().getName());
//					return true;
//				} else if (args[0].equalsIgnoreCase("limit")) {
//					this.plugin.sendWolfLimit(player, player.getWorld().getName());
//					return true;
//				}
//				// Adopt X wolves
//				plugin.adoptWolf(player, plugin.getWolfInt(args[0], player, "I didn't understand how many wolves you wanted!"));
//				return true;
//			case 2:
//				// change a setting!,
//				if (plugin.hasPermission(player, WolfPound.PERM_ADMIN) && args[0].equalsIgnoreCase("remove")) {
//					this.plugin.removeWorld(args[1]);
//					return true;
//				}
//				if (this.plugin.hasPermission(player, WolfPound.PERM_ADOPT) && args[0].equalsIgnoreCase("price") && args[1].equalsIgnoreCase("all")) {
//					this.plugin.sendWolfPrice(player, "");
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
