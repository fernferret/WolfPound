package com.fernferret.wolfpound;

import com.pneumaticraft.commandhandler.PermissionsInterface;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class WPPermissions implements PermissionsInterface {

    private WolfPound plugin;

    public WPPermissions(WolfPound plugin) {
        this.plugin = plugin;
    }

    public String getType() {
        return "Bukkit Permissions";
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

        if (sender.hasPermission(node)) {
            return true;
        } else if (player.isOp()) {
            return true;
        }
        return !isOpRequired;
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
