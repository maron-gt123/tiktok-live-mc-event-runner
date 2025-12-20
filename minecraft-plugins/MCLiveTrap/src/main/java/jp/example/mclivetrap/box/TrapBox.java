package jp.example.mclivetrap.box;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class TrapBox {

    private final Location center;
    private final int size;
    private final int half;
    private final World world;

    private boolean active = false;
    private final Random random = new Random();

    public TrapBox(Location center, int size) {
        this.center = center.getBlock().getLocation();
        this.size = size;
        this.half = size / 2;
        this.world = center.getWorld();
    }

    /* =========================
       状態
       ========================= */

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    /* =========================
       基本情報
       ========================= */

    public Location getCenter() {
        return center.clone();
    }

    public int getSize() {
        return size;
    }

    public int getHalf() {
        return half;
    }

    public World getWorld() {
        return world;
    }

    /* =========================
       範囲判定
       ========================= */

    public boolean isInside(Location loc) {
        if (loc == null || loc.getWorld() != world) return false;

        Location b = loc.getBlock().getLocation();

        int x = b.getBlockX();
        int y = b.getBlockY();
        int z = b.getBlockZ();

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        return x >= cx - half && x <= cx + half
            && y >= cy - half && y <= cy + half
            && z >= cz - half && z <= cz + half;
    }

    /* =========================
       内部ランダム座標
       ========================= */

    public Location getRandomInnerLocation() {
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        int minX = cx - half + 1;
        int maxX = cx + half - 1;
        int minY = cy - half + 1;
        int maxY = cy + half - 1;
        int minZ = cz - half + 1;
        int maxZ = cz + half - 1;

        int x = randomBetween(minX, maxX);
        int y = randomBetween(minY, maxY);
        int z = randomBetween(minZ, maxZ);

        return new Location(world, x, y, z);
    }

    private int randomBetween(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
