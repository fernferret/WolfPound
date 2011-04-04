package com.fernferret.wolfpound;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import cosine.boseconomy.BOSEconomy;

public class WolfPound extends JavaPlugin {
	
	public static final String PERM_CREATE = "wolfpound.create";
	public static final String PERM_USE = "wolfpound.use";
	public static final String PERM_ADOPT = "wolfpound.adopt";
	
	private static final String WOLF_POUND_CONFIG = "WolfPound.yml";
	private static final String ADOPT_PRICE_KEY = "adopt.price";
	private static final double DEFAULT_ADOPT_PRICE = 0.0;
	private static final String ADOPT_TYPE_KEY = "adopt.type";
	private static final int DEFAULT_ADOPT_TYPE = -1;
	public static final int NO_ITEM_FOUND = -2;
	public static final int MULTIPLE_ITEMS_FOUND = -3;
	public static final int MONEY_ITEM_FOUND = -1;
	public static final int INVALID_PRICE = -4;
	public static final ChatColor prefixValid = ChatColor.DARK_BLUE;
	public static final ChatColor prefixInvalid = ChatColor.DARK_BLUE;
	
	private WPPlayerListener playerListener;
	private WPBlockListener blockListener;
	public Configuration configWP;
	
	public static final Logger log = Logger.getLogger("Minecraft");
	public static final String logPrefix = "[WolfPound]";
	
	public static PermissionHandler Permissions = null;
	public static boolean usePermissions = false;
	public static final String chatPrefix = ChatColor.DARK_RED + "[WolfPound]" + ChatColor.WHITE;
	private double adoptPrice = 0.0;
	public WPBankAdapter bank;
	// Used as an item id for transactions with the /adopt command
	private int adoptType = -1;
	
	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		loadConfiguration();
		playerListener = new WPPlayerListener(this);
		blockListener = new WPBlockListener(this);
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");
		
