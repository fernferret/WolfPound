package com.fernferret.wolfpound.commands;

import com.fernferret.wolfpound.AnimalAge;
import com.fernferret.wolfpound.WolfPound;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class AdoptCommand extends WolfPoundCommand {
    public AdoptCommand(WolfPound plugin) {
        super(plugin);
        this.setName("Adopt Wolf");
        this.setCommandUsage("/wp adopt" + ChatColor.GOLD + " [NUMBER] [pup]");
        this.setArgRange(0, 2);
        this.addKey("wp adopt");
        this.addKey("wpa");
        this.addKey("adopt");
        this.setPermission("wolfpound.adopt", "Allows players to adopt wolves. If pup is added, the wolves are babies.", PermissionDefault.OP);
        this.addCommandExample("/wp adopt " + ChatColor.GOLD + "2");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            WolfPound.log.info("Only Players can adopt wolves!");
            return;
        }
        Player player = (Player) sender;
        AnimalAge age = AnimalAge.Adult;
        Integer amount = 1;
        if (args.size() >= 1) {
            if (args.get(0).equalsIgnoreCase("pup")) {
                age = AnimalAge.Baby;
            } else {
                try {
                    amount = Integer.parseInt(args.get(0));
                } catch (NumberFormatException e) {
                }
            }
        }
        if (args.size() >= 2) {
            if (args.get(1).equalsIgnoreCase("pup")) {
                age = AnimalAge.Baby;
            }
            try {
                amount = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
            }
        }
        this.plugin.adoptWolf(player, amount, age);
    }
}
