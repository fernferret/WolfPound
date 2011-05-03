package com.fernferret.allpay;

import org.bukkit.entity.Player;

import com.iConomy.iConomy;
/**
 * Adapter class for iConomy
 * Spoke with author, he requested all calls were made directly
 * This is the reason for all of the static-access warnings
 * @author Eric Stokes
 *
 */
public class iConomyBank extends GenericBank {
	private iConomy plugin;
	
	public iConomyBank(iConomy plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getEconUsed() {
		return " using iConomy Economy!";
	}
	
	@SuppressWarnings("static-access")
	public boolean hasMoney(Player player, double money, String message) {
		boolean result = plugin.getAccount(player.getName()).getHoldings().hasEnough(money);
		if(!result) {
			userIsTooPoor(player, -1, message);
		}
		return result;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void payMoney(Player player, double amount) {
		plugin.getAccount(player.getName()).getHoldings().subtract(amount);
		showReceipt(player, amount, -1);
	}

	@SuppressWarnings("static-access")
	@Override
	public String getFormattedMoneyAmount(double amount) {
		return plugin.format(amount);
	}
}
