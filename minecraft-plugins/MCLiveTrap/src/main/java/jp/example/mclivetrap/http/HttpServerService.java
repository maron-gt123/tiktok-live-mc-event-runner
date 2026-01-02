package jp.example.mclivetrap.http;

import com.sun.net.httpserver.HttpServer;
import jp.example.mclivetrap.box.TrapBoxManager;
import jp.example.mclivetrap.tnt.TNTAttackService;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerService {

    private final JavaPlugin plugin;
    private final TrapBoxManager trapBoxManager;
    private final TNTAttackService tntService;
    private HttpServer server;
    private final int port;

    public HttpServerService(JavaPlugin plugin, TrapBoxManager trapBoxManager, TNTAttackService tntService) {
        this.plugin = plugin;
        this.trapBoxManager = trapBoxManager;
        this.tntService = tntService;
        this.port = plugin.getConfig().getInt("http.port", 4567);
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            server.createContext("/event", new WebhookController(plugin, trapBoxManager, tntService));
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            plugin.getLogger().info("HTTP server listening on 0.0.0.0:" + port + " (/event)");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start HTTP server");
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("HTTP server stopped");
        }
    }
}
