package dev.bloks.beautiful_day_counter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import io.netty.buffer.Unpooled;
import dev.bloks.beautiful_day_counter.net.Packets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeautifulDayCounter implements ModInitializer {
    public static final String MOD_ID = "beautiful_day_counter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private long lastDay = -1L;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {}", MOD_ID);

        ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
    }

    private void onEndServerTick(MinecraftServer server) {
        // Use overworld time as the canonical day counter
        var world = server.overworld();
        if (world == null) return;
        long timeOfDay = world.getDayTime();
        long day = (timeOfDay / 24000L) + 1L; // Day 1 starts at 0..23999
        if (day != lastDay) {
            if (lastDay != -1L) {
                LOGGER.info("Welcome to Day {}!", day);
                // Send a networking packet to all players to update HUD / trigger toast
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeVarLong(day);
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (ServerPlayNetworking.canSend(player, Packets.DAY_CHANGE)) {
                        ServerPlayNetworking.send(player, Packets.DAY_CHANGE, buf.copy());
                    }
                }
            }
            lastDay = day;
        }
    }
}
