package com.fernferret.allpay;

import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

/**
 * Essentials class is on hold until they give me access to the currency string
 * @author Eric Stokes
 *
 */
public class EssentialsBank extends GenericBank {
	@SuppressWarnings("unused")
	private Essentials plugin;
	
	public EssentialsBank(Essentials plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getEconUsed() {
		return " using Essentials Economy!";
	}

	@Override
	public String getFormattedMoneyAmount(double amount) {
		//Until Essentials let's me read this, it's gonna be FernDollars
		return amount + "FernDollars";
	}

	@Override
	public boolean hasMoney(Player player, double money, String message) {
		return User.get(player).canAfford(money);
	}

	@Override
	public void payMoney(Player player, double amount) {
		User user = User.get(player);
		user.takeMoney(amount);
		// Don't need to show receipt, Essentials already does
		//showReceipt(player, amount, -1);
	}
}
