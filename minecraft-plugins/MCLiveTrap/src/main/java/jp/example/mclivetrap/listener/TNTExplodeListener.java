package jp.example.mclivetrap.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.entity.TNTPrimed;

public class TNTExplodeListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            // ブロック破壊を完全に防ぐ
            event.blockList().clear();
        }
    }
}
