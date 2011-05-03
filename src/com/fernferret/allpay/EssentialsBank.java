package com.fernferret.allpay;

import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

/**
 * Essentials class is on hold until they give me access to the currency string
 * @author stokesej
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
		//Util
		return null;
	}

	@Override
	public boolean hasMoney(Player player, double money) {
		return User.get(player).canAfford(money);
	}

	@Override
	public void payMoney(Player player, double amount) {
		User user = User.get(player);
		user.takeMoney(amount);
	}
}
