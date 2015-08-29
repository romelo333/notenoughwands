package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TeleportationWand extends GenericWand{
    public TeleportationWand() {
        setup("TeleportationWand", "teleportationWand").xpUsage(10).availability(AVAILABILITY_NORMAL).loot(6);

    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote){
            Vec3 lookVec = player.getLookVec();
            Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            Vec3 end = start.addVector(lookVec.xCoord * 30, lookVec.yCoord * 30, lookVec.zCoord * 30);
            MovingObjectPosition position = world.rayTraceBlocks(start, end);
            if (position == null){
                player.setPositionAndUpdate(end.xCoord,end.yCoord,end.zCoord);
            }
        }
        return stack;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this),
                "ee ",
                "ew ",
                "  w",
                'e', Items.ender_pearl, 'w', wandcore);
    }
}
