package com.freshearth.currency.command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.freshearth.currency.Plugin;

public class balance implements CommandExecutor{
    private final Plugin plugin;

    public balance(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            String senderName = "server";
            String uuid = "";

            if (sender instanceof Player) {
                senderName = ((Player) sender).getName();
                uuid = ((Player) sender).getUniqueId().toString();
            }
            if (!sender.hasPermission("feucurrency.user")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to run this command");
                return false;
            }
            if (args.length > 1) senderName = args[1];
                    if (this.plugin.getDatabase().accountNameExists(senderName))
                        sender.sendMessage("$" + this.plugin.getDatabase().getAccountValue(senderName));
                    else 
                        sender.sendMessage(ChatColor.RED +"Could not find account with name " + senderName);
        return true;
        } catch (Exception e){
            sender.sendMessage(ChatColor.RED + "Something went wrong, please contact an server operator.");
            System.err.println("An error occured");
            e.printStackTrace();
        return false;
        }
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
}