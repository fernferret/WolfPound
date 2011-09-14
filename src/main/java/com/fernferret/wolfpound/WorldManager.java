package com.fernferret.wolfpound;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldManager {
    private WolfPound plugin;
    private Map<String, WPWorld> worlds;
    private WPWorld globalWorld;

    public WorldManager(WolfPound plugin) {
        this.plugin = plugin;
        this.worlds = new HashMap<String, WPWorld>();
    }

    public void addWorld(String key, WPWorld world) {
        this.worlds.put(key, world);
    }

    /**
     * Removes a world from the config and from the ingame cache. True if it was removed.
     * 
     * @param name
     * @return
     */
    public boolean removeWorld(String name) {
        if (this.worlds.containsKey(name)) {
            this.worlds.get(name).removeFromConfig();
            this.worlds.remove(name);
            return true;
        }
        return false;
    }
    
    /**
     * Returns the existing wolfPound world that exists, or creates one if the world is a loaded world, but not created, or the global world.
     * 
     * @param world The worldname to get.
     * @return A WolfPound World
     */
    public WPWorld getWorld(String world) {
        if (this.worlds.containsKey(world)) {
            return this.worlds.get(world);
        } else if (this.plugin.getServer().getWorld(world) != null) {
            WPWorld w = new WPWorld(world, this.plugin.getConfig());
            this.worlds.put(world, w);
            return this.worlds.get(world);
        }
        return this.globalWorld;
    }
    
    public WPWorld getGlobalWorld() {
        return this.globalWorld;
    }
    
    public void setGlobalWorld(WPWorld world) {
        this.globalWorld = world;
    }

    public Set<String> getWorldNames() {
        return this.worlds.keySet();
    }

}
