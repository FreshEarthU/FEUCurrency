package com.freshearth.currency.tabCompletion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.freshearth.currency.Plugin;

public class balanceTabCompletion implements TabCompleter{
    
    private final Plugin plugin;

    public balanceTabCompletion(Plugin plugin) {
        this.plugin = plugin;
    }


    private List<String> createListOfAccountsAccess(String[] IDs) {

        List<String> accounts = new ArrayList<>();
        try {
        for (String string : IDs) {
            accounts.add(this.plugin.getDatabase().getAccountNameFromID(string)) ;
        }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        String senderName = "server";
        String uuid = "";

        if (sender instanceof Player) {
            senderName = ((Player) sender).getName();
            uuid = ((Player) sender).getUniqueId().toString();
        }
        try {
            commands.addAll(createListOfAccountsAccess(this.plugin.getDatabase().getUserAccountsID((uuid))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commands;
    }
}
