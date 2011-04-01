package com.fernferret.wolfpound;

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

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class WolfPound extends JavaPlugin {
	
	private WPPlayerListener playerListener;
	private WPBlockListener blockListener;
	//public Configuration configWP;
	
	public final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[WolfPound]";
	
	public static PermissionHandler Permissions = null;
	public static boolean useiConomy = false;
	public static boolean useEssentials = false;
	public static boolean usePermissions = false;
	
	@Override
	public void onEnable() {
		playerListener = new WPPlayerListener(this);
		blockListener = new WPBlockListener(this);
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");
		
		if (getEconPlugin()) {
			if (useiConomy) {
				log.info(logPrefix + " using iConomy Economy!");
			} else if (useEssentials) {
				log.info(logPrefix + " using Essentials Economy!");
			}
		}
		checkPermissions();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String commandName = command.getName().toLowerCase();
		if (commandName.equalsIgnoreCase("adopt")) {
			Player player = (Player) sender;
			if (hasPermission(player, "wolfpound.adopt")) {
				spawnWolf(player);
			}
			return true;
		}
		return false;
	}
	
	public void spawnWolf(Player p) {
		p.sendMessage("BAM! You got a Wolf!");
		Wolf w = (Wolf) p.getWorld().spawnCreature(p.getLocation(), CreatureType.WOLF);
		w.setAngry(false);
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Grab the iConomy plugin from the Plugin Manager.
	 */
	public boolean getEconPlugin() {
		Plugin testiConomy = this.getServer().getPluginManager().getPlugin("iConomy");
		useiConomy = (testiConomy != null);
		Plugin testEssentials = this.getServer().getPluginManager().getPlugin("Essentials");
		useEssentials = (testEssentials != null);
		return (useiConomy || useEssentials);
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
		try {
			Sign s = new CraftSign(block);
			return s.getLine(0).equals("¤1[WolfPound]");
		} catch (Exception e) {
			
		}
		return false;
	}
	
}
