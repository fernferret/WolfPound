package com.fernferret.wolfpound.commands;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import com.fernferret.wolfpound.WolfPound;

public class DebugCommand extends WolfPoundCommand {

    public DebugCommand(WolfPound plugin) {
        super(plugin);
        this.setName("Turn Debug on/off");
        this.setCommandUsage("/wp debug" + ChatColor.GOLD + " [1|2|3|off]");
        this.setArgRange(0, 1);
        this.addKey("wp debug");
        this.addKey("wp d");
        this.addKey("wpdebug");
        this.setPermission("wolfpound.debug", "Spams the console a bunch.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            if (args.get(0).equalsIgnoreCase("off")) {
                WolfPound.GlobalDebug = 0;
            } else {
                try {
                    int debugLevel = Integer.parseInt(args.get(0));
                    if (debugLevel > 3 || debugLevel < 0) {
                        throw new NumberFormatException();
                    }
                    WolfPound.GlobalDebug = debugLevel;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error" + ChatColor.WHITE + " setting debug level. Please use a number 0-3 " + ChatColor.AQUA + "(3 being many many messages!)");
                }

            }
        }
        this.displayDebugMode(sender);
    }

    private void displayDebugMode(CommandSender sender) {
        if (WolfPound.GlobalDebug == 0) {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.RED + "OFF");
        } else {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.GREEN + WolfPound.GlobalDebug);
            this.plugin.log(Level.FINE, "Multiverse Debug ENABLED");
        }
    }
}
