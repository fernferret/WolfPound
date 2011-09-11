package com.fernferret.wolfpound.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.fernferret.wolfpound.WolfPound;

public class ResetCommand extends WolfPoundCommand {
	private static final String[] RESET_KEYWORDS = { "global", "all" };
	public ResetCommand(WolfPound plugin) {
	    super(plugin);
        this.setName("Resets wolf Price in a world.");
        this.setCommandUsage("/wp reset" + ChatColor.GOLD + "[all | w:WORLD]");
        this.setArgRange(0, 1);
        this.addKey("wp reset");
        this.addKey("wpreset");
        this.addKey("wpr");
        this.setPermission("wolfpound.admin", "Resets the Price of wolves in the given world to use the default.", PermissionDefault.OP);
        this.addCommandExample("/wp reset");
        this.addCommandExample("/wp reset" + ChatColor.GOLD + " all");
        this.addCommandExample("/wp reset" + ChatColor.GOLD + " world2");
	}

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.getPermissions().hasPermission(player, "wolfpound.admin", true) && args.size() == 1) {
                if(isValidWorld(args.get(0))) {
                    this.plugin.removeWorld(getWorldName(args.get(0)), player);
                } else if(isAKeyword(args.get(0), RESET_KEYWORDS)) {
                    for(World w : this.plugin.getServer().getWorlds()) {
                        this.plugin.removeWorld(w.getName(), player);
                    }
                }
            }
        }
    }
}
