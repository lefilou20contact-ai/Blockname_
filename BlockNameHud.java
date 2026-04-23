package dev.dominique.blockname.ui;

import dev.dominique.blockname.config.BlockNameConfig;
import dev.dominique.blockname.scan.BlockInfo;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class BlockNameHud {

    private static BlockInfo currentInfo = null;
    private static boolean visible = false;
    private static float alpha = 0f;
    private static final float FADE_SPEED = 0.08f;

    public static void register() {
        HudRenderCallback.EVENT.register(BlockNameHud::render);
    }

    public static void show(BlockInfo info) {
        currentInfo = info;
        visible = true;
    }

    public static void hide() {
        visible = false;
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!BlockNameConfig.data.scan.enabled) return;
        if (currentInfo == null) return;

        // Fade in/out
        if (visible && alpha < 1f) alpha = Math.min(1f, alpha + FADE_SPEED);
        else if (!visible && alpha > 0f) alpha = Math.max(0f, alpha - FADE_SPEED);
        if (alpha <= 0f) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        boolean darkTheme = "dark".equals(BlockNameConfig.data.ui.theme);
        boolean compact = "compact".equals(BlockNameConfig.data.ui.mode);

        int x = BlockNameConfig.data.ui.positionX;
        int y = BlockNameConfig.data.ui.positionY;

        int bgColor = darkTheme
                ? (int)(alpha * BlockNameConfig.data.ui.glassOpacity * 255) << 24 | 0x1A1A2E
                : (int)(alpha * BlockNameConfig.data.ui.glassOpacity * 255) << 24 | 0xF0F0F0;

        int textColor = darkTheme ? 0xFFFFFF : 0x222222;
        int accentColor = 0x00CFFF;

        if (compact) {
            renderCompact(context, mc, x, y, bgColor, textColor, accentColor);
        } else {
            renderFull(context, mc, x, y, bgColor, textColor, accentColor);
        }
    }

    private static void renderCompact(DrawContext ctx, MinecraftClient mc, int x, int y,
                                       int bg, int text, int accent) {
        String label = "§b" + currentInfo.blockName + " §7| " + currentInfo.requiredTool;
        int w = mc.textRenderer.getWidth(label) + 12;
        int h = 16;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.drawText(mc.textRenderer, label, x + 6, y + 4, text, true);
    }

    private static void renderFull(DrawContext ctx, MinecraftClient mc, int x, int y,
                                    int bg, int text, int accent) {
        int w = 220;
        int lineHeight = 10;
        int padding = 8;

        // Calculer la hauteur dynamique
        int lines = 2; // nom + outil
        if (BlockNameConfig.data.scan.showHardness) lines++;
        if (BlockNameConfig.data.scan.showSilkTouch && currentInfo.requiresSilkTouch) lines++;
        if (BlockNameConfig.data.scan.showFortune && currentInfo.fortuneApplies) lines++;
        if (BlockNameConfig.data.debug.enabled) {
            if (BlockNameConfig.data.debug.showBlockId) lines++;
            if (BlockNameConfig.data.debug.showBlockProperties && !currentInfo.blockProperties.isEmpty()) lines++;
            if (BlockNameConfig.data.debug.showLootTable && !currentInfo.lootTable.isEmpty()) lines++;
            if (BlockNameConfig.data.debug.showFullTags && !currentInfo.tags.isEmpty()) lines++;
        }

        int h = padding * 2 + lines * lineHeight + (lines - 1) * 2;

        // Fond
        ctx.fill(x, y, x + w, y + h, bg);
        // Bordure accent gauche
        ctx.fill(x, y, x + 2, y + h, 0xFF00CFFF);

        int ty = y + padding;

        // Nom du bloc
        ctx.drawText(mc.textRenderer, "§b§l" + currentInfo.blockName, x + 8, ty, accent, true);
        ty += lineHeight + 2;

        // Outil
        ctx.drawText(mc.textRenderer, currentInfo.requiredTool, x + 8, ty, text, true);
        ty += lineHeight + 2;

        // Dureté
        if (BlockNameConfig.data.scan.showHardness) {
            ctx.drawText(mc.textRenderer,
                "§7Dureté : §f" + currentInfo.hardness + "  §7Résistance : §f" + currentInfo.blastResistance,
                x + 8, ty, text, true);
            ty += lineHeight + 2;
        }

        // Silk Touch
        if (BlockNameConfig.data.scan.showSilkTouch && currentInfo.requiresSilkTouch) {
            ctx.drawText(mc.textRenderer, "§d✦ Silk Touch requis", x + 8, ty, 0xFF88FF, true);
            ty += lineHeight + 2;
        }

        // Fortune
        if (BlockNameConfig.data.scan.showFortune && currentInfo.fortuneApplies) {
            ctx.drawText(mc.textRenderer, "§e✦ Fortune applicable", x + 8, ty, 0xFFDD55, true);
            ty += lineHeight + 2;
        }

        // Mode debug
        if (BlockNameConfig.data.debug.enabled) {
            if (BlockNameConfig.data.debug.showBlockId) {
                ctx.drawText(mc.textRenderer, "§8ID: " + currentInfo.blockId, x + 8, ty, 0xAAAAAA, true);
                ty += lineHeight + 2;
            }
            if (BlockNameConfig.data.debug.showLootTable && !currentInfo.lootTable.isEmpty()) {
                ctx.drawText(mc.textRenderer, "§8Loot: " + currentInfo.lootTable, x + 8, ty, 0xAAAAAA, true);
                ty += lineHeight + 2;
            }
            if (BlockNameConfig.data.debug.showFullTags && !currentInfo.tags.isEmpty()) {
                ctx.drawText(mc.textRenderer, "§8Tags: " + currentInfo.tags, x + 8, ty, 0xAAAAAA, true);
            }
        }
    }
}
