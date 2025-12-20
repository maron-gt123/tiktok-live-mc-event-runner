package jp.example.mclivetrap.box;

import org.bukkit.Location;

public class TrapBox {

    private final Location center;
    private boolean active = false;

    public TrapBox(Location center) {
        this.center = center;
    }

    public Location getCenter() {
        return center;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
