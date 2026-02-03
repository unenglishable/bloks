package dev.bloks.beautiful_day_counter;

import dev.bloks.beautiful_day_counter.net.DayChangePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeautifulDayCounter implements ModInitializer {
  public static final String MOD_ID = "beautiful_day_counter";
  public static final String DISPLAY_NAME = "Beautiful Day Counter";
  public static final String KEY_CATEGORY_TRANSLATION_KEY =
      "key.categories.beautiful_day_counter";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private long lastDay = -1L;

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing {}", MOD_ID);

    ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
  }

  private void onEndServerTick(MinecraftServer server) {
    // Use overworld time as the canonical day counter
    var world = server.getOverworld();
    if (world == null) {
      return;
    }
    long timeOfDay = world.getTimeOfDay();
    long day = (timeOfDay / 24000L) + 1L; // Day 1 starts at 0..23999
    if (day != lastDay) {
      if (lastDay != -1L) {
        LOGGER.info("Welcome to Day {}!", day);
        // Send typed payload to all players
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
          ServerPlayNetworking.send(player, new DayChangePayload(day));
        }
      }
      lastDay = day;
    }
  }
}
