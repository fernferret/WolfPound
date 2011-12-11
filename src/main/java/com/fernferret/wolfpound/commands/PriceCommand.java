package com.fernferret.wolfpound.commands;

import com.fernferret.wolfpound.WolfPound;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class PriceCommand extends WolfPoundCommand {
    private static final String[] PRICE_KEYWORDS = {"all"};

    public PriceCommand(WolfPound plugin) {
        super(plugin);
        this.setName("States the Wolf price.");
        this.setCommandUsage("/wp price" + ChatColor.GOLD + " [all | w:WORLD]");
        this.setArgRange(0, 1);
        this.addKey("wp price");
        this.addKey("wpprice");
        this.addKey("wpp");
        this.setPermission("wolfpound.price", "States what the price of a wolf in the given world.", PermissionDefault.OP);
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
                this.plugin.sendWolfPrice(sender, "all");
            } else if (isValidWorld(args.get(0))) {
                this.plugin.sendWolfPrice(sender, args.get(0));
            } else if (p == null) {
                WolfPound.log.info("Please use: wpprice [w:WORLD | all]");
            }
        }
    }

}
