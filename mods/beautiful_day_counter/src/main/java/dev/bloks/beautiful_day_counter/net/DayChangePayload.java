package dev.bloks.beautiful_day_counter.net;

import dev.bloks.beautiful_day_counter.BeautifulDayCounter;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DayChangePayload(long day) implements CustomPayload {
    public static final Id<DayChangePayload> ID = new Id<>(Identifier.of(BeautifulDayCounter.MOD_ID, "day_change"));
    // Legacy channel identifier for ByteBuf-based networking (avoids payload type registry)
    public static final Identifier CHANNEL = Identifier.of(BeautifulDayCounter.MOD_ID, "day_change");

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static DayChangePayload read(PacketByteBuf buf) {
        return new DayChangePayload(buf.readVarLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarLong(day);
    }
}
