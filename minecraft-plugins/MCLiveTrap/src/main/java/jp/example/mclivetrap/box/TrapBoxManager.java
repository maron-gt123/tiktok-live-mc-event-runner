package jp.example.mclivetrap.box;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class TrapBoxManager {

    private Location center;
    private int size;          // 一辺の長さ（9, 11, ...）
    private int half;          // size / 2
    private Material material;
    private World world;

    private final Set<Location> placedBlocks = new HashSet<>();
    private final Random random = new Random();

    /* =========================
       状態チェック
       ========================= */

    public boolean hasTrapBox() {
        return center != null;
    }

    /* =========================
       TrapBox 生成
       ========================= */

    public void createTrapBox(Location center, int size, Material material) {
        if (hasTrapBox()) {
            throw new IllegalStateException("TrapBox already exists");
        }

        this.center = center.getBlock().getLocation();
        this.size = size;
        this.half = size / 2;
        this.material = material;
        this.world = center.getWorld();

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        int minX = cx - half;
        int maxX = cx + half;
        int minY = cy - half;
        int maxY = cy + half;
        int minZ = cz - half;
        int maxZ = cz + half;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {

                    boolean isWall =
                            x == minX || x == maxX ||
                            z == minZ || z == maxZ;

                    boolean isFloor = (y == minY);
                    boolean isCeiling = (y == maxY);

                    // 天井は生成しない
                    if (isCeiling) continue;

                    if (isWall || isFloor) {
                        Location loc = new Location(world, x, y, z);
                        world.getBlockAt(loc).setType(material);
                        placedBlocks.add(loc);
                    }
                }
            }
        }
    }

    /* =========================
       TrapBox 削除
       ========================= */

    public void removeTrapBox() {
        if (!hasTrapBox()) return;

        for (Location loc : placedBlocks) {
            world.getBlockAt(loc).setType(Material.AIR);
        }

        placedBlocks.clear();
        center = null;
        world = null;
    }

    /* =========================
       ブロック保護判定
       ========================= */

    public boolean isProtected(Location location) {
        if (!hasTrapBox()) return false;

        Location blockLoc = location.getBlock().getLocation();
        return placedBlocks.contains(blockLoc);
    }

    /* =========================
       TNT 用：内部ランダム座標
       ========================= */

    public Optional<Location> getRandomInnerLocation() {
        if (!hasTrapBox()) return Optional.empty();

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

        return Optional.of(new Location(world, x, y, z));
    }

    private int randomBetween(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
