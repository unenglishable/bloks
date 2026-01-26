package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

/**
 * Minimal custom toast using Yarn 1.21.11 APIs.
 * Draws a single line of text for a fixed duration.
 */
public final class DayToast implements Toast {
    private final Text title;
    private final long displayMs;
    private long startTime = -1L;

    public DayToast(Text title, long displayMs) {
        this.title = title;
        this.displayMs = displayMs;
    }

    @Override
    public Visibility render(DrawContext context, ToastManager manager, long time) {
        if (startTime < 0) startTime = time;
        var mc = MinecraftClient.getInstance();
        if (mc != null && mc.textRenderer != null) {
            // Simple text centered vertically in the toast area
            context.drawText(mc.textRenderer, title, 8, 8, 0xFFFFFF, true);
        }
        return (time - startTime) < displayMs ? Visibility.SHOW : Visibility.HIDE;
    }
}

