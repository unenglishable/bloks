package dev.bloks.beautiful_day_counter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class Toasts {
    private Toasts() {}

    public static void showDayToast(long day, String label) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;
        Text title = Text.literal(label + " " + day);
        // Prevent queuing multiple toasts at once
        var state = dev.bloks.beautiful_day_counter.client.state.ClientState.get();
        if (state.isToastActive()) return;
        state.setToastActive(true);
        mc.getToastManager().add(new DayToast(title, 5000, () -> state.setToastActive(false)));
    }
}
