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
	
	public final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[WolfPound]";

	@Override
	public void onEnable() {
		playerListener = new WPPlayerListener(this);
		blockListener = new WPBlockListener(this);
		entityListener = new WPEntityListener(this);
		
		log.info(logPrefix + "- Version " + this.getDescription().getVersion() + " Enabled");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Low,this);
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal,this);
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	
}
