package com.fernferret.wolfpound;

import org.bukkit.entity.Player;

public abstract class Bank {
	public abstract boolean hasMoney(Player p, double money, int type);
	
	public abstract boolean payForWolf(Player p, double cost, int type);
	
	public abstract String getFormattedAmount(double amount, int type);
	
	public void userIsTooPoor(Player p, int item) {
		String type = (item == -1) ? "funds" : "items";
		p.sendMessage(WolfPound.chatPrefixError + "Sorry but you do not have the required " + type + " for a wolf");
	}
	
	public void showRecipt(Player p, double price, int item) {
		p.sendMessage(WolfPound.chatPrefix + " You have been charged " + price + " " + getFormattedAmount(price, item));
	}
	
	public abstract String getEconUsed();
}
