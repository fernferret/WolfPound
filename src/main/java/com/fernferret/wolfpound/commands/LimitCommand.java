package com.fernferret.wolfpound.commands;

import com.fernferret.wolfpound.WolfPound;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class LimitCommand extends WolfPoundCommand {
    private static final String[] LIMIT_KEYWORDS = {"all"};

    public LimitCommand(WolfPound plugin) {
        super(plugin);
        this.setName("States the Wolf adoption limit.");
        this.setCommandUsage("/wp limit" + ChatColor.GOLD + " [all | WORLD]");
        this.setArgRange(0, 1);
        this.addKey("wp limit");
        this.addKey("wplimit");
        this.addKey("wpl");
        this.setPermission("wolfpound.limit", "States how many wolves can be adopted at once. This is not a limit on how many someone can have.", PermissionDefault.OP);
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
                this.plugin.sendWolfLimit((Player) sender, "all");
            }
        } else if (args.size() == 1) {
            if (isAKeyword(args.get(0), LIMIT_KEYWORDS)) {
                this.plugin.sendWolfLimit((Player) sender, "all");
            } else if (isValidWorld(args.get(0))) {
                this.plugin.sendWolfLimit(sender, args.get(0));
            } else {
                WolfPound.log.info("Please use: wplimit [w:WORLD | all]");
            }
        }
    }
}
