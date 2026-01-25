package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public final class Toasts {
    private Toasts() {}

    public static void showDayToast(long day, String label) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        Component title = Component.literal(label + " " + day);
        // Use a generic system toast category; purely informational
        SystemToast toast = new SystemToast(SystemToast.SystemToastId.TUTORIAL_HINT, title, null);
        mc.getToasts().addToast(toast);
    }
}

