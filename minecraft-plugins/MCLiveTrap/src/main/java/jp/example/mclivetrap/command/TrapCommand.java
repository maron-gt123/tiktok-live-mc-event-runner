package jp.example.mclivetrap.command;

import jp.example.mclivetrap.box.TrapBoxManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrapCommand implements CommandExecutor {

    private final TrapBoxManager manager;

    public TrapCommand(TrapBoxManager manager) {
        this.manager = manager;
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

            default -> {
                sendHelp(sender);
                return true;
            }
        }
    }

    /* =======================
       create コマンド処理
       ======================= */
    private void handleCreate(Player player, String[] args) {

        // すでに存在する場合は拒否（重要）
        if (manager.hasTrapBox()) {
            player.sendMessage("§cTrapBoxはすでに存在します。先に /mclivetrap remove を実行してください。");
            return;
        }

        // デフォルトサイズ
        int size = 9;

        // サイズ指定がある場合
        if (args.length >= 2) {
            try {
                size = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cサイズは数値で指定してください");
                return;
            }
        }

        // 最低サイズ制限
        if (size < 5) {
            player.sendMessage("§cサイズは5以上を指定してください");
            return;
        }

        // 偶数防止（中心ズレ防止）
        if (size % 2 == 0) {
            player.sendMessage("§cサイズは奇数で指定してください（例: 7, 9, 11）");
            return;
        }

        Location center = player.getLocation();
        Material material = Material.GLASS; // デフォルト（後でconfig化可能）

        manager.createTrapBox(center, size, material);

        player.sendMessage("§aTrapBoxを生成しました");
        player.sendMessage("§7サイズ: " + size + " / 素材: " + material.name());
    }

    /* =======================
       remove コマンド処理
       ======================= */
    private void handleRemove(CommandSender sender) {

        if (!manager.hasTrapBox()) {
            sender.sendMessage("§eTrapBoxは存在しません");
            return;
        }

        manager.removeTrapBox();
        sender.sendMessage("§aTrapBoxを削除しました");
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
    }
}
