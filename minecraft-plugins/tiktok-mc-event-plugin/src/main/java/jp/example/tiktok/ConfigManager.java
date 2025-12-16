package jp.example.tiktok;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private final int likeThreshold;
    private final int likeCooldown;
    private final Map<String, GiftConfig> giftConfigs = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.likeThreshold = config.getInt("like.amount", 50);
        this.likeCooldown = config.getInt("like.cooldown", 10);

        if (config.isConfigurationSection("gifts")) {
            for (String key : config.getConfigurationSection("gifts").getKeys(false)) {
                int actionAmount = config.getInt("gifts." + key + ".actionAmount", 1);
                int cooldown = config.getInt("gifts." + key + ".cooldown", 5);
                giftConfigs.put(key, new GiftConfig(actionAmount, cooldown));
            }
        }
    }

    public int getLikeThreshold() {
        return likeThreshold;
    }

    public int getLikeCooldown() {
        return likeCooldown;
    }

    public GiftConfig getGiftConfig(String giftName) {
        return giftConfigs.get(giftName);
    }
}
