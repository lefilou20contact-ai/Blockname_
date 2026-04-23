package dev.dominique.blockname.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.dominique.blockname.BlockNameClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class BlockNameConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("blockname.json");

    public static ConfigData data = new ConfigData();

    public static void load() {
        try {
            if (CONFIG_PATH.toFile().exists()) {
                try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                    data = GSON.fromJson(reader, ConfigData.class);
                    if (data == null) data = new ConfigData();
                }
                BlockNameClient.LOGGER.info("[BlockName] Config chargée.");
            } else {
                save();
                BlockNameClient.LOGGER.info("[BlockName] Config créée par défaut.");
            }
        } catch (Exception e) {
            BlockNameClient.LOGGER.error("[BlockName] Erreur de chargement config : ", e);
            data = new ConfigData();
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(data, writer);
        } catch (Exception e) {
            BlockNameClient.LOGGER.error("[BlockName] Erreur de sauvegarde config : ", e);
        }
    }

    // ─── Données de config ───────────────────────────────────────────────────

    public static class ConfigData {
        public ScanConfig scan = new ScanConfig();
        public UiConfig ui = new UiConfig();
        public DebugConfig debug = new DebugConfig();
        public PerformanceConfig performance = new PerformanceConfig();
    }

    public static class ScanConfig {
        public boolean enabled = true;
        public boolean showToolRequired = true;
        public boolean showSilkTouch = true;
        public boolean showFortune = true;
        public boolean showHardness = true;
        public boolean showBlastResistance = true;
        public boolean showTags = false;
        public boolean showLootTable = false;
        public boolean multiplayerDisabled = true;
    }

    public static class UiConfig {
        public String theme = "dark"; // "dark" | "light"
        public String mode = "large"; // "large" | "compact"
        public float glassOpacity = 0.75f;
        public boolean roundedCorners = true;
        public boolean animations = true;
        public float animationSpeed = 1.0f;
        public int positionX = 10;
        public int positionY = 10;
        public float scale = 1.0f;
    }

    public static class DebugConfig {
        public boolean enabled = false;
        public boolean showHitbox = false;
        public boolean showFullTags = true;
        public boolean showLootTable = true;
        public boolean showHardness = true;
        public boolean showBlastResistance = true;
        public boolean showBlockProperties = true;
        public boolean showBlockId = true;
    }

    public static class PerformanceConfig {
        public boolean cacheEnabled = true;
        public int cacheSize = 256;
        public int tickInterval = 2;
    }
}
