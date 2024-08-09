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

public class currencyTabCompletion implements TabCompleter {


    private final Plugin plugin;

    public currencyTabCompletion(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        @SuppressWarnings("unused")
        String senderName = "server";
        String uuid = "";

        if (sender instanceof Player) {
            senderName = ((Player) sender).getName();
            uuid = ((Player) sender).getUniqueId().toString();
        }
        try {
        if (args.length == 1) {
            commands.add("list");
            if (sender.hasPermission("feucurrency.admin")) {
                commands.add("admin");
            }
            
        } else if (args.length > 1) { //TODO: add admin commands to Tab Complet
            if (sender.hasPermission("feucurrency.admin")) {
                commands.add("create <account name> [player]");
                commands.add("transfer <account from> <account to> <amount>");
            }
        }

        return commands;
        } catch (Exception e) {
            e.printStackTrace();
            return commands;
        }
    }
}
