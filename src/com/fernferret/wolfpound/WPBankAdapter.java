package com.fernferret.wolfpound;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.earth2me.essentials.User;
import com.nijiko.coelho.iConomy.iConomy;

import cosine.boseconomy.BOSEconomy;

public class WPBankAdapter {
	public enum Bank {
		iConomy, BOSEconomy, Essentials, None
	}
	
	public enum Type {
		Money, Item
	}
	
	public BOSEconomy BOSEcon;
	private Bank bankType = Bank.None;
	
	public WPBankAdapter(Bank bank) {
		this.bankType = bank;
	}
	
	public WPBankAdapter(Bank bank, BOSEconomy econ) {
		this.bankType = bank;
		BOSEcon = econ;
	}
	
	/**
	 * Does the user have the amount of money specified?
	 * 
	 * @param p The Player
	 * @param m The amount of money to check if the player has
	 * @return true if the player has at least m money, false if not
	 */
	public boolean hasMoney(Player p, double m, int type) {
		boolean playerHasEnough = false;
		
		if (m == 0 || isUsing(Bank.None))
			playerHasEnough = true;
		if (type != -1) {
			ItemStack item = p.getItemInHand();
			playerHasEnough = (item.getTypeId() == type && item.getAmount() >= m);
		} else if (isUsing(Bank.iConomy)) {
			playerHasEnough = iConomy.getBank().getAccount(p.getName()).hasEnough(m);
		} else if (isUsing(Bank.BOSEconomy)) {
			playerHasEnough = BOSEcon.getPlayerMoney(p.getName()) >= m;
		} else if (isUsing(Bank.Essentials)) {
			User user = User.get(p);
			playerHasEnough = (user.getMoney() >= m);
		}
		
		if (!playerHasEnough) {
			userIsTooPoor(p, type);
		}
		return playerHasEnough;
	}
	
	public boolean payForWolf(Player p, double cost, int type) {
		
		if (cost == 0 || isUsing(Bank.None))
			return true;
		if (type != -1) {
			ItemStack item = p.getItemInHand();
			int finalamount = item.getAmount() - (int)cost;
			p.getItemInHand().setAmount(finalamount);
		} else if (isUsing(Bank.iConomy)) {
			iConomy.getBank().getAccount(p.getName()).subtract(cost);
			return true;
		} else if (isUsing(Bank.BOSEconomy)) {
			int intPrice = (int) (-1 * cost);
			return BOSEcon.addPlayerMoney(p.getName(), intPrice, true);
			
		} else if (isUsing(Bank.Essentials)) {
			User user = User.get(p);
			user.takeMoney(cost);
			return true;
		}
		return false;
	}
	
	public void userIsTooPoor(Player p, int item) {
		String type = (item == -1) ? "funds" : "items";
		p.sendMessage("Sorry but you do not have the required " + type + " for a wolf");
	}
	
	public void showRecipt(Player p, double price, int item) {
		// Essentials already shows a message
		String moneyName = "dollars";
		if (item != -1) {
			ItemStack is = new ItemStack(item);
			moneyName = is.getType().toString();
		}
		if (isUsing(Bank.Essentials) || isUsing(Bank.None)) {
			return;
		} else if (isUsing(Bank.iConomy)) {
			moneyName = iConomy.getBank().getCurrency();
		} else if (isUsing(Bank.BOSEconomy)) {
			
		}
		p.sendMessage(ChatColor.WHITE + "[WolfPound]" + ChatColor.RED
				+ " You have been charged " + price + " " + moneyName);
	}
	
	private boolean isUsing(Bank banktype) {
		return bankType == banktype;
	}
	
	public void setEconType(Bank banktype) {
		bankType = banktype;
	}
	
	public String getEconUsed() {
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
