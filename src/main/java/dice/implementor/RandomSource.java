package dice.implementor;
/**
 * Implementor: where random numbers come from.
 *  - returns a value in [min, max], both inclusive;
 *  - throws IllegalArgumentException if min > max (caller bug);
 *  - throws RandomSourceException if the source cannot deliver a value (runtime failure).
 */
public interface RandomSource {
    int nextInt(int min, int max);
    static void requireValidRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min (" + min + ") must not exceed max (" + max + ")");
        }
    }
}
