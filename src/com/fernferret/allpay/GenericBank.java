package com.fernferret.allpay;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GenericBank {
	/**
	 * Check to ensure the player has enough items.
	 * 
	 * @param player Check this player's item in hand.
	 * @param amount How much money should we see if they have?
	 * @param type -1 for money, any other valid item id for items. This will check to see if they have the items in their inventory.
	 * @return true if they have enough money/items false if not.
	 */
	public final boolean hasItem(Player player, double amount, int type, String message) {
		if (amount == 0) {
			return true;
		}
		// TODO: Make this inventory
		ItemStack item = player.getItemInHand();
		boolean hasEnough = (item.getTypeId() == type && item.getAmount() >= amount);
		if(!hasEnough) {
			userIsTooPoor(player, type, message);
		}
		return hasEnough;
		
	}
	
	/**
	 * Check to ensure the player has enough money
	 * 
	 * @param player Check this player's bank/pocket for money.
	 * @param money How much money should we see if they have?
	 * @return
	 */
	public abstract boolean hasMoney(Player player, double money, String message);
	
	/**
	 * Convenience method that does not require a message
	 * 
	 * @param player
	 * @param money
	 * @return
	 */
	public final boolean hasMoney(Player player, double money) {
		return hasMoney(player, money, null);
	}
	
	/**
	 * Check to ensure the player has enough money or items.
	 * 
	 * @param player Check this player's bank/pocket for money.
	 * @param amount How much money should we see if they have?
	 * @param type -1 for money, any other valid item id for items. This will check to see if they have the items in their inventory.
	 * @return true if they have enough money/items false if not.
	 */
	public final boolean hasEnough(Player player, double amount, int type, String message) {
		if (type == -1) {
			return hasMoney(player, amount, message);
		} else {
			return hasItem(player, amount, type, message);
		}
	}
	/**
	 * Convenience method that does not require a message
	 * @param player
	 * @param amount
	 * @param type
	 * @return
	 */
	public final boolean hasEnough(Player player, double amount, int type) {
		return hasEnough(player, amount, type, null);
	}
	
	/**
	 * Take the required items/money from the player.
	 * 
	 * @param player The player to take from
	 * @param amount How much should we take
	 * @param type What should we take? (-1 for money, item id for item)
	 */
	public final void payItem(Player player, double amount, int type) {
		ItemStack item = player.getItemInHand();
		int finalamount = item.getAmount() - (int) amount;
		
		if (finalamount > 0) {
			player.getItemInHand().setAmount(finalamount);
		} else {
			player.getInventory().remove(item);
		}
	}
	
	public abstract void payMoney(Player player, double amount);
	
	public final void pay(Player player, double amount, int type) {
		if (type == -1) {
			payMoney(player, amount);
		} else {
			payItem(player, amount, type);
		}
	}
	
	/**
	 * Returns a formatted string of the given amount and type. If type is -1, will return a bank specific string like: "5 Dollars" If type is != -1 will return an item string like: "1 Diamond"
	 * 
	 * @param amount The number of money/items
	 * @param type Money(-1) or item
	 * @return A formatted string of the given amount and type
	 */
	public final String getFormattedItemAmount(double amount, int type) {
		// If we're here, we have to assume item, this method should only get called from it's children
		Material m = Material.getMaterial(type);
		if (m != null) {
			return m.toString();
		}
		return "NO ITEM FOUND";
	}
	
	public abstract String getFormattedMoneyAmount(double amount);
	
	public final String getFormattedAmount(double price, int item) {
		if (item == -1) {
			return getFormattedMoneyAmount(price);
		}
		return getFormattedItemAmount(price, item);
		
	}
	
	/**
	 * This method is called if a user does not have enough money or items. The message parameter allows you to customize what the user does not have enough money for. The format follows: "Sorry but you do not have the required [funds|items] {message}" You can't touch this. My my my...
	 * 
	 * @param player
	 * @param item
	 * @param message This message will appear after the sentence that follows: "{prefix}Sorry but you do not have the required [funds|items] {message}"
	 * @param prefix A prefix to show which plugin charged the user, if you don't want this, just put ""
	 */
	protected final void userIsTooPoor(Player player, int item, String message) {
		// TODO: Make this non-WolfPound generic
		String type = (item == -1) ? "funds" : "items";
		if (message == null) {
			message = "";
		} else {
			message = " " + message;
		}
		player.sendMessage(ChatColor.DARK_RED + AllPay.prefix + ChatColor.WHITE + "Sorry but you do not have the required " + type + message);
	}
	
	/**
	 * Prints a receipt to the user, this should only be called if the econ plugin does not already output when money is taken (Essentials does this) Yo, I told ya, Can't touch this.
	 * 
	 * @param player The player to send the receipt to
	 * @param price The price the user was charged for a wolf
	 * @param item The item the user was charged for a wolf (-1 is money)
	 */
	public void showReceipt(Player player, double price, int item) {
		if (price > 0)
			player.sendMessage(ChatColor.DARK_GREEN + AllPay.prefix + ChatColor.WHITE + "You have been charged " + ChatColor.GREEN + getFormattedAmount(price, item));
	}
	
	/**
	 * Simply prints the economy being used, this is shown to help users debug issues with other plugin interfacing.
	 * 
	 * @return The economy plugin used
	 */
	public abstract String getEconUsed();
}
