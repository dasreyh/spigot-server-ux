package com.weirtz.serverux;

import com.weirtz.serverux.announcements.Announce;
import com.weirtz.serverux.announcements.ServerMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private List<ServerMessage> messages = new ArrayList<ServerMessage>();
    private Announce announce;

    @Override
    public void onEnable() {
        //Register the ConnectionListener Listener, this sets custom join and quit messages.
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);

        loadConfig();

        if (getConfig().getBoolean("plugin-enabled")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    setupAnnouncer();
                    announce.startAnnouncer();
                }
            });
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setupAnnouncer() {
        getMessages();
        //Create announce object.
        announce = new Announce(this, messages);
    }

    //Copy and save config from plugin config template.
    private void loadConfig() {
        final FileConfiguration config = this.getConfig();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void getMessages() {
        for (String key : getConfig().getConfigurationSection("Messages").getKeys(false)) {
            int validLines = 0;
            List<String> messageList = getConfig().getStringList("Messages." + key);
            //If announcement is larger than 10 lines or is 0, reject.
            if (messageList.size() <= 10 && messageList.size() > 0) {
                for (String line : messageList) {
                    if (!line.equals("")) {
                        validLines += 1;
                    }
                }
                //If it is valid, add it to the ServerMessage List
                if (validLines > 0) {
                    messages.add(new ServerMessage(key, getConfig().getStringList("Messages." + key)));
                    //If invalid, print message.
                } else {
                    System.out.println(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix") + " " + ChatColor.RED + key + " Is An Invalid Announcement (Skipping)"));
                }
            } else {
                System.out.println(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix") + " " + ChatColor.RED + key + " Is An Invalid Announcement (Skipping)"));
            }
        }
    }

    //Reload plugin
    private void reloadPlugin() {
        messages.clear();
        if (announce != null) {
            announce.stopAnnouncer();
            announce = null;
        }
        reloadConfig();
        if (getConfig().getBoolean("plugin-enabled")) {
            setupAnnouncer();
            announce.startAnnouncer();
        }
    }





    //COMMANDS
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("announcements")) {

            //console commands
            if (sender instanceof ConsoleCommandSender) {

                //If no arguments provided, send help message
                if (args.length == 0) {
                    System.out.println("Invalid Arguments! Do /announcements help");

                    //If arguments length is 1, check for command.
                } else if (args.length == 1) {
                    //Reload command.
                    if (args[0].equalsIgnoreCase("reload")) {
                        reloadPlugin();
                        System.out.println(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix")) + " Reloaded The Config!");

                        //Toggle command.
                    } else if (args[0].equalsIgnoreCase("toggle")) {
                        if (announce.curr != null) {
                            announce.stopAnnouncer();
                            System.out.println(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix")) + ChatColor.GREEN + " Announcements Disabled");
                        } else if (announce.curr == null) {
                            announce.startAnnouncer();
                            System.out.println(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix")) + ChatColor.GREEN + " Announcements Enabled");
                        }

                        //Help command.
                    } else if (args[0].equalsIgnoreCase("help")) {
                        System.out.println(" ");
                        System.out.println("Server Announcements Help\n");
                        System.out.println("/announcements help");
                        System.out.println("/announcements reload");
                        System.out.println("/announcements toggle");
                        System.out.println(" ");

                    }
                }
            }

            //Player commands
            if (sender instanceof Player) {
                Player player = (Player) sender;

                //If no arguments provided, send help message
                if (args.length == 0 && player.hasPermission("announcements.*"))  {
                    player.sendMessage("Invalid Arguments, Try Doing /announcements help");

                    //If arguments length is 1, check for permissions.
                } else if (args.length == 1 && player.hasPermission("announcements.*")) {

                    //Reload command.
                    if (args[0].equalsIgnoreCase("reload")) {
                        reloadPlugin();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix")) + " Reloaded The Config!");

                        //Toggle command.
                    } else if (args[0].equalsIgnoreCase("toggle")) {
                        if (announce.curr != null) {
                            announce.stopAnnouncer();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix")) + ChatColor.WHITE + " Announcements "+ ChatColor.RED +"Disabled");
                        } else if (announce.curr == null) {
                            announce.startAnnouncer();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("announcer-prefix")) + ChatColor.WHITE + " Announcements "+ ChatColor.GREEN +"Enabled");
                        }

                        //Help command.
                    } else if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(ChatColor.AQUA + " ");
                        player.sendMessage(ChatColor.AQUA + "Server Announcements Help\n");
                        player.sendMessage(ChatColor.AQUA + "/announcements help");
                        player.sendMessage(ChatColor.AQUA + "/announcements reload");
                        player.sendMessage(ChatColor.AQUA + "/announcements toggle");
                        player.sendMessage(ChatColor.AQUA + " ");

                        //If the first argument, with permissions, is invalid, send help message.
                    } else {
                        player.sendMessage("Invalid Command. Do /announcements help for Commands");
                    }
                }

                //If player doesn't have permissions, send message.
                else if (!(player.hasPermission("announcements.*"))) {
                    player.sendMessage(ChatColor.RED + "Permission Denied.");
                }

                //If all else fails, invalid command.
                else {
                    player.sendMessage("Invalid Command. Do /announcements help for Commands");
                }
            }
        }
        return true;
    }

}

