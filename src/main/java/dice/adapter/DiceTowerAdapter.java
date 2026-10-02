package dice.adapter;
import dice.implementor.RandomSource;
import dice.implementor.RandomSourceException;
import dice.legacy.DiceTowerDevice;
/**
 * ADAPTER: makes DiceTowerDevice usable as a RandomSource (Concrete Implementor #3).
 * Translates:
 *  - call:    nextInt(min, max)
 *  - result:  0-based index
 *  - errors:  sentinel codes -1 / -2
 *  */
public class DiceTowerAdapter implements RandomSource {
    private static final String MODE = "STD";
    private final DiceTowerDevice tower;
    public DiceTowerAdapter(DiceTowerDevice tower) {
        this.tower = tower;
    }

    @Override
    public int nextInt(int min, int max) {
        RandomSource.requireValidRange(min, max);
        long sides = (long) max - min + 1;
        long raw = tower.rollRaw(sides, MODE);
        if (raw == DiceTowerDevice.ERR_JAMMED) {
            throw new RandomSourceException("Random source is temporarily unavailable");
        }
        if (raw == DiceTowerDevice.ERR_BAD_REQUEST) {
            throw new RandomSourceException("Random source cannot serve the range [" + min + ", " + max + "]");
        }
        if (raw < 0 || raw >= sides) {
            throw new RandomSourceException("Random source returned an invalid value");
        }
        return (int) (min + raw);
    }
}
