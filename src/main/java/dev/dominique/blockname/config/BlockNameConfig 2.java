package dev.dominique.blockname.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.dominique.blockname.BlockNameClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class BlockNameConfig {

    public static final String MOD_VERSION = "1.0.0";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("blockname.json");

    public static ConfigData data = new ConfigData();

    // ─── Load / Save ─────────────────────────────────────────────────────────

    public static void load() {
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
                data = (loaded != null) ? loaded : new ConfigData();
                BlockNameClient.LOGGER.info("[BlockName] Config chargée depuis {}", file.getPath());
            } catch (Exception e) {
                BlockNameClient.LOGGER.error("[BlockName] Erreur config — réinitialisation.", e);
                data = new ConfigData();
                save();
            }
        } else {
            save(); // Crée le fichier par défaut
            BlockNameClient.LOGGER.info("[BlockName] Config créée : {}", file.getPath());
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(data, writer);
        } catch (Exception e) {
            BlockNameClient.LOGGER.error("[BlockName] Impossible de sauvegarder la config.", e);
        }
    }

    // ─── Modèles de données ───────────────────────────────────────────────────

    public static class ConfigData {
        public ScanConfig       scan        = new ScanConfig();
        public UiConfig         ui          = new UiConfig();
        public DebugConfig      debug       = new DebugConfig();
        public PerformanceConfig performance = new PerformanceConfig();
        public I18nConfig        i18n        = new I18nConfig();
    }

    public static class ScanConfig {
        public boolean enabled             = true;
        public boolean showToolRequired    = true;
        public boolean showSilkTouch       = true;
        public boolean showFortune         = true;
        public boolean showHardness        = true;
        public boolean showBlastResistance = true;
        public boolean showTags            = false;
        public boolean showLootTable       = false;
        public boolean multiplayerDisabled = true;
    }

    public static class UiConfig {
        public String  theme          = "dark";   // "dark" | "light"
        public String  mode           = "large";  // "large" | "compact"
        public float   glassOpacity   = 0.75f;
        public boolean roundedCorners = true;
        public boolean animations     = true;
        public float   animationSpeed = 1.0f;
        public int     positionX      = 10;
        public int     positionY      = 10;
        public float   scale          = 1.0f;
    }

    public static class DebugConfig {
        public boolean enabled              = false;
        public boolean showHitbox           = false;
        public boolean showFullTags         = true;
        public boolean showLootTable        = true;
        public boolean showHardness         = true;
        public boolean showBlastResistance  = true;
        public boolean showBlockProperties  = true;
        public boolean showBlockId          = true;
    }

    public static class PerformanceConfig {
        public boolean cacheEnabled = true;
        public int     cacheSize    = 256;
        public int     tickInterval = 2;
    }

    public static class I18nConfig {
        public String language = "auto"; // "auto" = suit la langue de Minecraft
    }
}
