package dev.bloks.beautiful_day_counter.client.ui;

import dev.bloks.beautiful_day_counter.client.state.ClientState;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

public final class PostMortemOverlay {
  private PostMortemOverlay() {}

  public static void render(DrawContext context) {
    var mc = MinecraftClient.getInstance();
    if (mc == null || mc.textRenderer == null || context == null) {
      return;
    }

    Window window = mc.getWindow();
    if (window == null) {
      return;
    }

    long day = ClientState.get().getCurrentDay();
    if (day <= 0 && mc.world != null) {
      day = (mc.world.getTimeOfDay() / 24000L) + 1L;
    }
    if (day <= 0) {
      return;
    }

    String dayLabel = ClientState.get().getDayLabel();
    Text lineOne = Text.literal("Survived " + dayLabel + " " + day);
    String detail = describeWorld(mc);
    Text lineTwo = detail.isEmpty() ? Text.empty() : Text.literal(detail);

    int width = window.getScaledWidth();
    int height = window.getScaledHeight();
    int centerX = width / 2;
    int y = height / 2 + 32;

    context.drawCenteredTextWithShadow(mc.textRenderer, lineOne, centerX, y, 0xFFFFAA00);
    if (!lineTwo.getString().isEmpty()) {
      y += mc.textRenderer.fontHeight + 3;
      context.drawCenteredTextWithShadow(mc.textRenderer, lineTwo, centerX, y, 0xFFAAAAAA);
    }
  }

  private static String describeWorld(MinecraftClient mc) {
    var world = mc.world;
    if (world == null) {
      return "";
    }

    boolean hardcore = world.getLevelProperties().isHardcore();
    var gameMode =
        mc.interactionManager != null ? mc.interactionManager.getCurrentGameMode() : null;
    var difficulty = world.getDifficulty();

    StringBuilder builder = new StringBuilder();
    if (hardcore) {
      builder.append("Hardcore");
    } else if (gameMode != null) {
      builder.append(capitalize(gameMode.name().toLowerCase(Locale.ROOT)));
    }

    if (difficulty != null) {
      if (!builder.isEmpty()) {
        builder.append(" • ");
      }
      builder.append(capitalize(difficulty.getName()));
    }
    return builder.toString();
  }

  private static String capitalize(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }
    if (input.length() == 1) {
      return input.toUpperCase();
    }
    return Character.toUpperCase(input.charAt(0)) + input.substring(1);
  }
}
