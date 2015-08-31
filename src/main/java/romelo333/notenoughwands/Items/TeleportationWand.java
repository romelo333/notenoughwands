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
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.varia.Tools;

public class TeleportationWand extends GenericWand {

    private float teleportVolume = 1.0f;
    private int maxdist = 30;

    public TeleportationWand() {
        setup("TeleportationWand", "teleportationWand").xpUsage(10).availability(AVAILABILITY_NORMAL).loot(6);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        teleportVolume = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_volume", teleportVolume, "Volume of the teleportation sound (set to 0 to disable)").getDouble();
        maxdist =  cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxdist", maxdist, "Maximum teleportation distance").getInt();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote){
            if (!checkUsage(stack, player, world)){
                return stack;
            }
            Vec3 lookVec = player.getLookVec();
            Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            Vec3 end = start.addVector(lookVec.xCoord * maxdist, lookVec.yCoord * maxdist, lookVec.zCoord * maxdist);
            MovingObjectPosition position = world.rayTraceBlocks(start, end);
            if (position == null){
                player.setPositionAndUpdate(end.xCoord,end.yCoord,end.zCoord);
            }   else {
                int x = position.blockX;
                int y = position.blockY;
                int z = position.blockZ;
                if (world.isAirBlock(x,y+1,z)&&world.isAirBlock(x,y+2,z)){
                    player.setPositionAndUpdate(x,y+1,z);
                }   else {
                    switch (ForgeDirection.getOrientation(position.sideHit)){
                        case DOWN:
                            player.setPositionAndUpdate(x,y-2,z);
                            break;
                        case UP:
                            Tools.error(player,"You will suffocate if you teleport there");
                            return stack;
                        case NORTH:
                            player.setPositionAndUpdate(x,y,z-1);
                            break;
                        case SOUTH:
                            player.setPositionAndUpdate(x,y,z+1);
                            break;
                        case WEST:
                            player.setPositionAndUpdate(x-1,y,z);
                            break;
                        case EAST:
                            player.setPositionAndUpdate(x+1,y,z);
                            break;
                        case UNKNOWN:
                            break;
                    }
                }
            }
            registerUsage(stack, player, world);
            if (teleportVolume >= 0.01) {
                ((EntityPlayerMP) player).worldObj.playSoundAtEntity(player, NotEnoughWands.MODID + ":teleport", teleportVolume, 1.0f);
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
