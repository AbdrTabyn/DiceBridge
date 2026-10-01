package dice.abstraction;
import dice.implementor.RandomSource;

/** Refined Abstraction #2: roll d20 twice and keep the higher result. */
public class AdvantageRoll extends Roll {
    public AdvantageRoll(RandomSource source) {
        super(source);
    }
    @Override
    public int roll() {
        int first = source.nextInt(1, 20);
        int second = source.nextInt(1, 20);
        return Math.max(first, second);
    }
    @Override
    public String describe() {
        return "d20 with advantage";
    }
}
