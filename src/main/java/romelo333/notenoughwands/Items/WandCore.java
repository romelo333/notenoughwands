package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import romelo333.notenoughwands.NotEnoughWands;

public class WandCore extends Item {
    public WandCore (String name, String texture) {
        setMaxStackSize(1);
        setUnlocalizedName(name);
        setCreativeTab(NotEnoughWands.tabNew);
        setTextureName(NotEnoughWands.MODID + ":" + texture);
        GameRegistry.registerItem(this, name);
    }
}
