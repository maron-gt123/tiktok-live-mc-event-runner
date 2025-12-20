package jp.example.mclivetrap.listener;

import jp.example.mclivetrap.box.TrapBox;
import jp.example.mclivetrap.box.TrapBoxManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TrapBoxPlaceListener implements Listener {

    private final TrapBoxManager manager;

    public TrapBoxPlaceListener(TrapBoxManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!manager.hasTrapBox()) return;

        TrapBox box = manager.getTrapBox();
        if (!box.isInside(event.getBlockPlaced().getLocation())) return;

        if (!manager.isAutoConvertEnabled()) return;

        Material converted = manager.getAutoBlockType(event.getBlockPlaced().getY());
        event.getBlockPlaced().setType(converted);
    }
}
