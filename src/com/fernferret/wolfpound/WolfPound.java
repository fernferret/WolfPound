package com.fernferret.wolfpound;

import java.io.File;
import java.util.logging.Logger;

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

public class WolfPound extends JavaPlugin{

	private WPPlayerListener playerListener;
	private WPBlockListener blockListener;
	public Configuration configWP;
	
	public final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[WolfPound]";
	
	public static PermissionHandler Permissions = null;
	public static boolean useiConomy = false;

	@Override
	public void onEnable() {
		getDataFolder().mkdirs();
		configWP = new Configuration(new File(this.getDataFolder(), "WolfPound.yml"));
		playerListener = new WPPlayerListener(this);
		blockListener = new WPBlockListener(this);
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");

		if (checkiConomy()) {
			useiConomy = configWP.getString("econ", "").equals("iconomy");
			if(useiConomy) {
				log.info(logPrefix + " using iConomy!");
			}
		}
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal,this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
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
	
	public boolean checkiConomy() {
		Plugin test = this.getServer().getPluginManager().getPlugin("iConomy");
		return (test != null);
	}
	
}
