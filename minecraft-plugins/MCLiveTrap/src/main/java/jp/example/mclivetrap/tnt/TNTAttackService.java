package jp.example.mclivetrap.tnt;

import jp.example.mclivetrap.box.TrapBoxManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TNTPrimed;

import java.util.Optional;

public class TNTAttackService {

    private final TrapBoxManager trapBoxManager;

    public TNTAttackService(TrapBoxManager trapBoxManager) {
        this.trapBoxManager = trapBoxManager;
    }

    /**
     * TrapBox 内のランダム位置に TNT を生成する
     *
     * @param fuseTicks 爆発までの tick
     */
    public void spawnRandomTNT(int fuseTicks) {
        Optional<Location> opt = trapBoxManager.getRandomInnerLocation();
        if (opt.isEmpty()) return;

        Location loc = opt.get().clone().add(0.5, 0, 0.5);
        World world = loc.getWorld();

        TNTPrimed tnt = world.spawn(loc, TNTPrimed.class);
        tnt.setFuseTicks(fuseTicks);
        tnt.setYield(0.0F); // ブロック破壊なし
        tnt.setIsIncendiary(false);
    }
}
