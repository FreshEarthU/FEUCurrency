package com.freshearth.currency.tabCompletion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.freshearth.currency.Plugin;

public class payTabCompletion implements TabCompleter {


    private final Plugin plugin;

    public payTabCompletion(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        @SuppressWarnings("unused")
        String senderName = "server";
        String uuid = "";
        try {
        if (sender instanceof Player) {
            senderName = ((Player) sender).getName();
            uuid = ((Player) sender).getUniqueId().toString();
        }

        if (args.length == 1) {
                commands.add("0");
            }
            else if (args.length == 2) {
                commands.add("to");
            }
            else if (args.length == 3) {

                commands.addAll(Arrays.asList(this.plugin.getDatabase().getAllAccountsNames()));
            }
            else if (args.length == 4) {
                commands.add("from");
            }
            else if (args.length == 5) {
                commands.addAll(createListOfAccountsAccess(this.plugin.getDatabase().getUserAccountsID((uuid))));
            }



        return commands;
        } catch (SQLException e) {
            e.printStackTrace();
            return commands;
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
