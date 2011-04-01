package com.fernferret.wolfpound;

import org.bukkit.Location;
import org.bukkit.World;

public class Pound {
	private Location location;
	private World world;
	
	public Pound(World w, Location l) {
		this.world = w;
		this.location = l;
	}
	
	public void setLocation(Location l) {
		this.location = l;
	}
	public void setWorld(World w) {
		this.world = w;
	}
	
	public Location getLocation() {
		return this.location;
	}
	public World getWorld() {
		return this.world;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Pound) {
			Pound p = (Pound) obj;
			return (p.world.equals(this.world) && p.location.equals(this.location));
		}
		return false;
	}
}
