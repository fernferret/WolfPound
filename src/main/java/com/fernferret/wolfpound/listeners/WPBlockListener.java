package com.fernferret.wolfpound.listeners;

import com.fernferret.wolfpound.WolfPound;
import com.fernferret.wolfpound.utils.SignTools;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;

public class WPBlockListener implements Listener {
    private final WolfPound plugin;

    public WPBlockListener(final WolfPound plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void signChange(SignChangeEvent event) {
        Player p = event.getPlayer();

        if (event.getLine(0).equalsIgnoreCase("[WolfPound]") || event.getLine(0).equalsIgnoreCase("[wp]")) {
            boolean secondLineValid = SignTools.validateItemLine(event.getLine(1), p);
            if (plugin.getPermissions().hasPermission(p, WolfPound.PERM_CREATE, true) && secondLineValid) {
                event.getPlayer().sendMessage(WolfPound.chatPrefix + "Successfully created Wolf Pound!");
                event.setLine(0, WolfPound.prefixValid + "[WolfPound]");
            } else {

                event.setLine(0, WolfPound.prefixInvalid + "[WolfPound]");
            }
            if (!secondLineValid) {
                event.getPlayer().sendMessage(WolfPound.chatPrefixError + "Pound creation failed!");
                event.setLine(1, WolfPound.prefixInvalid + event.getLine(1));
            }
        }
        // TODO: Make wolves assigned to people
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (event.getBlock() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();
            // Don't let the user make this an auth'd sign
            if (sign.getLine(0).matches(WolfPound.prefixValid + "(?i)\\[WolfPound\\]")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Sign)) {
            return;
        }
        Sign s = (Sign) event.getBlock().getState();
        if (plugin.blockIsValidWolfSign(s)) {
            if (!plugin.getPermissions().hasPermission(event.getPlayer(), WolfPound.PERM_CREATE, true)) {
                event.setCancelled(true);
            } else {
                event.getPlayer().sendMessage(WolfPound.chatPrefixError + "Destroying Wolf Pound");
            }
        }
    }
}
