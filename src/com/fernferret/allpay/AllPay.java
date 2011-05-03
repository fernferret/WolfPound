package com.fernferret.allpay;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.iConomy.iConomy;

import cosine.boseconomy.BOSEconomy;
import fr.crafter.tickleman.RealEconomy.RealEconomy;
import fr.crafter.tickleman.RealPlugin.RealPlugin;

/**
 * AllPay is a nifty little payment wrapper class that takes the heavy lifting out of integrating payments into your plugin!
 * 
 * @author Eric Stokes
 * 
 */
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
	
	/**
	 * Load an econ plugin. Plugins are loaded in this order: iConomy, BOSEconomy, RealShop, Essentials and simple items
	 * 
	 * @return The GenericBank object to process payments.
	 */
	public GenericBank loadEconPlugin() {
		loadiConomy(); // Supports both 4.x and 5.x
		loadBOSEconomy();
		loadRealShopEconomy();
		loadEssentialsEconomoy();
		loadDefaultItemEconomy();
		return this.bank;
	}
	
	/**
	 * Returns the AllPay GenericBank object that you can issue calls to and from
	 * 
	 * @return The GenericBank object to process payments.
	 */
	public GenericBank getEconPlugin() {
		return this.bank;
	}
	
	private void loadEssentialsEconomoy() {
		if (this.bank == null) {
			Essentials essentialsPlugin = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (essentialsPlugin != null) {
				this.bank = new EssentialsBank(essentialsPlugin);
				log.info(logPrefix + " - hooked into Essentials Economy for " + plugin.getDescription().getFullName());
			}
		}
	}
	
	private void loadRealShopEconomy() {
		if (this.bank == null) {
			Plugin realShopPlugin = plugin.getServer().getPluginManager().getPlugin("RealShop");
			if (realShopPlugin != null) {
				RealEconomy realEconPlugin = new RealEconomy((RealPlugin) realShopPlugin);
				log.info(logPrefix + " - hooked into RealEconomy for " + plugin.getDescription().getFullName());
				this.bank = new RealEconomyBank(realEconPlugin);
			}
		}
	}
	
	private void loadBOSEconomy() {
		if (this.bank == null) {
			BOSEconomy boseconPlugin = (BOSEconomy) plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
			if (boseconPlugin != null) {
				this.bank = new BOSEconomyBank(boseconPlugin);
				log.info(logPrefix + " - hooked into BOSEconomy " + plugin.getDescription().getFullName());
			}
		}
	}
	
	private void loadDefaultItemEconomy() {
		if (this.bank == null) {
			this.bank = new ItemBank();
			log.info(logPrefix + " - using only an item based economy for " + plugin.getDescription().getFullName());
		}
	}
	
	private void loadiConomy() {
		if (this.bank == null) {
			Plugin iConomyTest = plugin.getServer().getPluginManager().getPlugin("iConomy");
			try {
				if (iConomyTest != null && iConomyTest instanceof com.iConomy.iConomy) {
					this.bank = new iConomyBank((iConomy) iConomyTest);
					log.info(logPrefix + " - hooked into iConomy for " + plugin.getDescription().getFullName());
				}
			} catch (NoClassDefFoundError e) {
				if (iConomyTest != null) {
					loadiConomy4X();
				}
			}
		}
	}
	
	private void loadiConomy4X() {
		com.nijiko.coelho.iConomy.iConomy iConomyPlugin = (com.nijiko.coelho.iConomy.iConomy) plugin.getServer().getPluginManager().getPlugin("iConomy");
		if (iConomyPlugin != null) {
			this.bank = new iConomyBank4X(iConomyPlugin);
			log.info(logPrefix + " - hooked into iConomy(4.X) for " + plugin.getDescription().getFullName());
		}
	}
	
}
