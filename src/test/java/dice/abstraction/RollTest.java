package dice.abstraction;

import dice.implementor.RandomSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Normal delegation of Refined Abstractions to a MOCKED Implementor. */
class RollTest {
    private final RandomSource source = mock(RandomSource.class);

    @Test
    void damageRollSumsDiceAndAddsBonus() {
        when(source.nextInt(1, 6)).thenReturn(3, 4);

        int result = new DamageRoll(source, 2, 6, 1).roll();

        assertEquals(8, result);                       // 3 + 4 + 1
        verify(source, times(2)).nextInt(1, 6);        // delegated once per die
    }

    @Test
    void advantageRollKeepsTheHigherOfTwoD20() {
        when(source.nextInt(1, 20)).thenReturn(7, 15);

        int result = new AdvantageRoll(source).roll();

        assertEquals(15, result);
        verify(source, times(2)).nextInt(1, 20);
    }

    @Test
    void damageRollRejectsInvalidDice() {
        assertThrows(IllegalArgumentException.class, () -> new DamageRoll(source, 0, 6, 0));
        assertThrows(IllegalArgumentException.class, () -> new DamageRoll(source, 1, 1, 0));
    }
}
