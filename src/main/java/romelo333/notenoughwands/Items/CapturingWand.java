package romelo333.notenoughwands.Items;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import romelo333.notenoughwands.varia.Tools;

public class CapturingWand extends GenericWand{
    public CapturingWand() {
        setup("CapturingWand", "capturingWand").xpUsage(10).availability(AVAILABILITY_ADVANCED).loot(3);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote){
            NBTTagCompound tagCompound = Tools.getTagCompound(stack);
            if (tagCompound.hasKey("mob")){
                NBTBase mobCompound = tagCompound.getTag("mob");
                String type = tagCompound.getString("type");
                EntityLivingBase entityLivingBase;
                try {
                    entityLivingBase = (EntityLivingBase) Class.forName(type).getConstructor(World.class).newInstance(world);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                entityLivingBase.readEntityFromNBT((NBTTagCompound)mobCompound);
                entityLivingBase.setLocationAndAngles(x, y, z, 0, 0);
                world.spawnEntityInWorld(entityLivingBase);
            }
        }return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.worldObj.isRemote){
            if (entity instanceof EntityLivingBase){
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                NBTTagCompound tagCompound = new NBTTagCompound();
                entityLivingBase.writeToNBT(tagCompound);
                Tools.getTagCompound(stack).setTag("mob", tagCompound);
                Tools.getTagCompound(stack).setString("type",entity.getClass().getCanonicalName());
                player.worldObj.removeEntity(entity);
            }
        }
        return true;
    }
}
