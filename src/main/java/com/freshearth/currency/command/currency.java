package com.freshearth.currency.command;

import java.sql.SQLException;

import org.bukkit.ChatColor;
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
                case "balance": //TODO: Redundant
                    if (args.length > 1) senderName = args[1];
                    if (this.plugin.getDatabase().accountNameExists(senderName))
                        sender.sendMessage("$" + this.plugin.getDatabase().getAccountValue(senderName));
                    else 
                        sender.sendMessage(ChatColor.RED +"Could not find account with name " + senderName);
                break;
                        
                case "pay": // /currency pay <amount> to <recieverAccount> from <senderAccount>
                        sender.sendMessage(ChatColor.RED +"Please use /pay instead"); //To lazy to fix the auto complete
                break;

                
                    
                case "admin": //TODO: add admin commands [transfer, add, create, join]
                sender.sendMessage("Uh oh, Please insert the admin");
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
            stringBuilder += this.plugin.getDatabase().getAccountNameFromID(string) + ",";
        }
        return stringBuilder;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }


}
