package dev.dominique.blockname.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Écran de configuration in-game pour BlockName.
 * Affiché via ModMenu ou directement depuis le menu pause.
 */
public class BlockNameConfigScreen extends Screen {

    private final Screen parent;
    private int currentTab = 0; // 0=Scan 1=UI 2=Debug 3=Perf

    private static final int TAB_COUNT  = 4;
    private static final int TAB_W      = 80;
    private static final int TAB_H      = 20;
    private static final int PANEL_X    = 20;
    private static final int PANEL_Y    = 50;
    private static final int ROW_H      = 24;

    public BlockNameConfigScreen(Screen parent) {
        super(Text.translatable("blockname.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        addWidgets();
    }

    private void addWidgets() {
        clearChildren();

        String[] tabKeys = {
            "blockname.config.scan",
            "blockname.config.ui",
            "blockname.config.debug",
            "blockname.config.performance"
        };

        // ── Onglets ──────────────────────────────────────────────────────────
        int totalTabW = TAB_COUNT * (TAB_W + 4);
        int startX = (this.width - totalTabW) / 2;

        for (int i = 0; i < TAB_COUNT; i++) {
            final int tab = i;
            int tx = startX + i * (TAB_W + 4);
            addDrawableChild(ButtonWidget.builder(
                    Text.translatable(tabKeys[i]),
                    btn -> { currentTab = tab; init(); }
            ).dimensions(tx, 25, TAB_W, TAB_H).build());
        }

        // ── Bouton Fermer ────────────────────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(
                Text.literal("✔ Sauvegarder & Fermer"),
                btn -> {
                    BlockNameConfig.save();
                    this.client.setScreen(parent);
                }
        ).dimensions(this.width / 2 - 80, this.height - 30, 160, 20).build());

        // ── Widgets de l'onglet actif ────────────────────────────────────────
        int y = PANEL_Y + 10;
        switch (currentTab) {
            case 0 -> y = buildScanTab(y);
            case 1 -> y = buildUiTab(y);
            case 2 -> y = buildDebugTab(y);
            case 3 -> y = buildPerfTab(y);
        }
    }

    // ── Onglet Scan ──────────────────────────────────────────────────────────

    private int buildScanTab(int y) {
        BlockNameConfig.ScanConfig s = BlockNameConfig.data.scan;

        y = addToggle(y, "blockname.scan.enabled",          s.enabled,             v -> { s.enabled = v; init(); });
        y = addToggle(y, "blockname.scan.show_tool",        s.showToolRequired,    v -> s.showToolRequired = v);
        y = addToggle(y, "blockname.scan.show_silk_touch",  s.showSilkTouch,       v -> s.showSilkTouch = v);
        y = addToggle(y, "blockname.scan.show_fortune",     s.showFortune,         v -> s.showFortune = v);
        y = addToggle(y, "blockname.scan.show_hardness",    s.showHardness,        v -> s.showHardness = v);
        y = addToggle(y, "blockname.scan.multiplayer_disabled", s.multiplayerDisabled, v -> s.multiplayerDisabled = v);
        return y;
    }

    // ── Onglet UI ────────────────────────────────────────────────────────────

    private int buildUiTab(int y) {
        BlockNameConfig.UiConfig u = BlockNameConfig.data.ui;

        // Theme toggle
        y = addButton(y,
                "blockname.ui.theme",
                "dark".equals(u.theme) ? "blockname.ui.theme.dark" : "blockname.ui.theme.light",
                btn -> { u.theme = "dark".equals(u.theme) ? "light" : "dark"; init(); });

        // Mode toggle
        y = addButton(y,
                "blockname.ui.mode",
                "large".equals(u.mode) ? "blockname.ui.mode.large" : "blockname.ui.mode.compact",
                btn -> { u.mode = "large".equals(u.mode) ? "compact" : "large"; init(); });

        y = addToggle(y, "blockname.ui.animations", u.animations, v -> u.animations = v);
        return y;
    }

    // ── Onglet Debug ─────────────────────────────────────────────────────────

    private int buildDebugTab(int y) {
        BlockNameConfig.DebugConfig d = BlockNameConfig.data.debug;

        y = addToggle(y, "blockname.debug.enabled",          d.enabled,             v -> { d.enabled = v; init(); });
        y = addToggle(y, "blockname.debug.show_hitbox",      d.showHitbox,          v -> d.showHitbox = v);
        y = addToggle(y, "blockname.debug.show_tags",        d.showFullTags,        v -> d.showFullTags = v);
        y = addToggle(y, "blockname.debug.show_loot_table",  d.showLootTable,       v -> d.showLootTable = v);
        y = addToggle(y, "blockname.debug.show_properties",  d.showBlockProperties, v -> d.showBlockProperties = v);
        y = addToggle(y, "blockname.debug.show_id",          d.showBlockId,         v -> d.showBlockId = v);
        return y;
    }

    // ── Onglet Performance ───────────────────────────────────────────────────

    private int buildPerfTab(int y) {
        BlockNameConfig.PerformanceConfig p = BlockNameConfig.data.performance;

        y = addToggle(y, "blockname.perf.cache", p.cacheEnabled, v -> p.cacheEnabled = v);
        return y;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private int addToggle(int y, String labelKey, boolean value, java.util.function.Consumer<Boolean> onChange) {
        String status = value ? "§a✔ ON" : "§c✘ OFF";
        addDrawableChild(ButtonWidget.builder(
                Text.translatable(labelKey).append(" : " + status),
                btn -> { onChange.accept(!value); init(); }
        ).dimensions(PANEL_X, y, 240, 18).build());
        return y + ROW_H;
    }

    private int addButton(int y, String labelKey, String valueKey, ButtonWidget.PressAction action) {
        addDrawableChild(ButtonWidget.builder(
                Text.translatable(labelKey).append(" : ").append(Text.translatable(valueKey)),
                action
        ).dimensions(PANEL_X, y, 240, 18).build());
        return y + ROW_H;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Titre
        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.translatable("blockname.config.title"),
                this.width / 2, 8, 0x00CFFF
        );

        // Bordure panneau
        context.fill(PANEL_X - 2, PANEL_Y - 2,
                this.width - PANEL_X + 2, this.height - 40, 0x55000020);
        context.fill(PANEL_X - 2, PANEL_Y - 2,
                this.width - PANEL_X + 2, PANEL_Y - 1, 0xFF00CFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        BlockNameConfig.save();
        this.client.setScreen(parent);
    }
}
