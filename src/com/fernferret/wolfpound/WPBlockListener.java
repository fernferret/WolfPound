package com.fernferret.wolfpound;

import net.minecraft.server.Block;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class WPBlockListener extends BlockListener {
	private final WolfPound plugin;
	public WPBlockListener(final WolfPound plugin) {
		this.plugin = plugin;
	}
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		p.sendMessage("You placed a block!");
		if(event.getBlock().getType().equals(Block.SIGN_POST)){
			p.sendMessage("You placed a sign!");
		}
	}
	
	@Override
	public void onSignChange(SignChangeEvent event) {
		Player p = event.getPlayer();
		if(event.getLine(0).equalsIgnoreCase("[WolfPound]")) {
			p.sendMessage("Setting up your Wolf Pound!");
			Wolf w = (Wolf) p.getWorld().spawnCreature(p.getLocation(), CreatureType.WOLF);
			w.setAngry(false);
			//TODO: Make wolves assigned to people
		}
		
	}
}
