package com.fernferret.wolfpound;

import org.bukkit.event.block.BlockListener;

public class WPBlockListener extends BlockListener {
	private final WolfPound plugin;
	public WPBlockListener(final WolfPound plugin) {
		this.plugin = plugin;
	}
}
