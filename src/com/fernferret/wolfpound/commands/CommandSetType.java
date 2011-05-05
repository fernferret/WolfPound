package com.fernferret.wolfpound.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fernferret.wolfpound.WolfPound;

public class CommandSetType extends WolfPoundCommand {
	public CommandSetType(WolfPound plugin) {
		super(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}
	
}
