package com.fernferret.wolfpound.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.fernferret.wolfpound.WolfPound;

public class LimitCommand extends WPCommand {
    private static final String[] LIMIT_KEYWORDS = { "all" };

    public LimitCommand(WolfPound plugin) {
        super(plugin);
        this.setName("States the Wolf adoption limit.");
        this.setCommandUsage("/wp limit" + ChatColor.GOLD + "[all | w:WORLD]");
        this.setArgRange(0, 1);
        this.addKey("wp limit");
        this.addKey("wplimit");
        this.addKey("wpl");
        this.setPermission("wolfpound.use", "States how many wolves can be adopted at once. This is not a limit on how many someone can have.", PermissionDefault.OP);
        this.addCommandExample("/wp limit");
        this.addCommandExample("/wp limit" + ChatColor.GOLD + " all");
        this.addCommandExample("/wp limit" + ChatColor.GOLD + " world2");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Someone tried just /wplimit
        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }
        if (args.size() == 0) {
            if (p != null) {
                plugin.sendWolfLimit(p, p.getWorld().getName());
            } else {
                sender.sendMessage("From the console, please use: wp limit" + ChatColor.GREEN + " {w:WORLD | all}");
            }
        } else if (args.size() == 1) {
            if (isAKeyword(args.get(0), LIMIT_KEYWORDS)) {
                if (p != null) {
                    this.plugin.sendWolfLimit((Player) sender, "all");
                } else {
                    // TODO: Add console support
                    WolfPound.log.info("This command is currently not supported from the console.");
                }
            } else if (isValidWorld(args.get(0))) {
                if (p != null) {
                    this.plugin.sendWolfLimit((Player) sender, getWorldName(args.get(0)));
                } else {
                    // TODO: Add console support
                    WolfPound.log.info("This command is currently not supported from the console.");
                }
            } else if (p == null) {
                WolfPound.log.info("Please use: wplimit [w:WORLD | all]");
            }
        }
    }
}
