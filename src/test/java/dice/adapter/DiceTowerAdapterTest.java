package dice.adapter;
import dice.abstraction.AdvantageRoll;
import dice.implementor.RandomSourceException;
import dice.legacy.DiceTowerDevice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The adapted implementation: parameter/result translation and failure translation, with a STUBBED tower. */
class DiceTowerAdapterTest {

    private final DiceTowerDevice tower = mock(DiceTowerDevice.class);
    private final DiceTowerAdapter adapter = new DiceTowerAdapter(tower);

    // ---------- translation of the normal path ----------

    @Test
    void convertsRangeToSidesAndZeroBasedIndexToValue() {
        when(tower.rollRaw(6, "STD")).thenReturn(2L);          // index 2 on a six-sided die

        assertEquals(3, adapter.nextInt(1, 6));                // 1 + 2
        verify(tower).rollRaw(6, "STD");
    }

    // ---------- translation of failures ----------
    @Test
    void jammedSentinelBecomesRandomSourceException() {
        when(tower.rollRaw(anyLong(), anyString())).thenReturn(DiceTowerDevice.ERR_JAMMED);

        RandomSourceException e = assertThrows(RandomSourceException.class, () -> adapter.nextInt(1, 6));
        assertEquals("Random source is temporarily unavailable", e.getMessage());
    }

    @Test
    void invalidTowerResultsBecomeRandomSourceException() {
        when(tower.rollRaw(anyLong(), anyString()))
                .thenReturn(DiceTowerDevice.ERR_BAD_REQUEST);

        assertThrows(
                RandomSourceException.class,
                () -> adapter.nextInt(1, 6)
        );
    }

    // ---------- nothing adapter-specific leaks through the Abstraction ----------
    @Test
    void abstractionSeesOnlyContractExceptionWhenTowerFails() {
        when(tower.rollRaw(anyLong(), anyString())).thenReturn(DiceTowerDevice.ERR_JAMMED);

        RandomSourceException e = assertThrows(RandomSourceException.class,
                () -> new AdvantageRoll(adapter).roll());

        assertFalse(e.getMessage().toLowerCase().contains("tower"));
        assertFalse(e.getMessage().toLowerCase().contains("jam"));
    }
}
