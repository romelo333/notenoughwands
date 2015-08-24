package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class SwappingWand extends GenericWand {

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null){
            list.add(EnumChatFormatting.RED+"No selected block");
        }else{
            int id = compound.getInteger("block");
            Block block = (Block)Block.blockRegistry.getObjectById(id);
            int meta = compound.getInteger("meta");
            String name = getBlockName(block, meta);
            list.add(EnumChatFormatting.GREEN+"Selected block: "+name);
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            if (player.isSneaking()){
                selectBlock(stack, player, world, x, y, z);
            } else {
                placeBlock(stack, player, world, x, y, z);
            }
        }
        return true;
    }

    private void placeBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null){
            error(player, "First select a block by sneaking");
            return;
        }
        int id = tagCompound.getInteger("block");
        Block block = (Block)Block.blockRegistry.getObjectById(id);
        int meta = tagCompound.getInteger("meta");
        if (consumeInventoryItem(Item.getItemFromBlock(block),meta,player.inventory)) {
            Block oldblock = world.getBlock(x, y, z);
            int oldmeta = world.getBlockMetadata(x,y,z);
            player.inventory.addItemStackToInventory(new ItemStack(oldblock,1,oldmeta));
            world.setBlock(x, y, z, block, meta, 2);
            player.openContainer.detectAndSendChanges();
        } else {
            error(player, "You don't have the right block");
        }
    }

    private void selectBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        NBTTagCompound tagCompound = getTagCompound(stack);
        String name = getBlockName(block, meta);
        if (name == null) {
            error(player, "You cannot select this block!");
        } else {
            int id = Block.blockRegistry.getIDForObject(block);
            tagCompound.setInteger("block", id);
            tagCompound.setInteger("meta", meta);
            notify(player, "Selected block: " + name);
        }
    }

}
