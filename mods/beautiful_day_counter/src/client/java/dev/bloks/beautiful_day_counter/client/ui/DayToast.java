package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Minimal custom toast using Yarn 1.21.11 APIs.
 * Draws a single line of text for a fixed duration.
 */
public final class DayToast implements Toast {
    private final Text title;
    private final long displayMs;
    private long startTime = -1L;
    private Visibility visibility = Visibility.SHOW;

    public DayToast(Text title, long displayMs) {
        this.title = title;
        this.displayMs = displayMs;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long time) {
        // Warm background dimensions (approximate default toast size)
        int bw = Math.max(120, textRenderer.getWidth(title) + 12 + 16 + 4);
        int bh = textRenderer.fontHeight + 6;
        int bx = 0;
        int by = 0;
        // Background with soft golden color and subtle border
        context.fill(bx, by, bx + bw, by + bh, 0xC0FFCC66);
        context.fill(bx, by, bx + bw, by + 1, 0x80E6B450);
        context.fill(bx, by + bh - 1, bx + bw, by + bh, 0x80E6B450);
        context.fill(bx, by, bx + 1, by + bh, 0x80E6B450);
        context.fill(bx + bw - 1, by, bx + bw, by + bh, 0x80E6B450);

        // Icon (clock) + text
        int iconX = bx + 6;
        int iconY = by + 2;
        context.drawItem(new ItemStack(Items.CLOCK), iconX, iconY);
        context.drawTextWithShadow(textRenderer, title, iconX + 16 + 4, by + 4, 0xFF2A2A2A);
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (startTime < 0) startTime = time;
        if (time - startTime >= displayMs) {
            visibility = Visibility.HIDE;
        }
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }
}
