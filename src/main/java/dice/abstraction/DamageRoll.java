package dice.abstraction;
import dice.implementor.RandomSource;

public class DamageRoll extends Roll {
    private final int count;
    private final int sides;
    private final int bonus;

    public DamageRoll(RandomSource source, int count, int sides, int bonus) {
        super(source);
        if (count < 1 || sides < 2) {
            throw new IllegalArgumentException(
                    "Need at least 1 die with at least 2 sides"
            );
        }
        this.count = count;
        this.sides = sides;
        this.bonus = bonus;
    }

    public int[] rollDice() {
        int[] results = new int[count];
        for (int i = 0; i < count; i++) {
            results[i] = source.nextInt(1, sides);
        }
        return results;
    }
    @Override
    public int roll() {
        int total = bonus;
        int[] results = rollDice();
        for (int result : results) {
            total += result;
        }
        return total;
    }
    @Override
    public String describe() {
        return count + "d" + sides
                + (bonus != 0 ? "+" + bonus : "");
    }
}