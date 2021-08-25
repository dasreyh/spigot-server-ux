package com.weirtz.serverux;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void joinMessage(PlayerJoinEvent eventJoin){
        eventJoin.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.stripColor(eventJoin.getPlayer().getDisplayName()));
    }

    @EventHandler
    public void quitMessage(PlayerQuitEvent eventQuit){
        eventQuit.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.stripColor(eventQuit.getPlayer().getDisplayName()));
    }
}
