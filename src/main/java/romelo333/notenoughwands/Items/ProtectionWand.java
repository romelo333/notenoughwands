package romelo333.notenoughwands.Items;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import romelo333.notenoughwands.ProtectedBlocks;

public class ProtectionWand extends GenericWand{
    public ProtectionWand() {
        setup("ProtectionWand", "protectionWand").xpUsage(10).availability(AVAILABILITY_ADVANCED).loot(4);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            if (player.isSneaking()) {
                protectedBlocks.unprotect(world,x,y,z);
            } else {
                protectedBlocks.protect(world,x,y,z);
            }
        }
        return true;
    }
}
