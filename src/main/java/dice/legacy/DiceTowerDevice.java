package dice.legacy;

import java.util.Random;

/** ADAPTEE. Pretend this is a third-party driver for a physical dice tower.*/
public class DiceTowerDevice {
    public static final long ERR_JAMMED = -1;
    public static final long ERR_BAD_REQUEST = -2;
    public static final long MAX_SIDES = 1000;

    private final Random random;
    private boolean jammed;
    public DiceTowerDevice() {
        this(new Random());
    }
    public DiceTowerDevice(Random random) {
        this.random = random;
    }

    /** Simulates the physical tower getting stuck (used for demos). */
    public void jam() {
        jammed = true;
    }
    public void unjam() {
        jammed = false;
    }

    public long rollRaw(long sides, String mode) {
        if (jammed) {
            return ERR_JAMMED;
        }
        if (sides < 1 || sides > MAX_SIDES || !"STD".equals(mode)) {
            return ERR_BAD_REQUEST;
        }
        return random.nextLong(sides);
    }
}
