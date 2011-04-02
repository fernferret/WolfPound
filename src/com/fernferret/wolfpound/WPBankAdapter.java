package com.fernferret.wolfpound;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;
import com.nijiko.coelho.iConomy.iConomy;

import cosine.boseconomy.BOSEconomy;

public class WPBankAdapter {
	public enum Bank {
		iConomy, BOSEconomy, Essentials, None
	}
	
	public static BOSEconomy BOSEcon;
	private static Bank bankType = Bank.None;
	
	public static boolean hasMoney(Player p, double money) {
		if (money == 0 || isUsing(Bank.None))
			return true;
		if (isUsing(Bank.iConomy)) {
			return iConomy.getBank().getAccount(p.getName()).hasEnough(money);
		} else if (isUsing(Bank.BOSEconomy)) {
			return BOSEcon.getPlayerMoney(p.getName()) >= money;
		} else if (isUsing(Bank.Essentials)) {
			User user = User.get(p);
			return (user.getMoney() >= money);
		}
		
		return false;
	}
	
	public static void payForWolf(Player p, double cost) {
		
	}
	
	public static void showRecipt(Player p, double price, String moneyName) {
		// Essentials already shows a message
		if (bankType == Bank.Essentials) {
			return;
		}
		p.sendMessage(ChatColor.WHITE + "[WolfPound]" + ChatColor.RED
				+ " You have been charged " + price + " " + moneyName);
	}
	
	private static boolean isUsing(Bank banktype) {
		return bankType == banktype;
	}
	
	public static void setEconType(Bank banktype) {
		bankType = banktype;
	}
}
