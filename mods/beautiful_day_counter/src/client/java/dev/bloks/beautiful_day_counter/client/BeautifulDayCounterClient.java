package dev.bloks.beautiful_day_counter.client;

import dev.bloks.beautiful_day_counter.client.state.ClientState;
import dev.bloks.beautiful_day_counter.net.Packets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeautifulDayCounterClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("beautiful_day_counter:client");
    private static net.minecraft.client.KeyMapping TOGGLE_KEY;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client init: Beautiful Day Counter");
        // Keybinding: toggle HUD on/off (default: H)
        TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(
                new net.minecraft.client.KeyMapping(
                        "key.beautiful_day_counter.toggle",
                        org.lwjgl.glfw.GLFW.GLFW_KEY_H,
                        "category.beautiful_day_counter"
                )
        );
        // Register packet receiver to update state (and later show toast)
        ClientPlayNetworking.registerGlobalReceiver(Packets.DAY_CHANGE, (client, handler, buf, responseSender) -> {
            long day = buf.readVarLong();
            client.execute(() -> {
                ClientState.get().setCurrentDay(day);
                LOGGER.debug("Day updated from packet: {}", day);
                // TODO: trigger toast once UI is implemented
            });
        });
        // TODO: Register HUD overlay to render subtle day counter when enabled

        // Client-only fallback: detect day change based on client world time if no packet arrives
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            long computedDay = (client.world.getTimeOfDay() / 24000L) + 1L;
            var state = ClientState.get();
            // Handle toggle key
            if (TOGGLE_KEY != null && TOGGLE_KEY.consumeClick()) {
                state.toggleHudVisible();
                if (client.player != null) {
                    client.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(
                                    state.isHudVisible() ? "Day counter: ON" : "Day counter: OFF"
                            ),
                            true
                    );
                }
            }
            if (state.getCurrentDay() == 0L) {
                state.setCurrentDay(computedDay); // initialize on join to avoid catch-up toast
                return;
            }
            if (computedDay > state.getCurrentDay()) {
                state.setCurrentDay(computedDay);
                LOGGER.debug("Day advanced (client fallback): {}", computedDay);
                // TODO: trigger toast and refresh HUD once UI is implemented
            }
            // Minimal HUD overlay via action bar (throttled)
            if (state.isHudVisible() && client.player != null) {
                state.incrementOverlayTick();
                if (state.getTickSinceOverlayUpdate() >= 40) { // ~2 seconds at 20 TPS
                    String text = state.getDayLabel() + " " + state.getCurrentDay();
                    client.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(text),
                            true
                    );
                    state.resetOverlayTick();
                }
            } else {
                state.resetOverlayTick();
            }
        });
    }
}
