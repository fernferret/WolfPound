package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fernferret.wolfpound.WolfPound;

public class CommandSetAggro extends WolfPoundCommand implements SavableProperty {
	private String propertyName;
	private static final String[] AGGRO_KEYWORDS = { "global" };
	
	public CommandSetAggro(WolfPound plugin) {
		super(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = null;
		if (sender instanceof Player) {
			p = (Player) sender;
		}
		if (args.length == 1) {
			if (p != null) {
				this.plugin.changeSetting("aggro", args[0], p.getWorld().getName(), p);
				return true;
			}
		} else if (args.length == 2) {
			if (p != null) {
				if (checkKeyword(args[1], AGGRO_KEYWORDS) == "global") {
					// Change the global aggro
					this.plugin.changeSetting("aggroglobal", args[0], "", p);
				} else if (isValidWorld(args[1])) {
					this.plugin.changeSetting("aggro", args[0], getWorldName(args[1]), p);
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
	
	@Override
	public String getPropertyName() {
		return this.propertyName;
	}
	
	@Override
	public void setPropertyName(String name) {
		this.propertyName = name;
	}
	
	@Override
	public void saveProperty(String world) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void savePropertyGlobal() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void checkProperty(String world) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void checkPropertyGlobal() {
		// TODO Auto-generated method stub
		
	}
}
