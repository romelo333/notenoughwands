package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import romelo333.notenoughwands.ProtectedBlocks;

public class ProtectionWand extends GenericWand{
    public ProtectionWand() {
        setup("ProtectionWand", "protectionWand").xpUsage(10).availability(AVAILABILITY_CREATIVE).loot(0);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            if (player.isSneaking()) {
                protectedBlocks.unprotect(world,x,y,z);
            } else {
                if (!checkUsage(stack, player, 1.0f)) {
                    return true;
                }
                protectedBlocks.protect(world,x,y,z);
                registerUsage(stack, player, 1.0f);
            }
        }
        return true;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "re ", "ew ", "  w", 'r', Items.comparator, 'e', Items.ender_eye, 'w', wandcore);
    }

}
