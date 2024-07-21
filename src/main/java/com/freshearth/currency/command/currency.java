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
                    sender.sendMessage(createListOfAccounts(this.plugin.getDatabase().getUserAccountsID(uuid)));
                    } catch (SQLException e) {
                    e.printStackTrace();    }
                break;
                case "balance":
                    if (args.length > 1) senderName = args[1];
                    if (this.plugin.getDatabase().accountNameExists(senderName))
                        sender.sendMessage("$" + this.plugin.getDatabase().getAccountValue(senderName));
                    else 
                        sender.sendMessage(ChatColor.RED +"Could not find account with name " + senderName);
                break;
                        
                case "pay": // /currency pay <amount> to <recieverAccount> from <senderAccount>
                        pay(sender, args, uuid, senderName);
                break;

                
                    
                case "admin":
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

    private void pay(CommandSender sender,String[] args, String uuid, String senderName) {
        
            try {
                String senderAccount = "";
                if (args.length > 5 ) {
                    senderAccount = args[5];
                } else if (args.length == 4) {
                    senderAccount = senderName;
                }
                else {
                    sender.sendMessage(ChatColor.RED +"Not enough arguments\nUsage: /currency pay <amount> to <account> or /currency pay <amount> to <account> from <account>");
                    return;
                }
                String recieverAccount = args[3];
                int amount = Integer.parseInt(args[1]);
                if (!this.plugin.getDatabase().accountNameExists(args[3])) {
                    sender.sendMessage(ChatColor.RED + "Recieving account not found");
                    return;
                }
                

                if (!this.plugin.getDatabase().accountNameExists(senderAccount)) {
                    sender.sendMessage(ChatColor.RED + "Sending account not found");
                    return;
                }
                if (!(this.plugin.getDatabase().playerHasAccessToAccount(uuid, this.plugin.getDatabase().getAccountIDFromName(senderAccount)) > 0)) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to access that account");
                    return;
                }
                if (recieverAccount.toLowerCase() == senderAccount.toLowerCase()) {
                    sender.sendMessage(ChatColor.RED + "You cannot pay account with the same account");
                    return;
                }
                if ((this.plugin.getDatabase().getAccountValue(senderAccount) - amount) < 0) {
                    sender.sendMessage(ChatColor.RED + "Sending doesn't have enough money to send that amount");
                    return;
                }

                this.plugin.getDatabase().transferMoney(senderAccount, uuid, recieverAccount,  amount);
                sender.sendMessage("Successfully transfered $" + args[1] + " from " + senderAccount + " to " + recieverAccount);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Amount entered is not a valid number");
                //e.printStackTrace();
            } catch (SQLException e) {
                sender.sendMessage(ChatColor.RED + "Something went wrong, Please contact server operator.");
                e.printStackTrace();
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
