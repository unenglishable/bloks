package dev.bloks.beautiful_day_counter.client.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import dev.bloks.beautiful_day_counter.client.state.ClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
  private final Screen parent;
  private TextFieldWidget labelField;
  private ButtonWidget toggleHudBtn;
  private ButtonWidget cornerBtn;
  private ButtonWidget toastBtn;
  // Moon feature removed
  private ButtonWidget saveBtn;
  private ButtonWidget cancelBtn;
  private final Config config;
  private int scroll = 0;
  private int contentHeight = 0;
  private static final int ROW_SPACING = 30;
  private static final int CONTROL_HEIGHT = 20;
  private static final int MARGIN = 12;

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Holding parent reference for navigation; not exposed outside")
  public ConfigScreen(Screen parent, Config config) {
    super(Text.translatable("ui.beautiful_day_counter.config.title"));
    this.parent = parent;
    this.config = config;
  }

  @Override
  protected void init() {
    int centerX = this.width / 2;
    int y = this.height / 3;

    // Label input
    labelField =
        new TextFieldWidget(
            this.textRenderer,
            centerX - 100,
            y,
            200,
            20,
            Text.translatable("ui.beautiful_day_counter.label.caption"));
    labelField.setMaxLength(32);
    labelField.setText(config.label);
    // Hint text when empty, similar to Create World "World Name" field
    updateSuggestion();
    labelField.setChangedListener(s -> updateSuggestion());
    addDrawableChild(labelField);

    y += 30;
    // Toggle HUD button
    toggleHudBtn =
        ButtonWidget.builder(
                hudLabel(),
                b -> {
                  config.hudVisible = !config.hudVisible;
                  b.setMessage(hudLabel());
                })
            .dimensions(centerX - 100, y, 200, 20)
            .build();
    addDrawableChild(toggleHudBtn);

    y += 30;
    // Corner selector
    cornerBtn =
        ButtonWidget.builder(
                cornerLabel(),
                b -> {
                  config.hudCorner = nextCorner(config.hudCorner);
                  b.setMessage(cornerLabel());
                })
            .dimensions(centerX - 100, y, 200, 20)
            .build();
    addDrawableChild(cornerBtn);

    y += 30;
    // Toast toggle (placed after HUD settings to keep them grouped)
    toastBtn =
        ButtonWidget.builder(
                toastLabel(),
                b -> {
                  config.showToast = !config.showToast;
                  b.setMessage(toastLabel());
                })
            .dimensions(centerX - 100, y, 200, 20)
            .build();
    addDrawableChild(toastBtn);

    y += 30;
    // Moon feature removed
    // Save
    saveBtn =
        ButtonWidget.builder(
                Text.translatable("ui.beautiful_day_counter.save"),
                b -> {
                  config.label = labelField.getText();
                  config.save();
                  // Apply to live state
                  var state = ClientState.get();
                  state.setDayLabel(config.label);
                  if (state.isHudVisible() != config.hudVisible) {
                    state.toggleHudVisible();
                  }
                  state.setHudCorner(config.hudCorner);
                  state.setToastEnabled(config.showToast);
                  // Moon feature removed
                  MinecraftClient.getInstance().setScreen(parent);
                })
            .dimensions(centerX - 100, y, 95, CONTROL_HEIGHT)
            .build();
    addDrawableChild(saveBtn);

    // Cancel
    cancelBtn =
        ButtonWidget.builder(
                Text.translatable("ui.beautiful_day_counter.cancel"),
                b -> MinecraftClient.getInstance().setScreen(parent))
            .dimensions(centerX + 5, y, 95, CONTROL_HEIGHT)
            .build();
    addDrawableChild(cancelBtn);

    // Initial layout (centered if fits; else enable scroll)
    relayout();
  }

  private Text hudLabel() {
    return config.hudVisible
        ? Text.translatable("ui.beautiful_day_counter.hud.visible")
        : Text.translatable("ui.beautiful_day_counter.hud.hidden");
  }

  private Text toastLabel() {
    return config.showToast
        ? Text.translatable("ui.beautiful_day_counter.toast.enabled")
        : Text.translatable("ui.beautiful_day_counter.toast.disabled");
  }

  // Moon feature removed

  private Text cornerLabel() {
    return switch (config.hudCorner.toLowerCase()) {
      case "top_left" -> Text.translatable("ui.beautiful_day_counter.corner.top_left");
      case "top_right" -> Text.translatable("ui.beautiful_day_counter.corner.top_right");
      case "bottom_left" -> Text.translatable("ui.beautiful_day_counter.corner.bottom_left");
      default -> Text.translatable("ui.beautiful_day_counter.corner.bottom_right");
    };
  }

  private String nextCorner(String cur) {
    return switch (cur.toLowerCase()) {
      case "top_left" -> "top_right";
      case "top_right" -> "bottom_right";
      case "bottom_right" -> "bottom_left";
      default -> "top_left";
    };
  }

  private void updateSuggestion() {
    if (labelField == null) {
      return;
    }
    boolean empty = labelField.getText() == null || labelField.getText().isEmpty();
    labelField.setSuggestion(
        empty ? Text.translatable("ui.beautiful_day_counter.label.suggestion").getString() : null);
  }

  @Override
  public void render(
      net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    if (labelField != null) {
      int lx = labelField.getX();
      int ly = labelField.getY() - 12;
      context.drawTextWithShadow(
          this.textRenderer,
          Text.translatable("ui.beautiful_day_counter.label.caption"),
          lx,
          ly,
          0xFFFFFFFF);
    }
    // Optional simple scrollbar indicator on the right
    int viewport = this.height - 2 * MARGIN;
    if (contentHeight > viewport) {
      float ratio = (float) viewport / (float) contentHeight;
      int barH = Math.max(12, (int) (viewport * ratio));
      int maxScroll = contentHeight - viewport;
      int barY =
          MARGIN
              + (maxScroll == 0
                  ? 0
                  : (int) ((this.scroll / (float) maxScroll) * (viewport - barH)));
      int barX = this.width - 6;
      context.fill(barX, MARGIN, barX + 3, MARGIN + viewport, 0x30000000);
      context.fill(barX, barY, barX + 3, barY + barH, 0x80FFFFFF);
    }
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    int viewport = this.height - 2 * MARGIN;
    if (contentHeight > viewport) {
      int maxScroll = Math.max(0, contentHeight - viewport);
      // Scroll direction: positive verticalAmount scrolls up (reduce scroll)
      scroll -= (int) (verticalAmount * 12);
      if (scroll < 0) {
        scroll = 0;
      }
      if (scroll > maxScroll) {
        scroll = maxScroll;
      }
      relayout();
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
  }

  @Override
  public void resize(int width, int height) {
    int prevScroll = this.scroll;
    super.resize(width, height);
    this.scroll = prevScroll;
    relayout();
  }

  private void relayout() {
    int centerX = this.width / 2;
    // Compute content height using row spacing
    int rows = 0;
    if (labelField != null) {
      rows++;
    }
    if (toggleHudBtn != null) {
      rows++;
    }
    if (cornerBtn != null) {
      rows++;
    }
    if (toastBtn != null) {
      rows++;
    }
    if (saveBtn != null) {
      rows++;
    }
    // + Cancel shares row with Save (same y)
    contentHeight = rows * ROW_SPACING;
    int viewport = this.height - 2 * MARGIN;
    int startY;
    if (contentHeight <= viewport) {
      // Center vertically
      startY = (this.height - contentHeight) / 2;
      scroll = 0;
    } else {
      startY = MARGIN - scroll;
    }
    int y = startY;
    if (labelField != null) {
      labelField.setX(centerX - 100);
      labelField.setY(y);
      y += ROW_SPACING;
    }
    if (toggleHudBtn != null) {
      toggleHudBtn.setX(centerX - 100);
      toggleHudBtn.setY(y);
      y += ROW_SPACING;
    }
    if (cornerBtn != null) {
      cornerBtn.setX(centerX - 100);
      cornerBtn.setY(y);
      y += ROW_SPACING;
    }
    if (toastBtn != null) {
      toastBtn.setX(centerX - 100);
      toastBtn.setY(y);
      y += ROW_SPACING;
    }
    // Moon feature removed
    if (saveBtn != null && cancelBtn != null) {
      saveBtn.setX(centerX - 100);
      saveBtn.setY(y);
      cancelBtn.setX(centerX + 5);
      cancelBtn.setY(y);
    }
  }
}
