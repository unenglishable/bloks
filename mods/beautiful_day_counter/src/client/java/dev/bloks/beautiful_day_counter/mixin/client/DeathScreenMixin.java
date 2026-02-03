package dev.bloks.beautiful_day_counter.mixin.client;

import dev.bloks.beautiful_day_counter.client.ui.PostMortemOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
  protected DeathScreenMixin(Text title) {
    super(title);
  }

  @Inject(method = "render", at = @At("TAIL"))
  private void beautiful_day_counter$injectPostMortem(
      DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    PostMortemOverlay.render(context);
  }
}
