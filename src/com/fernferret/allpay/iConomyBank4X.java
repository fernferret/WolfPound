package com.fernferret.allpay;

import org.bukkit.entity.Player;
import com.nijiko.coelho.iConomy.iConomy;

/**
 * Adapter class for iConomy
 * Spoke with author, he requested all calls were made directly
 * This is the reason for all of the static-access warnings
 * @author Eric Stokes
 *
 */
public class iConomyBank4X extends GenericBank {
	private iConomy plugin;
	
	public iConomyBank4X(iConomy plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getEconUsed() {
		return " using iConomy(4X)!";
	}
	
	@SuppressWarnings("static-access")
	public boolean hasMoney(Player player, double money, String message) {
		boolean result = plugin.getBank().getAccount(player.getName()).hasEnough(money);
		if(!result) {
			userIsTooPoor(player, -1, message);
		}
		return result;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void payMoney(Player player, double amount) {
		plugin.getBank().getAccount(player.getName()).subtract(amount);
	}

	@SuppressWarnings("static-access")
	@Override
	public String getFormattedMoneyAmount(double amount) {
		return amount + " " + plugin.getBank().getCurrency();
	}
}
