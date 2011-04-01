package com.fernferret.wolfpound;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WolfPound extends JavaPlugin{

	private WPPlayerListener playerListener;
	private WPBlockListener blockListener;
	private WPEntityListener entityListener;
	
	private final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[WolfPound]";

	@Override
	public void onEnable() {
		playerListener = new WPPlayerListener();
		blockListener = new WPBlockListener();
		entityListener = new WPEntityListener();
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Low,this); // Low so it acts above any other.
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	
}
