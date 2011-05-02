package com.fernferret.wolfpound;

import org.bukkit.entity.Player;

public abstract class GenericBank {
	/**
	 * Check to ensure the player has enough money or items.
	 * 
	 * @param player Check this player's bank/pocket for money.
	 * @param money How much money should we see if they have?
	 * @param type -1 for money, any other valid item id for items. This will check to see if they have the items in their inventory.
	 * @return true if they have enough money/items false if not.
	 */
	public abstract boolean hasMoney(Player player, double money, int type);
	
	/**
	 * Take the required items/money from the player.
	 * 
	 * @param player The player to take from
	 * @param cost How much should we take
	 * @param type What should we take? (-1 for money, item id for item)
	 */
	public abstract void payForWolf(Player player, double cost, int type);
	
	/**
	 * Returns a formatted string of the given amount and type. If type is -1, will return a bank specific string like: "5 Dollars" If type is != -1 will return an item string like: "1 Diamond"
	 * 
	 * @param amount The number of money/items
	 * @param type Money(-1) or item
	 * @return A formatted string of the given amount and type
	 */
	public abstract String getFormattedAmount(double amount, int type);
	
	/**
	 * This method is called if a user does not have enough money. You can't touch this. My my my...
	 * 
	 * @param player
	 * @param item
	 */
	public final void userIsTooPoor(Player player, int item) {
		String type = (item == -1) ? "funds" : "items";
		player.sendMessage(WolfPound.chatPrefixError + "Sorry but you do not have the required " + type + " for a wolf");
	}
	
	/**
	 * Prints a receipt to the user, this should only be called if the econ plugin does not already output when money is taken (Essentials does this) Yo, I told ya, Can't touch this.
	 * 
	 * @param player The player to send the receipt to
	 * @param price The price the user was charged for a wolf
	 * @param item The item the user was charged for a wolf (-1 is money)
	 */
	public final void showReceipt(Player player, double price, int item) {
		player.sendMessage(WolfPound.chatPrefix + " You have been charged " + price + " " + getFormattedAmount(price, item));
	}
	/**
	 * Simply prints the economy being used, this is shown to help users debug issues with other plugin interfacing.
	 * @return The economy plugin used
	 */
	public String getEconUsed() {
		return " using simple items as economy!";
	}
}
