package dev.dominique.blockname.scan;

import dev.dominique.blockname.config.BlockNameConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockScanner {

    // ── Cache LRU ────────────────────────────────────────────────────────────
    private static final Map<String, BlockInfo> CACHE = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, BlockInfo> eldest) {
            return size() > BlockNameConfig.data.performance.cacheSize;
        }
    };

    public static BlockInfo scan(ClientWorld world, BlockPos pos, PlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        String cacheKey  = getCacheKey(state, pos);

        if (BlockNameConfig.data.performance.cacheEnabled) {
            BlockInfo cached = CACHE.get(cacheKey);
            if (cached != null) return cached;
        }

        BlockInfo info = compute(world, pos, state, player);
        if (info != null && BlockNameConfig.data.performance.cacheEnabled) {
            CACHE.put(cacheKey, info);
        }
        return info;
    }

    // ── Calcul principal ─────────────────────────────────────────────────────

    private static BlockInfo compute(ClientWorld world, BlockPos pos,
                                     BlockState state, PlayerEntity player) {
        Block block = state.getBlock();
        if (block == null) return null;

        BlockInfo info = new BlockInfo();
        info.blockId           = getBlockId(state);
        info.blockName         = block.getName().getString();
        info.hardness          = state.getHardness(world, pos);
        info.blastResistance   = block.getBlastResistance();
        info.requiredTool      = resolveRequiredTool(state);
        info.requiresSilkTouch = detectSilkTouch(state);
        info.fortuneApplies    = detectFortune(state);
        info.blockProperties   = state.toString();

        if (BlockNameConfig.data.debug.enabled) {
            info.lootTable = block.getLootTableKey().toString();
            info.tags      = collectTags(state);
        }

        return info;
    }

    // ── Outil requis ─────────────────────────────────────────────────────────

    private static String resolveRequiredTool(BlockState state) {
        if (state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) return "⛏ Pioche en diamant";
        if (state.isIn(BlockTags.NEEDS_IRON_TOOL))    return "⛏ Pioche en fer";
        if (state.isIn(BlockTags.NEEDS_STONE_TOOL))   return "⛏ Pioche en pierre";
        if (state.isIn(BlockTags.AXE_MINEABLE))       return "🪓 Hache";
        if (state.isIn(BlockTags.SHOVEL_MINEABLE))    return "Pelle";
        if (state.isIn(BlockTags.HOE_MINEABLE))       return "Houe";
        if (state.isIn(BlockTags.PICKAXE_MINEABLE))   return "⛏ Pioche";
        return "✋ Main";
    }

    // ── Silk Touch ───────────────────────────────────────────────────────────

    private static boolean detectSilkTouch(BlockState state) {
        Block b = state.getBlock();
        return b instanceof net.minecraft.block.GlassBlock
            || b instanceof net.minecraft.block.TintedGlassBlock
            || b instanceof net.minecraft.block.GlassPaneBlock
            || b instanceof net.minecraft.block.IceBlock
            || b instanceof net.minecraft.block.MushroomBlock
            || b instanceof net.minecraft.block.GrassBlock
            || b instanceof net.minecraft.block.MyceliumBlock;
    }

    // ── Fortune ──────────────────────────────────────────────────────────────

    private static boolean detectFortune(BlockState state) {
        return state.isIn(BlockTags.PICKAXE_MINEABLE)
            && !(state.getBlock() instanceof net.minecraft.block.AbstractBlock);
    }

    // ── Tags (mode debug) ────────────────────────────────────────────────────

    private static String collectTags(BlockState state) {
        StringBuilder sb = new StringBuilder();
        if (state.isIn(BlockTags.PICKAXE_MINEABLE))       sb.append("mineable/pickaxe ");
        if (state.isIn(BlockTags.AXE_MINEABLE))           sb.append("mineable/axe ");
        if (state.isIn(BlockTags.SHOVEL_MINEABLE))        sb.append("mineable/shovel ");
        if (state.isIn(BlockTags.HOE_MINEABLE))           sb.append("mineable/hoe ");
        if (state.isIn(BlockTags.NEEDS_DIAMOND_TOOL))     sb.append("needs_diamond_tool ");
        if (state.isIn(BlockTags.NEEDS_IRON_TOOL))        sb.append("needs_iron_tool ");
        if (state.isIn(BlockTags.NEEDS_STONE_TOOL))       sb.append("needs_stone_tool ");
        if (state.isIn(BlockTags.LOGS))                   sb.append("logs ");
        if (state.isIn(BlockTags.LEAVES))                 sb.append("leaves ");
        if (state.isIn(BlockTags.PLANKS))                 sb.append("planks ");
        if (state.isIn(BlockTags.STONE_ORE_REPLACEABLES)) sb.append("stone_ore_replaceables ");
        return sb.toString().trim();
    }

    // ── Utilitaires ──────────────────────────────────────────────────────────

    private static String getBlockId(BlockState state) {
        return Registries.BLOCK.getId(state.getBlock()).toString();
    }

    private static String getCacheKey(BlockState state, BlockPos pos) {
        return getBlockId(state) + "@" + state.toString();
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
