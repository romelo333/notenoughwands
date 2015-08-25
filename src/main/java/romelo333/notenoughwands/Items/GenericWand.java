package romelo333.notenoughwands.Items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.varia.Tools;

public class GenericWand extends Item {
    protected int needsxp = 10;
    protected int needsrf = 0;
    protected int maxrf = 0;
    protected int maxdurability = 0;
    protected int availability = 2;     // 0 means not available, 1 means available but not craftable, 2 means craftable

    public GenericWand(String name, String texture) {
        if (availability > 0) {
            setMaxStackSize(1);
            setUnlocalizedName(name);
            setCreativeTab(NotEnoughWands.tabNew);
            setTextureName(NotEnoughWands.MODID + ":" + texture);
            GameRegistry.registerItem(this, name);
        }
    }

    public void initConfig(Configuration cfg) {
        needsxp = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
        needsrf = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
        maxrf = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
        maxdurability = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxdurability", maxdurability, "Maximum durability for this wand").getInt();
        availability = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_availability", availability, "Is this wand available? (0=no, 1=yes but not craftable, 2=craftable").getInt();
    }

    protected boolean checkUsage(ItemStack stack, EntityPlayer player, World world) {
        if (needsxp > 0) {
            if (!Tools.addPlayerXP(player, -needsxp)) {
                Tools.error(player, "Not enough experience!");
                return false;
            }
        }
        return true;
    }

    protected void registerUsage(ItemStack stack, EntityPlayer player, World world) {
    }
    public void setupCrafting (){
        if (availability==2){
            setupCraftingInt();
        }
    }
    protected void setupCraftingInt () {

    }
}
