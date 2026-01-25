package dev.bloks.beautiful_day_counter.net;

import dev.bloks.beautiful_day_counter.BeautifulDayCounter;
import net.minecraft.util.Identifier;

public final class Packets {
    public static final Identifier DAY_CHANGE = new Identifier(BeautifulDayCounter.MOD_ID, "day_change");

    private Packets() {}
}

