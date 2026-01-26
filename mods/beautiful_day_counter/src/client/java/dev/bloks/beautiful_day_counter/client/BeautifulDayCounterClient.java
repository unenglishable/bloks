package dev.bloks.beautiful_day_counter.client;

import dev.bloks.beautiful_day_counter.client.state.ClientState;
import dev.bloks.beautiful_day_counter.net.Packets;
import dev.bloks.beautiful_day_counter.client.ui.DayToast;
import dev.bloks.beautiful_day_counter.client.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeautifulDayCounterClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("beautiful_day_counter:client");
    private static net.minecraft.client.KeyMapping TOGGLE_KEY;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client init: Beautiful Day Counter");
        // Load config and apply to live state
        Config cfg = Config.load();
        ClientState.get().setDayLabel(cfg.label);
        ClientState.get().setHudCorner(cfg.hudCorner);
        if (ClientState.get().isHudVisible() != cfg.hudVisible) {
            ClientState.get().toggleHudVisible();
        }
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
                DayToast.show(day, ClientState.get().getDayLabel());
            });
        });

        // HUD overlay: render a subtle day counter each frame when visible
        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            var state = ClientState.get();
            if (mc == null || mc.player == null) return;
            if (mc.options.hideGui) return; // respect F1 Hide GUI
            if (!state.isHudVisible()) return;
            long day = state.getCurrentDay();
            if (day <= 0) return;
            String text = state.getDayLabel() + " " + day;
            int screenW = mc.getWindow().getGuiScaledWidth();
            int screenH = mc.getWindow().getGuiScaledHeight();
            int textW = mc.font.width(text);
            int textH = mc.font.lineHeight;
            int margin = 4;
            int x;
            int y;
            String corner = state.getHudCorner().toLowerCase();
            switch (corner) {
                case "top_left" -> { x = margin; y = margin; }
                case "top_right" -> { x = screenW - margin - textW; y = margin; }
                case "bottom_left" -> { x = margin; y = screenH - margin - textH; }
                default -> { x = screenW - margin - textW; y = screenH - margin - textH; }
            }
            guiGraphics.drawString(mc.font, net.minecraft.network.chat.Component.literal(text), x, y, 0xFFFFFF, true);
        });

        // Client-only fallback: detect day change based on client world time if no packet arrives
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            long computedDay = (client.world.getTimeOfDay() / 24000L) + 1L;
            var state = ClientState.get();
            // Handle toggle key
            if (TOGGLE_KEY != null && TOGGLE_KEY.consumeClick()) {
                state.toggleHudVisible();
                // Persist toggle to config
                cfg.hudVisible = state.isHudVisible();
                cfg.save();
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
                DayToast.show(computedDay, state.getDayLabel());
            }
            // No action-bar spam; HUD overlay renders each frame via HudRenderCallback
        });
    }
}
