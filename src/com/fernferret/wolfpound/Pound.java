package com.fernferret.wolfpound;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Pound {
	private Location location;
	private Player owner;
	private Double price;
	
	public Pound(Location l, Player o) {
		this.location = l;
		this.owner = o;
	}
	
	public Pound(Location l, Player o, Double p) {
		this.location = l;
		this.owner = o;
		this.price = p;
	}
	public void setLocation(Location l) {
		this.location = l;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public Location getLocation() {
		return this.location;
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
			return (p.location.equals(this.location));
		}
		return false;
	}
}
