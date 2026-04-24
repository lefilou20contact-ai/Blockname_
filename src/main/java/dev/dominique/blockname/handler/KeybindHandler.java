package dev.dominique.blockname.handler;

import dev.dominique.blockname.BlockNameClient;
import net.minecraft.client.MinecraftClient;

public class KeybindHandler {

    public static void onTick(MinecraftClient client) {
        if (client.player == null) return;

        // Touche Scan (F9)
        while (BlockNameClient.toggleScanKey.wasPressed()) {
            ClientTickHandler.toggleScan();
            boolean active = ClientTickHandler.isScanActive();
            client.player.sendMessage(
                    net.minecraft.text.Text.translatable(
                            active ? "blockname.hud.scan_on" : "blockname.hud.scan_off"
                    ),
                    true // actionbar
            );
        }

        // Touche Debug (F10)
        while (BlockNameClient.toggleDebugKey.wasPressed()) {
            ClientTickHandler.toggleDebug();
            boolean active = ClientTickHandler.isDebugActive();
            client.player.sendMessage(
                    net.minecraft.text.Text.translatable(
                            active ? "blockname.hud.debug_on" : "blockname.hud.debug_off"
                    ),
                    true
            );
        }
    }
}
