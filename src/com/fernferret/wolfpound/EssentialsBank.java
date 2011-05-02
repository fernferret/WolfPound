package com.fernferret.wolfpound;

import org.bukkit.entity.Player;

public class EssentialsBank extends GenericBank {
	
	@Override
	public String getEconUsed() {
		return " using Essentials Economy!";
	}
	
	@Override
	public String getFormattedAmount(double amount, int type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasMoney(Player p, double money, int type) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void payForWolf(Player p, double cost, int type) {
		// TODO Auto-generated method stub
	}
	
}
