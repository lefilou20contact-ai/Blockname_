package dev.dominique.blockname.handler;

import dev.dominique.blockname.BlockNameClient;
import dev.dominique.blockname.config.BlockNameConfig;
import dev.dominique.blockname.scan.BlockScanner;
import dev.dominique.blockname.scan.BlockInfo;
import dev.dominique.blockname.ui.BlockNameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ClientTickHandler {

    private static int tickCounter = 0;
    private static BlockInfo lastInfo = null;
    private static boolean scanActive = false;

    public static void onTick(MinecraftClient client) {
        if (!BlockNameConfig.data.scan.enabled) return;
        if (client.world == null || client.player == null) return;

        // Désactivation automatique en multijoueur
        if (BlockNameConfig.data.scan.multiplayerDisabled && !isSinglePlayer(client)) {
            if (scanActive) {
                scanActive = false;
                BlockNameHud.hide();
            }
            return;
        }

        if (!scanActive) return;

        tickCounter++;
        int interval = Math.max(1, BlockNameConfig.data.performance.tickInterval);
        if (tickCounter % interval != 0) return;

        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            BlockNameHud.hide();
            lastInfo = null;
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hit;
        ClientWorld world = client.world;
        PlayerEntity player = client.player;

        BlockInfo info = BlockScanner.scan(world, blockHit.getBlockPos(), player);
        if (info != null && !info.equals(lastInfo)) {
            lastInfo = info;
            BlockNameHud.show(info);
        }
    }

    public static void toggleScan() {
        scanActive = !scanActive;
        BlockNameClient.LOGGER.info("[BlockName] Scan mode : " + (scanActive ? "ON" : "OFF"));
        if (!scanActive) {
            BlockNameHud.hide();
            lastInfo = null;
        }
    }

    public static boolean isScanActive() {
        return scanActive;
    }

    private static boolean isSinglePlayer(MinecraftClient client) {
        return client.isIntegratedServerRunning();
    }
}
