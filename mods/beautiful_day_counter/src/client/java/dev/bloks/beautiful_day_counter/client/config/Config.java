package dev.bloks.beautiful_day_counter.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "beautiful_day_counter.json";

    @SerializedName("label")
    public String label = "Day";

    @SerializedName("hudVisible")
    public boolean hudVisible = true;

    public static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static Config load() {
        Path path = configPath();
        if (Files.isRegularFile(path)) {
            try (Reader r = Files.newBufferedReader(path)) {
                return GSON.fromJson(r, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Config();
    }

    public void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer w = Files.newBufferedWriter(path)) {
                GSON.toJson(this, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

