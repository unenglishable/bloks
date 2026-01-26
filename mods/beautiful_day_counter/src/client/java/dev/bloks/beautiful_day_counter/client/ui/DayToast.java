package dev.bloks.beautiful_day_counter.client.ui;

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

    public DayToast(Text title, long displayMsIgnored) {
        this.title = title;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long time) {
        // Simple text inside the toast
        context.drawText(textRenderer, title, 8, 8, 0xFFFFFF, true);
    }
}
