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
                                payment(sender, uuid, args[2], args[4], args[3]);
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

    public void payment(CommandSender sender, String uuid, String senderAccount, String strAmount, String recieverAccount) {
        
        try {
            
            int amount = Integer.parseInt(strAmount);
            if (!this.plugin.getDatabase().accountNameExists(recieverAccount)) { //Do accounts exist
                sender.sendMessage(ChatColor.RED + "Recieving account not found");
                return;
            }
            if (!this.plugin.getDatabase().accountNameExists(senderAccount)) { //^^^
                sender.sendMessage(ChatColor.RED + "Sending account not found");
                return;
            }


            if (strAmount.startsWith("-")){ //Negative number check
                sender.sendMessage(ChatColor.RED + "You cannot use zero or negative numbers");
                return;
            }
            if (amount < 1){ //^^^
                sender.sendMessage(ChatColor.RED + "You cannot use zero or negative numbers");
                return;
            }


            if (recieverAccount.toLowerCase().equals(senderAccount.toLowerCase())) { //If both specified accounts are equal
                sender.sendMessage(ChatColor.RED + "You cannot pay account with the same account");
                return;
            }

            this.plugin.getDatabase().transferMoney(senderAccount, uuid, recieverAccount,  amount);
            sender.sendMessage("Successfully transfered $" + strAmount + " from " + senderAccount + " to " + recieverAccount);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount entered is not a valid integer");
            //e.printStackTrace();
        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "Something went wrong, Please contact server operator.");
            e.printStackTrace();
        }
}
}
