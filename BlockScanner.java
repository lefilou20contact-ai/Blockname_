package dev.dominique.blockname.scan;

import dev.dominique.blockname.config.BlockNameConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockScanner {

    // ─── Cache LRU simple ────────────────────────────────────────────────────
    private static final Map<BlockPos, BlockInfo> CACHE = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<BlockPos, BlockInfo> eldest) {
            return size() > BlockNameConfig.data.performance.cacheSize;
        }
    };

    public static BlockInfo scan(ClientWorld world, BlockPos pos, PlayerEntity player) {
        if (!BlockNameConfig.data.performance.cacheEnabled) {
            return compute(world, pos, player);
        }

        BlockState current = world.getBlockState(pos);
        BlockInfo cached = CACHE.get(pos);
        if (cached != null && cached.blockId.equals(getBlockId(current))) {
            return cached;
        }

        BlockInfo info = compute(world, pos, player);
        if (info != null) CACHE.put(pos.toImmutable(), info);
        return info;
    }

    private static BlockInfo compute(ClientWorld world, BlockPos pos, PlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block == null) return null;

        BlockInfo info = new BlockInfo();
        info.blockId = getBlockId(state);
        info.blockName = block.getName().getString();
        info.hardness = state.getHardness(world, pos);
        info.blastResistance = block.getBlastResistance();
        info.requiresSilkTouch = requiresSilkTouch(state);
        info.fortuneApplies = fortuneApplies(state);
        info.requiredTool = getRequiredTool(state, player);
        info.blockProperties = state.toString();

        if (BlockNameConfig.data.debug.enabled) {
            info.lootTable = block.getLootTableKey().toString();
            info.tags = collectTags(state);
        }

        return info;
    }

    private static String getBlockId(BlockState state) {
        return net.minecraft.registry.Registries.BLOCK
                .getId(state.getBlock()).toString();
    }

    private static String getRequiredTool(BlockState state, PlayerEntity player) {
        if (state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) return "⛏ Pioche en diamant";
        if (state.isIn(BlockTags.NEEDS_IRON_TOOL))    return "⛏ Pioche en fer";
        if (state.isIn(BlockTags.NEEDS_STONE_TOOL))   return "⛏ Pioche en pierre";
        if (state.isIn(BlockTags.AXE_MINEABLE))       return "🪓 Hache";
        if (state.isIn(BlockTags.SHOVEL_MINEABLE))    return "삽 Pelle";
        if (state.isIn(BlockTags.HOE_MINEABLE))       return "🪛 Houe";
        if (state.isIn(BlockTags.PICKAXE_MINEABLE))   return "⛏ Pioche";
        return "✋ Main";
    }

    private static boolean requiresSilkTouch(BlockState state) {
        // Blocs connus nécessitant silk touch
        return state.isIn(BlockTags.NEEDS_TOOL_LEVEL_3) ||
               state.getBlock() instanceof net.minecraft.block.GlassBlock ||
               state.getBlock() instanceof net.minecraft.block.TintedGlassBlock;
    }

    private static boolean fortuneApplies(BlockState state) {
        return state.isIn(BlockTags.PICKAXE_MINEABLE) &&
               !(state.getBlock() instanceof net.minecraft.block.OreBlock);
    }

    private static String collectTags(BlockState state) {
        StringBuilder sb = new StringBuilder();
        // Tags les plus communs
        if (state.isIn(BlockTags.PICKAXE_MINEABLE)) sb.append("minecraft:mineable/pickaxe ");
        if (state.isIn(BlockTags.AXE_MINEABLE)) sb.append("minecraft:mineable/axe ");
        if (state.isIn(BlockTags.SHOVEL_MINEABLE)) sb.append("minecraft:mineable/shovel ");
        if (state.isIn(BlockTags.HOE_MINEABLE)) sb.append("minecraft:mineable/hoe ");
        if (state.isIn(BlockTags.LOGS)) sb.append("minecraft:logs ");
        if (state.isIn(BlockTags.STONE_ORE_REPLACEABLES)) sb.append("minecraft:stone_ore_replaceables ");
        return sb.toString().trim();
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
