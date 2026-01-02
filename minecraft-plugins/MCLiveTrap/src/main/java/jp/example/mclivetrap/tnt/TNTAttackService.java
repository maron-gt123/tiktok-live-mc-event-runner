package jp.example.mclivetrap.tnt;

import jp.example.mclivetrap.box.TrapBox;
import jp.example.mclivetrap.box.TrapBoxManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TNTPrimed;

public class TNTAttackService {

    private final TrapBoxManager trapBoxManager;

    public TNTAttackService(TrapBoxManager trapBoxManager) {
        this.trapBoxManager = trapBoxManager;
    }

    /**
     * TrapBox 内のランダム位置に TNT を生成する（Fuse指定）
     */
    public void spawnRandomTNT(int fuseTicks) {
        if (!trapBoxManager.hasTrapBox()) return;

        TrapBox box = trapBoxManager.getTrapBox();
        if (!box.isActive()) return;

        Location loc = box.getRandomInnerLocation().add(0.5, 0, 0.5);
        World world = loc.getWorld();
        if (world == null) return;

        TNTPrimed tnt = world.spawn(loc, TNTPrimed.class);
        tnt.setFuseTicks(fuseTicks);
        tnt.setYield(0.0F);
        tnt.setIsIncendiary(false);
    }

    /**
     * TrapBox 内のランダム位置に複数の TNT を生成する（デフォルトFuse 60ticks）
     */
    public void spawnTNT(int amount) {
        for (int i = 0; i < amount; i++) {
            for (TrapBox box : trapBoxManager.getTrapBoxes()) {
                Location loc = box.getLocation();
                World world = loc.getWorld();
                if (world != null) {
                    TNTPrimed tnt = world.spawn(loc, TNTPrimed.class);
                    tnt.setFuseTicks(60); // デフォルト3秒
                    tnt.setYield(0.0F);
                    tnt.setIsIncendiary(false);
                }
            }
        }
    }
}
