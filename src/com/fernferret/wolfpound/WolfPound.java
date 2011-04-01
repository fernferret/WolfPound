package com.fernferret.wolfpound;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
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

public class WolfPound extends JavaPlugin{

	private WPPlayerListener playerListener;
	private WPBlockListener blockListener;
	public Configuration configWP;
	public Configuration configPounds;
	
	public final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[WolfPound]";
	
	
	public static PermissionHandler Permissions = null;
	public static boolean useiConomy = false;
	public static boolean useEssentials = false;
	public static boolean usePermissions = false;
	public static HashMap<Block, Pound> pounds = new HashMap<Block, Pound>();;

	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		configWP = new Configuration(new File(this.getDataFolder(), "WolfPound.yml"));
		configPounds = new Configuration(new File(this.getDataFolder(), "Pounds.yml"));
		configWP.load();
		configPounds.load();
		playerListener = new WPPlayerListener(this);
		blockListener = new WPBlockListener(this);
		
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");

		if (getEconPlugin()) {
			if(useiConomy) {
				log.info(logPrefix + " using iConomy Economy!");
			} else if(useEssentials) {
				log.info(logPrefix + " using Essentials Economy!");
			}
		}
		checkPermissions();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal,this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
	}
	
	public void spawnWolf(Player p){
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
		if(!usePermissions) {
			return true;
		}
		if(!WolfPound.Permissions.has(p,permission)) {
			p.sendMessage("You don't have permission(" + permission + ") to do this!");
			return false;
		}
		
		return true;
	}
	
}
