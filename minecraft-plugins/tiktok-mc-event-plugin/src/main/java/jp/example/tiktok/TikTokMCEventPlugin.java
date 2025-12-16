package jp.example.tiktok;

import org.bukkit.plugin.java.JavaPlugin;

public final class TikTokMCEventPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private TikTokEventHandler eventHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        eventHandler = new TikTokEventHandler(this, configManager);

        int port = getConfig().getInt("server.port", 30000);
        eventHandler.startHttpServer(port);

        getLogger().info("TikTok MC Event Plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (eventHandler != null) {
            eventHandler.stopHttpServer();
        }
        getLogger().info("TikTok MC Event Plugin disabled!");
    }
}
