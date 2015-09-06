package romelo333.notenoughwands;


import cpw.mods.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Items.*;
import romelo333.notenoughwands.blocks.LightBlock;

public class ModBlocks {
    public static LightBlock lightBlock;
    public static void init() {
        lightBlock = new LightBlock();
        GameRegistry.registerBlock(lightBlock, "lightBlock");
    }
}
