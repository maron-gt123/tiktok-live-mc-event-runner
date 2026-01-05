package jp.example.mclivetrap.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jp.example.mclivetrap.MCLiveTrapPlugin;
import jp.example.mclivetrap.box.TrapBox;
import jp.example.mclivetrap.box.TrapBoxManager;
import jp.example.mclivetrap.commandloader.CommandLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class WebhookController implements HttpHandler {

    private final MCLiveTrapPlugin plugin;
    private final TrapBoxManager trapBoxManager;
    private final CommandLoader commandLoader;
    private final Random random = new Random();

    public WebhookController(MCLiveTrapPlugin plugin,
                             TrapBoxManager trapBoxManager,
                             CommandLoader commandLoader) {
        this.plugin = plugin;
        this.trapBoxManager = trapBoxManager;
        this.commandLoader = commandLoader;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        plugin.getLogger().info("Received webhook: " + body);

        if (!plugin.isGameActive()) {
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
        String nickname = data.has("nickname") && !data.get("nickname").isJsonNull()
                ? data.get("nickname").getAsString()
                : data.get("user").getAsString();

        // 標準メッセージ
        switch (type) {
            case "like" -> Bukkit.broadcastMessage("§a[LIKE] §f" + nickname);
            case "follow" -> Bukkit.broadcastMessage("§b[FOLLOW] §f" + nickname);
            case "share" -> Bukkit.broadcastMessage("§d[SHARE] §f" + nickname);
            case "gift" -> {
                String giftName = data.get("gift_name").getAsString();
                int count = data.get("count").getAsInt();
                Bukkit.broadcastMessage("§c[GIFT] §f" + nickname + " sent " + giftName + " x" + count);
            }
            case "subscribe" -> Bukkit.broadcastMessage("§6[SUBSCRIBE] §f" + nickname);
            default -> plugin.getLogger().warning("Unknown event type: " + type);
        }

        // コマンド実行
        String commandId;
        if ("gift".equalsIgnoreCase(type)) {
            commandId = data.get("gift_name").getAsString();
        } else {
            commandId = type;
        }

        List<String> commands = commandLoader.getCommands(commandId);
        if (commands.isEmpty()) return;

        if (!trapBoxManager.hasTrapBox()) {
            plugin.getLogger().warning("No TrapBox exists, skipping commands");
            return;
        }

        // 複数 TrapBox があればランダム選択
        TrapBox box = trapBoxManager.getTrapBoxes().get(random.nextInt(trapBoxManager.getTrapBoxes().size()));

        int amount = 1; // デフォルト 1 回
        if (data.has("count")) {
            amount = data.get("count").getAsInt();
        }

        for (int i = 0; i < amount; i++) {
            for (String cmdTemplate : commands) {
                String cmd = replacePlaceholders(cmdTemplate, nickname, box);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }
    }

    private String replacePlaceholders(String template, String playerName, TrapBox box) {
        Location loc = box.getRandomInnerLocation();
        int randX = loc.getBlockX();
        int randZ = loc.getBlockZ();

        // {randX} と {randZ} を TrapBox 内ランダムに置換
        int offsetX = random.nextInt(5) - 2; // -2 〜 2
        int offsetZ = random.nextInt(5) - 2;

        return template
                .replace("{player}", playerName)
                .replace("{randX}", String.valueOf(randX + offsetX))
                .replace("{randZ}", String.valueOf(randZ + offsetZ));
    }
}
