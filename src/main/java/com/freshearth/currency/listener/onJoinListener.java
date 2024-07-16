package com.freshearth.currency.listener;

import org.bukkit.event.Listener;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.freshearth.currency.Plugin;



public class onJoinListener implements Listener{


    private final Plugin plugin;

    public onJoinListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        try {
            this.plugin.getDatabase().addUser(event.getPlayer());
            if (!this.plugin.getDatabase().playerOwnsAnAccount(event.getPlayer().getUniqueId().toString())) {
                this.plugin.getDatabase().createAccount(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
            }
        }catch (SQLException e) {
            this.plugin.getLogger().warning("An error occured while adding user to database");
            event.getPlayer().sendMessage(ChatColor.RED + "[FEUCurrency] An error occured adding user to the database. Please contact an server operator.");
            e.printStackTrace();
        }
        
    }
}
