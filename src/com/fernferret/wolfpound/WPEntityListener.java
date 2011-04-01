package com.fernferret.wolfpound;

import org.bukkit.event.entity.EntityListener;

public class WPEntityListener extends EntityListener {
	private final WolfPound plugin;
	public WPEntityListener(final WolfPound plugin) {
		this.plugin = plugin;
	}
}
