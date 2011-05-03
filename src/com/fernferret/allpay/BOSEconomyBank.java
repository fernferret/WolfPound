package com.fernferret.allpay;

import org.bukkit.entity.Player;

import cosine.boseconomy.BOSEconomy;

public class BOSEconomyBank extends GenericBank {
	private BOSEconomy plugin;
	
	public BOSEconomyBank(BOSEconomy plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getFormattedMoneyAmount(double amount) {
		if (amount == 1) {
			return plugin.getMoneyName();
		}
		return plugin.getMoneyNamePlural();
	}
	
	@Override
	public boolean hasMoney(Player player, double money, String message) {
		boolean result = plugin.getPlayerMoney(player.getName()) >= money;
		if (!result) {
			userIsTooPoor(player, -1, message);
		}
		return result;
	}
	
	@Override
	public void payMoney(Player player, double amount) {
		int negativePrice = (int) (-1 * Math.abs(amount));
		plugin.addPlayerMoney(player.getName(), negativePrice, true);
		showReceipt(player, amount, -1);
	}
	
	@Override
	public String getEconUsed() {
		return " using BOSEconomy!";
	}
	
}
