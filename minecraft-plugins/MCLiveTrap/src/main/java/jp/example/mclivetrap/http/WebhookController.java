package jp.example.mclivetrap.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jp.example.mclivetrap.MCLiveTrapPlugin;
import jp.example.mclivetrap.box.TrapBoxManager;
import jp.example.mclivetrap.tnt.TNTAttackService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebhookController implements HttpHandler {

    private final JavaPlugin plugin;
    private final TrapBoxManager trapBoxManager;
    private final TNTAttackService tntService;

    public WebhookController(JavaPlugin plugin, TrapBoxManager trapBoxManager, TNTAttackService tntService) {
        this.plugin = plugin;
        this.trapBoxManager = trapBoxManager;
        this.tntService = tntService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        plugin.getLogger().info("Received webhook: " + body);

        if (plugin instanceof MCLiveTrapPlugin mtp && !mtp.isGameActive()) {
            plugin.getLogger().info("Webhook received but game is not active yet: " + body);
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
            return;
        }

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
        if (!(plugin instanceof MCLiveTrapPlugin mtp)) return;

        // 標準メッセージ
        switch (type) {
            case "like" -> Bukkit.broadcastMessage("§a[LIKE] §f" + data.get("user").getAsString());
            case "follow" -> Bukkit.broadcastMessage("§b[FOLLOW] §f" + data.get("user").getAsString());
            case "share" -> Bukkit.broadcastMessage("§d[SHARE] §f" + data.get("user").getAsString());
            case "gift" -> Bukkit.broadcastMessage("§c[GIFT] §f" + data.get("user").getAsString() +
                    " sent " + data.get("gift_name").getAsString() + " x" + data.get("count").getAsInt());
            case "subscribe" -> Bukkit.broadcastMessage("§6[SUBSCRIBE] §f" + data.get("user").getAsString());
            default -> plugin.getLogger().warning("Unknown event type: " + type);
        }

        // アクション処理
        List<Map<String, Object>> actions = new ArrayList<>();
        try {
            List<Map<?, ?>> rawActions;
            if ("gift".equalsIgnoreCase(type)) {
                String giftName = data.get("gift_name").getAsString();
                rawActions = mtp.getConfig().getMapList("events.gift." + giftName + ".actions");
            } else {
                rawActions = mtp.getConfig().getMapList("events." + type + ".actions");
            }

            for (Map<?, ?> m : rawActions) {
                Map<String, Object> casted = new HashMap<>();
                for (Map.Entry<?, ?> entry : m.entrySet()) {
                    casted.put(String.valueOf(entry.getKey()), entry.getValue());
                }
                actions.add(casted);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to read actions from config for type: " + type);
            e.printStackTrace();
        }

        for (Map<String, Object> a : actions) {
            if (!Boolean.TRUE.equals(a.get("enabled"))) continue;

            String actionType = (String) a.get("type");
            switch (actionType.toLowerCase()) {
                case "tnt" -> tntService.spawnTNT(((Double) a.get("amount")).intValue());
                case "zombie" -> trapBoxManager.spawnZombies(((Double) a.get("amount")).intValue());
                case "message" -> Bukkit.broadcastMessage((String) a.get("text"));
                default -> plugin.getLogger().warning("Unknown action type in config: " + actionType);
            }
        }
    }
}
