package com.freshearth.currency.database;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.bukkit.entity.Player;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database{
    private final Connection connection;
    private String databaseName;

    public Database(String ip, String username, String password, String db) throws SQLException{
        databaseName = db;
        final Properties prop=new Properties();
        prop.setProperty("user", username);
        prop.setProperty("password", password);
        prop.setProperty("useSSL", "false");
        prop.setProperty("autoReconnect", "true");

        Connection initconnection = DriverManager.getConnection("jdbc:mysql://" + ip, prop);
        try(Statement statement = initconnection.createStatement();){
            statement.execute("CREATE DATABASE IF NOT EXISTS " + databaseName);
            statement.execute("USE " + databaseName);
            initconnection.close();
        }

        connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + db, prop);
        
        
        
        try(Statement statement = connection.createStatement();){
               statement.execute("""
        CREATE TABLE IF NOT EXISTS users (
        UUID varchar(255) PRIMARY KEY, 
        username varchar(255) NOT NULL)
                """);
                statement.execute("""
        CREATE TABLE IF NOT EXISTS currencyAccounts (
        accountID int NOT NULL AUTO_INCREMENT,
        accountName varchar(255) NOT NULL,
        ownerUUID varchar(255),
        value int NOT NULL DEFAULT 0,
        PRIMARY KEY (accountID),
        UNIQUE (accountName))
                """);
                statement.execute("""
        CREATE TABLE IF NOT EXISTS currencyAccountCoOwner (
        ID int NOT NULL AUTO_INCREMENT,
        accountID int NOT NULL,
        UUID varchar(255) NOT NULL,
        permissions int NOT NULL DEFAULT 0,
        PRIMARY KEY (ID))
                """);
                statement.execute("""
        CREATE TABLE IF NOT EXISTS currencyHistory (
        ID int NOT NULL AUTO_INCREMENT,
        senderID int NOT NULL,
        senderUUID varchar(255),
        senderAmount int NOT NULL,
        recieverID int NOT NULL,
        recieverAmount int NOT NULL,
        amount int NOT NULL,
        PRIMARY KEY (ID))
                """);
        }
    }

    public void addUser(Player p) throws SQLException {
        String uuid = p.getUniqueId().toString();
        String username = p.getName();
        if (userExists(p.getUniqueId().toString())) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET username = ? WHERE uuid = ?")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, uuid);
                preparedStatement.executeUpdate();
                return;
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (uuid, username) VALUES (?,?)")) {
        preparedStatement.setString(1, uuid);
        preparedStatement.setString(2, username);
        preparedStatement.executeUpdate();
        }
    }


    public boolean userExists(String uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }


    public void createAccount(String uuid, String accountName) throws SQLException {
        String accountID = "";

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currencyAccounts (accountName, ownerUUID) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, accountName);
            preparedStatement.setString(2, uuid);
            
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()){
            accountID = resultSet.getString(1);
            }
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currencyAccountCoOwner (accountID, UUID, permissions) VALUES (?,?,?)")) {
            preparedStatement.setString(1, accountID);
            preparedStatement.setString(2, uuid);
            preparedStatement.setString(3, "10");
            preparedStatement.executeUpdate();
        }
    }


    public boolean accountNameExists(String accountName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts WHERE accountName = ?")) {
            preparedStatement.setString(1, accountName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }


    public boolean accountIDExists(String accountID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts WHERE accountID = ?")) {
            preparedStatement.setString(1, accountID);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public int playerHasAccessToAccount(String uuid, int accountID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccountCoOwner WHERE accountID = ? AND UUID = ?")) {
            preparedStatement.setInt(1, accountID);
            preparedStatement.setString(2, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt("permissions");
            return 0;
        }
    }
    public boolean playerOwnsAnAccount(String uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts WHERE ownerUUID = ?")) {
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public String getPlayerUUID(String name) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("UUID");
        }
    }

    public String[] getUserAccountsID(String uuid) throws SQLException {
            ArrayList<String> list = new ArrayList<String>();
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccountCoOwner WHERE UUID = ?")) {
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    list.add(resultSet.getString("accountID"));
                }
                String[] result = new String[list.size()];
                result = list.toArray(result);
                return result;
            }
    }

    public String[] getAllAccountsNames() throws SQLException {
        ArrayList<String> list = new ArrayList<String>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("accountName"));
            }
            String[] result = new String[list.size()];
            result = list.toArray(result);
            return result;
        }
    }

    public String getAccountNameFromID(String ID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts WHERE accountID = ?")) {
            preparedStatement.setString(1, ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("accountName");
        }
    }

    public int getAccountIDFromName(String name) throws SQLException {

        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts WHERE accountName = ?")) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("accountID");
        }
    }

    public int getAccountValue(String accountName) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts WHERE accountName = ?")) {
            preparedStatement.setString(1, accountName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt("value");
            return 0;
        }
    }

    public String[] getTopAccountsNames(int num) throws SQLException{
        String[] output = new String[num];
        try(PreparedStatement preparedStatement = connection.prepareStatement("Select * FROM currencyAccounts ORDER BY value DESC")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            for (int i = 0; i < output.length; i++) {
                if (resultSet.next()) {
                    output[i] = resultSet.getString("accountName");
                } else {
                    break;
                }
            }
        }
        return output;
    }

    public void transferMoney(String senderName, String senderUUID, String recieverName, int amount) throws SQLException {
        int senderID = getAccountIDFromName(senderName);
        int recieverID = getAccountIDFromName(recieverName);
        transferMoney(senderID, senderUUID, recieverID, amount);


    }

    private void transferMoney(int senderID, String senderUUID, int recieverID, int amount) throws SQLException {
        int senderAmount;
        int recieverAmount;
        if (senderID == recieverID) {
            return;
        }

        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencyAccounts WHERE accountID = ?")) { //Gets the value of each account
            preparedStatement.setString(1, senderID+"");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            senderAmount = resultSet.getInt("value");
            preparedStatement.setString(1, recieverID+"");
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            recieverAmount = resultSet.getInt("value");
        }

        //Creates an entry for transaction history
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currencyHistory (senderID, senderUUID, senderAmount, recieverID, recieverAmount, amount) VALUES (?,?,?,?,?,?)")) {
            preparedStatement.setInt(1, senderID);
            preparedStatement.setString(2, senderUUID);
            preparedStatement.setInt(3, senderAmount);
            preparedStatement.setInt(4, recieverID);
            preparedStatement.setInt(5, recieverAmount);
            preparedStatement.setInt(6, amount);
            preparedStatement.executeUpdate();
        }
        senderAmount -= amount;
        recieverAmount += amount;
        //Updates account values
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE currencyAccounts SET value = ? WHERE accountID = ?")) {
            preparedStatement.setInt(1, senderAmount);
            preparedStatement.setInt(2, senderID);
            preparedStatement.executeUpdate();
            preparedStatement.setInt(1, recieverAmount);
            preparedStatement.setInt(2, recieverID);
            preparedStatement.executeUpdate();
        }

    }


    public void closeConnection() throws SQLException{
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
