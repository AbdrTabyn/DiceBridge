package dice.implementor;

import java.util.Random;

/** Concrete Implementor #1: fast pseudo-random numbers (seedable, good for reproducible games). */
public class PseudoRandomSource implements RandomSource {

    private final Random random;

    public PseudoRandomSource() {
        this(new Random());
    }

    public PseudoRandomSource(Random random) {
        this.random = random;
    }

    @Override
    public int nextInt(int min, int max) {
        RandomSource.requireValidRange(min, max);
        return (int) random.nextLong(min, (long) max + 1);
    }
}
