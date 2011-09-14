package com.fernferret.wolfpound.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.fernferret.wolfpound.WolfPound;
import com.pneumaticraft.commandhandler.Command;

public abstract class WolfPoundCommand extends Command {

    protected WolfPound plugin;

    public WolfPoundCommand(WolfPound plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

    /**
     * Checks to see if the given string was: 1. In the format w:WORLD 2. WORLD is a valid worldname
     * 
     * @param s The passed in string
     * @return true if valid, false if not
     */
    protected boolean isValidWorld(String s) {
        String[] values = s.split(":");
        return (values.length == 2 && values[0].equalsIgnoreCase("w") && plugin.getServer().getWorld(values[1]) != null);
    }

    /**
     * Checks to see if the given list contains the given string
     * 
     * @param s The passed in string
     * @param choices The list to check
     * @return true if the string is in the list, false if not
     */
    protected boolean isAKeyword(String s, String[] choices) {
        for (String word : choices) {
            if (s.matches("(?i)(.*" + word + ".*)")) {
                return true;
            }
        }
        return false;
    }
}