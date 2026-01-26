package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Minimal custom toast using Yarn 1.21.11 APIs.
 * Draws a single line of text for a fixed duration.
 */
public final class DayToast implements Toast {
    private final Text title;
    private final long displayMs;
    private final int phase; // 0..7
    private final boolean useSystemMoonTexture;
    private long startTime = -1L;
    private Visibility visibility = Visibility.SHOW;

    public DayToast(Text title, long displayMs, int phase, boolean useSystemMoonTexture) {
        this.title = title;
        this.displayMs = displayMs;
        this.phase = Math.floorMod(phase, 8);
        this.useSystemMoonTexture = useSystemMoonTexture;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long time) {
        // Layout constants
        int pad = 8;
        int iconSize = 16;
        int iconPad = 6;

        int textW = textRenderer.getWidth(title);
        int bw = Math.max(140, pad + iconSize + iconPad + textW + pad);
        int bh = Math.max(24, textRenderer.fontHeight + pad);
        int bx = 0;
        int by = 0;

        // Background with soft golden color and 75% opacity (alpha ~ 0xBF/0xC0)
        context.fill(bx, by, bx + bw, by + bh, 0xBFFFCC66);
        // Subtle border
        int border = 0x80E6B450;
        context.fill(bx, by, bx + bw, by + 1, border);
        context.fill(bx, by + bh - 1, bx + bw, by + bh, border);
        context.fill(bx, by, bx + 1, by + bh, border);
        context.fill(bx + bw - 1, by, bx + bw, by + bh, border);

        int iconX = bx + pad;
        int iconY = by + (bh - iconSize) / 2;
        if (useSystemMoonTexture) {
            // Render moon phase from system texture (4x2 grid)
            Identifier moon = Identifier.of("minecraft", "textures/environment/moon_phases.png");
            int texW = 256, texH = 128; // vanilla defaults; packs scale proportionally
            int frameW = texW / 4;
            int frameH = texH / 2;
            int col = phase % 4;
            int row = phase / 4;
            int u = col * frameW;
            int v = row * frameH;
            context.drawTexture(moon, iconX, iconY, u, v, iconSize, iconSize, texW, texH);
        } else {
            // Fallback: draw a minimalist circle outline as a placeholder
            context.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, 0x20FFFFFF);
            context.drawTextWithShadow(textRenderer, Text.literal("◐"), iconX + 3, iconY + 2, 0xFFFFFFFF);
        }

        // Bright, readable text with shadow, vertically centered
        int textX = iconX + iconSize + iconPad;
        int textY = by + (bh - textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(textRenderer, title, textX, textY, 0xFFFFFFFF);
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
