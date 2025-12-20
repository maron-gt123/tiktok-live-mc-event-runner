package jp.example.mclivetrap.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WebhookController implements HttpHandler {

    private final JavaPlugin plugin;

    public WebhookController(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        plugin.getLogger().info("Received webhook: " + body);

        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String type = json.get("type").getAsString();
            JsonObject data = json.getAsJsonObject("data");

            Bukkit.getScheduler().runTask(plugin, () -> handleEvent(type, data));

            exchange.sendResponseHeaders(200, 0);
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid webhook payload");
            e.printStackTrace();
            exchange.sendResponseHeaders(400, 0);
        } finally {
            exchange.getResponseBody().close();
        }
    }

    private void handleEvent(String type, JsonObject data) {
        switch (type) {
            case "like" -> {
                String user = data.get("user").getAsString();
                Bukkit.broadcastMessage("§a[LIKE] §f" + user);
            }
            case "follow" -> {
                String user = data.get("user").getAsString();
                Bukkit.broadcastMessage("§b[FOLLOW] §f" + user);
            }
            case "share" -> {
                String user = data.get("user").getAsString();
                Bukkit.broadcastMessage("§d[SHARE] §f" + user);
            }
            case "gift" -> {
                String user = data.get("user").getAsString();
                String giftName = data.get("gift_name").getAsString();
                int count = data.get("count").getAsInt();

                Bukkit.broadcastMessage(
                    "§c[GIFT] §f" + user + " sent " + giftName + " x" + count
                );
            }
            case "subscribe" -> {
                String user = data.get("user").getAsString();
                Bukkit.broadcastMessage("§6[SUBSCRIBE] §f" + user);
            }
            default -> plugin.getLogger().warning("Unknown event type: " + type);
        }
    }
}
