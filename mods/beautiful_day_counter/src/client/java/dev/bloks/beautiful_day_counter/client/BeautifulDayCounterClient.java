package dev.bloks.beautiful_day_counter.client;

import dev.bloks.beautiful_day_counter.client.state.ClientState;
import dev.bloks.beautiful_day_counter.client.ui.Toasts;
import dev.bloks.beautiful_day_counter.net.DayChangePayload;
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
    private static net.minecraft.client.option.KeyBinding TOGGLE_KEY; // temporarily unused until category API pinned

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
        // Keybinding constructor signature changed in 1.21.11 (Category enum). We will reintroduce
        // registration after pinning the exact API. For now, rely on config/UI toggle only.
        TOGGLE_KEY = null;
        // Register legacy ByteBuf-based receiver (no payload type registry needed)
        ClientPlayNetworking.registerGlobalReceiver(DayChangePayload.CHANNEL, (client, handler, buf, responseSender) -> {
            long day = buf.readVarLong();
            client.execute(() -> {
                ClientState.get().setCurrentDay(day);
                LOGGER.debug("Day updated from payload: {}", day);
                Toasts.showDayToast(day, ClientState.get().getDayLabel());
            });
        });

        // HUD overlay: render a subtle day counter each frame when visible
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            var mc = net.minecraft.client.MinecraftClient.getInstance();
            var state = ClientState.get();
            if (mc == null || mc.player == null) return;
            if (mc.options.hudHidden) return; // respect F1 Hide GUI
            if (!state.isHudVisible()) return;
            long day = state.getCurrentDay();
            if (day <= 0) return;
            String text = state.getDayLabel() + " " + day;
            int screenW = mc.getWindow().getScaledWidth();
            int screenH = mc.getWindow().getScaledHeight();
            int textW = mc.textRenderer.getWidth(text);
            int textH = mc.textRenderer.fontHeight;
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
            drawContext.drawTextWithShadow(mc.textRenderer, net.minecraft.text.Text.literal(text), x, y, 0xFFFFFF);
        });

        // Client-only fallback: detect day change based on client world time if no packet arrives
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            long computedDay = (client.world.getTimeOfDay() / 24000L) + 1L;
            var state = ClientState.get();
            // Toggle key temporarily disabled until KeyBinding category API is pinned
            if (state.getCurrentDay() == 0L) {
                state.setCurrentDay(computedDay); // initialize on join to avoid catch-up toast
                return;
            }
            if (computedDay > state.getCurrentDay()) {
                state.setCurrentDay(computedDay);
                LOGGER.debug("Day advanced (client fallback): {}", computedDay);
                Toasts.showDayToast(computedDay, state.getDayLabel());
            }
            // No action-bar spam; HUD overlay renders each frame via HudRenderCallback
        });
    }
}
