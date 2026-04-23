package dev.dominique.blockname.scan;

import java.util.Objects;

public class BlockInfo {
    public String blockId = "";
    public String blockName = "";
    public float hardness = 0f;
    public float blastResistance = 0f;
    public boolean requiresSilkTouch = false;
    public boolean fortuneApplies = false;
    public String requiredTool = "✋ Main";
    public String blockProperties = "";
    public String lootTable = "";
    public String tags = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockInfo)) return false;
        BlockInfo that = (BlockInfo) o;
        return Objects.equals(blockId, that.blockId)
                && Objects.equals(blockProperties, that.blockProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockId, blockProperties);
    }
}
