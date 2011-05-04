package com.fernferret.wolfpound;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.fernferret.allpay.*;
import com.fernferret.wolfpound.commands.CommandAdoptWolf;
import com.fernferret.wolfpound.commands.CommandSetAggro;
import com.fernferret.wolfpound.commands.CommandSetPrice;
import com.fernferret.wolfpound.commands.WolfPoundCommand;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class WolfPound extends JavaPlugin {
	
	public static final String PERM_CREATE = "wolfpound.create";
	public static final String PERM_USE = "wolfpound.use";
	public static final String PERM_ADOPT = "wolfpound.adopt";
	public static final String PERM_ADMIN = "wolfpound.admin";
	
	private static final String WOLF_POUND_CONFIG = "WolfPound.yml";
	
	private static final double DEFAULT_ADOPT_PRICE = 0.0;
	
	private static final int DEFAULT_ADOPT_TYPE = -1;
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
	public static final String chatPrefixError = ChatColor.DARK_RED + "[WolfPound]" + ChatColor.WHITE + " ";
	public static final String chatPrefix = ChatColor.DARK_GREEN + "[WolfPound]" + ChatColor.WHITE + " ";
	private static final String ADOPT_KEY = "adopt";
	private static final String AGGRO_KEY = "aggro";
	private static final String LIMIT_KEY = "limit";
	private static final String PRICE_KEY = "price";
	private static final String TYPE_KEY = "type";
	private static final String MULTI_WORLD_KEY = "worlds";
	private static final int DEFAULT_ADOPT_LIMIT = 10;
	public static final String ADOPT_NEUTRAL = "neutral";
	public static final String ADOPT_FRIEND = "friend";
	public static final String ADOPT_ANGRY = "angry";
	private static final String DEFAULT_ADOPT_AGGRO = ADOPT_FRIEND;
	
	//public WPBankAdapter bank;
	public GenericBank bank;
	// Used as an item id for transactions with the /adopt command
	private double adoptPrice = DEFAULT_ADOPT_PRICE;
	private int adoptType = DEFAULT_ADOPT_TYPE;
	private int adoptLimit = DEFAULT_ADOPT_LIMIT;
	private String adoptAggro = DEFAULT_ADOPT_AGGRO;
	// For Multi-WorldSupport
	private HashMap<String, Double> adoptPriceWorlds = new HashMap<String, Double>();
	private HashMap<String, Integer> adoptTypeWorlds = new HashMap<String, Integer>();
	private HashMap<String, Integer> adoptLimitWorlds = new HashMap<String, Integer>();
	private HashMap<String, String> adoptAggroWorlds = new HashMap<String, String>();
	
	@Override
	public void onEnable() {
		loadConfiguration();
		playerListener = new WPPlayerListener(this);
		blockListener = new WPBlockListener(this);
		pluginListener = new WPPluginListener(this);
		
		log.info(logPrefix + " - Version " + this.getDescription().getVersion() + " Enabled");
		
//		if (setEconPlugin()) {
//			log.info(logPrefix + bank.getEconUsed());
//		}
		checkPermissions();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
	}
	
	private void loadConfiguration() {
		getDataFolder().mkdirs();
		configWP = new Configuration(new File(this.getDataFolder(), WOLF_POUND_CONFIG));
		configWP.load();
		registerCommands();
		// If the config was empty or not specified correctly, overwrite it!
		checkPriceProperty("");
		checkAggroProperty("");
		checkTypeProperty("");
		checkLimitProperty("");
		
		Map<String, ConfigurationNode> nodes = configWP.getNodes(ADOPT_KEY + "." + MULTI_WORLD_KEY);
		if (nodes != null && nodes.size() > 0) {
			
			for (String s : nodes.keySet()) {
				checkPriceProperty(s);
				checkTypeProperty(s);
				checkLimitProperty(s);
				checkAggroProperty(s);
				String world = MULTI_WORLD_KEY + "." + s + ".";
				this.adoptAggro = configWP.getString(ADOPT_KEY + "." + PRICE_KEY, DEFAULT_ADOPT_AGGRO);
				this.adoptPriceWorlds.put(s, configWP.getDouble(ADOPT_KEY + "." + world + PRICE_KEY, DEFAULT_ADOPT_PRICE));
				this.adoptTypeWorlds.put(s, configWP.getInt(ADOPT_KEY + "." + world + TYPE_KEY, DEFAULT_ADOPT_TYPE));
				this.adoptLimitWorlds.put(s, configWP.getInt(ADOPT_KEY + "." + world + LIMIT_KEY, DEFAULT_ADOPT_LIMIT));
			}
		}
		this.adoptAggro = configWP.getString(ADOPT_KEY + "." + AGGRO_KEY, DEFAULT_ADOPT_AGGRO);
		this.adoptPrice = configWP.getDouble(ADOPT_KEY + "." + PRICE_KEY, DEFAULT_ADOPT_PRICE);
		this.adoptType = configWP.getInt(ADOPT_KEY + "." + TYPE_KEY, DEFAULT_ADOPT_TYPE);
		this.adoptLimit = configWP.getInt(ADOPT_KEY + "." + LIMIT_KEY, DEFAULT_ADOPT_LIMIT);
		
	}
	

	
	private boolean aggroValueCheck(String value) {
		return (value.equalsIgnoreCase(ADOPT_ANGRY) || value.equalsIgnoreCase(ADOPT_FRIEND) || value.equalsIgnoreCase(ADOPT_NEUTRAL));
	}
	
	private void checkLimitProperty(String world) {
		String worldString = world;
		if (!world.equalsIgnoreCase("")) {
			world = MULTI_WORLD_KEY + "." + world + ".";
		}
		if (configWP.getProperty(ADOPT_KEY + "." + world + LIMIT_KEY) == null || !(configWP.getProperty(ADOPT_KEY + "." + world + LIMIT_KEY) instanceof Integer)) {
			configWP.setProperty(ADOPT_KEY + "." + world + LIMIT_KEY, DEFAULT_ADOPT_LIMIT);
			configWP.save();
			if (worldString.equalsIgnoreCase("")) {
				this.adoptLimit = DEFAULT_ADOPT_LIMIT;
			} else {
				this.adoptLimitWorlds.put(worldString, DEFAULT_ADOPT_LIMIT);
			}
		}
	}
	
	private void checkTypeProperty(String world) {
		String worldString = world;
		if (!world.equalsIgnoreCase("")) {
			world = MULTI_WORLD_KEY + "." + world + ".";
		}
		if (configWP.getProperty(ADOPT_KEY + "." + world + TYPE_KEY) == null) {
			configWP.setProperty(ADOPT_KEY + "." + world + TYPE_KEY, DEFAULT_ADOPT_TYPE);
			configWP.save();
			if (worldString.equalsIgnoreCase("")) {
				this.adoptType = DEFAULT_ADOPT_TYPE;
			} else {
				this.adoptTypeWorlds.put(worldString, DEFAULT_ADOPT_TYPE);
			}
		} else if (!WPBlockListener.checkItem(configWP.getInt(ADOPT_KEY + "." + world + TYPE_KEY, DEFAULT_ADOPT_TYPE) + "")) {
			configWP.setProperty(ADOPT_KEY + "." + world + TYPE_KEY, DEFAULT_ADOPT_TYPE);
			configWP.save();
			if (worldString.equalsIgnoreCase("")) {
				this.adoptType = DEFAULT_ADOPT_TYPE;
			} else {
				this.adoptTypeWorlds.put(worldString, DEFAULT_ADOPT_TYPE);
			}
		}
	}
	
	private void checkPriceProperty(String world) {
		String worldString = world;
		if (!world.equalsIgnoreCase("")) {
			world = MULTI_WORLD_KEY + "." + world + ".";
		}
		if (configWP.getProperty(ADOPT_KEY + "." + world + PRICE_KEY) == null || !(configWP.getProperty(ADOPT_KEY + "." + world + PRICE_KEY) instanceof Double)) {
			configWP.setProperty(ADOPT_KEY + "." + world + PRICE_KEY, DEFAULT_ADOPT_PRICE);
			configWP.save();
			if (worldString.equalsIgnoreCase("")) {
				this.adoptPrice = DEFAULT_ADOPT_PRICE;
			} else {
				this.adoptPriceWorlds.put(worldString, DEFAULT_ADOPT_PRICE);
			}
		}
		
	}
	
	private void checkAggroProperty(String world) {
		String worldString = world;
		if (!world.equalsIgnoreCase("")) {
			world = MULTI_WORLD_KEY + "." + world + ".";
		}
		if (configWP.getProperty(ADOPT_KEY + "." + world + AGGRO_KEY) == null || !aggroValueCheck(configWP.getString(ADOPT_KEY + "." + world + AGGRO_KEY))) {
			configWP.setProperty(ADOPT_KEY + "." + world + AGGRO_KEY, DEFAULT_ADOPT_AGGRO);
			configWP.save();
			if (worldString.equalsIgnoreCase("")) {
				this.adoptAggro = DEFAULT_ADOPT_AGGRO;
			} else {
				this.adoptAggroWorlds.put(worldString, DEFAULT_ADOPT_AGGRO);
			}
		}
	}
	
	private void registerCommands() {
        // Page 1
        getCommand("adopt").setExecutor(new CommandAdoptWolf(this));
        getCommand("wpsetprice").setExecutor(new CommandSetPrice(this));
        getCommand("wpsetaggro").setExecutor(new CommandSetAggro(this));
    }
	
	public void removeWorld(String string) {
		configWP.removeProperty(ADOPT_KEY + "." + MULTI_WORLD_KEY + "." + string);
		configWP.save();
		
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
		} else {
			p.sendMessage(chatPrefix + "It costs " + bank.getFormattedAmount(price, type) + " to adopt a wolf " + end);
		}
	}
	
	public void sendWolfPrice(Player p, String world) {
		if (hasPermission(p, PERM_ADOPT)) {
			if (world.equalsIgnoreCase("")) {
				String everywhere = "everywhere";
				for (String s : this.adoptPriceWorlds.keySet()) {
					getHumanReadablePriceMessage(p, this.adoptPriceWorlds.get(s), this.adoptTypeWorlds.get(s), "in " + ChatColor.AQUA + s + ChatColor.WHITE + "!");
					everywhere = "everywhere else";
				}
				
				getHumanReadablePriceMessage(p, this.adoptPrice, this.adoptType, ChatColor.AQUA + everywhere + ChatColor.WHITE + "!");
			} else if (adoptPriceWorlds.containsKey(world) && adoptTypeWorlds.containsKey(world)) {
				getHumanReadablePriceMessage(p, this.adoptPriceWorlds.get(world), this.adoptTypeWorlds.get(world), "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
			} else {
				getHumanReadablePriceMessage(p, this.adoptPrice, this.adoptType, "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
			}
		}
	}
	
	public void sendWolfLimit(Player p, String world) {
		if (hasPermission(p, PERM_ADOPT)) {
			if (world.equalsIgnoreCase("")) {
				String everywhere = "everywhere";
				for (String s : this.adoptPriceWorlds.keySet()) {
					getHumanReadableAdoptLimitMessage(p, this.adoptLimitWorlds.get(s), "in " + ChatColor.AQUA + s + ChatColor.WHITE + "!");
					everywhere = "everywhere else";
				}
				getHumanReadableAdoptLimitMessage(p, this.adoptLimit, ChatColor.AQUA + everywhere + ChatColor.WHITE + "!");
			} else if (adoptLimitWorlds.containsKey(world)) {
				getHumanReadableAdoptLimitMessage(p, this.adoptLimitWorlds.get(world), "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
			} else {
				getHumanReadableAdoptLimitMessage(p, this.adoptLimit, "in " + ChatColor.AQUA + world + ChatColor.WHITE + "!");
			}
		}
	}
	
	public boolean changeSetting(String command, String value, String world, Player p) {
		
		String worldString = world;
		world = MULTI_WORLD_KEY + "." + world + ".";
		if (command.matches("(.*global.*)")) {
			world = "";
		}
		
		if (command.toLowerCase().matches("(.*price.*)")) {
			try {
				double newprice = Double.parseDouble(value);
				configWP.setProperty(ADOPT_KEY + "." + world + PRICE_KEY, newprice);
				configWP.save();
				checkLimitProperty(worldString);
				checkTypeProperty(worldString);
				checkAggroProperty(worldString);
				if (world.equalsIgnoreCase("")) {
					adoptPrice = newprice;
					p.sendMessage(chatPrefix + "Global price changed successfully!");
				} else {
					adoptPriceWorlds.put(worldString, newprice);
					p.sendMessage(chatPrefix + "Price for " + worldString + " changed successfully!");
				}
				
				return true;
			} catch (NumberFormatException e) {
				if (adoptPriceWorlds.containsKey(value)) {
					sendWolfPrice(p, value);
					return true;
				} else {
					p.sendMessage(chatPrefixError + "Sorry, world " + value + " does not exist!");
					return false;
				}
			}
		} else if (command.toLowerCase().matches("(.*type.*)")) {
			int type = WPBlockListener.getRightSide(value);
			if (value.equalsIgnoreCase("money")) {
				type = DEFAULT_ADOPT_TYPE;
			} else if (type == NO_ITEM_FOUND) {
				p.sendMessage(chatPrefixError + "Could not find item: " + value);
				return false;
			} else if (type == MULTIPLE_ITEMS_FOUND) {
				p.sendMessage(chatPrefixError + "Found multiple items that match: " + value);
				return false;
			}
			configWP.setProperty(ADOPT_KEY + "." + world + TYPE_KEY, type);
			configWP.save();
			checkLimitProperty(worldString);
			checkPriceProperty(worldString);
			checkAggroProperty(worldString);
			if (world.equalsIgnoreCase("")) {
				adoptType = type;
				p.sendMessage(chatPrefix + "Global currency type for changed successfully!");
			} else {
				adoptTypeWorlds.put(worldString, type);
				p.sendMessage(chatPrefix + "Currency type for " + worldString + " changed successfully!");
			}
			return true;
		} else if (command.toLowerCase().matches("(.*limit.*)")) {
			int limit = DEFAULT_ADOPT_LIMIT;
			try {
				limit = Integer.parseInt(value);
				if (limit < -1) {
					limit = -1;
				}
				configWP.setProperty(ADOPT_KEY + "." + world + LIMIT_KEY, limit);
				configWP.save();
				checkPriceProperty(worldString);
				checkTypeProperty(worldString);
				checkAggroProperty(worldString);
				if (world.equalsIgnoreCase("")) {
					adoptLimit = limit;
					p.sendMessage(chatPrefix + "Global wolf limit changed successfully!");
				} else {
					adoptLimitWorlds.put(worldString, limit);
					p.sendMessage(chatPrefix + "Wolf limit for " + worldString + " changed successfully!");
				}
				return true;
			} catch (NumberFormatException e) {
				if (adoptLimitWorlds.containsKey(value)) {
					sendWolfLimit(p, value);
					return true;
				} else {
					p.sendMessage(chatPrefixError + "Sorry, world " + value + " does not exist!");
					return false;
				}
			}
		} else if (command.toLowerCase().matches("(.*ag{1,2}ro.*)")) {
			if (aggroValueCheck(value)) {
				checkPriceProperty(worldString);
				checkTypeProperty(worldString);
				checkLimitProperty(worldString);
				if (world.equalsIgnoreCase("")) {
					adoptAggro = value;
					p.sendMessage(chatPrefix + "Global wolf limit changed successfully!");
				} else {
					adoptAggroWorlds.put(worldString, value);
					p.sendMessage(chatPrefix + "Wolf aggro for " + worldString + " changed successfully!");
				}
				configWP.setProperty(ADOPT_KEY + "." + world + AGGRO_KEY, value);
				configWP.save();
				return true;
			} else {
				p.sendMessage(chatPrefixError + "Value not set, valid aggro types are " + 
						ChatColor.AQUA + "neutral" + 
						ChatColor.WHITE + ", " + 
						ChatColor.GREEN + "friend" + 
						ChatColor.WHITE + " or " + 
						ChatColor.RED + "angry");
				return false;
			}
		}
		return false;
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
		double price = this.adoptPrice;
		int type = this.adoptType;
		int limit = this.adoptLimit;
		String aggro = this.adoptAggro;
		if (this.adoptPriceWorlds.containsKey(world)) {
			price = this.adoptPriceWorlds.get(world);
			limit = this.adoptLimitWorlds.get(world);
			type = this.adoptTypeWorlds.get(world);
			aggro = this.adoptAggroWorlds.get(world);
			
		}
		if (limit >= 0) {
			wolves = (wolves > limit) ? limit : wolves;
		}
		if (hasPermission(p, PERM_ADOPT) && bank.hasEnough(p, price * wolves, type)) {
			bank.pay(p, price * wolves, type);
			for (int i = 0; i < wolves; i++) {
				spawnWolf(p, aggro);
			}
		}
	}
	
	public void spawnWolf(Player p, String aggro) {
		
		Wolf w = (Wolf) p.getWorld().spawnCreature(p.getLocation(), CreatureType.WOLF);
		
		if (aggro != null && aggro.equals(ADOPT_FRIEND)) {
			w.setOwner(p);
			w.setSitting(false);
			p.sendMessage(chatPrefix + "BAM! Your trusty companion is ready for battle!");
		} else if (aggro != null && aggro.equals(ADOPT_ANGRY)) {
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
	
	/**
	 * Grab the Permissions plugin from the Plugin Manager.
	 */
	private void checkPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		if (test != null) {
			log.info(logPrefix + " using Permissions");
			Permissions = ((Permissions) test).getHandler();
			usePermissions = true;
		}
	}
	
	/**
	 * Determines if the specified player has the permission. If no permissions plugin is used or player is an op, always true
	 * 
	 * @param p Player
	 * @param permission String permission
	 * @return True if they do, false if not, also displays you dont have permission
	 */
	public boolean hasPermission(Player p, String permission) {
		if (!usePermissions || p.isOp()) {
			return true;
		}
		if (!WolfPound.Permissions.has(p, permission)) {
			p.sendMessage(chatPrefixError + "You don't have permission(" + permission + ") to do this!");
			return false;
		}
		return true;
	}
	
	public boolean blockIsValidWolfSign(Block block) {
		// TODO: Make this exception more specific
		try {
			Sign s = new CraftSign(block);
			return s.getLine(0).equals(prefixValid + "[WolfPound]");
		} catch (Exception e) {
			
		}
		return false;
	}
	
}
