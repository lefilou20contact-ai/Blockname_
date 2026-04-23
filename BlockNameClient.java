package dev.dominique.blockname;

import dev.dominique.blockname.config.BlockNameConfig;
import dev.dominique.blockname.handler.ClientTickHandler;
import dev.dominique.blockname.handler.KeybindHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class BlockNameClient implements ClientModInitializer {

    public static final String MOD_ID = "blockname";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static KeyBinding toggleScanKey;
    public static KeyBinding toggleDebugKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[BlockName] Initialisation du mod...");

        // Chargement de la config
        BlockNameConfig.load();

        // Enregistrement des keybinds
        toggleScanKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blockname.toggle_scan",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.blockname"
        ));

        toggleDebugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blockname.toggle_debug",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F10,
                "category.blockname"
        ));

        // Tick handler client
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeybindHandler.onTick(client);
            ClientTickHandler.onTick(client);
        });

        LOGGER.info("[BlockName] Mod initialisé avec succès ✔");
    }
}
