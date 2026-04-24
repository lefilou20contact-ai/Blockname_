package dev.dominique.blockname.ui;

import dev.dominique.blockname.config.BlockNameConfig;
import dev.dominique.blockname.scan.BlockInfo;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class BlockNameHud {

    private static BlockInfo currentInfo  = null;
    private static boolean   visible      = false;
    private static float     alpha        = 0f;

    private static final float FADE_IN    = 0.10f;
    private static final float FADE_OUT   = 0.06f;
    private static final int   PAD        = 8;
    private static final int   LINE_H     = 11;
    private static final int   HUD_W      = 230;

    // ── Enregistrement ───────────────────────────────────────────────────────

    public static void register() {
        HudRenderCallback.EVENT.register(BlockNameHud::onHudRender);
    }

    public static void show(BlockInfo info) {
        currentInfo = info;
        visible = true;
    }

    public static void hide() {
        visible = false;
    }

    // ── Rendu principal ──────────────────────────────────────────────────────

    private static void onHudRender(DrawContext ctx, RenderTickCounter tick) {
        if (!BlockNameConfig.data.scan.enabled) return;
        if (currentInfo == null) return;

        // Animations
        float speed = BlockNameConfig.data.ui.animationSpeed;
        if (BlockNameConfig.data.ui.animations) {
            if (visible) alpha = Math.min(1f, alpha + FADE_IN * speed);
            else         alpha = Math.max(0f, alpha - FADE_OUT * speed);
        } else {
            alpha = visible ? 1f : 0f;
        }

        if (alpha <= 0.01f) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        boolean dark    = "dark".equals(BlockNameConfig.data.ui.theme);
        boolean compact = "compact".equals(BlockNameConfig.data.ui.mode);

        int x = BlockNameConfig.data.ui.positionX;
        int y = BlockNameConfig.data.ui.positionY;

        if (compact) renderCompact(ctx, mc, x, y, dark);
        else         renderFull(ctx, mc, x, y, dark);
    }

    // ── Mode compact ─────────────────────────────────────────────────────────

    private static void renderCompact(DrawContext ctx, MinecraftClient mc, int x, int y, boolean dark) {
        int textColor = dark ? 0xFFFFFF : 0x111111;
        String label  = "§b" + currentInfo.blockName + " §7| " + currentInfo.requiredTool;
        int w = mc.textRenderer.getWidth(label) + PAD * 2;
        int h = LINE_H + PAD;

        drawBackground(ctx, x, y, w, h, dark);
        ctx.drawText(mc.textRenderer, label, x + PAD, y + PAD / 2 + 1, applyAlpha(textColor), true);
    }

    // ── Mode large ───────────────────────────────────────────────────────────

    private static void renderFull(DrawContext ctx, MinecraftClient mc, int x, int y, boolean dark) {
        BlockNameConfig.ScanConfig  s = BlockNameConfig.data.scan;
        BlockNameConfig.DebugConfig d = BlockNameConfig.data.debug;

        // Compter les lignes
        int lines = 2; // nom + outil
        if (s.showHardness)                                         lines++;
        if (s.showSilkTouch  && currentInfo.requiresSilkTouch)     lines++;
        if (s.showFortune    && currentInfo.fortuneApplies)         lines++;
        if (d.enabled) {
            if (d.showBlockId         && !currentInfo.blockId.isEmpty())         lines++;
            if (d.showLootTable       && !currentInfo.lootTable.isEmpty())       lines++;
            if (d.showBlockProperties && !currentInfo.blockProperties.isEmpty()) lines++;
            if (d.showFullTags        && !currentInfo.tags.isEmpty())            lines++;
        }

        int h = PAD * 2 + lines * LINE_H + (lines - 1) * 2;

        drawBackground(ctx, x, y, HUD_W, h, dark);

        // Barre accent gauche
        ctx.fill(x, y, x + 3, y + h, applyAlpha(0x00CFFF));

        int textColor  = dark ? 0xFFFFFF : 0x111111;
        int mutedColor = dark ? 0xAAAAAA : 0x555555;
        int ty = y + PAD;
        int tx = x + 10;

        // Nom du bloc
        ctx.drawText(mc.textRenderer, "§b§l" + currentInfo.blockName, tx, ty, applyAlpha(0x00CFFF), true);
        ty += LINE_H + 2;

        // Outil
        ctx.drawText(mc.textRenderer, currentInfo.requiredTool, tx, ty, applyAlpha(textColor), true);
        ty += LINE_H + 2;

        // Dureté & résistance
        if (s.showHardness) {
            String line = "§7Dureté §f" + currentInfo.hardness
                    + "  §7Résistance §f" + currentInfo.blastResistance;
            ctx.drawText(mc.textRenderer, line, tx, ty, applyAlpha(textColor), true);
            ty += LINE_H + 2;
        }

        // Silk Touch
        if (s.showSilkTouch && currentInfo.requiresSilkTouch) {
            ctx.drawText(mc.textRenderer, "§d✦ Silk Touch requis", tx, ty, applyAlpha(0xFF88FF), true);
            ty += LINE_H + 2;
        }

        // Fortune
        if (s.showFortune && currentInfo.fortuneApplies) {
            ctx.drawText(mc.textRenderer, "§e✦ Fortune applicable", tx, ty, applyAlpha(0xFFDD55), true);
            ty += LINE_H + 2;
        }

        // ── Section debug ────────────────────────────────────────────────────
        if (d.enabled) {
            if (d.showBlockId && !currentInfo.blockId.isEmpty()) {
                ctx.drawText(mc.textRenderer, "§8ID: " + currentInfo.blockId,
                        tx, ty, applyAlpha(mutedColor), true);
                ty += LINE_H + 2;
            }
            if (d.showLootTable && !currentInfo.lootTable.isEmpty()) {
                ctx.drawText(mc.textRenderer, "§8Loot: " + currentInfo.lootTable,
                        tx, ty, applyAlpha(mutedColor), true);
                ty += LINE_H + 2;
            }
            if (d.showBlockProperties && !currentInfo.blockProperties.isEmpty()) {
                String props = currentInfo.blockProperties;
                if (props.length() > 40) props = props.substring(0, 40) + "…";
                ctx.drawText(mc.textRenderer, "§8Props: " + props,
                        tx, ty, applyAlpha(mutedColor), true);
                ty += LINE_H + 2;
            }
            if (d.showFullTags && !currentInfo.tags.isEmpty()) {
                ctx.drawText(mc.textRenderer, "§8Tags: " + currentInfo.tags,
                        tx, ty, applyAlpha(mutedColor), true);
            }
        }
    }

    // ── Utilitaires graphiques ────────────────────────────────────────────────

    private static void drawBackground(DrawContext ctx, int x, int y, int w, int h, boolean dark) {
        int baseColor   = dark ? 0x1A1A2E : 0xF5F5F5;
        int opacity     = (int)(alpha * BlockNameConfig.data.ui.glassOpacity * 200);
        int bgColor     = (opacity << 24) | baseColor;
        int borderColor = applyAlpha(dark ? 0x2A2A4A : 0xCCCCCC);

        // Ombre
        ctx.fill(x + 2, y + 2, x + w + 2, y + h + 2, applyAlpha(0x000000) & 0x44FFFFFF);
        // Fond
        ctx.fill(x, y, x + w, y + h, bgColor);
        // Bordure fine
        ctx.fill(x,     y,     x + w, y + 1, borderColor);
        ctx.fill(x,     y + h - 1, x + w, y + h, borderColor);
        ctx.fill(x,     y,     x + 1, y + h, borderColor);
        ctx.fill(x + w - 1, y, x + w, y + h, borderColor);
    }

    private static int applyAlpha(int rgb) {
        int a = (int)(alpha * 255) & 0xFF;
        return (a << 24) | (rgb & 0x00FFFFFF);
    }
}
