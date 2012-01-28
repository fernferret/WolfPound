/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.fernferret.wolfpound.commands;

import com.fernferret.wolfpound.WolfPound;
import com.fernferret.wolfpound.utils.webpaste.PasteFailedException;
import com.fernferret.wolfpound.utils.webpaste.PasteService;
import com.fernferret.wolfpound.utils.webpaste.PasteServiceFactory;
import com.fernferret.wolfpound.utils.webpaste.PasteServiceType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Dumps version info to the console.
 */
public class VersionCommand extends WolfPoundCommand {

    public VersionCommand(WolfPound plugin) {
        super(plugin);
        this.setName("WolfPound Version");
        this.setCommandUsage("/wp version " + ChatColor.GOLD + "-[pb]");
        this.setArgRange(0, 1);
        this.addKey("wp version");
        this.addKey("wpv");
        this.addKey("wpversion");
        this.setPermission("wolfpound.version",
                "Dumps version info to the console, optionally to pastie.org with -p or pastebin.com with a -b.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            sender.sendMessage("Version info dumped to console. Please check your server logs.");
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("WolfPound Version: ").append(this.plugin.getDescription().getVersion());
        buffer.append("Bukkit Version: ").append(this.plugin.getServer().getVersion());
        buffer.append("Economy being used: ").append(this.plugin.getBanker().getEconPlugin().getEconUsed());
        buffer.append("Permissions Plugin: ").append(this.plugin.getPermissions().getType());
        buffer.append("Op Fallback: ").append(this.plugin.getConfig().getString("opfallback", "NOT SET"));
        buffer.append("");
        buffer.append("Global Values:");
        buffer.append("  Limit: ").append(this.plugin.getConfig().getString("adopt.limit", "NOT SET"));
        buffer.append("  Price: ").append(this.plugin.getConfig().getString("adopt.price", "NOT SET"));
        buffer.append("  Aggro: ").append(this.plugin.getConfig().getString("adopt.aggro", "NOT SET"));
        buffer.append("  Curr: ").append(this.plugin.getConfig().getString("adopt.type", "NOT SET"));
        buffer.append("");
        Set<String> keys = this.plugin.getConfig().getConfigurationSection("adopt.worlds").getKeys(false);
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                buffer.append("Values for: ").append(key);
                buffer.append("  Limit: ").append(this.plugin.getConfig().getString("adopt.worlds." + key + ".limit", "NOT SET"));
                buffer.append("  Price: ").append(this.plugin.getConfig().getString("adopt.worlds." + key + ".price", "NOT SET"));
                buffer.append("  Aggro: ").append(this.plugin.getConfig().getString("adopt.worlds." + key + ".aggro", "NOT SET"));
                buffer.append("  Curr: ").append(this.plugin.getConfig().getString("adopt.worlds." + key + ".type", "NOT SET"));
                buffer.append("");
            }
        }

        // log to console
        String[] lines = buffer.toString().split("\n");
        for (String line : lines) {
            this.plugin.log(Level.INFO, line);
        }

        if (args.size() == 1) {
            String pasteUrl = "";
            if (args.get(0).equalsIgnoreCase("-p")) {
                pasteUrl = postToService(PasteServiceType.PASTIE, true, buffer.toString()); // private post to pastie
            } else if (args.get(0).equalsIgnoreCase("-b")) {
                pasteUrl = postToService(PasteServiceType.PASTEBIN, true, buffer.toString()); // private post to pastie
            } else {
                return;
            }

            sender.sendMessage("Version info dumped here: " + ChatColor.GREEN + pasteUrl);
            this.plugin.log(Level.INFO, "Version info dumped here: " + pasteUrl);
        }
    }

    /**
     * Send the current contents of this.pasteBinBuffer to a web service.
     *
     * @param type      Service type to send to
     * @param isPrivate Should the paste be marked as private.
     * @return URL of visible paste
     */
    private static String postToService(PasteServiceType type, boolean isPrivate, String pasteData) {
        PasteService ps = PasteServiceFactory.getService(type, isPrivate);
        try {
            return ps.postData(ps.encodeData(pasteData), ps.getPostURL());
        } catch (PasteFailedException e) {
            System.out.print(e);
            return "Error posting to service";
        }
    }
}
