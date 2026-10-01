package dice.app;
import dice.abstraction.AdvantageRoll;
import dice.abstraction.DamageRoll;
import dice.abstraction.Roll;
import dice.adapter.DiceTowerAdapter;
import dice.implementor.PseudoRandomSource;
import dice.implementor.RandomSourceException;
import dice.implementor.SecureRandomSource;
import dice.legacy.DiceTowerDevice;

import java.util.Random;

public class DiceApp {

    public static void main(String[] args) {
        DiceTowerDevice tower = new DiceTowerDevice();
        RollCommandFactory factory = buildFactory(tower);
        Random random = new Random();

        System.out.println("DiceBridge");
        System.out.println();
        // Demo 1: DamageRoll + PseudoRandomSource
        runDamageRoll(factory, random, "pseudo");
        // Demo 2: DamageRoll + SecureRandomSource
        runDamageRoll(factory, random, "secure");
        // Demo 3: DamageRoll + DiceTowerAdapter
        runDamageRoll(factory, random, "tower");
        // Demo 4: AdvantageRoll
        runAdvantageRoll(factory, "pseudo");
        // Demo 5: Adapter failure translation
        runTowerFailureDemo(factory, tower);

    }

    private static void runDamageRoll(
            RollCommandFactory factory,
            Random random,
            String source) {
        int count = random.nextInt(1, 6);
        int sides = random.nextInt(4, 21);
        String command = count + "d" + sides + " @" + source;
        try {
            DamageRoll roll = (DamageRoll) factory.create(command);
            int[] results = roll.rollDice();
            int total = 0;
            for (int result : results) {
                total += result;
            }

            System.out.println("Rolling " + count + "d" + sides);
            System.out.println("Source: " + formatSource(source));
            System.out.println("Dice: " + formatDice(results));
            System.out.println("Total: " + total);
            System.out.println();

        } catch (RandomSourceException e) {
            System.out.println("Rolling " + count + "d" + sides);
            System.out.println("Source: " + formatSource(source));
            System.out.println("Roll failed: " + e.getMessage());
            System.out.println();
        }
    }

    private static void runAdvantageRoll(
            RollCommandFactory factory,
            String source) {
        try {
            AdvantageRoll roll =
                    (AdvantageRoll) factory.create("adv @" + source);
            int result = roll.roll();
            System.out.println("Rolling d20 with advantage");
            System.out.println("Source: " + formatSource(source));
            System.out.println("Result: " + result);
            System.out.println();
        } catch (RandomSourceException e) {
            System.out.println("Advantage roll failed: " + e.getMessage());
            System.out.println();
        }
    }

    private static void runTowerFailureDemo(
            RollCommandFactory factory,
            DiceTowerDevice tower) {
        System.out.println("Checking Dice Tower connection...");
        tower.jam();
        try {
            Roll roll = factory.create("1d6 @tower");
            roll.roll();
            System.out.println("Dice Tower is available.");
        } catch (RandomSourceException e) {
            System.out.println("Dice Tower unavailable.");
            System.out.println("Error translated successfully.");
        } finally {
            tower.unjam();
        }
        System.out.println();
    }

    private static String formatSource(String source) {
        return switch (source) {
            case "pseudo" -> "Pseudo Random Source";
            case "secure" -> "Secure Random Source";
            case "tower" -> "Dice Tower";
            default -> source;
        };
    }

    private static String formatDice(int[] results) {
        StringBuilder output = new StringBuilder("[");
        for (int i = 0; i < results.length; i++) {
            if (i > 0) {
                output.append(", ");
            }
            output.append(results[i]);
        }
        output.append("]");
        return output.toString();
    }

    static RollCommandFactory buildFactory(DiceTowerDevice tower) {
        RollCommandFactory factory = new RollCommandFactory();
        factory.registerSource(
                "pseudo",
                PseudoRandomSource::new
        );
        factory.registerSource(
                "secure",
                SecureRandomSource::new
        );
        factory.registerSource(
                "tower",
                () -> new DiceTowerAdapter(tower)
        );
        factory.registerKind(
                "(\\d+)d(\\d+)(?:\\+(\\d+))?",
                (m, source) -> new DamageRoll(
                        source,
                        Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)),
                        m.group(3) == null
                                ? 0
                                : Integer.parseInt(m.group(3))
                )
        );
        factory.registerKind(
                "adv",
                (m, source) -> new AdvantageRoll(source)
        );
        return factory;
    }
}