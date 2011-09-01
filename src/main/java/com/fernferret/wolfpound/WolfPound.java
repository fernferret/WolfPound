package com.fernferret.wolfpound;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import com.fernferret.allpay.ItemBank;
import com.fernferret.wolfpound.commands.AdoptWolfCommand;
import com.fernferret.wolfpound.commands.HelpCommand;
import com.fernferret.wolfpound.commands.LimitCommand;
import com.fernferret.wolfpound.commands.PriceCommand;
import com.fernferret.wolfpound.listeners.WPBlockListener;
import com.fernferret.wolfpound.listeners.WPPlayerListener;
import com.fernferret.wolfpound.listeners.WPPluginListener;
import com.nijiko.permissions.PermissionHandler;
import com.pneumaticraft.commandhandler.CommandHandler;

public class WolfPound extends JavaPlugin {

    public static final String PERM_CREATE = "wolfpound.create";
    public static final String PERM_USE = "wolfpound.use";
    public static final String PERM_ADOPT = "wolfpound.adopt";
    public static final String PERM_ADMIN = "wolfpound.admin";

    private static final String WOLF_POUND_CONFIG = "WolfPound.yml";

    public static final int NO_ITEM_FOUND = -2;
    public static final int MULTIPLE_ITEMS_FOUND = -3;
    public static final int MONEY_ITEM_FOUND = -1;
    public static final int INVALID_PRICE = -4;
    public static final ChatColor prefixValid = ChatColor.DARK_BLUE;
    public static final ChatColor prefixInvalid = ChatColor.DARK_RED;

    private WPPlayerListener playerListener;
    private WPBlockListener blockListener;
    private WPPluginListener pluginListener;
    public Configuration configWP;

    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String logPrefix = "[WolfPound]";

    public static PermissionHandler Permissions = null;
    public static boolean usePermissions = false;
    private static int GlobalDebug;
    public static final String chatPrefixError = ChatColor.DARK_RED + logPrefix + ChatColor.WHITE + " ";
    public static final String chatPrefix = ChatColor.DARK_GREEN + logPrefix + ChatColor.WHITE + " ";
    private static final String ADOPT_KEY = "adopt";
    private static final String MULTI_WORLD_KEY = "worlds";
    public static final String ADOPT_NEUTRAL = "neutral";
    public static final String ADOPT_FRIEND = "friend";
    public static final String ADOPT_ANGRY = "angry";

    // public WPBankAdapter bank;
    public GenericBank bank;
    // For Multi-WorldSupport
    private AllPay banker = new AllPay(this, "[WolfPound]");

    private WPPermissions permissions = new WPPermissions(this);
    private CommandHandler commandHandler = new CommandHandler(this, this.permissions);
    private Map<String, WPWorld> worlds;
    private WPWorld globalWorld;

    @Override
    public void onEnable() {
        this.worlds = new HashMap<String, WPWorld>();
        this.globalWorld = new WPWorld(null);
        loadConfiguration();
        playerListener = new WPPlayerListener(this);
        blockListener = new WPBlockListener(this);
        pluginListener = new WPPluginListener(this);

        log.info(logPrefix + " - Version " + this.getDescription().getVersion() + " Enabled");

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);

