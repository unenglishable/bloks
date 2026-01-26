package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class Toasts {
    private Toasts() {}

    public static void showDayToast(long day, String label) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        Text title = Text.literal(label + " " + day);
        mc.getToastManager().add(new DayToast(title, 3000));
    }
}
