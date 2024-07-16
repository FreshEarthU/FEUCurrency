package com.freshearth.currency;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.freshearth.currency.command.currency;
import com.freshearth.currency.command.currencyTabCompletion;
import com.freshearth.currency.database.Database;
import com.freshearth.currency.listener.onJoinListener;

/*
 * currency java plugin
 */
public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("FEUCurrency");
  private Database database;

  FileConfiguration config = getConfig();
  static Connection connection;

  public void onEnable()
  {
    configSetup();
    try{
      
      database = new Database(config.getString("DATABASE_URL"), config.getString("DATABASE_USERNAME"), config.getString("DATABASE_PASSWORD"));
      
    } catch (SQLException e) {
      
      e.printStackTrace();
      LOGGER.warning("Failed to connect to the database");
      this.getServer().getPluginManager().disablePlugin(this);
    }
    getServer().getPluginManager().registerEvents(new onJoinListener(this), this);
    registerCommands();
    LOGGER.info("currency enabled");
  }

  public void configSetup() {
    //config.addDefault("USE_MYSQL", false); //Wont work with local database, Might use SQLite for local db If I ever get around to it.
    config.addDefault("DATABASE_URL",  "127.0.0.1/feu");
    config.addDefault("DATABASE_USERNAME", "username");
    config.addDefault("DATABASE_PASSWORD", "password");
    config.options().copyDefaults(true);
    saveConfig();
  }


  public void registerCommands() {
      getCommand("currency").setExecutor(new currency(this));
      getCommand("currency").setTabCompleter(new currencyTabCompletion(this));
  }

  public Database getDatabase(){
    return this.database;
  }

  public void onDisable()
  {
    try {
      database.closeConnection();
    } catch (SQLException e){
      e.printStackTrace();
    }
  }
}
