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
	
	/**
	 * Does the user have the amount of money specified?
	 * @param p The Player
	 * @param m The amount of money to check if the player has
	 * @return true if the player has at least m money, false if not
	 */
	public static boolean hasMoney(Player p, double m) {
		if (m == 0 || isUsing(Bank.None))
			return true;
		if (isUsing(Bank.iConomy)) {
			return iConomy.getBank().getAccount(p.getName()).hasEnough(m);
		} else if (isUsing(Bank.BOSEconomy)) {
			return BOSEcon.getPlayerMoney(p.getName()) >= m;
		} else if (isUsing(Bank.Essentials)) {
			User user = User.get(p);
			return (user.getMoney() >= m);
		}
		return false;
	}
	
	public static boolean payForWolf(Player p, double cost) {
		if (cost == 0 || isUsing(Bank.None))
			return true;
		if (isUsing(Bank.iConomy)) {
			iConomy.getBank().getAccount(p.getName()).subtract(cost);
			return true;
		} else if (isUsing(Bank.BOSEconomy)) {
			int intPrice = (int)(-1*cost);
			return BOSEcon.addPlayerMoney(p.getName(), intPrice, true);
			
		} else if (isUsing(Bank.Essentials)) {
			User user = User.get(p);
			user.takeMoney(cost);
			return true;
		}
		return false;
	}
	
	public static void userIsTooPoor(Player p) {
		p.sendMessage("Sorry but you do not have the required funds for a wolf");
	}
	
	public static void showRecipt(Player p, double price) {
		// Essentials already shows a message
		String moneyName = "dollars";
		if (isUsing(Bank.Essentials) || isUsing(Bank.None)) {
			return;
		} else if(isUsing(Bank.iConomy)) {
			moneyName = iConomy.getBank().getCurrency();
		} else if(isUsing(Bank.BOSEconomy)){
			
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

	public static String getEconUsed() {
		if (isUsing(Bank.iConomy)) {
			return " using iConomy Economy!";
		} else if (isUsing(Bank.BOSEconomy)) {
			return " using BOSEconomy!";
		} else if (isUsing(Bank.Essentials)) {
			return " using Essentials Economy!";
		} else {
			return " is not using an Economy Plugin!";
		}
	}
}
