package dev.bloks.beautiful_day_counter.mixin.client;

import dev.bloks.beautiful_day_counter.client.ui.PostMortemOverlay;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
  @Shadow @Final private List<ButtonWidget> buttons;

  protected DeathScreenMixin(Text title) {
    super(title);
  }

  @Unique private int beautiful_day_counter$scoreTextY = Integer.MIN_VALUE;

  @Inject(method = "render", at = @At("HEAD"))
  private void beautiful_day_counter$resetScoreAnchor(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    beautiful_day_counter$scoreTextY = Integer.MIN_VALUE;
  }

  @ModifyArg(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
              ordinal = 1),
      index = 3)
  private int beautiful_day_counter$captureScoreY(int y) {
    beautiful_day_counter$scoreTextY = y;
    return y;
  }

  @Inject(method = "render", at = @At("TAIL"))
  private void beautiful_day_counter$injectPostMortem(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    int topY =
        beautiful_day_counter$scoreTextY != Integer.MIN_VALUE
            ? beautiful_day_counter$scoreTextY + textRenderer.fontHeight + 6
            : (height / 2);
    int clampBottom = buttons.stream().mapToInt(ButtonWidget::getY).min().orElse(height - 40);

    PostMortemOverlay.render(context, topY, clampBottom);
  }
}
