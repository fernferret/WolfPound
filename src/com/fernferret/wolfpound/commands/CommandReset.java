package com.fernferret.wolfpound.commands;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandReset extends WolfPoundCommand {
	private static final String[] RESET_KEYWORDS = { "global", "all" };
	public CommandReset(WolfPound plugin) {
		super(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (plugin.hasPermission(player, WolfPound.PERM_ADMIN) && args.length == 1) {
				if(isValidWorld(args[0])) {
					this.plugin.removeWorld(getWorldName(args[0]));
				} else if(isAKeyword(args[0], RESET_KEYWORDS)) {
					for(World w : this.plugin.getServer().getWorlds()) {
						this.plugin.removeWorld(w.getName());
					}
				} else {
					// Was an INVALID world or keyword, return false.
					return false;
				}
				// Was a VALID world or keyword, return true.
				return true;
			}
		}
		return false;
	}
}
