package jp.example.mclivetrap.box;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;
import java.util.List;

public class TrapBoxManager {

    private final List<TrapBox> trapBoxes = new ArrayList<>();

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
