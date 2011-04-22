package com.fernferret.wolfpound;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.earth2me.essentials.User;
import com.nijiko.coelho.iConomy.iConomy;

import cosine.boseconomy.BOSEconomy;
import fr.crafter.tickleman.RealEconomy.RealEconomy;

public class WPBankAdapter {
	public enum Bank {
		iConomy, BOSEconomy, Essentials, RealShop, None
	}
	
	public enum Type {
		Money, Item
	}
	
	public BOSEconomy BOSEcon;
	public RealEconomy RealEcon;
	private Bank bankType = Bank.None;
	
	public WPBankAdapter(Bank bank) {
		this.bankType = bank;
	}
	
	public WPBankAdapter(Bank bank, BOSEconomy econ) {
		this.bankType = bank;
		this.BOSEcon = econ;
	}
	
	public WPBankAdapter(Bank bank, RealEconomy econ) {
		this.bankType = bank;
		this.RealEcon = econ;
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
		
		if (m == 0 || (isUsing(Bank.None) && type == -1)) {
			playerHasEnough = true;
		} else if (type != -1) {
			ItemStack item = p.getItemInHand();
			playerHasEnough = (item.getTypeId() == type && item.getAmount() >= m);
		} else if (isUsing(Bank.iConomy)) {
			playerHasEnough = iConomy.getBank().getAccount(p.getName()).hasEnough(m);
		} else if (isUsing(Bank.BOSEconomy)) {
			playerHasEnough = BOSEcon.getPlayerMoney(p.getName()) >= m;
		} else if (isUsing(Bank.RealShop)) {
			playerHasEnough = RealEcon.getBalance(p.getName()) >= m;
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
		
		if (cost <= 0 || (isUsing(Bank.None) && type == -1))
			return true;
		else if (type != -1) {
			ItemStack item = p.getItemInHand();
			int finalamount = item.getAmount() - (int) cost;
			
			if (finalamount > 0) {
				p.getItemInHand().setAmount(finalamount);
			} else {
				p.getInventory().remove(item);
			}
			// p.getItemInHand().setAmount(0);
		} else if (isUsing(Bank.iConomy)) {
			iConomy.getBank().getAccount(p.getName()).subtract(cost);
			return true;
		} else if (isUsing(Bank.BOSEconomy)) {
			int intPrice = (int) (-1 * cost);
			return BOSEcon.addPlayerMoney(p.getName(), intPrice, true);
			
		} else if (isUsing(Bank.RealShop)) {
			double totalmoney = RealEcon.getBalance(p.getName());
			return RealEcon.setBalance(p.getName(), totalmoney - cost);
			
		} else if (isUsing(Bank.Essentials)) {
			User user = User.get(p);
			user.takeMoney(cost);
			return true;
		}
		return false;
	}
	
	public void userIsTooPoor(Player p, int item) {
		String type = (item == -1) ? "funds" : "items";
		p.sendMessage(WolfPound.chatPrefixError + "Sorry but you do not have the required " + type + " for a wolf");
	}
	
	public void showRecipt(Player p, double price, int item) {
		p.sendMessage(WolfPound.chatPrefix + " You have been charged " + price + " " + getBankCurrency(price, item));
	}
	
	private boolean isUsing(Bank banktype) {
		return bankType == banktype;
	}
	
	public boolean isUsingEcon(int item) {
		if (item == -1) {
			return bankType != Bank.None;
		}
		return true;
	}
	
	public String getBankCurrency(double amount, int type) {
		if (type != -1) {
			Material m = Material.getMaterial(type);
			if (m != null) {
				return m.toString();
			} else {
				return "NO ITEM FOUND";
			}
		}
		
		if (bankType == Bank.iConomy) {
			return iConomy.getBank().getCurrency();
		}
		if (bankType == Bank.BOSEconomy) {
			if (amount == 1) {
				return BOSEcon.getMoneyName();
			}
			return BOSEcon.getMoneyNamePlural();
		}
		if (bankType == Bank.RealShop) {
			return RealEcon.getCurrency();
		}
		if (bankType == Bank.Essentials) {
			if (amount == 1) {
				return "dollar";
			}
			return "dollars";
		}
		
		return "";
	}
	
	public void setEconType(Bank banktype) {
		bankType = banktype;
	}
	
	public String getEconUsed() {
		if (isUsing(Bank.iConomy)) {
			return " using iConomy Economy!";
		} else if (isUsing(Bank.BOSEconomy)) {
			return " using BOSEconomy!";
		} else if (isUsing(Bank.RealShop)) {
			return " using RealEconomy!";
		} else if (isUsing(Bank.Essentials)) {
			return " using Essentials Economy!";
		} else {
			return " is not using an Economy Plugin!";
		}
	}
}
