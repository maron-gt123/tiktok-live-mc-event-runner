package jp.example.mclivetrap;

import jp.example.mclivetrap.box.TrapBoxManager;
import jp.example.mclivetrap.command.TrapCommand;
import jp.example.mclivetrap.commandloader.CommandLoader;
import jp.example.mclivetrap.http.HttpServerService;
import jp.example.mclivetrap.listener.TNTExplodeListener;
import jp.example.mclivetrap.listener.TrapProtectListener;
import jp.example.mclivetrap.tnt.TNTAttackService;
import jp.example.mclivetrap.listener.TrapBoxPlaceListener;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class MCLiveTrapPlugin extends JavaPlugin {

    private TrapBoxManager trapBoxManager;
    private TNTAttackService tntAttackService;
    private HttpServerService httpServerService;
    private CommandLoader commandLoader;

    private boolean gameActive = false; // ゲーム状態管理

    @Override
    public void onEnable() {

        // 設定ファイルをロード
        saveDefaultConfig();

        // マネージャ・サービス初期化
        trapBoxManager = new TrapBoxManager();
        tntAttackService = new TNTAttackService(trapBoxManager);
        commandLoader = new CommandLoader(this);

        // コマンド登録
        TrapCommand trapCommand = new TrapCommand(trapBoxManager, this);
        getCommand("mclivetrap").setExecutor(trapCommand);

        // イベントリスナー登録
        getServer().getPluginManager().registerEvents(new TNTExplodeListener(), this);
        getServer().getPluginManager().registerEvents(new TrapProtectListener(trapBoxManager), this);
        getServer().getPluginManager().registerEvents(new TrapBoxPlaceListener(trapBoxManager), this);

        // events ディレクトリ作成 & デフォルトイベントコピー 詳細は下部
        setupEventsFolder();

        // HTTPサーバ起動
        httpServerService = new HttpServerService(this, trapBoxManager, commandLoader);
        httpServerService.start();

        getLogger().info("MCLiveTrap enabled");
    }
    public CommandLoader getCommandLoader() {
        return commandLoader;
    }

    @Override
    public void onDisable() {
        // HTTPサーバー停止時の処理
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
    public boolean isGameActive() {
        return gameActive;
    }
    public void setGameActive(boolean active) {
        this.gameActive = active;
    }

     // プラグイン起動時に resources/events/*.yml を plugins/MCLiveTrap/events/ にコピー*//
    private void setupEventsFolder() {
        File eventsDir = new File(getDataFolder(), "events");
        if (!eventsDir.exists()) eventsDir.mkdirs();

        // デフォルトイベントファイルリスト
        String[] defaultEvents = {"bedrock_tnt.yml", "message_thanks.yml"};

        for (String fileName : defaultEvents) {
            File outFile = new File(eventsDir, fileName);
            if (!outFile.exists()) { // 既存ファイルは上書きしない
                try (InputStream is = getResource("events/" + fileName)) {
                    if (is != null) {
                        Files.copy(is, outFile.toPath());
                        getLogger().info("Copied default event file: " + fileName);
                    } else {
                        getLogger().warning("Default event resource not found: " + fileName);
                    }
                } catch (IOException e) {
                    getLogger().severe("Failed to copy event file: " + fileName);
                    e.printStackTrace();
                }
            }
        }
    }
}
