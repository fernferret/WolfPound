package com.fernferret.wolfpound;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.pneumaticraft.commandhandler.PermissionsInterface;

public class WPPermissions implements PermissionsInterface {

    private WolfPound plugin;
    private PermissionHandler permissions = null;

    public WPPermissions(WolfPound plugin) {
        this.plugin = plugin;
        // We have to see if permissions was loaded before MV was
        if (this.plugin.getServer().getPluginManager().getPlugin("Permissions") != null) {
            this.setPermissions(((Permissions) this.plugin.getServer().getPluginManager().getPlugin("Permissions")).getHandler());
            this.plugin.log(Level.INFO, "- Attached to Permissions");
        }
    }

    public void setPermissions(PermissionHandler handler) {
        this.permissions = handler;
    }

    public String getType() {
        String opsfallback = "";
        if (this.plugin.getConfig().getBoolean("opfallback", true)) {
            opsfallback = " WITH OPs.txt fallback";
        }
        if (this.permissions != null) {
            return "Permissions " + this.plugin.getServer().getPluginManager().getPlugin("Permissions").getDescription().getVersion() + opsfallback;
        }

        return "Bukkit Permissions" + opsfallback;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String node, boolean isOpRequired) {

        if (!(sender instanceof Player)) {
            return true;
        }

        // NO one can access a null permission (mainly used for destinations):w
        if (node == null) {
            return false;
        }
        // Everyone can access an empty permission
        // Currently used for the PlayerDestination
        if (node.equals("")) {
            return true;
        }

        Player player = (Player) sender;

        boolean opFallback = this.plugin.getConfig().getBoolean("opfallback", true);
        if (this.permissions != null && this.permissions.has(player, node)) {
            // If Permissions is enabled we check against them.
            // this.plugin.log(Level.WARNING, "Allowed by P3/P2 ");
            return true;
        } else if (sender.hasPermission(node)) {
            // If Now check the bukkit permissions
            // this.plugin.log(Level.WARNING, "Allowed by BukkitPerms");
            return true;
        } else if (player.isOp() && opFallback) {
            // If Player is Op we always let them use it if they have the fallback enabled!
            // this.plugin.log(Level.WARNING, "Allowed by OP");
            return true;
        }

        // If the Player doesn't have Permissions and isn't an Op then
        // we return true if OP is not required, otherwise we return false
        // This allows us to act as a default permission guidance

        // If they have the op fallback disabled, NO commands will work without a permissions plugin.
        return !isOpRequired && opFallback;
    }

    @Override
    public boolean hasAnyPermission(CommandSender sender, List<String> nodes, boolean opRequired) {
        for (String node : nodes) {
            if (this.hasPermission(sender, node, opRequired)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAllPermission(CommandSender sender, List<String> nodes, boolean opRequired) {
        for (String node : nodes) {
            if (!this.hasPermission(sender, node, opRequired)) {
                return false;
            }
        }
        return true;
    }

}
