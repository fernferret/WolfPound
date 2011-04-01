package com.fernferret.wolfpound;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Pound {
	private Location location;
	private World world;
	private Player owner;
	private Double price;
	
	public Pound(World w, Location l, Player o) {
		this.world = w;
		this.location = l;
		this.owner = o;
	}
	
	public Pound(World w, Location l, Player o, Double p) {
		this.world = w;
		this.location = l;
		this.owner = o;
		this.price = p;
	}
	public void setLocation(Location l) {
		this.location = l;
	}
	public void setWorld(World w) {
		this.world = w;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public Location getLocation() {
		return this.location;
	}
	public World getWorld() {
		return this.world;
	}
	public Double getPrice() {
		return this.price;
	}
	public Player getOwner() {
		return this.owner;
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
