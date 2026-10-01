package dice.implementor;

import java.security.SecureRandom;

/** Concrete Implementor #2: cryptographically strong randomness (unpredictable, "no cheating" mode). */
public class SecureRandomSource implements RandomSource {

    private final SecureRandom random = new SecureRandom();

    @Override
    public int nextInt(int min, int max) {
        RandomSource.requireValidRange(min, max);
        return (int) random.nextLong(min, (long) max + 1);
    }
}
