package com.fernferret.allpay;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.iConomy.iConomy;

import cosine.boseconomy.BOSEconomy;
import fr.crafter.tickleman.RealEconomy.RealEconomy;
import fr.crafter.tickleman.RealPlugin.RealPlugin;

public class AllPay {
	public static final String logPrefix = "[AllPay]";
	private static final Logger log = Logger.getLogger("Minecraft");
	protected static String prefix;
	private Plugin plugin;
	private GenericBank bank;
	
	public AllPay(Plugin plugin, String prefix) {
		this.plugin = plugin;
		AllPay.prefix = prefix;
	}
	
	
	public GenericBank loadEconPlugin() {
		loadiConomy();
		loadBOSEconomy();
		loadRealShopEconomy();
		loadEssentialsEconomoy();
		loadDefaultItemEconomy();
		return this.bank;
	}
	
	private void loadEssentialsEconomoy() {
		Essentials essentialsPlugin = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
		if (essentialsPlugin != null) {
			this.bank = new EssentialsBank(essentialsPlugin);
			log.info(logPrefix + " - hooked into Essentials Economy for " + plugin.getDescription().getFullName());
		}
	}
	
	private void loadRealShopEconomy() {
		Plugin realShopPlugin = plugin.getServer().getPluginManager().getPlugin("RealShop");
		if (realShopPlugin != null && this.bank == null) {
			RealEconomy realEconPlugin = new RealEconomy((RealPlugin) realShopPlugin);
			log.info(logPrefix + " - hooked into RealEconomy for " + plugin.getDescription().getFullName());
			this.bank = new RealEconomyBank(realEconPlugin);
		}
	}
	
	private void loadBOSEconomy() {
		BOSEconomy boseconPlugin = (BOSEconomy) plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
		if (boseconPlugin != null && this.bank == null) {
			this.bank = new BOSEconomyBank(boseconPlugin);
			log.info(logPrefix + " - hooked into BOSEconomy " + plugin.getDescription().getFullName());
		}
	}
	
	private void loadDefaultItemEconomy() {
		if (this.bank == null) {
			this.bank = new ItemBank();
			log.info(logPrefix + " - using only an item based economy for " + plugin.getDescription().getFullName());
		}
	}
	
	private void loadiConomy() {
		iConomy iConomyPlugin = (iConomy) plugin.getServer().getPluginManager().getPlugin("iConomy");
		if (iConomyPlugin != null && this.bank == null) {
			this.bank = new iConomyBank(iConomyPlugin);
			log.info(logPrefix + " - hooked into iConomy for " + plugin.getDescription().getFullName());
		}
	}
	
	public GenericBank getEconPlugin() {
		return this.bank;
	}
}
