package com.fernferret.allpay;

import org.bukkit.entity.Player;
/**
 * Special bank class that handles items. If any money (item id = -1) 
 * comes in here, it will always return true and never take away
 * This class should be the default bank
 * @author Eric Stokes
 *
 */
public class ItemBank extends GenericBank {

	public ItemBank() {
		
	}
	@Override
	public String getFormattedMoneyAmount(double amount) {
		return "";
	}

	@Override
	public boolean hasMoney(Player player, double money) {
		// The player always has enough money in this bank
		// someone needs to configure a bank differently if they're getting here...
		return true;
	}

	@Override
	public void payMoney(Player player, double amount) {
		// No need to take anything away here, someone needs to configure a bank differently if they're getting here...
	}
	/**
	 * For this bank type we only want to show a receipt if there was an item involved
	 */
	@Override
	public void showReceipt(Player player, double price, int item) {
		if(item != -1) {
			super.showReceipt(player, price, item);
		}
	}
	
}
