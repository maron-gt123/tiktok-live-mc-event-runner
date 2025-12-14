package jp.example.tiktok;

import org.bukkit.plugin.java.JavaPlugin;

public class TikTokEventBridge extends JavaPlugin {

    private TikTokHttpServer httpServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        int port = getConfig().getInt("http-port", 8080);

        httpServer = new TikTokHttpServer(this, port);
        httpServer.start();

        getLogger().info("TikTokEventBridge enabled on port " + port);
    }

    @Override
    public void onDisable() {
        if (httpServer != null) {
            httpServer.stop();
        }
    }
}
