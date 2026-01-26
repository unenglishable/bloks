package dev.bloks.beautiful_day_counter.client.config;

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
    private ButtonWidget moonTexBtn;
    private final Config config;

    public ConfigScreen(Screen parent, Config config) {
        super(Text.literal("Beautiful Day Counter Config"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 3;

        // Label input
        labelField = new TextFieldWidget(this.textRenderer, centerX - 100, y, 200, 20,
                Text.translatable("ui.beautiful_day_counter.label.caption"));
        labelField.setMaxLength(32);
        labelField.setText(config.label);
        // Hint text when empty, similar to Create World "World Name" field
        labelField.setSuggestion(Text.translatable("ui.beautiful_day_counter.label.suggestion").getString());
        addDrawableChild(labelField);

        y += 30;
        // Toggle HUD button
        toggleHudBtn = ButtonWidget.builder(Text.literal(hudLabel()), b -> {
            config.hudVisible = !config.hudVisible;
            b.setMessage(Text.literal(hudLabel()));
        }).dimensions(centerX - 100, y, 200, 20).build();
        addDrawableChild(toggleHudBtn);

        y += 30;
        // Corner selector
        cornerBtn = ButtonWidget.builder(Text.literal(cornerLabel()), b -> {
            config.hudCorner = nextCorner(config.hudCorner);
            b.setMessage(Text.literal(cornerLabel()));
        }).dimensions(centerX - 100, y, 200, 20).build();
        addDrawableChild(cornerBtn);

        y += 30;
        // Toast toggle (placed after HUD settings to keep them grouped)
        toastBtn = ButtonWidget.builder(Text.literal(toastLabel()), b -> {
            config.showToast = !config.showToast;
            b.setMessage(Text.literal(toastLabel()));
        }).dimensions(centerX - 100, y, 200, 20).build();
        addDrawableChild(toastBtn);

        y += 30;
        // Moon texture source toggle
        moonTexBtn = ButtonWidget.builder(Text.literal(moonTextureLabel()), b -> {
            config.useSystemMoonTexture = !config.useSystemMoonTexture;
            b.setMessage(Text.literal(moonTextureLabel()));
        }).dimensions(centerX - 100, y, 200, 20).build();
        addDrawableChild(moonTexBtn);

        y += 30;
        // Save
        addDrawableChild(ButtonWidget.builder(Text.translatable("ui.beautiful_day_counter.save"), b -> {
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
            state.setUseSystemMoonTexture(config.useSystemMoonTexture);
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(centerX - 100, y, 95, 20).build());

        // Cancel
        addDrawableChild(ButtonWidget.builder(Text.translatable("ui.beautiful_day_counter.cancel"), b ->
                MinecraftClient.getInstance().setScreen(parent)
        ).dimensions(centerX + 5, y, 95, 20).build());
    }

    private String hudLabel() {
        return (config.hudVisible
                ? Text.translatable("ui.beautiful_day_counter.hud.visible")
                : Text.translatable("ui.beautiful_day_counter.hud.hidden")).getString();
    }

    private String toastLabel() {
        return (config.showToast
                ? Text.translatable("ui.beautiful_day_counter.toast.enabled")
                : Text.translatable("ui.beautiful_day_counter.toast.disabled")).getString();
    }

    private String moonTextureLabel() {
        return (config.useSystemMoonTexture
                ? Text.translatable("ui.beautiful_day_counter.moon.texture.system")
                : Text.translatable("ui.beautiful_day_counter.moon.texture.fallback")).getString();
    }

    private String cornerLabel() {
        return "HUD Corner: " + switch (config.hudCorner.toLowerCase()) {
            case "top_left" -> "Top Left";
            case "top_right" -> "Top Right";
            case "bottom_left" -> "Bottom Left";
            default -> "Bottom Right";
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

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (labelField != null) {
            int lx = labelField.getX();
            int ly = labelField.getY() - 12;
            context.drawTextWithShadow(this.textRenderer,
                    Text.translatable("ui.beautiful_day_counter.label.caption"), lx, ly, 0xFFFFFFFF);
    }
    }
}
