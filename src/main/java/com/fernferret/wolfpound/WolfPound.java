package com.fernferret.wolfpound;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import com.fernferret.allpay.ItemBank;
import com.fernferret.wolfpound.commands.AdoptCommand;
import com.fernferret.wolfpound.commands.DebugCommand;
import com.fernferret.wolfpound.commands.HelpCommand;
import com.fernferret.wolfpound.commands.LimitCommand;
import com.fernferret.wolfpound.commands.PriceCommand;
import com.fernferret.wolfpound.commands.ResetCommand;
import com.fernferret.wolfpound.commands.SetPropertyCommand;
import com.fernferret.wolfpound.commands.VersionCommand;
import com.fernferret.wolfpound.listeners.WPBlockListener;
import com.fernferret.wolfpound.listeners.WPPlayerListener;
import com.fernferret.wolfpound.listeners.WPPluginListener;
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

    public static int GlobalDebug = 0;
    public static final String chatPrefixError = ChatColor.DARK_RED + logPrefix + ChatColor.WHITE + " ";
    public static final String chatPrefix = ChatColor.DARK_GREEN + logPrefix + ChatColor.WHITE + " ";

    // public WPBankAdapter bank;
    public GenericBank bank;
    // For Multi-WorldSupport
    private AllPay banker;

    private WPPermissions permissions;
    private CommandHandler commandHandler;
    private WorldManager worldManager;
    private static double allpayversion = 3;
    private static double chversion = 1;

    @Override
    public void onEnable() {
        if (!this.validateAllpay()) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!this.validateCH()) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.permissions = new WPPermissions(this);
        this.commandHandler = new CommandHandler(this, this.permissions);
        this.worldManager = new WorldManager(this);
        this.banker = new AllPay(this, "[WolfPound]");
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

    private boolean validateAllpay() {
        try {
            this.banker = new AllPay(this, "Verify");
            if (this.banker.getVersion() >= allpayversion) {
                return true;
            } else {
                log.info(logPrefix + " - Version " + this.getDescription().getVersion() + " was NOT ENABLED!!!");
                log.info(logPrefix + " A plugin that has loaded before WolfPound has an incompatable version of AllPay (an internal library)!");
                log.info(logPrefix + " The Following Plugins MAY out of date!");
                log.info(logPrefix + " This plugin needs AllPay v" + allpayversion + " or higher and another plugin has loaded v" + this.banker.getVersion() + "!");
                log.info(logPrefix + AllPay.pluginsThatUseUs.toString());
                return false;
            }
        } catch (Throwable t) {
        }
        log.info(logPrefix + " - Version " + this.getDescription().getVersion() + " was NOT ENABLED!!!");
        log.info(logPrefix + " A plugin that has loaded before WolfPound has an incompatable version of AllPay (an internal library)!");
        log.info(logPrefix + " Check the logs for [AllPay] - Version ... for PLUGIN NAME to find the culprit! Then Yell at that dev!");
        log.info(logPrefix + " Or update that plugin :P");
        log.info(logPrefix + " This plugin needs AllPay v" + allpayversion + " or higher!");
        return false;
    }

    private boolean validateCH() {
        try {
            this.commandHandler = new CommandHandler(this, null);
            if (this.commandHandler.getVersion() >= chversion) {
                return true;
            } else {
                log.info(logPrefix + " - Version " + this.getDescription().getVersion() + " was NOT ENABLED!!!");
                log.info(logPrefix + " A plugin that has loaded before " + this.getDescription().getName() + " has an incompatable version of CommandHandler (an internal library)!");
                log.info(logPrefix + " Please contact this plugin author!!!!!!!");
                log.info(logPrefix + " This plugin needs CommandHandler v" + chversion + " or higher and another plugin has loaded v" + this.commandHandler.getVersion() + "!");
                return false;
            }
        } catch (Throwable t) {
        }
        log.info(logPrefix + " - Version " + this.getDescription().getVersion() + " was NOT ENABLED!!!");
        log.info(logPrefix + " A plugin that has loaded before " + this.getDescription().getName() + " has an incompatable version of CommandHandler (an internal library)!");
        log.info(logPrefix + " Please contact this plugin author!!!!!!!");
        log.info(logPrefix + " This plugin needs CommandHandler v" + chversion + " or higher!");
        return false;
    }

    private void loadConfiguration() {
        getDataFolder().mkdirs();
        this.configWP = new Configuration(new File(this.getDataFolder(), WOLF_POUND_CONFIG));
        this.configWP.load();
        if (this.configWP.getKeys().size() == 0) {
            this.configWP.setProperty("adopt.price", 0);
            this.configWP.setProperty("adopt.type", -1);
            this.configWP.setProperty("adopt.limit", 1);
            this.configWP.setProperty("adopt.aggro", "friend");
            this.configWP.save();
            System.out.print("Createing defaults...");
        }
        this.worldManager.setGlobalWorld(new WPWorld(null, this.getConfig()));
        if (configWP.getKeys("adopt.worlds") != null) {
            for (String s : configWP.getKeys("adopt.worlds")) {
                this.worldManager.addWorld(s, new WPWorld(s, this.getConfig()));
                this.log(Level.FINE, "Loaded WolfPound Config for: " + s);
            }
        }

        registerCommands();
    }

    private void registerCommands() {
        // Page 1
        this.commandHandler = new CommandHandler(this, this.permissions);
        this.commandHandler.registerCommand(new HelpCommand(this));
        this.commandHandler.registerCommand(new AdoptCommand(this));
        this.commandHandler.registerCommand(new LimitCommand(this));
        this.commandHandler.registerCommand(new PriceCommand(this));
        this.commandHandler.registerCommand(new SetPropertyCommand(this));
        this.commandHandler.registerCommand(new ResetCommand(this));
        this.commandHandler.registerCommand(new VersionCommand(this));
        this.commandHandler.registerCommand(new DebugCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!this.isEnabled()) {
            sender.sendMessage("WolfPound is Disabled!");
            return true;
        }
        ArrayList<String> allArgs = new ArrayList<String>(Arrays.asList(args));
        allArgs.add(0, command.getName());
        return this.commandHandler.locateAndRunCommand(sender, allArgs);
    }

    private void getHumanReadableAdoptLimitMessage(CommandSender s, int limit, String end) {
        if (limit == -1) {
            s.sendMessage(chatPrefix + "WARNING: There is no limit to how many wolves you can adopt at once " + end);
        } else {
            s.sendMessage(chatPrefix + "You can adopt " + limit + " wolves at once " + end);
        }
    }

    private void getHumanReadablePriceMessage(CommandSender sender, double price, int type, String end) {
        if (price == 0) {
            sender.sendMessage(chatPrefix + "Adopting a wolf is " + ChatColor.GREEN + "FREE " + ChatColor.WHITE + end);
        } else if (bank instanceof ItemBank && type == -1) {
            if (this.permissions.hasPermission(sender, PERM_ADMIN, true))
                sender.sendMessage(chatPrefixError + "You have set the price to a currency, yet no currency plugin is installed! Use" + ChatColor.AQUA + " /wp set curr " + ChatColor.GREEN + "{ITEM_ID}" + ChatColor.WHITE + " to set an item type for trade or install an economy plugin!");
            sender.sendMessage(chatPrefix + "Adopting a wolf is " + ChatColor.GREEN + "FREE " + ChatColor.WHITE + end);
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(chatPrefix + "It costs " + bank.getFormattedAmount((Player) sender, price, type) + " to adopt a wolf " + end);
            } else {
                sender.sendMessage(chatPrefix + "It costs " + bank.getFormattedAmount(null, price, type) + " to adopt a wolf " + end);
            }
        }
    }

    public void sendWolfPrice(CommandSender s, String world) {
        if (this.permissions.hasPermission(s, PERM_ADOPT, true)) {
            if (world.equalsIgnoreCase("all")) {
                String everywhere = "everywhere";
                for (String worldName : this.worldManager.getWorldNames()) {
                    WPWorld w = this.worldManager.getWorld(worldName);
                    getHumanReadablePriceMessage(s, w.getPrice(), w.getCurrency(), "in " + ChatColor.AQUA + worldName + ChatColor.WHITE + "!");
                    everywhere = "everywhere else";
                }
                getHumanReadablePriceMessage(s, this.worldManager.getGlobalWorld().getPrice(), this.worldManager.getWorld(world).getCurrency(), ChatColor.AQUA + everywhere + ChatColor.WHITE + "!");
            } else {
                getHumanReadablePriceMessage(s, this.worldManager.getWorld(world).getPrice(), this.worldManager.getWorld(world).getCurrency(), "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
            }
        }
    }

    public void sendWolfLimit(CommandSender sender, String world) {

        if (this.permissions.hasPermission(sender, PERM_ADOPT, false)) {
            // WPWorld w = this.getWolfPoundWorld(world);
            if (world.equalsIgnoreCase("all")) {
                String everywhere = "everywhere";
                for (String worldName : this.worldManager.getWorldNames()) {
                    WPWorld w = this.worldManager.getWorld(worldName);
                    getHumanReadableAdoptLimitMessage(sender, w.getLimit(), "in " + ChatColor.AQUA + worldName + ChatColor.WHITE + "!");
                    everywhere = "everywhere else";
                }
                getHumanReadableAdoptLimitMessage(sender, this.worldManager.getGlobalWorld().getLimit(), ChatColor.AQUA + everywhere + ChatColor.WHITE + "!");
            } else {
                getHumanReadableAdoptLimitMessage(sender, this.worldManager.getWorld(world).getLimit(), "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
            }
        }
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
        WPWorld w = this.worldManager.getWorld(world);

        if (w.getLimit() >= 0) {
            wolves = (wolves > w.getLimit()) ? w.getLimit() : wolves;
        }
        if (this.permissions.hasPermission(p, PERM_ADOPT, true) && bank.hasEnough(p, w.getPrice() * wolves, w.getCurrency())) {

            for (int i = 0; i < wolves; i++) {
                if (spawnWolf(p, w.getAggro())) {
                    bank.pay(p, w.getPrice(), w.getCurrency());
                }
            }
        }
    }

    public boolean spawnWolf(Player p, WolfAggro aggro) {

        Wolf w = (Wolf) p.getWorld().spawnCreature(p.getLocation(), CreatureType.WOLF);
        w.setHealth(20);
        if (aggro != null && aggro == WolfAggro.FRIEND) {
            EntityTameEvent event = new EntityTameEvent(w, p);
            this.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                w.damage(200, w);
                w.remove();
                p.sendMessage(chatPrefix + "You already have enough tame wolves silly.");
                return false;
            } else {
                w.setOwner(p);
                w.setSitting(false);
                p.sendMessage(chatPrefix + "BAM! Your trusty companion is ready for battle!");
            }
        } else if (aggro != null && aggro == WolfAggro.ANGRY) {
            w.setAngry(true);
            p.sendMessage(chatPrefixError + "Run Awayyyy! That thing looks angry!");
        } else {
            p.sendMessage(chatPrefix + "Woah! A wolf! You should befriend it!");
        }
        return true;
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
        // debugLog.log(level, "[WP-Debug] " + msg);
    }

    public Configuration getConfig() {
        return this.configWP;
    }

    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    public WPPermissions getPermissions() {
        return this.permissions;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }

}
