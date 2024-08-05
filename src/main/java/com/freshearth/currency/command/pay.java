package com.freshearth.currency.command;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.freshearth.currency.Plugin;

public class pay implements CommandExecutor {

    private final Plugin plugin;

    public pay(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            String senderName = "server";
            String uuid = "";

            if (sender instanceof Player) { //A bunch of checks on the play and if the command is correct
                senderName = ((Player) sender).getName();
                uuid = ((Player) sender).getUniqueId().toString();
            }
            if (!sender.hasPermission("feucurrency.pay")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to run this command");
                return false;
            }
            String senderAccount;
            if (args.length > 4 ) {
                senderAccount = args[4];
            } else if (args.length == 3) {
                senderAccount = senderName;
            }
            else {
                sender.sendMessage(ChatColor.RED +"Not enough arguments\nUsage: /currency pay <amount> to <account> or /currency pay <amount> to <account> from <account>");
                return false;
            }

        payment(sender, uuid, senderAccount, args[0], args[2]);

        return true;
        } catch (Exception e){
        sender.sendMessage(ChatColor.RED + "Something went wrong, please contact an server operator.");
        System.err.println("An error occured");
        e.printStackTrace();
        return false;
        }
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

                if (!(this.plugin.getDatabase().playerHasAccessToAccount(uuid, this.plugin.getDatabase().getAccountIDFromName(senderAccount)) > 0)) {// player account permission
                    sender.sendMessage(ChatColor.RED + "You do not have permission to access that account");
                    return;
                }

                if (strAmount.startsWith("-")){ //Negative number check
                    sender.sendMessage(ChatColor.RED + "You cannot use negative numbers");
                    return;
                }
                if (amount < 0){ //^^^
                    sender.sendMessage(ChatColor.RED + "You cannot use negative numbers");
                    return;
                }


                if (recieverAccount.toLowerCase().equals(senderAccount.toLowerCase())) { //If both specified accounts are equal
                    sender.sendMessage(ChatColor.RED + "You cannot pay account with the same account");
                    return;
                }
                if ((this.plugin.getDatabase().getAccountValue(senderAccount) - amount) < 0) { // If account will not be in the negatives
                    sender.sendMessage(ChatColor.RED + senderAccount + " doesn't have enough money to send that amount");
                    return;
                }

                this.plugin.getDatabase().transferMoney(senderAccount, uuid, recieverAccount,  amount);
                sender.sendMessage("Successfully transfered $" + strAmount + " from " + senderAccount + " to " + recieverAccount);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Amount entered is not a valid number");
                //e.printStackTrace();
            } catch (SQLException e) {
                sender.sendMessage(ChatColor.RED + "Something went wrong, Please contact server operator.");
                e.printStackTrace();
            }
    }
    
}