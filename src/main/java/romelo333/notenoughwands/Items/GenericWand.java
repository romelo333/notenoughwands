package romelo333.notenoughwands.Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GenericWand extends Item {

    public GenericWand() {
        setMaxStackSize(1);
    }

    protected boolean checkUsage(ItemStack stack, EntityPlayer player, World world) {
        return true;
    }

    protected void registerUsage(ItemStack stack, EntityPlayer player, World world) {
    }
}
