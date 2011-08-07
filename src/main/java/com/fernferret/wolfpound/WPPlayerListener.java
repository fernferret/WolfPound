package com.fernferret.wolfpound;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.block.Sign;

public class WPPlayerListener extends PlayerListener {
    private final WolfPound plugin;

    public WPPlayerListener(final WolfPound plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Sign s = (Sign) event.getClickedBlock().getState();
            if (plugin.blockIsValidWolfSign(s) && plugin.hasPermission(p, WolfPound.PERM_USE)) {
                // We have a valid pound!
                if (checkSignParams(s, 1, p)) {
                    // We have valid pound params!
                    double price = getPrice(s, 1, p);
                    int item = getType(s, 1, p);
                    String aggro = getAggro(s, 2, p);
                    if (plugin.bank.hasEnough(p, price, item)) {
                        plugin.bank.pay(p, price, item);
                        plugin.spawnWolf(p, aggro);
                    }
                }
            }
        }
    }

    private String getAggro(Sign s, int l, Player p) {

        String line = s.getLine(l);
        if (line.matches("(?i)(.*" + WolfPound.ADOPT_FRIEND + ".*)")) {
            return WolfPound.ADOPT_FRIEND;
        }
        if (line.matches("(?i)(.*" + WolfPound.ADOPT_NEUTRAL + ".*)")) {
            return WolfPound.ADOPT_NEUTRAL;
        }
        if (line.matches("(?i)(.*" + WolfPound.ADOPT_ANGRY + ".*)")) {
            return WolfPound.ADOPT_ANGRY;
        }
        return "";
    }

    private boolean checkSignParams(Sign s, int l, Player p) {
        String line = s.getLine(l);
        String[] items = line.split(":");
        if (items.length == 0) {
            return true;
        }
        if (items.length == 1) {
            return WPBlockListener.checkLeftSide(p, items[0]);
        }
        if (items.length == 2) {
            return WPBlockListener.checkLeftSide(p, items[0]) && WPBlockListener.checkRightSide(p, items[1]);
        }
        return false;
    }

    private Double getPrice(Sign s, int l, Player p) {
        String line = s.getLine(l);
        String[] items = line.split(":");
        if (items.length > 0) {
            return WPBlockListener.getLeftSide(items[0]);
        }
        return 0.0;
    }

    private int getType(Sign s, int l, Player p) {

        String line = s.getLine(l);
        String[] items = line.split(":");
        if (items.length > 1) {
            return WPBlockListener.getRightSide(items[1]);
        }
        return WolfPound.MONEY_ITEM_FOUND;

    }
}
