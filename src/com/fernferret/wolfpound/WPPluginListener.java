package com.fernferret.wolfpound;

import java.util.logging.Logger;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.fernferret.allpay.*;
import com.iConomy.iConomy;

import cosine.boseconomy.BOSEconomy;
import fr.crafter.tickleman.RealEconomy.RealEconomy;
import fr.crafter.tickleman.RealPlugin.RealPlugin;
import fr.crafter.tickleman.RealShop.RealShop;

public class WPPluginListener extends ServerListener {
	private WolfPound plugin;
	public static final Logger log = Logger.getLogger("Minecraft");
	public WPPluginListener(WolfPound plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		// If we don't have a bank or the essentials plugin loaded first...
		if (plugin.bank == null) {
			iConomy iConomyPlugin = (iConomy) plugin.getServer().getPluginManager().getPlugin("iConomy");
			if (iConomyPlugin != null) {
				plugin.bank = new iConomyBank(iConomyPlugin);
				log.info(WolfPound.logPrefix + " - hooked into iConomy.");
				return;
			}
			// BOSE and RealEcon aren't yet done
//			BOSEconomy boseconPlugin = (BOSEconomy) plugin.getServer().getPluginManager().getPlugin("BOSEconomy");
//			if (boseconPlugin != null) {
//				plugin.bank = new BOSEconomyBank(boseconPlugin);
//			log.info(WolfPound.logPrefix + " - hooked into BOSEconomy.");
//				return;
//			}
//			
//			Plugin realShopPlugin = plugin.getServer().getPluginManager().getPlugin("RealShop");
//			if (realShopPlugin != null) {
//				RealEconomy realEconPlugin = new RealEconomy((RealPlugin) realShopPlugin);
//			log.info(WolfPound.logPrefix + " - hooked into RealEconomy.");
//				plugin.bank = new RealEconomy(realEconPlugin);
//				return;
//			}
			
			// Essentials econ is disabled until they produce a build with the Utils class... #rage
			// Essentials essentialsPlugin = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			// if (essentialsPlugin != null) {
			// plugin.bank = new EssentialsBank(essentialsPlugin);
			// log.info(WolfPound.logPrefix + " - hooked into Essentials.");
			// return;
			// }
			plugin.bank = new ItemBank();
			log.info(WolfPound.logPrefix + " - using only items for economy.");
		}
	}
}
