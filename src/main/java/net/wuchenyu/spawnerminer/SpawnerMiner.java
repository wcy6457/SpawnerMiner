package net.wuchenyu.spawnerminer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnerMiner extends JavaPlugin {
    private static SpawnerMiner instance;
    public static SpawnerMiner getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new MiningListener() , this);
        getLogger().info("注册监听事件成功");
        Bukkit.getPluginCommand("spawner_miner").setExecutor(new Command());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
