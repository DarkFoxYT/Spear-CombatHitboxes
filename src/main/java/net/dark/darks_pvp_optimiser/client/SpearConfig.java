package net.dark.darks_pvp_optimiser.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class SpearConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("spear_combathitboxes.json");
    private static final SpearConfig INSTANCE = load();

    public int spearColor = 0xffff9000;
    public int meleeColor = 0xffff0000;
    public float lineWidth = 3.0f;
    public boolean accurateHitbox = false;
    public int marginColor = 0xa0ff9000;
    public float marginLineWidth = 1.5f;

    public static SpearConfig get() {
        return INSTANCE;
    }

    private static SpearConfig load() {
        if (!Files.exists(FILE)) return new SpearConfig();
        try (var reader = Files.newBufferedReader(FILE)) {
            SpearConfig config = GSON.fromJson(reader, SpearConfig.class);
            if (config == null) return new SpearConfig();
            config.lineWidth = validWidth(config.lineWidth, 3.0f);
            config.marginLineWidth = validWidth(config.marginLineWidth, 1.5f);
            return config;
        } catch (IOException | RuntimeException exception) {
            LoggerFactory.getLogger("Spear+CombatHitboxes").warn("Could not load spear settings", exception);
            return new SpearConfig();
        }
    }

    private static float validWidth(float value, float fallback) {
        return Float.isFinite(value) ? Math.clamp(value, 0.5f, 10.0f) : fallback;
    }

    public void save() {
        Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(temporary, GSON.toJson(this));
            Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save spear settings", exception);
        }
    }
}
