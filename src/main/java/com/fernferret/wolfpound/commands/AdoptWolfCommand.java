package com.fernferret.wolfpound.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.fernferret.wolfpound.WolfPound;

public class AdoptWolfCommand extends WPCommand {
	public AdoptWolfCommand(WolfPound plugin) {
        super(plugin);
        this.setName("Adopt Wolf");
        this.setCommandUsage("/wp adopt" + ChatColor.GOLD + " [NUMBER]");
        this.setArgRange(0, 1);
        this.addKey("wp adopt");
        this.addKey("wp a");
        this.addKey("adopt");
        this.setPermission("wolfpound.adopt", "Allows players to adopt wolves.", PermissionDefault.OP);
        this.addCommandExample("/wp adopt " + ChatColor.GOLD + "2");
    }
    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // TODO: Allow players to buy multiple wolves
        if(!(sender instanceof Player)) {
            WolfPound.log.info("Only Players can adopt wolves!");
            return;
        }
        Player player = (Player)sender;
        if (args.size() == 0) {
            // Adopt a wolf with no params
            this.plugin.adoptWolf(player, 1);
        } else if(args.size() == 1) {
            try{
                int wolves = Integer.parseInt(args.get(0));
                this.plugin.adoptWolf(player, wolves);
                return;
            } catch (NumberFormatException e) {
            }
        }
    }
}
