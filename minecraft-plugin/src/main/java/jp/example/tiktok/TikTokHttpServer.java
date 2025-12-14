package jp.example.tiktok;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class TikTokHttpServer {

    private final JavaPlugin plugin;
    private HttpServer server;

    public TikTokHttpServer(JavaPlugin plugin, int port) {
        this.plugin = plugin;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/tiktok/event", new TikTokHandler());
            server.setExecutor(null);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create HTTP server");
            e.printStackTrace();
        }
    }

    public void start() {
        if (server != null) {
            server.start();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    class TikTokHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            plugin.getLogger().info("[TikTok] " + body);

            byte[] response = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        }
    }
}
