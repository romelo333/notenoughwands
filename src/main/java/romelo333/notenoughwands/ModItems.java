package romelo333.notenoughwands;


import cpw.mods.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Items.SwappingWand;

public class ModItems {
    public static SwappingWand swappingWand;
    public static void init() {
        swappingWand = new SwappingWand();
        swappingWand.setUnlocalizedName("SwappingWand");
        swappingWand.setCreativeTab(NotEnoughWands.tabNew);
        swappingWand.setTextureName(NotEnoughWands.MODID + ":swappingWand");
        GameRegistry.registerItem(swappingWand, "swappingWand");

    }
}
