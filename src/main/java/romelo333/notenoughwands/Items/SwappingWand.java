package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class SwappingWand extends Item {
    public SwappingWand() {
        setMaxStackSize(1);
    }

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
            ItemStack s = new ItemStack(block,1,meta);
            String name = s.getDisplayName();
            list.add(EnumChatFormatting.BLUE+"Selected block: "+name);
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            if (player.isSneaking()){
                selectBlock(stack, world, x, y, z);
            } else {
                placeBlock(stack, player, world, x, y, z);

            }
        }
        System.out.println("SwappingWand.onItemUse");
        return true;
    }

    private void placeBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null){
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"First select a block by sneaking"));
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
        }else{
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED+"You don't have the right block"));
        }

    }

    private void selectBlock(ItemStack stack, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null){
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        int id = Block.blockRegistry.getIDForObject(block);
        int meta = world.getBlockMetadata(x,y,z);
        tagCompound.setInteger("block",id);
        tagCompound.setInteger("meta",meta);
    }

    public boolean consumeInventoryItem(Item item, int meta, InventoryPlayer inv) {
        int i = this.finditem(item, meta, inv);

        if (i < 0) {
            return false;
        } else {
            if (--inv.mainInventory[i].stackSize <= 0) {
                inv.mainInventory[i] = null;
            }

            return true;
        }
    }

    private int finditem(Item item, int meta, InventoryPlayer inv) {
        for (int i = 0; i < inv.mainInventory.length; ++i) {
            if (inv.mainInventory[i] != null && inv.mainInventory[i].getItem() == item && meta == inv.mainInventory[i].getItemDamage()) {
                return i;
            }
        }

        return -1;
    }

}
