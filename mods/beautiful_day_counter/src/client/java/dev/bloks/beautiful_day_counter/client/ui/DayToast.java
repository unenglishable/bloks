package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Minimal custom toast using Yarn 1.21.11 APIs. Draws a single line of text for a fixed duration.
 */
public final class DayToast implements Toast {
  private final Text title;
  private final long displayMs;
  private final Runnable onHide;
  private long startTime = -1L;
  private boolean notified = false;
  private Visibility visibility = Visibility.SHOW;

  public DayToast(Text title, long displayMs, Runnable onHide) {
    this.title = title;
    this.displayMs = displayMs;
    this.onHide = onHide;
  }

  @Override
  public void draw(DrawContext context, TextRenderer textRenderer, long time) {
    // Layout constants
    int pad = 8;
    int iconSize = 16;
    int iconPad = 6;

    int textW = textRenderer.getWidth(title);
    // Space: [pad][clock 16][pad][text][pad]
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
    // Left icon: clock item
    context.drawItem(new ItemStack(Items.CLOCK), iconX, iconY);

    // Bright, readable text with shadow, vertically centered
    int textX = iconX + iconSize + iconPad;
    int textY = by + (bh - textRenderer.fontHeight) / 2;
    context.drawTextWithShadow(textRenderer, title, textX, textY, 0xFFFFFFFF);

    // No right icon (moon) after de-scope of moon feature
  }

  @Override
  public void update(ToastManager manager, long time) {
    if (startTime < 0) {
      startTime = time;
    }
    if (time - startTime >= displayMs) {
      visibility = Visibility.HIDE;
      if (!notified && onHide != null) {
        notified = true;
        onHide.run();
      }
    }
  }

  @Override
  public Visibility getVisibility() {
    return visibility;
  }
}
