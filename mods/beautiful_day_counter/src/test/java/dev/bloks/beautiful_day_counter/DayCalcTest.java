package dev.bloks.beautiful_day_counter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for day calculation logic: day = floor(timeOfDay / 24000) + 1.
 */
public class DayCalcTest {
    private long calc(long timeOfDay) {
        return (timeOfDay / 24000L) + 1L;
    }

    @Test
    void dayStartsAtOne() {
        assertEquals(1L, calc(0));
        assertEquals(1L, calc(23999));
    }

    @Test
    void dayIncrementsAt24000() {
        assertEquals(2L, calc(24000));
        assertEquals(2L, calc(24001));
        assertEquals(3L, calc(48000));
    }

    @Test
    void largeTimes() {
        assertEquals(1001L, calc(1000L * 24000L));
        assertEquals(5001L, calc(5000L * 24000L));
    }
}

