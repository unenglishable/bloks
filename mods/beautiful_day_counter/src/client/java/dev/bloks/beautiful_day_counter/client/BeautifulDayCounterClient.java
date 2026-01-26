package dev.bloks.beautiful_day_counter.client;

import dev.bloks.beautiful_day_counter.client.state.ClientState;
import dev.bloks.beautiful_day_counter.client.ui.Toasts;
import dev.bloks.beautiful_day_counter.net.DayChangePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
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
    private static net.minecraft.client.option.KeyBinding TOGGLE_KEY;

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
                new net.minecraft.client.option.KeyBinding(
                        "key.beautiful_day_counter.toggle",
                        org.lwjgl.glfw.GLFW.GLFW_KEY_H,
                        net.minecraft.client.option.KeyBinding.Category.MISC
                )
        );
        // Register typed payload for S2C
        PayloadTypeRegistry.playS2C().register(DayChangePayload.ID, DayChangePayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(DayChangePayload.ID, (payload, context) -> {
            long day = payload.day();
            var client = net.minecraft.client.MinecraftClient.getInstance();
            client.execute(() -> {
                ClientState.get().setCurrentDay(day);
                LOGGER.debug("Day updated from payload: {}", day);
                // Temporary in-HUD toast until SystemToast is finalized
                ClientState.get().setToastTicks(300); // ~5s at ~60 FPS
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
            if (day <= 0 && mc.world != null) {
                // Fallback compute for first-frame visibility
                day = (mc.world.getTimeOfDay() / 24000L) + 1L;
            }
            String text = state.getDayLabel() + " " + Math.max(day, 1);
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
            // Draw day text (opaque white)
            drawContext.drawTextWithShadow(mc.textRenderer, net.minecraft.text.Text.literal(text), x, y, 0xFFFFFFFF);
            // Lightweight debug tag to help verify rendering path
            // (top-left, tiny gray)
            drawContext.drawText(mc.textRenderer, net.minecraft.text.Text.literal("BDC"), 2, 2, 0xA0A0A0, false);

            // Ephemeral toast-like banner at top-right when toastTicks > 0
            if (state.getToastTicks() > 0) {
                String toastText = state.getDayLabel() + " " + Math.max(day, 1);
                int tw = mc.textRenderer.getWidth(toastText);
                int th = mc.textRenderer.fontHeight + 6;
                int bx = screenW - margin - Math.max(tw + 12, 100);
                int by = margin;
                int bw = Math.max(tw + 12, 100);
                int bh = th;
                // Background (warm morning vibe)
                drawContext.fill(bx, by, bx + bw, by + bh, 0xC0FFCC66);
                // Border
                drawContext.fill(bx, by, bx + bw, by + 1, 0x80E6B450);
                drawContext.fill(bx, by + bh - 1, bx + bw, by + bh, 0x80E6B450);
                drawContext.fill(bx, by, bx + 1, by + bh, 0x80E6B450);
                drawContext.fill(bx + bw - 1, by, bx + bw, by + bh, 0x80E6B450);
                // Icon (clock) + text
                int iconX = bx + 6;
                int iconY = by + 2;
                drawContext.drawItem(new net.minecraft.item.ItemStack(net.minecraft.item.Items.CLOCK), iconX, iconY);
                drawContext.drawTextWithShadow(mc.textRenderer, net.minecraft.text.Text.literal(toastText), iconX + 16 + 4, by + 4, 0xFF2A2A2A);
                state.decrementToastTicks();
            }
        });

        // Client-only fallback: detect day change based on client world time if no packet arrives
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            long computedDay = (client.world.getTimeOfDay() / 24000L) + 1L;
            var state = ClientState.get();
            // Handle toggle key
            if (TOGGLE_KEY != null && TOGGLE_KEY.wasPressed()) {
                state.toggleHudVisible();
                cfg.hudVisible = state.isHudVisible();
                cfg.save();
                // Brief confirmation toast
                Toasts.showDayToast(state.getCurrentDay(), state.isHudVisible() ? state.getDayLabel() : "");
            }
            if (state.getCurrentDay() == 0L) {
                state.setCurrentDay(computedDay); // initialize on join to avoid catch-up toast
                return;
            }
            if (computedDay > state.getCurrentDay()) {
                state.setCurrentDay(computedDay);
                LOGGER.debug("Day advanced (client fallback): {}", computedDay);
                state.setToastTicks(300); // ~5s at ~60 FPS
            }
            // No action-bar spam; HUD overlay renders each frame via HudRenderCallback
        });
    }
}