		if (setEconPlugin()) {
			log.info(logPrefix + bank.getEconUsed());
		}
		checkPermissions();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
	}
	
	private void loadConfiguration() {
		configWP = new Configuration(new File(this.getDataFolder(), WOLF_POUND_CONFIG));
		configWP.load();
		// If the config was empty or not specified correctly, overwrite it!
		if (configWP.getProperty(ADOPT_PRICE_KEY) == null) {
			configWP.setProperty(ADOPT_PRICE_KEY, DEFAULT_ADOPT_PRICE);
			configWP.save();
		}
		
		if (configWP.getProperty(ADOPT_TYPE_KEY) == null) {
			configWP.setProperty(ADOPT_TYPE_KEY, DEFAULT_ADOPT_TYPE);
			configWP.save();
		}
		this.adoptPrice = configWP.getDouble(ADOPT_PRICE_KEY, DEFAULT_ADOPT_PRICE);
		this.adoptType = configWP.getInt(ADOPT_TYPE_KEY, DEFAULT_ADOPT_TYPE);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String commandName = command.getName().toLowerCase();
		
		if (commandName.equalsIgnoreCase("adopt")) {
			Player player = (Player) sender;
			switch (args.length) {
				case 0:
					// Adopt a wolf with no params
					adoptWolf(player, 1);
					return true;
				case 1:
					// Display the wolf price
					if (args[0].equals("price")) {
						sendWolfPrice(player);
						return true;
					}
					// Adopt X wolves
					adoptWolf(player, getWolfInt(args[0], player, "I didn't understand how many wolves you wanted!"));
					return true;
				case 2:
					// change a setting!,
					if(!hasPermission(player, PERM_CREATE)) {
						return false;
					}
					if (changeSetting(args[0], args[1])) {
						player.sendMessage("Setting changed successfully!");
					} else {
						player.sendMessage("Setting change failed.");
					}
					return true;
				default:
					player.sendMessage("Usage: /adopt [x]");
					player.sendMessage("       /adopt price");
					player.sendMessage("       /adopt setprice [x]");
					return false;
			}
		}
		return false;
	}
	
	private void sendWolfPrice(Player p) {
		if (hasPermission(p, PERM_ADOPT))
			if (this.adoptPrice == 0) {
				p.sendMessage("Adopting a wolf is FREE!");
			} else if(this.adoptType == -1) {
				p.sendMessage("It costs " + adoptPrice + " to adopt a wolf!");
			} else {
				Material m = Material.getMaterial(adoptType);
				if (m != null) {
					p.sendMessage("It costs " + adoptPrice + " " + m.toString() + " to adopt a wolf!");
				} else {
					p.sendMessage("It costs " + adoptPrice + " items to adopt a wolf!");
				}
				
			}
		
	}
	
	private boolean changeSetting(String command, String value) {
		if (command.equals("setprice") || command.equals("price")) {
			try {
				double newprice = Double.parseDouble(value);
				configWP.setProperty(ADOPT_PRICE_KEY, newprice);
				configWP.save();
				adoptPrice = newprice;
				return true;
			} catch (NumberFormatException e) {
				
			}
		} else if (command.equals("settype") || command.equals("type")) {
			int type = getItemInt(value);
			if (value.equalsIgnoreCase("money")) {
				type = -1;
			} else if (type == -2) {
				return false;
			}
			configWP.setProperty(ADOPT_TYPE_KEY, type);
			configWP.save();
			this.adoptType = type;
			return true;
		}
		return false;
	}
	
	private int getItemInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			if (Material.matchMaterial(value) != null) {
				return Material.matchMaterial(value).getId();
			}
			return -2;
			
		}
	}
	
	private int getWolfInt(String wolves, Player p, String errorMsg) {
		try {
			return Math.abs(Integer.parseInt(wolves));
		} catch (NumberFormatException e) {
			p.sendMessage(errorMsg);
			return 0;
		}
	}
	
	/**
	 * Allows the user to adopt a wolf
	 * 
	 * @param p The player
	 * @param wolves How many wolves
	 */
	private void adoptWolf(Player p, int wolves) {
		if (hasPermission(p, PERM_ADOPT) && bank.hasMoney(p, adoptPrice * wolves, this.adoptType)) {
			bank.payForWolf(p, adoptPrice * wolves, this.adoptType);
			if (adoptPrice > 0) {
				bank.showRecipt(p, adoptPrice * wolves, this.adoptType);
			}
			for (int i = 0; i < wolves; i++) {
				spawnWolf(p);
			}
		}
	}
	
	public void spawnWolf(Player p) {
		p.sendMessage("BAM! You got a Wolf!");
		Wolf w = (Wolf) p.getWorld().spawnCreature(p.getLocation(), CreatureType.WOLF);
		w.setAngry(false);
	}
	
	@Override
	public void onDisable() {
		log.info(logPrefix + "- Disabled");
	}
	
	/**
	 * Grab the iConomy plugin from the Plugin Manager.
	 */
	public boolean setEconPlugin() {
		Plugin testiConomy = this.getServer().getPluginManager().getPlugin("iConomy");
		Plugin testBOSE = this.getServer().getPluginManager().getPlugin("BOSEconomy");
		Plugin testEssentials = this.getServer().getPluginManager().getPlugin("Essentials");
		if (testiConomy != null) {
			bank = new WPBankAdapter(WPBankAdapter.Bank.iConomy);
			return true;
		} else if (testBOSE != null) {
			bank = new WPBankAdapter(WPBankAdapter.Bank.BOSEconomy, (BOSEconomy) testBOSE);
			return true;
		} else if (testEssentials != null) {
			bank = new WPBankAdapter(WPBankAdapter.Bank.Essentials);
			return true;
		} else {
			bank = new WPBankAdapter(WPBankAdapter.Bank.None);
			return false;
		}
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
			p.sendMessage("You don't have permission(" + permission + ") to do this!");
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
