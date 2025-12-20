package jp.example.mclivetrap.command;

import jp.example.mclivetrap.MCLiveTrapPlugin;
import jp.example.mclivetrap.box.TrapBoxManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrapCommand implements CommandExecutor {

    private final TrapBoxManager manager;
    private final MCLiveTrapPlugin plugin;

    public TrapCommand(TrapBoxManager manager, MCLiveTrapPlugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行できます");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "create" -> {
                handleCreate(player, args);
                return true;
            }

            case "remove" -> {
                handleRemove(sender);
                return true;
            }

            case "start" -> {
                int delay = 0;
                if (args.length >= 2) {
                    try {
                        delay = Integer.parseInt(args[1]);
                        if (delay < 0) delay = 0;
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c秒数は数値で指定してください");
                        return true;
                    }
                }
                startGame(player, delay);
                return true;
            }

            case "stop" -> {
                stopGame(player);
                return true;
            }

            default -> {
                sendHelp(sender);
                return true;
            }
        }
    }

    /* =======================
       create コマンド
       ======================= */
    private void handleCreate(Player player, String[] args) {
        if (plugin.isGameActive()) {
            player.sendMessage("§cゲーム中はTrapBoxを生成できません");
            return;
        }

        if (manager.hasTrapBox()) {
            player.sendMessage("§cTrapBoxはすでに存在します。先に /mclivetrap remove を実行してください。");
            return;
        }

        int size = 9;
        if (args.length >= 2) {
            try { size = Integer.parseInt(args[1]); }
            catch (NumberFormatException e) {
                player.sendMessage("§cサイズは数値で指定してください");
                return;
            }
        }

        if (size < 5) {
            player.sendMessage("§cサイズは5以上を指定してください");
            return;
        }

        if (size % 2 == 0) {
            player.sendMessage("§cサイズは奇数で指定してください（例: 7, 9, 11）");
            return;
        }

        Location center = player.getLocation();
        Material material = Material.valueOf(plugin.getConfig().getString("trap.block", "GLASS"));

        manager.createTrapBox(center, size, material);

        player.sendMessage("§aTrapBoxを生成しました");
        player.sendMessage("§7サイズ: " + size + " / 素材: " + material.name());
    }

    /* =======================
       remove コマンド
       ======================= */
    private void handleRemove(CommandSender sender) {
        if (plugin.isGameActive()) {
            sender.sendMessage("§cゲーム中はTrapBoxを削除できません");
            return;
        }

        if (!manager.hasTrapBox()) {
            sender.sendMessage("§eTrapBoxは存在しません");
            return;
        }

        manager.removeTrapBox();
        sender.sendMessage("§aTrapBoxを削除しました");
    }

    /* =======================
       start コマンド
       ======================= */
    private void startGame(Player player, int delaySeconds) {
        if (!manager.hasTrapBox()) {
            player.sendMessage("§cTrapBoxが存在しません。先に /mclivetrap create を実行してください");
            return;
        }

        if (plugin.isGameActive()) {
            player.sendMessage("§eゲームはすでに開始されています");
            return;
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            int secondsLeft = delaySeconds;

            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    plugin.setGameActive(true);
                    player.sendTitle("§aゲーム開始！", "", 10, 70, 20);
                    player.sendActionBar("§aゲームが開始されました！");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    plugin.getServer().getScheduler().cancelTasks(plugin); // タスク停止
                    return;
                }
                player.sendTitle("§e開始まで", "§c" + secondsLeft + "秒", 0, 20, 0);
                player.sendActionBar("§c" + secondsLeft + "秒");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
                secondsLeft--;
            }
        }, 0L, 20L);
    }

    /* =======================
       stop コマンド
       ======================= */
    private void stopGame(Player player) {
        if (!plugin.isGameActive()) {
            player.sendMessage("§eゲームはすでに停止しています");
            return;
        }
        plugin.setGameActive(false);
        player.sendTitle("§cゲーム停止", "", 10, 70, 20);
        player.sendActionBar("§cゲームは停止されました");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
    }

    /* =======================
       ヘルプ表示
       ======================= */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6==== MCLiveTrap ====");
        sender.sendMessage("§e/mclivetrap create [size]");
        sender.sendMessage("§7  TrapBoxを生成（size省略時: 9）");
        sender.sendMessage("§e/mclivetrap remove");
        sender.sendMessage("§7  TrapBoxを削除");
        sender.sendMessage("§e/mclivetrap start [秒数]");
        sender.sendMessage("§7  ゲーム開始（秒数省略可）");
        sender.sendMessage("§e/mclivetrap stop");
        sender.sendMessage("§7  ゲーム停止");
    }
}
