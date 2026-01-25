package dev.bloks.beautiful_day_counter.client.config;

import dev.bloks.beautiful_day_counter.client.state.ClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private EditBox labelField;
    private Button toggleHudBtn;
    private final Config config;

    public ConfigScreen(Screen parent, Config config) {
        super(Component.literal("Beautiful Day Counter Config"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 3;

        // Label input
        labelField = new EditBox(this.font, centerX - 100, y, 200, 20, Component.literal("Day label"));
        labelField.setMaxLength(32);
        labelField.setValue(config.label);
        addRenderableWidget(labelField);

        y += 30;
        // Toggle HUD button
        toggleHudBtn = Button.builder(Component.literal(hudLabel()), b -> {
            config.hudVisible = !config.hudVisible;
            b.setMessage(Component.literal(hudLabel()));
        }).bounds(centerX - 100, y, 200, 20).build();
        addRenderableWidget(toggleHudBtn);

        y += 30;
        // Save
        addRenderableWidget(Button.builder(Component.literal("Save"), b -> {
            config.label = labelField.getValue();
            config.save();
            // Apply to live state
            var state = ClientState.get();
            state.setDayLabel(config.label);
            if (state.isHudVisible() != config.hudVisible) {
                state.toggleHudVisible();
            }
            Minecraft.getInstance().setScreen(parent);
        }).bounds(centerX - 100, y, 95, 20).build());

        // Cancel
        addRenderableWidget(Button.builder(Component.literal("Cancel"), b ->
                Minecraft.getInstance().setScreen(parent)
        ).bounds(centerX + 5, y, 95, 20).build());
    }

    private String hudLabel() {
        return "HUD: " + (config.hudVisible ? "Visible" : "Hidden");
    }
}

