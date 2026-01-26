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
        // Use a generic system toast type; avoid relying on a specific enum name
        SystemToast.Type[] types = SystemToast.Type.values();
        SystemToast.Type type = types.length > 0 ? types[0] : SystemToast.Type.values()[0];
        SystemToast toast = new SystemToast(type, title, null);
        mc.getToastManager().add(toast);
    }
}
