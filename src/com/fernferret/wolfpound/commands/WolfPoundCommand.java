package com.fernferret.wolfpound.commands;

import java.util.HashSet;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;

import com.fernferret.wolfpound.WolfPound;

public abstract class WolfPoundCommand implements CommandExecutor {
	protected WolfPound plugin;
	
	public WolfPoundCommand(WolfPound plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Currently unused, used for parsing more advanecd worldstrings
	 * 
	 * @param s
	 * @return
	 */
	protected HashSet<String> parseWorlds(String s) {
		String[] values = s.split(":");
		HashSet<String> worldList = new HashSet<String>();
		if (values.length == 2 && values[0].equalsIgnoreCase("w")) {
			String[] worlds = values[1].split(",");
			
			if (worlds.length == 1 && worlds[0] == "all") {
				worldList.add("all");
				return worldList;
			}
			Server server = plugin.getServer();
			
			for (String world : worlds) {
				if (server.getWorld(world) != null) {
					worldList.add("w:" + world);
				}
			}
		}
		return worldList;
	}
	/**
	 * Checks to see if the given string was:
	 * 1. In the format w:WORLD
	 * 2. WORLD is a valid worldname
	 * @param s The passed in string
	 * @return true if valid, false if not
	 */
	protected boolean isValidWorld(String s) {
		String[] values = s.split(":");
		return(values.length == 2 && values[0].equalsIgnoreCase("w") && 
				plugin.getServer().getWorld(values[1]) != null);
	}
	
	/**
	 * Checks to see if the given string was:
	 * 1. In the format w:WORLD
	 * 2. WORLD is a valid worldname
	 * @param s The passed in string
	 * @return true if valid, false if not
	 */
	protected String checkKeyword(String s, String[] choices) {
		for(String word : choices) {
			if(s.matches("(?i)(.*" + word + ".*)")) {
				return word;
			}
		}
		return null;
	}
}
