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
     * TrapBox 内のランダム位置に TNT を生成する
     */
    public void spawnRandomTNT(int fuseTicks) {
        if (!trapBoxManager.hasTrapBox()) return;

        TrapBox box = trapBoxManager.getTrapBox();
        if (!box.isActive()) return;

        Location loc = box.getRandomInnerLocation().add(0.5, 0, 0.5);
        World world = loc.getWorld();

        TNTPrimed tnt = world.spawn(loc, TNTPrimed.class);
        tnt.setFuseTicks(fuseTicks);
        tnt.setYield(0.0F);
        tnt.setIsIncendiary(false);
    }
}
