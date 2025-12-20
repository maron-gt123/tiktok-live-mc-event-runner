package jp.example.mclivetrap.http;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerService {

    private final JavaPlugin plugin;
    private final int port;
    private HttpServer server;

    public HttpServerService(JavaPlugin plugin, int port) {
        this.plugin = plugin;
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(
                    new InetSocketAddress("0.0.0.0", port),
                    0
            );

            // Python 側の POST 先
            server.createContext("/event", new WebhookController(plugin));

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();

            plugin.getLogger().info(
                    "HTTP server listening on 0.0.0.0:" + port + " (/event)"
            );

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
