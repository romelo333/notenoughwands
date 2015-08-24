package romelo333.notenoughwands.varia;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Tools {
    public static void error(EntityPlayer player, String msg) {
        player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + msg));
    }

    public static void notify(EntityPlayer player, String msg) {
        player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + msg));
    }

    public static boolean consumeInventoryItem(Item item, int meta, InventoryPlayer inv) {
        int i = finditem(item, meta, inv);

        if (i < 0) {
            return false;
        } else {
            if (--inv.mainInventory[i].stackSize <= 0) {
                inv.mainInventory[i] = null;
            }

            return true;
        }
    }

    private static int finditem(Item item, int meta, InventoryPlayer inv) {
        for (int i = 0; i < inv.mainInventory.length; ++i) {
            if (inv.mainInventory[i] != null && inv.mainInventory[i].getItem() == item && meta == inv.mainInventory[i].getItemDamage()) {
                return i;
            }
        }

        return -1;
    }

    public static NBTTagCompound getTagCompound(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null){
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        return tagCompound;
    }

    public static String getBlockName(Block block, int meta) {
        ItemStack s = new ItemStack(block,1,meta);
        if (s.getItem() == null) {
            return null;
        }
        return s.getDisplayName();
    }
}
