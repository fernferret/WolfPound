package com.fernferret.wolfpound.utils;

import com.fernferret.wolfpound.WolfPound;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SignTools {

    public static boolean validateItemLine(String line, Player p) {
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
}
