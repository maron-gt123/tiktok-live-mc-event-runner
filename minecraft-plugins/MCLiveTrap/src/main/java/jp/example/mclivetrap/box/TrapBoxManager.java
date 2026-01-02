package jp.example.mclivetrap.box;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TrapBoxManager {

    private TrapBox trapBox;
    private Material material;
    private World world;

    // 自動変換フラグ
    private boolean autoConvertEnabled = false;

    // 枠のブロック（保護対象）
    private final Set<Location> placedBlocks = new HashSet<>();

    // Zombies 用
    private final List<TrapBox> trapBoxes = new ArrayList<>();

    /* =========================
       状態
       ========================= */

    public boolean hasTrapBox() {
        return trapBox != null;
    }

    public TrapBox getTrapBox() {
        return trapBox;
    }

    public boolean isAutoConvertEnabled() {
        return autoConvertEnabled;
    }

    public void setAutoConvertEnabled(boolean enabled) {
        this.autoConvertEnabled = enabled;
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

                    boolean isWall = x == minX || x == maxX || z == minZ || z == maxZ;
                    boolean isFloor = y == minY;
                    boolean isCeiling = y == maxY;

                    if (isCeiling) continue;

                    Location loc = new Location(world, x, y, z);

                    if (isWall || isFloor) {
                        world.getBlockAt(loc).setType(material);
                        placedBlocks.add(loc);
                    }
                }
            }
        }
    }

    /* =========================
       自動変換用
       ========================= */

    public Material getAutoBlockType(int y) {
        if (!hasTrapBox()) return Material.STONE;

        int minY = trapBox.getCenter().getBlockY() - trapBox.getHalf() + 1;
        int maxY = trapBox.getCenter().getBlockY() + trapBox.getHalf();

        int height = maxY - minY;
        int relativeY = y - minY;
        int layerHeight = height / 3;

        if (relativeY >= 2 * layerHeight) return Material.DIAMOND_BLOCK;
        else if (relativeY >= layerHeight) return Material.GOLD_BLOCK;
        else return Material.IRON_BLOCK;
    }

    /* =========================
       TrapBox fill
       ========================= */

    public void fillInsideWithAutoConvert() {
        if (!hasTrapBox()) return;

        Location center = trapBox.getCenter();
        World world = center.getWorld();
        int half = trapBox.getHalf();

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        int minX = cx - half + 1;
        int maxX = cx + half - 1;
        int minY = cy - half + 1;
        int maxY = cy + half - 1;
        int minZ = cz - half + 1;
        int maxZ = cz + half - 1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material mat = getAutoBlockType(y);
                    world.getBlockAt(x, y, z).setType(mat, false);
                }
            }
        }
    }

    /* =========================
       TrapBox clear
       ========================= */

    public void clearInside() {
        if (!hasTrapBox()) return;

        Location center = trapBox.getCenter();
        World world = center.getWorld();
        int half = trapBox.getHalf();

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        int minX = cx - half + 1;
        int maxX = cx + half - 1;
        int minY = cy - half + 1;
        int maxY = cy + half - 1;
        int minZ = cz - half + 1;
        int maxZ = cz + half - 1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR, false);
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

    /* =========================
       Zombies 用
       ========================= */

    public void addTrapBox(TrapBox box) {
        trapBoxes.add(box);
    }

    public List<TrapBox> getTrapBoxes() {
        return trapBoxes;
    }

    public void spawnZombies(int amount) {
        for (int i = 0; i < amount; i++) {
            for (TrapBox box : trapBoxes) {
                Location loc = box.getLocation();
                World world = loc.getWorld();
                if (world != null) {
                    world.spawn(loc, Zombie.class);
                }
            }
        }
    }
}
