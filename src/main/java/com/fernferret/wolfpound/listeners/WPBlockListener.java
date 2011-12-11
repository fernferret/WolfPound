package com.fernferret.wolfpound.listeners;

import com.fernferret.wolfpound.WolfPound;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;

public class WPBlockListener extends BlockListener {
    private final WolfPound plugin;

    public WPBlockListener(final WolfPound plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSignChange(SignChangeEvent event) {
        Player p = event.getPlayer();

        if (event.getLine(0).equalsIgnoreCase("[WolfPound]")) {
            boolean secondLineValid = validateItemLine(event.getLine(1), p);
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

    private boolean validateItemLine(String line, Player p) {
        String[] items = line.split(":");
        if (items == null || items.length == 0) {
            return true;
        } else if (items.length == 1) {
            return checkLeftSide(p, items[0]);
        } else if (items.length == 2) {
            return checkLeftSide(p, items[0]) && checkRightSide(p, items[1]);
        }
        p.sendMessage(WolfPound.chatPrefixError + "You have more than 1 colon on your 2nd line!");
        return false;
    }

    public static boolean checkRightSide(Player p, String item) {
        int result = getRightSide(item);
        if (result == WolfPound.MULTIPLE_ITEMS_FOUND) {
            p.sendMessage(WolfPound.chatPrefixError + "Found multiple items that match: " + item);
            return false;
        } else if (result == WolfPound.NO_ITEM_FOUND) {
            p.sendMessage(WolfPound.chatPrefixError + "Could not find item: " + item);
            return false;
        }
        return true;
    }

    public static boolean checkItem(String item) {
        int result = getRightSide(item);
        if (result == WolfPound.MULTIPLE_ITEMS_FOUND) {
            return false;
        } else if (result == WolfPound.NO_ITEM_FOUND) {
            return false;
        }
        return true;
    }

    public static boolean checkLeftSide(Player p, String item) {
        double leftSide = getLeftSide(item);
        if (leftSide == WolfPound.INVALID_PRICE) {
            p.sendMessage(WolfPound.chatPrefixError + "Please enter a valid price on Line 2");
            return false;
        }
        return true;
    }

    public static int getRightSide(String item) {
        try {
            Material m = Material.getMaterial(Integer.parseInt(item));
            return m.getId();
        } catch (NullPointerException e) {
            return WolfPound.NO_ITEM_FOUND;
        } catch (NumberFormatException e) {
            Material m = Material.matchMaterial(item);
            if (m != null) {
                return m.getId();
            }
        }

        return parseMaterialFromString(item);
    }

    public static double getLeftSide(String item) {
        if (item.equalsIgnoreCase("free") || item.length() == 0) {
            return WolfPound.MONEY_ITEM_FOUND;
        }
        try {
            return Double.parseDouble(item.replaceAll("\\D", ""));

        } catch (NumberFormatException e) {

        }
        return WolfPound.INVALID_PRICE;
    }

    public static int parseMaterialFromString(String materialString) {
        ArrayList<Material> materials = new ArrayList<Material>();
        for (Material mat : Material.values()) {
            materialString = materialString.replace(" ", "_");
            materialString = materialString.toUpperCase();
            String materialRegex = "(.*" + materialString + ".*)";
            if (mat.toString().matches(materialRegex)) {
                materials.add(mat);
            }
        }
        if (materials.size() == 1) {
            return materials.get(0).getId();
        } else if (materials.size() > 1) {
            return WolfPound.MULTIPLE_ITEMS_FOUND;
        }
        return WolfPound.NO_ITEM_FOUND;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();
            // Don't let the user make this an auth'd sign
            if (sign.getLine(0).matches(WolfPound.prefixValid + "(?i)\\[WolfPound\\]")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
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
