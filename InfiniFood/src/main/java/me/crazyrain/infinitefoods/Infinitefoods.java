package me.crazyrain.infinitefoods;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Infinitefoods extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getCommand("infini").setExecutor(new Commands(this));
        getCommand("infrl").setExecutor(new Commands(this));
        getPlugin(Infinitefoods.class).getLogger().log(Level.INFO, "InfiniFoods is online");
        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
