package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

public class DayToast implements Toast {
    private static final ResourceLocation TOASTS_TEXTURE = new ResourceLocation("textures/gui/toasts.png");
    private static final ItemStack ICON = new ItemStack(Items.CLOCK);
    private final Component title;
    private final long displayMs;

    public DayToast(Component title, long displayMs) {
        this.title = title;
        this.displayMs = displayMs;
    }

    public static void show(long day, String label) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        Component title = Component.literal(label + " " + day);
        mc.getToasts().addToast(new DayToast(title, 3000));
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long time) {
        // Background (160x32) like SystemToast
        graphics.blit(TOASTS_TEXTURE, 0, 0, 0, 32, 160, 32);
        // Icon
        graphics.renderFakeItem(ICON, 6, 8);
        // Text
        graphics.drawString(Minecraft.getInstance().font, title, 30, 12, 0xFFFFFF, false);
        return time >= displayMs ? Visibility.HIDE : Visibility.SHOW;
    }
}

