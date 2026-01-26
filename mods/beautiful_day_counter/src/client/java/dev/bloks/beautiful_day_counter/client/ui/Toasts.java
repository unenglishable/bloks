package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public final class Toasts {
    private Toasts() {}

    public static void showDayToast(long day, String label) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        Text title = Text.literal(label + " " + day);
        // Use a generic system toast type; purely informational
        SystemToast toast = new SystemToast(SystemToast.Type.TUTORIAL_HINT, title, null);
        mc.getToastManager().add(toast);
    }
}
