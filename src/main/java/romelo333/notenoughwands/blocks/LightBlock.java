package romelo333.notenoughwands.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class LightBlock extends Block{
    public LightBlock() {
        super(Material.portal);
        setHardness(0.5f);
        setResistance(0.5f);
        setHarvestLevel("pickaxe", 0);
        setBlockName("blockLight");
    }

    @Override
    public int getLightValue() {
        return 15;
    }
}
