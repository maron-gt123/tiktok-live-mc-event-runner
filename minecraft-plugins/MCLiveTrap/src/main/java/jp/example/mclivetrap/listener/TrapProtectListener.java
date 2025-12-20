package jp.example.mclivetrap.listener;

import jp.example.mclivetrap.box.TrapBox;
import jp.example.mclivetrap.box.TrapBoxManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TrapProtectListener implements Listener {

    private final TrapBoxManager manager;

    public TrapProtectListener(TrapBoxManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!manager.hasTrapBox()) return;

        TrapBox box = manager.getTrapBox();
        if (box.isInside(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
