package jp.example.mclivetrap;

import jp.example.mclivetrap.box.TrapBoxManager;
import jp.example.mclivetrap.command.TrapCommand;
import jp.example.mclivetrap.http.HttpServerService;
import jp.example.mclivetrap.listener.TNTExplodeListener;
import jp.example.mclivetrap.listener.TrapProtectListener;
import jp.example.mclivetrap.tnt.TNTAttackService;
import org.bukkit.plugin.java.JavaPlugin;

public class MCLiveTrapPlugin extends JavaPlugin {

    private TrapBoxManager trapBoxManager;
    private TNTAttackService tntAttackService;
    private HttpServerService httpServerService;

    private boolean gameActive = false; // ← 追加

    @Override
    public void onEnable() {
        saveDefaultConfig();

        trapBoxManager = new TrapBoxManager();
        tntAttackService = new TNTAttackService(trapBoxManager);

        TrapCommand trapCommand = new TrapCommand(trapBoxManager, this);
        getCommand("mclivetrap").setExecutor(trapCommand);

        getServer().getPluginManager().registerEvents(new TNTExplodeListener(), this);
        getServer().getPluginManager().registerEvents(new TrapProtectListener(trapBoxManager), this);

        int port = getConfig().getInt("http.port", 4567);
        if (port <= 0) {
            getLogger().severe("Invalid http.port in config.yml");
            getLogger().severe("HTTP server will NOT start");
        } else {
            getLogger().info("Starting HTTP server on port " + port);
            httpServerService = new HttpServerService(this, port);
            httpServerService.start();
        }

        getLogger().info("MCLiveTrap enabled");
    }

    @Override
    public void onDisable() {
        if (httpServerService != null) {
            httpServerService.stop();
        }
        getLogger().info("MCLiveTrap disabled");
    }

    public TrapBoxManager getTrapBoxManager() {
        return trapBoxManager;
    }

    public TNTAttackService getTntAttackService() {
        return tntAttackService;
    }

    // ==============================
    // ゲーム状態管理
    // ==============================
    public boolean isGameActive() {
        return gameActive;
    }

    public void setGameActive(boolean active) {
        this.gameActive = active;
    }
}
