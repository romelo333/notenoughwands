package romelo333.notenoughwands;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import romelo333.notenoughwands.Items.SwappingWand;

public class ModCrafting {
    public static void init (){
        GameRegistry.addRecipe(new ItemStack(ModItems.wandCore),"bn ","nbn"," nb",'b', Items.blaze_rod,'n', Items.gold_nugget);
        ModItems.swappingWand.setupCrafting();
    }
}
