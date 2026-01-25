package dev.bloks.beautiful_day_counter.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeautifulDayCounterClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("beautiful_day_counter:client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client init: Beautiful Day Counter");
        // TODO: Register keybinding to toggle HUD on/off
        // TODO: Register packet receiver to show a toast for new day notifications
        // TODO: Register HUD overlay to render subtle day counter when enabled
    }
}

