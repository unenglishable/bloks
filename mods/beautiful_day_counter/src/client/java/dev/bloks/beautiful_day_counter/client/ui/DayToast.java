package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
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
    public Visibility draw(DrawContext context, TextRenderer textRenderer, long time) {
        if (startTime < 0) startTime = time;
        // Simple text inside the toast
        context.drawText(textRenderer, title, 8, 8, 0xFFFFFF, true);
        return (time - startTime) < displayMs ? Visibility.SHOW : Visibility.HIDE;
    }
}
