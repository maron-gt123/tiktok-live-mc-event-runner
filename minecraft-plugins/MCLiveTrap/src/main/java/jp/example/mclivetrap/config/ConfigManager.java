package jp.example.mclivetrap.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Map<String, Object> config;

    public ConfigManager(InputStream yamlStream) {
        Yaml yaml = new Yaml();
        this.config = yaml.load(yamlStream);
    }

    @SuppressWarnings("unchecked")
    public List<Action> getGiftActions(String giftName) {
        try {
            Map<String, Object> events = (Map<String, Object>) config.get("events");
            Map<String, Object> giftEvents = (Map<String, Object>) events.get("gift");
            Map<String, Object> gift = (Map<String, Object>) giftEvents.get(giftName);
            List<Map<String, Object>> actionsList = (List<Map<String, Object>>) gift.get("actions");

            List<Action> result = new ArrayList<>();
            for (Map<String, Object> a : actionsList) {
                result.add(new Action(
                        (String) a.get("command"),
                        a.get("amount") != null ? (Integer) a.get("amount") : 1,
                        a.get("enabled") != null ? (Boolean) a.get("enabled") : true
                ));
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Action> getEventActions(String eventType) {
        try {
            Map<String, Object> events = (Map<String, Object>) config.get("events");
            Map<String, Object> event = (Map<String, Object>) events.get(eventType);
            List<Map<String, Object>> actionsList = (List<Map<String, Object>>) event.get("actions");

            List<Action> result = new ArrayList<>();
            for (Map<String, Object> a : actionsList) {
                result.add(new Action(
                        (String) a.get("command"),
                        a.get("amount") != null ? (Integer) a.get("amount") : 1,
                        a.get("enabled") != null ? (Boolean) a.get("enabled") : true
                ));
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static class Action {
        private final String command;
        private final int amount;
        private final boolean enabled;

        public Action(String command, int amount, boolean enabled) {
            this.command = command;
            this.amount = amount;
            this.enabled = enabled;
        }

        public String command() { return command; }
        public int amount() { return amount; }
        public boolean enabled() { return enabled; }
    }
}
