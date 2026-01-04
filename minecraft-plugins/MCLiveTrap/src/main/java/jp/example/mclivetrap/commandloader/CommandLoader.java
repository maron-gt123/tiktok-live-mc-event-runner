package jp.example.mclivetrap.command;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class CommandLoader {

    private final Map<String, List<String>> commands = new HashMap<>();
    private final JavaPlugin plugin;

    public CommandLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        loadAllCommands();
    }

    /**
     * plugins/MCLiveTrap/events/*.yml をロードして commands に保持
     */
    public void loadAllCommands() {
        File eventsDir = new File(plugin.getDataFolder(), "events");
        if (!eventsDir.exists()) return;

        File[] files = eventsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        Yaml yaml = new Yaml();
        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Map<?, ?> data = yaml.load(fis);
                if (data == null) continue;

                List<String> cmdList = new ArrayList<>();
                Object commandsObj = data.get("commands");
                if (commandsObj instanceof List<?> list) {
                    for (Object o : list) {
                        cmdList.add(String.valueOf(o));
                    }
                }

                String key = file.getName().replace(".yml", "").toLowerCase();
                commands.put(key, cmdList);
                plugin.getLogger().info("Loaded event commands: " + key + " (" + cmdList.size() + ")");
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to load event command file: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * コマンド取得
     *
     * @param type command type (ymlファイル名)
     * @return コマンドリスト or 空リスト
     */
    public List<String> getCommands(String type) {
        return commands.getOrDefault(type.toLowerCase(), Collections.emptyList());
    }
}
