package com.weirtz.serverux;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        //Register the ConnectionListener Listener, this sets custom join and quit messages.
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



}

