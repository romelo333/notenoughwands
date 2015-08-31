package romelo333.notenoughwands.Items;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class CapturingWand extends GenericWand {
    public CapturingWand() {
        setup("CapturingWand", "capturingWand").xpUsage(10).availability(AVAILABILITY_ADVANCED).loot(3);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("mob")) {
                String type = tagCompound.getString("type");
                String name = null;
                try {
                    name = Class.forName(type).getSimpleName();
                } catch (ClassNotFoundException e) {
                    name = "?";
                }
                list.add(EnumChatFormatting.GREEN + "Captured mob: " + name);
            }
        }
        list.add("Left click on creature to capture it.");
        list.add("Right click on block to respawn creature.");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            NBTTagCompound tagCompound = Tools.getTagCompound(stack);
            if (tagCompound.hasKey("mob")) {
                NBTBase mobCompound = tagCompound.getTag("mob");
                String type = tagCompound.getString("type");
                EntityLivingBase entityLivingBase = createEntity(player, world, type);
                if (entityLivingBase == null) {
                    Tools.error(player, "Something went wrong trying to spawn creature!");
                    return true;
                }
                entityLivingBase.readEntityFromNBT((NBTTagCompound) mobCompound);
                entityLivingBase.setLocationAndAngles(x+.5, y+1, z+.5, 0, 0);
                tagCompound.removeTag("mob");
                tagCompound.removeTag("type");
                world.spawnEntityInWorld(entityLivingBase);
            } else {
                Tools.error(player, "There is no mob captured in this wand!");
            }
        }
        return true;
    }

    private EntityLivingBase createEntity(EntityPlayer player, World world, String type) {
        EntityLivingBase entityLivingBase;
        try {
            entityLivingBase = (EntityLivingBase) Class.forName(type).getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            entityLivingBase = null;
        }
        return entityLivingBase;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.worldObj.isRemote) {
            if (entity instanceof EntityLivingBase) {
                if (Tools.getTagCompound(stack).hasKey("mob")) {
                    Tools.error(player, "There is already a mob in this wand!");
                    return true;
                }
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityLivingBase instanceof EntityPlayer) {
                    Tools.error(player, "I don't think that player would appreciate being captured!");
                    return true;
                }
                if (!checkUsage(stack, player, player.worldObj)) {
                    return true;
                }

                NBTTagCompound tagCompound = new NBTTagCompound();
                entityLivingBase.writeToNBT(tagCompound);
                Tools.getTagCompound(stack).setTag("mob", tagCompound);
                Tools.getTagCompound(stack).setString("type", entity.getClass().getCanonicalName());
                player.worldObj.removeEntity(entity);

                registerUsage(stack, player, player.worldObj);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
