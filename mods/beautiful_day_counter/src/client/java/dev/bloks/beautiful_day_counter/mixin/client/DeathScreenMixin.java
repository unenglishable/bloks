package dev.bloks.beautiful_day_counter.mixin.client;

import dev.bloks.beautiful_day_counter.client.state.ClientState;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
  @Shadow private Text scoreText;

  @Unique private Text beautiful_day_counter$baseScoreText;

  protected DeathScreenMixin(Text title) {
    super(title);
  }

  @Inject(method = "render", at = @At("HEAD"))
  private void beautiful_day_counter$decorateScoreText(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    if (beautiful_day_counter$baseScoreText == null) {
      beautiful_day_counter$baseScoreText = scoreText;
    } else {
      scoreText = beautiful_day_counter$baseScoreText;
    }
    long day = ClientState.get().getCurrentDay();
    var mc = MinecraftClient.getInstance();
    if (day <= 0 && mc != null && mc.world != null) {
      day = (mc.world.getTimeOfDay() / 24000L) + 1L;
    }
    if (day <= 0 || mc == null) {
      return;
    }
    String detail = beautiful_day_counter$describeWorld(mc);
    String label = ClientState.get().getDayLabel();
    Text decorated =
        beautiful_day_counter$baseScoreText
            .copy()
            .append(Text.literal(" • " + label + " " + Math.max(day, 1)))
            .append(detail.isEmpty() ? Text.empty() : Text.literal(" (" + detail + ")"));
    scoreText = decorated;
  }

  @Inject(method = "render", at = @At("TAIL"))
  private void beautiful_day_counter$restoreScoreText(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    if (beautiful_day_counter$baseScoreText != null) {
      scoreText = beautiful_day_counter$baseScoreText;
    }
  }

  @Unique
  private static String beautiful_day_counter$describeWorld(MinecraftClient mc) {
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

  @Unique
  private static String capitalize(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }
    if (input.length() == 1) {
      return input.toUpperCase(Locale.ROOT);
    }
    return Character.toUpperCase(input.charAt(0)) + input.substring(1);
  }
}
