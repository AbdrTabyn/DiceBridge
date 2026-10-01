package dice.app;
import dice.abstraction.AdvantageRoll;
import dice.abstraction.DamageRoll;
import dice.abstraction.Roll;
import dice.implementor.RandomSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/** Dynamic implementor selection: the INPUT decides which implementation is used. */
class RollCommandFactoryTest {

    private RandomSource pseudo;
    private RandomSource tower;
    private Supplier<RandomSource> pseudoSupplier;
    private Supplier<RandomSource> towerSupplier;
    private RollCommandFactory factory;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        pseudo = mock(RandomSource.class);
        tower = mock(RandomSource.class);
        pseudoSupplier = mock(Supplier.class);
        towerSupplier = mock(Supplier.class);
        when(pseudoSupplier.get()).thenReturn(pseudo);
        when(towerSupplier.get()).thenReturn(tower);

        factory = new RollCommandFactory();
        factory.registerSource("pseudo", pseudoSupplier);
        factory.registerSource("tower", towerSupplier);
        factory.registerKind("(\\d+)d(\\d+)(?:\\+(\\d+))?",
                (m, src) -> new DamageRoll(src, Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)), m.group(3) == null ? 0 : Integer.parseInt(m.group(3))));
        factory.registerKind("adv", (m, src) -> new AdvantageRoll(src));
    }

    @Test
    void sourceNameInInputSelectsTheImplementor() {
        when(tower.nextInt(1, 6)).thenReturn(5);
        Roll roll = factory.create("1d6 @tower");
        assertEquals(5, roll.roll());
        verify(tower).nextInt(1, 6);
        verify(pseudoSupplier, never()).get();         // the other implementation was never touched
    }

    @Test
    void rollKindInInputSelectsTheRefinedAbstraction() {
        assertInstanceOf(DamageRoll.class, factory.create("2d6+3"));
        assertInstanceOf(AdvantageRoll.class, factory.create("adv @tower"));
    }

    @Test
    void sameCommandWithDifferentSourceUsesDifferentImplementation() {
        when(pseudo.nextInt(1, 20)).thenReturn(2, 3);
        when(tower.nextInt(1, 20)).thenReturn(18, 9);
        assertEquals(3, factory.create("adv @pseudo").roll());
        assertEquals(18, factory.create("adv @tower").roll());
    }
}
