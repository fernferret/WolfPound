package com.fernferret.wolfpound;

import java.io.File;
import java.util.logging.Logger;

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
	private static final String ADOPT_PRICE_KEY = "adoptprice";
	private static final double DEFAULT_ADOPT_PRICE = 0.0;
	private WPPlayerListener playerListener;
	private WPBlockListener blockListener;
	public Configuration configWP;
	
	
	public final Logger log = Logger.getLogger("Minecraft");
	public final String logPrefix = "[WolfPound]";
	
	public static PermissionHandler Permissions = null;
	public static boolean usePermissions = false;
	private double adoptPrice = 0.0;
	
	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		loadConfiguration();
		playerListener = new WPPlayerListener(this);
		blockListener = new WPBlockListener(this);
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");
		
		if (setEconPlugin()) {
			log.info(logPrefix + WPBankAdapter.getEconUsed());
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
		this.adoptPrice = configWP.getDouble(ADOPT_PRICE_KEY, DEFAULT_ADOPT_PRICE);
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
					changeSetting(args[0], args[1]);
					return true;
				default:
					player.sendMessage("TODO: Explain the /adopt command, it's on the wiki right now...");
					return false;
			}
		}
		return false;
		
	}
	
	private void sendWolfPrice(Player p) {
		if (hasPermission(p, PERM_ADOPT))
			p.sendMessage("It costs " + adoptPrice + " to adopt a wolf!");
	}
	
	private void changeSetting(String command, String value) {
		if (command.equals("setprice")) {
			try {
				double newprice = Double.parseDouble(value);
				configWP.setProperty(ADOPT_PRICE_KEY, newprice);
				configWP.save();
				adoptPrice = newprice;
			} catch (NumberFormatException e) {
				log.info("Did not set wolf price!");
			}
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
	
	private void adoptWolf(Player p, int wolves) {
		if (hasPermission(p, PERM_ADOPT) && WPBankAdapter.hasMoney(p, adoptPrice)) {
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
			WPBankAdapter.setEconType(WPBankAdapter.Bank.iConomy);
			return true;
		} else if (testBOSE != null) {
			WPBankAdapter.BOSEcon = (BOSEconomy) testBOSE;
			WPBankAdapter.setEconType(WPBankAdapter.Bank.BOSEconomy);
			return true;
		} else if (testEssentials != null) {
			WPBankAdapter.setEconType(WPBankAdapter.Bank.iConomy);
			return true;
		}
		return false;
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
	
	public boolean hasPermission(Player p, String permission) {
		if (!usePermissions) {
			return true;
		}
		if (!WolfPound.Permissions.has(p, permission)) {
			p.sendMessage("You don't have permission(" + permission + ") to do this!");
			return false;
		}
		
		return true;
	}
	
	public boolean blockIsValidWolfSign(Block block) {
		//TODO: Make this exception more specific
		try {
			Sign s = new CraftSign(block);
			return s.getLine(0).equals("¤1[WolfPound]");
		} catch (Exception e) {
			
		}
		return false;
	}
	
}
