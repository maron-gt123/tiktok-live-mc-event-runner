package jp.example.tiktok;

import org.bukkit.plugin.java.JavaPlugin;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TikTokEventHandler {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private EventServer server;

    public TikTokEventHandler(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void startHttpServer(int port) {
        try {
            server = new EventServer(port);
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            plugin.getLogger().info("[TikTokMCEvent] HTTP server started on port " + port);
        } catch (Exception e) {
            plugin.getLogger().severe("[TikTokMCEvent] Failed to start HTTP server: " + e.getMessage());
        }
    }

    public void stopHttpServer() {
        if (server != null) {
            server.stop();
            plugin.getLogger().info("[TikTokMCEvent] HTTP server stopped");
        }
    }

    private class EventServer extends NanoHTTPD {
        public EventServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            if (session.getMethod() == Method.POST && "/event".equals(session.getUri())) {
                try {
                    Map<String, String> files = new HashMap<>();
                    session.parseBody(files);
                    String body = files.get("postData");
                    plugin.getLogger().info("[TikTokEvent] " + body);

                    // Minecraftメインスレッドでイベント処理
                    plugin.getServer().getScheduler().runTask(plugin, () -> handleEvent(body));

                    return newFixedLengthResponse(Response.Status.OK, "text/plain", "OK");
                } catch (Exception e) {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
                }
            }
            return newFixedLengthResponse("Hello");
        }
    }

    private void handleEvent(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String type = obj.getString("type");

            if ("like".equals(type)) {
                String user = obj.getJSONObject("data").getString("user");
                handleLike(user);
            } else if ("gift".equals(type)) {
                JSONObject data = obj.getJSONObject("data");
                String user = data.getString("user");
                String giftName = data.getString("gift_name");
                int diamond = data.optInt("diamond", 0);
                int count = data.optInt("count", 1);
                handleGift(user, giftName, diamond, count);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[TikTokMCEvent] Failed to handle event: " + e.getMessage());
        }
    }

    private void handleLike(String user) {
        plugin.getLogger().info("[TikTokMCEvent] LIKE: user=" + user);
        // TODO: LikeRate に基づく妨害処理をここで実行
    }

    private void handleGift(String user, String giftName, int diamond, int count) {
        plugin.getLogger().info("[TikTokMCEvent] GIFT: user=" + user + ", gift=" + giftName + ", diamond=" + diamond + ", count=" + count);
        // TODO: GiftConfig に基づく妨害処理をここで実行
    }
}
