package com.fernferret.wolfpound.commands;

import com.fernferret.wolfpound.WolfPound;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class ResetCommand extends WolfPoundCommand {
    private static final String[] RESET_KEYWORDS = {"global", "all"};

    public ResetCommand(WolfPound plugin) {
        super(plugin);
        this.setName("Resets wolf Price in a world.");
        this.setCommandUsage("/wp reset" + ChatColor.GOLD + " [all | WORLD]");
        this.setArgRange(0, 1);
        this.addKey("wp reset");
        this.addKey("wpreset");
        this.addKey("wp remove");
        this.addKey("wpremove");
        this.addKey("wpr");
        this.setPermission("wolfpound.admin", "Resets the Price of wolves in the given world to use the default.", PermissionDefault.OP);
        this.addCommandExample("/wp reset" + ChatColor.GOLD + " all");
        this.addCommandExample("/wp reset" + ChatColor.GOLD + " world2");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (plugin.getPermissions().hasPermission(sender, "wolfpound.admin", true) && args.size() == 0) {
            if (sender instanceof Player) {
                this.plugin.getWorldManager().removeWorld(((Player) sender).getWorld().getName());
                sender.sendMessage("WolfPound settings for " + ChatColor.AQUA + ((Player) sender).getWorld().getName() + ChatColor.WHITE + " were reset to the defaults");
            } else {
                sender.sendMessage("From the console, a world or all is required.");
            }
        } else if (plugin.getPermissions().hasPermission(sender, "wolfpound.admin", true)) {
            if (this.plugin.getWorldManager().removeWorld(args.get(0))) {
                sender.sendMessage("WolfPound settings for " + ChatColor.AQUA + args.get(0) + ChatColor.WHITE + " were reset to the defaults");
            } else if (isAKeyword(args.get(0), RESET_KEYWORDS)) {
                for (World w : this.plugin.getServer().getWorlds()) {
                    this.plugin.getWorldManager().removeWorld(w.getName());
                }
                sender.sendMessage("WolfPound settings for " + ChatColor.AQUA + args.get(0) + " worlds" + ChatColor.WHITE + " were reset to the defaults");
            }
        }
    }
}
