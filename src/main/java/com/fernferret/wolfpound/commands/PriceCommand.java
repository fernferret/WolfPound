package com.fernferret.wolfpound.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.fernferret.wolfpound.WolfPound;

public class PriceCommand extends WPCommand {
	private static final String[] PRICE_KEYWORDS = { "all" };
	
	public PriceCommand(WolfPound plugin) {
	    super(plugin);
        this.setName("States the Wolf price.");
        this.setCommandUsage("/wp price" + ChatColor.GOLD + "[all | w:WORLD]");
        this.setArgRange(0, 1);
        this.addKey("wp price");
        this.addKey("wpprice");
        this.addKey("wpp");
        this.setPermission("wolfpound.use", "States what the price of a wolf in the given world.", PermissionDefault.OP);
        this.addCommandExample("/wp price");
        this.addCommandExample("/wp price" + ChatColor.GOLD + " all");
        this.addCommandExample("/wp price" + ChatColor.GOLD + " world2");
	}

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Someone tried just /wpprice
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }
        if (args.size() == 0) {
            if (p != null) {
                plugin.sendWolfPrice(p, p.getWorld().getName());
            } else {
                WolfPound.log.info("Please use: wpprice [w:WORLD | all]");
            }
        } else if (args.size() == 1) {
            if (isAKeyword(args.get(0), PRICE_KEYWORDS)) {
                if (p != null) {
                    this.plugin.sendWolfPrice((Player) sender, "all");
                } else {
                    // TODO: Add console support
                    WolfPound.log.info("This command is currently not supported from the console.");
                }
            } else if (isValidWorld(args.get(0))) {
                if (p != null) {
                    this.plugin.sendWolfPrice((Player) sender, getWorldName(args.get(0)));
                } else {
                    // TODO: Add console support
                    WolfPound.log.info("This command is currently not supported from the console.");
                }
            } else if (p == null) {
                WolfPound.log.info("Please use: wpprice [w:WORLD | all]");
            }
        }
    }
	
}