        this.bank = banker.loadEconPlugin();
    }

    public WPWorld getWolfPoundWorld(String world) {
        if (this.worlds.containsKey(world)) {
            return this.worlds.get(world);
        }
        return this.globalWorld;
    }

    public WPWorld getGlobalWorld() {
        return this.globalWorld;
    }

    private void loadConfiguration() {
        getDataFolder().mkdirs();
        configWP = new Configuration(new File(this.getDataFolder(), WOLF_POUND_CONFIG));
        configWP.load();
        for (String s : configWP.getKeys("adopt.worlds")) {
            World w = this.getServer().getWorld(s);
            if (w != null) {
                this.worlds.put(s, new WPWorld(w.getName()));
                this.log(Level.FINE, "Loaded WolfPound Config for: " + w);
            } else {
                this.log(Level.FINE, "NO WolfPound Config Found for: " + w);
            }
        }
        // If the config was empty or not specified correctly, overwrite it!
        this.globalWorld = new WPWorld(null);
        registerCommands();
    }

    private void registerCommands() {
        // Page 1

        this.commandHandler = new CommandHandler(this, this.permissions);
        this.commandHandler.registerCommand(new HelpCommand(this));
        this.commandHandler.registerCommand(new AdoptWolfCommand(this));
        this.commandHandler.registerCommand(new LimitCommand(this));
        this.commandHandler.registerCommand(new PriceCommand(this));
    }

    public void removeWorld(String string, Player p) {
        if (configWP.getProperty(ADOPT_KEY + "." + MULTI_WORLD_KEY + "." + string) != null) {
            p.sendMessage(chatPrefix + "Reverting " + ChatColor.GREEN + string + ChatColor.WHITE + " to global default...");
            configWP.removeProperty(ADOPT_KEY + "." + MULTI_WORLD_KEY + "." + string);
            configWP.save();
        }

    }

    private void getHumanReadableAdoptLimitMessage(Player p, int limit, String end) {
        if (limit == -1) {
            p.sendMessage(chatPrefix + "WARNING: There is no limit to how many wolves you can adopt at once " + end);
        } else {
            p.sendMessage(chatPrefix + "You can adopt " + limit + " wolves at once " + end);
        }
    }

    private void getHumanReadablePriceMessage(Player p, double price, int type, String end) {
        if (price == 0) {
            p.sendMessage(chatPrefix + "Adopting a wolf is " + ChatColor.GREEN + "FREE " + ChatColor.WHITE + end);
        } else if (bank instanceof ItemBank && type == -1) {
            if (this.permissions.hasPermission(p, PERM_ADMIN, true))
                p.sendMessage(chatPrefixError + "You have set the price to a currency, yet no currency plugin is installed! Use /wpsettype to set an item type for trade or install an economy plugin!");
            p.sendMessage(chatPrefix + "Adopting a wolf is " + ChatColor.GREEN + "FREE " + ChatColor.WHITE + end);
        } else {
            p.sendMessage(chatPrefix + "It costs " + bank.getFormattedAmount(p, price, type) + " to adopt a wolf " + end);
        }
    }

    public void sendWolfPrice(Player p, String world) {
        if (this.permissions.hasPermission(p, PERM_ADOPT, true)) {
            if (world.equalsIgnoreCase("all")) {
                String everywhere = "everywhere";
                for (String worldName : this.worlds.keySet()) {
                    WPWorld w = this.worlds.get(worldName);
                    getHumanReadablePriceMessage(p, w.getPrice(), w.getCurrency(), "in " + ChatColor.AQUA + worldName + ChatColor.WHITE + "!");
                    everywhere = "everywhere else";
                }

                getHumanReadablePriceMessage(p, this.globalWorld.getPrice(), this.getWolfPoundWorld(world).getCurrency(), ChatColor.AQUA + everywhere + ChatColor.WHITE + "!");
            } else {
                getHumanReadablePriceMessage(p, this.getWolfPoundWorld(world).getPrice(), this.getWolfPoundWorld(world).getCurrency(), "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
            }
        }
    }

    public void sendWolfLimit(Player p, String world) {
        
        if (this.permissions.hasPermission(p, PERM_ADOPT, false)) {
            //WPWorld w = this.getWolfPoundWorld(world);
            if (world.equalsIgnoreCase("all")) {
                String everywhere = "everywhere";
                for (String worldName : this.worlds.keySet()) {
                    WPWorld w = this.worlds.get(worldName);
                    getHumanReadableAdoptLimitMessage(p, w.getLimit(), "in " + ChatColor.AQUA + worldName + ChatColor.WHITE + "!");
                    everywhere = "everywhere else";
                }
                getHumanReadableAdoptLimitMessage(p, this.globalWorld.getLimit(), ChatColor.AQUA + everywhere + ChatColor.WHITE + "!");
            } else {
                getHumanReadableAdoptLimitMessage(p, this.getWolfPoundWorld(world).getLimit(), "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
            }
        }
    }

    public Map<String, WPWorld> getWolfPoundWorlds() {
        return this.worlds;
    }

    public int getWolfInt(String wolves, Player p, String errorMsg) {
        try {
            return Math.abs(Integer.parseInt(wolves));
        } catch (NumberFormatException e) {
            p.sendMessage(chatPrefixError + errorMsg);
            return 0;
        }
    }

    /**
     * Allows the user to adopt a wolf
     * 
     * @param p The player
     * @param wolves How many wolves
     */
    public void adoptWolf(Player p, int wolves) {
        String world = p.getWorld().getName();
        // this will return the world settings the player is in
        // or the global if there are no settings
        WPWorld w = this.getWolfPoundWorld(world);
        
        if (w.getLimit() >= 0) {
            wolves = (wolves > w.getLimit()) ? w.getLimit() : wolves;
        }
        if (this.permissions.hasPermission(p, PERM_ADOPT, true) && bank.hasEnough(p, w.getPrice() * wolves, w.getCurrency())) {
            bank.pay(p, w.getPrice() * wolves, w.getCurrency());
            for (int i = 0; i < wolves; i++) {
                spawnWolf(p, w.getAggro());
            }
        }
    }

    public void spawnWolf(Player p, WolfAggro aggro) {

        Wolf w = (Wolf) p.getWorld().spawnCreature(p.getLocation(), CreatureType.WOLF);
        w.setHealth(20);
        if (aggro != null && aggro == WolfAggro.FRIEND) {
            w.setOwner(p);
            w.setSitting(false);
            p.sendMessage(chatPrefix + "BAM! Your trusty companion is ready for battle!");
        } else if (aggro != null && aggro == WolfAggro.ANGRY) {
            w.setAngry(true);
            p.sendMessage(chatPrefixError + "Run Awayyyy! That thing looks angry!");
        } else {
            p.sendMessage(chatPrefix + "Woah! A wolf! You should befriend it!");
        }

    }

    @Override
    public void onDisable() {
        log.info(logPrefix + " - Disabled");
    }

    public boolean blockIsValidWolfSign(Sign s) {
        return s.getLine(0).equals(prefixValid + "[WolfPound]");
    }

    public AllPay getBanker() {
        return this.banker;
    }

    public void setBank(GenericBank bank) {
        this.bank = bank;
    }

    public void log(Level level, String msg) {
        staticLog(level, msg);
    }

    public static void staticLog(Level level, String msg) {
        if (level == Level.FINE && GlobalDebug >= 1) {
            staticDebugLog(Level.INFO, msg);
            return;
        } else if (level == Level.FINER && GlobalDebug >= 2) {
            staticDebugLog(Level.INFO, msg);
            return;
        } else if (level == Level.FINEST && GlobalDebug >= 3) {
            staticDebugLog(Level.INFO, msg);
            return;
        } else if (level != Level.FINE && level != Level.FINER && level != Level.FINEST) {
            log.log(level, "[WolfPound]" + " " + msg);
        }
    }

    public static void staticDebugLog(Level level, String msg) {
        log.log(level, "[WP-Debug] " + msg);
        //debugLog.log(level, "[WP-Debug] " + msg);
    }

    public Configuration getConfig() {
        return this.configWP;
    }

    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

}
