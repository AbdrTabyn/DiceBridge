package dice.app;

import dice.abstraction.Roll;
import dice.implementor.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Required complexity module: DYNAMIC IMPLEMENTOR SELECTION.
 *
 * Input like "2d6+3 @tower" decides BOTH axes at runtime:
 *   "2d6+3" -> which Refined Abstraction, "@tower" -> which Implementor (default: pseudo).
 * New abstraction kinds and new sources are added with register... calls, no existing class changes.
 */
public class RollCommandFactory {

    public static final String DEFAULT_SOURCE = "pseudo";

    private record Kind(Pattern pattern, BiFunction<Matcher, RandomSource, Roll> builder) {
    }

    private final Map<String, Supplier<RandomSource>> sources = new HashMap<>();
    private final List<Kind> kinds = new ArrayList<>();

    public void registerSource(String name, Supplier<RandomSource> supplier) {
        sources.put(name.toLowerCase(), supplier);
    }

    public void registerKind(String regex, BiFunction<Matcher, RandomSource, Roll> builder) {
        kinds.add(new Kind(Pattern.compile(regex), builder));
    }

    public Roll create(String command) {
        String[] parts = command.trim().split("\\s*@\\s*", 2);
        String spec = parts[0].trim();
        String sourceName = parts.length > 1 ? parts[1].trim().toLowerCase() : DEFAULT_SOURCE;

        for (Kind kind : kinds) {
            Matcher matcher = kind.pattern().matcher(spec);
            if (matcher.matches()) {
                return kind.builder().apply(matcher, createSource(sourceName));
            }
        }
        throw new IllegalArgumentException("Unknown roll: '" + spec + "'");
    }

    private RandomSource createSource(String name) {
        Supplier<RandomSource> supplier = sources.get(name);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown source: '" + name + "'");
        }
        return supplier.get();
    }
}
