package com.fernferret.wolfpound;

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;
import com.fernferret.allpay.ItemBank;
import com.fernferret.wolfpound.commands.*;
import com.fernferret.wolfpound.listeners.WPBlockListener;
import com.fernferret.wolfpound.listeners.WPPlayerListener;
import com.fernferret.wolfpound.listeners.WPPluginListener;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public FileConfiguration configWP;

    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String logPrefix = "[WolfPound]";

    public static int GlobalDebug = 0;
    public static final String chatPrefixError = ChatColor.DARK_RED + logPrefix + ChatColor.WHITE + " ";
    public static final String chatPrefix = ChatColor.DARK_GREEN + logPrefix + ChatColor.WHITE + " ";

    public GenericBank bank;
    private AllPay banker;

    private WPPermissions permissions;
    private CommandHandler commandHandler;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
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
        pm.registerEvents(this.playerListener, this);
        pm.registerEvents(this.blockListener, this);
        pm.registerEvents(this.pluginListener, this);

        this.bank = banker.loadEconPlugin();
    }

    private void loadConfiguration() {
        getDataFolder().mkdirs();
        this.configWP = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), WOLF_POUND_CONFIG));
        if (this.configWP.getConfigurationSection("adopt") == null) {
            this.configWP.createSection("adopt");
        }
        if (this.configWP.getConfigurationSection("adopt").getKeys(false).size() == 0) {
            this.configWP.set("adopt.price", 0);
            this.configWP.set("adopt.type", -1);
            this.configWP.set("adopt.limit", 1);
            this.configWP.set("adopt.aggro", "friend");
            System.out.print("Creating defaults...");
        }
        this.worldManager.setGlobalWorld(new WPWorld(null, this.getWPConfig(), this));
        ConfigurationSection worlds = configWP.getConfigurationSection("adopt.worlds");
        if(worlds == null) {
            worlds = configWP.createSection("adopt.worlds");
        }
        Set<String> keys = worlds.getKeys(false);
        if (keys != null) {
            for (String s : keys) {
                this.worldManager.addWorld(s, new WPWorld(s, this.getWPConfig(), this));
                this.log(Level.FINE, "Loaded WolfPound Config for: " + s);
            }
        }
        this.saveConfig();
        registerCommands();
    }

    public void saveConfig() {
        try {
            this.configWP.save(new File(this.getDataFolder(), WOLF_POUND_CONFIG));
        } catch (IOException e) {
            this.log(Level.SEVERE, "Error saving wolfpound config.");
        }
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
     * @param p      The player
     * @param wolves How many wolves
     */
    public void adoptWolf(Player p, int wolves, AnimalAge age, WolfAggro aggro) {
        String world = p.getWorld().getName();
        // this will return the world settings the player is in
        // or the global if there are no settings
        WPWorld w = this.worldManager.getWorld(world);

        if (w.getLimit() >= 0) {
            wolves = (wolves > w.getLimit()) ? w.getLimit() : wolves;
        }
        if (this.permissions.hasPermission(p, PERM_ADOPT, true) && bank.hasEnough(p, w.getPrice() * wolves, w.getCurrency())) {
            for (int i = 0; i < wolves; i++) {
                if (spawnWolf(p, aggro, age)) {
                    bank.take(p, w.getPrice(), w.getCurrency());
                }
            }
        }
    }

    public boolean spawnWolf(Player p, WolfAggro aggro, AnimalAge age) {
        Wolf w = (Wolf) p.getWorld().spawnCreature(p.getLocation(), CreatureType.WOLF);
        w.setHealth(8);
        w.setAge(age.getAge());
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

    public FileConfiguration getWPConfig() {
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
