package dice.abstraction;
import dice.implementor.RandomSource;

import java.util.Objects;
/**
 * ABSTRACTION: a kind of dice roll ("what we roll").
 * Depends ONLY on the RandomSource interface (the bridge).
 */
public abstract class Roll {
    protected final RandomSource source;
    protected Roll(RandomSource source) {
        this.source = Objects.requireNonNull(source, "source");
    }
    public abstract int roll();
    public abstract String describe();
}
