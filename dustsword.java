package com.dust.sword;
import org.bukkit.plugin.java.JavaPlugin;
public class DustSword extends JavaPlugin {
    public void onEnable() {
        getCommand("dustsword").setExecutor(new DustCommand(this));
        getServer().getPluginManager().registerEvents(new DustListener(this), this);
    }
}