package dev.dominique.blockname.handler;

import dev.dominique.blockname.BlockNameClient;
import dev.dominique.blockname.config.BlockNameConfig;
import dev.dominique.blockname.scan.BlockInfo;
import dev.dominique.blockname.scan.BlockScanner;
import dev.dominique.blockname.ui.BlockNameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ClientTickHandler {

    private static int     tickCounter = 0;
    private static boolean scanActive  = false;
    private static boolean debugActive = false;
    private static BlockInfo lastInfo  = null;

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

        // Throttle : ne recalcule que tous les N ticks
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

    // ── Contrôles ────────────────────────────────────────────────────────────

    public static void toggleScan() {
        scanActive = !scanActive;
        BlockNameClient.LOGGER.info("[BlockName] Scan : {}", scanActive ? "ON" : "OFF");
        if (!scanActive) { BlockNameHud.hide(); lastInfo = null; }
    }

    public static void toggleDebug() {
        debugActive = !debugActive;
        BlockNameConfig.data.debug.enabled = debugActive;
        BlockNameClient.LOGGER.info("[BlockName] Debug : {}", debugActive ? "ON" : "OFF");
    }

    public static boolean isScanActive()  { return scanActive;  }
    public static boolean isDebugActive() { return debugActive; }

    private static boolean isSinglePlayer(MinecraftClient client) {
        return client.isIntegratedServerRunning();
    }
}
