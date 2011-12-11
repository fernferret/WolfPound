package com.fernferret.wolfpound.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.fernferret.wolfpound.WolfPound;

public class VersionCommand extends WolfPoundCommand {

    public VersionCommand(WolfPound plugin) {
        super(plugin);
        this.setName("WolfPound Version");
        this.setCommandUsage("/wp version " + ChatColor.GOLD + "-p");
        this.setArgRange(0, 1);
        this.addKey("wp version");
        this.addKey("wpv");
        this.addKey("wpversion");
        this.setPermission("wolfpound.version", "Dumps version info to the console, optionally to pastebin.com with a -p.", PermissionDefault.TRUE);
    }

    private String pasteBinBuffer = "";

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            sender.sendMessage("Version info dumped to console. Please check your server logs.");
        }

        logAndAddToPasteBinBuffer("WolfPound Version: " + this.plugin.getDescription().getVersion());
        logAndAddToPasteBinBuffer("Bukkit Version: " + this.plugin.getServer().getVersion());
        logAndAddToPasteBinBuffer("Economy being used: " + this.plugin.getBanker().getEconPlugin().getEconUsed());
        logAndAddToPasteBinBuffer("Permissions Plugin: " + this.plugin.getPermissions().getType());
        logAndAddToPasteBinBuffer("Op Fallback: " + this.plugin.getConfig().getString("opfallback", "NOT SET"));
        logAndAddToPasteBinBuffer("");
        logAndAddToPasteBinBuffer("Global Values:");
        logAndAddToPasteBinBuffer("  Limit: " + this.plugin.getConfig().getString("adopt.limit", "NOT SET"));
        logAndAddToPasteBinBuffer("  Price: " + this.plugin.getConfig().getString("adopt.price", "NOT SET"));
        logAndAddToPasteBinBuffer("  Aggro: " + this.plugin.getConfig().getString("adopt.aggro", "NOT SET"));
        logAndAddToPasteBinBuffer("  Curr: " + this.plugin.getConfig().getString("adopt.type", "NOT SET"));
        logAndAddToPasteBinBuffer("");
        Set<String> keys = this.plugin.getConfig().getConfigurationSection("adopt.worlds").getKeys(false);
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                logAndAddToPasteBinBuffer("Values for: " + key);
                logAndAddToPasteBinBuffer("  Limit: " + this.plugin.getConfig().getString("adopt.worlds." + key + ".limit", "NOT SET"));
                logAndAddToPasteBinBuffer("  Price: " + this.plugin.getConfig().getString("adopt.worlds." + key + ".price", "NOT SET"));
                logAndAddToPasteBinBuffer("  Aggro: " + this.plugin.getConfig().getString("adopt.worlds." + key + ".aggro", "NOT SET"));
                logAndAddToPasteBinBuffer("  Curr: " + this.plugin.getConfig().getString("adopt.worlds." + key + ".type", "NOT SET"));
                logAndAddToPasteBinBuffer("");
            }
        }
        logAndAddToPasteBinBuffer("Special Code: FRN001");

        if (args.size() == 1 && args.get(0).equalsIgnoreCase("-p")) {
            String pasteBinUrl = postToPasteBin();
            sender.sendMessage("Version info dumped here: " + ChatColor.GREEN + pasteBinUrl);
            this.plugin.log(Level.INFO, "Version info dumped here: " + pasteBinUrl);
        }
    }

    private void logAndAddToPasteBinBuffer(String string) {
        this.pasteBinBuffer += "[WolfPound] " + string + "\n";
        this.plugin.log(Level.INFO, string);
    }

    private String postToPasteBin() {
        try {
            String data = URLEncoder.encode("api_dev_key", "UTF-8") + "=" + URLEncoder.encode("33ab32380506d99d872b69d35dc9d007", "UTF-8");
            data += "&" + URLEncoder.encode("api_option", "UTF-8") + "=" + URLEncoder.encode("paste", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_code", "UTF-8") + "=" + URLEncoder.encode(this.pasteBinBuffer, "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_private", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_format", "UTF-8") + "=" + URLEncoder.encode("yaml", "UTF-8");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            data += "&" + URLEncoder.encode("api_paste_name", "UTF-8") + "=" + URLEncoder.encode("WolfPound Dump " + dateFormat.format(date), "UTF-8");
            data += "&" + URLEncoder.encode("api_user_key", "UTF-8") + "=" + URLEncoder.encode("6a52cf4a933ba328784ae2b25a70da29", "UTF-8");

            URL url = new URL("http://pastebin.com/api/api_post.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String pasteBinUrl = "";
            while ((line = rd.readLine()) != null) {
                pasteBinUrl = line;
            }
            wr.close();
            rd.close();
            return pasteBinUrl;
        } catch (Exception e) {
            System.out.print(e);
            return "Error Posting to pastebin.com";
        }
    }
}
