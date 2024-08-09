package com.freshearth.currency.command;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.freshearth.currency.Plugin;

public class currency implements CommandExecutor {

    private final Plugin plugin;

    public currency(Plugin plugin) {
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
            

            if (args.length > 0) { 
                switch (args[0]) {

                case "list":
                try {
                    sender.sendMessage(createListOfAccounts(this.plugin.getDatabase().getUserAccountsID(uuid))); //Generates a list of accounts that the player owns
                    } catch (SQLException e) {
                    e.printStackTrace();    }
                break;             
                
                    
                case "admin": //TODO: add admin commands [transfer, add, create, join]
                    
                if (args.length > 1 && sender.hasPermission("feucurrency.admin")) {    
                    switch (args[1]) {
                        case "create":
                            if (args.length > 2) {
                                if (args.length > 3) {
                                    uuid = this.plugin.getDatabase().getPlayerUUID(args[3]);
                                }
                                this.plugin.getDatabase().createAccount(uuid, args[2]);
                                sender.sendMessage("Created account");
                            }
                            else {
                                sender.sendMessage(ChatColor.RED + "Not enough arguments");
                            }
                            break;
                        case "transfer":
                            if (args.length > 4) {
                            this.plugin.getDatabase().transferMoney(args[2], uuid, args[3], Integer.parseInt(args[4]));
                            } else {
                                sender.sendMessage(ChatColor.RED + "Not enough arguments");
                            }
                        break;

                        case "add":
                        break;
                        case "join":
                    
                        default:
                            break;
                    }
                }
                        break;
                    default:
                        break;
                }
            }
        return true;
        } catch (Exception e){
            sender.sendMessage(ChatColor.RED + "Something went wrong, please contact an server operator.");
            System.err.println("An error occured");
            e.printStackTrace();
        return false;
        }
    }


    private String createListOfAccounts(String[] IDs) {

        String stringBuilder = "";

        try {
        for (String string : IDs) {
            stringBuilder += this.plugin.getDatabase().getAccountNameFromID(string) + ", ";
        }
        return stringBuilder;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }


}
