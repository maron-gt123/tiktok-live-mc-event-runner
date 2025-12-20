package jp.example.mclivetrap.box;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TrapBoxManager {

    private TrapBox trapBox;
    private Material material;
    private World world;

    private final Set<Location> placedBlocks = new HashSet<>();

    /* =========================
       状態
       ========================= */

    public boolean hasTrapBox() {
        return trapBox != null;
    }

    public TrapBox getTrapBox() {
        return trapBox;
    }

    /* =========================
       TrapBox 生成
       ========================= */

    public void createTrapBox(Location center, int size, Material material) {
        if (hasTrapBox()) {
            throw new IllegalStateException("TrapBox already exists");
        }

        this.trapBox = new TrapBox(center, size);
        this.material = material;
        this.world = center.getWorld();

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        int half = trapBox.getHalf();

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
        trapBox = null;
        world = null;
    }

    /* =========================
       保護判定
       ========================= */

    public boolean isProtected(Location location) {
        if (!hasTrapBox()) return false;
        return placedBlocks.contains(location.getBlock().getLocation());
    }

    /* =========================
       TNT 用
       ========================= */

    public Optional<Location> getRandomInnerLocation() {
        if (!hasTrapBox() || !trapBox.isActive()) {
            return Optional.empty();
        }
        return Optional.of(trapBox.getRandomInnerLocation());
    }
}
