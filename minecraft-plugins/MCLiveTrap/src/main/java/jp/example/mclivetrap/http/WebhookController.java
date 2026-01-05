package jp.example.mclivetrap.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jp.example.mclivetrap.MCLiveTrapPlugin;
import jp.example.mclivetrap.box.TrapBox;
import jp.example.mclivetrap.box.TrapBoxManager;
import jp.example.mclivetrap.commandloader.CommandLoader;
import jp.example.mclivetrap.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class WebhookController implements HttpHandler {

    private final MCLiveTrapPlugin plugin;
    private final TrapBoxManager trapBoxManager;
    private final CommandLoader commandLoader;
    private final ConfigManager configManager;
    private final Random random = new Random();

    public WebhookController(MCLiveTrapPlugin plugin,
                             TrapBoxManager trapBoxManager,
                             CommandLoader commandLoader,
                             ConfigManager configManager) {
        this.plugin = plugin;
        this.trapBoxManager = trapBoxManager;
        this.commandLoader = commandLoader;
        this.configManager = configManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        plugin.getLogger().info("Received webhook: " + body);

        // ゲーム未開始時は処理せず OK を返す
        if (!plugin.isGameActive()) {
            plugin.getLogger().info("Webhook received but game is not active yet");
            sendResponse(exchange, 200, "Game not active");
            return;
        }

        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String type = json.has("type") ? json.get("type").getAsString() : null;
            JsonObject data = json.has("data") ? json.getAsJsonObject("data") : null;

            if (type == null || data == null) {
                plugin.getLogger().warning("Webhook missing type or data");
                sendResponse(exchange, 400, "Missing type or data");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> handleEvent(type, data));

            sendResponse(exchange, 200, "OK");

        } catch (Exception e) {
            plugin.getLogger().warning("Invalid webhook payload: " + e.getMessage());
            plugin.getLogger().fine("StackTrace: " + e);
            sendResponse(exchange, 400, "Invalid payload");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleEvent(String type, JsonObject data) {
        String nickname = data.has("nickname") && !data.get("nickname").isJsonNull()
                ? data.get("nickname").getAsString()
                : data.get("user").getAsString();

        // 標準メッセージ表示
        switch (type.toLowerCase()) {
            case "like" -> Bukkit.broadcastMessage("§a[LIKE] §f" + nickname);
            case "follow" -> Bukkit.broadcastMessage("§b[FOLLOW] §f" + nickname);
            case "share" -> Bukkit.broadcastMessage("§d[SHARE] §f" + nickname);
            case "gift" -> {
                String giftName = data.has("gift_name") ? data.get("gift_name").getAsString() : "Unknown";
                int count = data.has("count") ? data.get("count").getAsInt() : 1;
                Bukkit.broadcastMessage("§c[GIFT] §f" + nickname + " sent " + giftName + " x" + count);
            }
            case "subscribe" -> Bukkit.broadcastMessage("§6[SUBSCRIBE] §f" + nickname);
            default -> plugin.getLogger().warning("Unknown event type: " + type);
        }

        // TrapBox がない場合はスキップ
        List<TrapBox> boxes = trapBoxManager.getTrapBoxes();
        if (boxes.isEmpty()) {
            plugin.getLogger().warning("No TrapBox exists, skipping commands");
            return;
        }

        // ランダム選択
        TrapBox box = boxes.get(random.nextInt(boxes.size()));

        // config.yml から該当イベントの action を取得
        List<ConfigManager.Action> actions = "gift".equalsIgnoreCase(type) && data.has("gift_name")
                ? configManager.getGiftActions(data.get("gift_name").getAsString())
                : configManager.getEventActions(type);

        if (actions == null || actions.isEmpty()) return;

        for (ConfigManager.Action action : actions) {
            if (!action.enabled()) continue;

            List<String> commands = commandLoader.getCommands(action.command());
            if (commands.isEmpty()) continue;

            int amount = Math.max(1, action.amount());

            for (int i = 0; i < amount; i++) {
                for (String cmdTemplate : commands) {
                    String cmd = replacePlaceholders(cmdTemplate, nickname, box);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            }
        }
    }

    private String replacePlaceholders(String template, String playerName, TrapBox box) {
        Location loc = box.getRandomInnerLocation();
        int offsetX = random.nextInt(5) - 2; // -2 〜 2
        int offsetZ = random.nextInt(5) - 2;

        return template
                .replace("{player}", playerName)
                .replace("{randX}", String.valueOf(loc.getBlockX() + offsetX))
                .replace("{randZ}", String.valueOf(loc.getBlockZ() + offsetZ));
    }
}
