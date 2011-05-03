package com.fernferret.allpay;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.RealEconomy.RealEconomy;

public class RealEconomyBank extends GenericBank {
	private RealEconomy plugin;
	
	public RealEconomyBank(RealEconomy plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getFormattedMoneyAmount(double amount) {
		// NOTE: This plugin does not support currency plurals
		return plugin.getCurrency();
	}
	
	@Override
	public boolean hasMoney(Player player, double money, String message) {
		boolean result = plugin.getBalance(player.getName()) >= money;
		if (!result) {
			userIsTooPoor(player, -1, message);
		}
		return result;
	}
	
	@Override
	public void payMoney(Player player, double amount) {
		double totalmoney = plugin.getBalance(player.getName());
		plugin.setBalance(player.getName(), totalmoney - amount);
		showReceipt(player, amount, -1);
	}
	
	@Override
	public String getEconUsed() {
		return " using RealEconomy!";
	}
	
}
