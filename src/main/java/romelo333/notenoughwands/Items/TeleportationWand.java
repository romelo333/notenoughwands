package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.NotEnoughWands;

public class TeleportationWand extends GenericWand {

    private float teleportVolume = 1.0f;

    public TeleportationWand() {
        setup("TeleportationWand", "teleportationWand").xpUsage(10).availability(AVAILABILITY_NORMAL).loot(6);

    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        teleportVolume = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_volume", teleportVolume, "Volume of the teleportation sound (set to 0 to disable)").getDouble();
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
                if (teleportVolume >= 0.01) {
                    ((EntityPlayerMP) player).worldObj.playSoundAtEntity(player, NotEnoughWands.MODID + ":teleport", teleportVolume, 1.0f);
                }
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
