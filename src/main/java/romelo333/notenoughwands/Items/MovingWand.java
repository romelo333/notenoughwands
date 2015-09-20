package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;
import java.util.List;

public class MovingWand extends GenericWand {
    private float maxHardness = 49;

    public MovingWand() {
        setup("MovingWand", "movingWand").xpUsage(3).availability(AVAILABILITY_NORMAL).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            list.add(EnumChatFormatting.RED + "Wand is empty.");
        } else {
            int id = compound.getInteger("block");
            Block block = (Block) Block.blockRegistry.getObjectById(id);
            int meta = compound.getInteger("meta");
            String name = Tools.getBlockName(block, meta);
            list.add(EnumChatFormatting.GREEN + "Block: " + name);
        }
        list.add("Right click to take a block.");
        list.add("Right click again on block to place it down.");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) {
                pickup(stack, player, world, x, y, z);
            } else {
                place(stack, player, world, x, y, z, side);
            }
        }
        return true;
    }

    private void place(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        int xx = x + ForgeDirection.getOrientation(side).offsetX;
        int yy = y + ForgeDirection.getOrientation(side).offsetY;
        int zz = z + ForgeDirection.getOrientation(side).offsetZ;
        NBTTagCompound tagCompound = stack.getTagCompound();
        int id = tagCompound.getInteger("block");
        Block block = (Block) Block.blockRegistry.getObjectById(id);
        int meta = tagCompound.getInteger("meta");

        world.setBlock(xx, yy, zz, block, meta, 3);
        stack.setTagCompound(null);
    }

    private void pickup(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        if (hardness > maxHardness){
            Tools.error(player,"This block is to hard to take.");
            return;
        }
        if (!block.canEntityDestroy(world,x,y,z,player)){
            Tools.error(player,"You are not allowed to take this block");
            return;
        }
        NBTTagCompound tagCompound = Tools.getTagCompound(stack);
        String name = Tools.getBlockName(block, meta);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            int id = Block.blockRegistry.getIDForObject(block);
            tagCompound.setInteger("block", id);
            tagCompound.setInteger("meta", meta);
            world.setBlockToAir(x, y, z);
            Tools.notify(player, "You took: " + name);
        }
        registerUsage(stack, player, 1.0f);
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "re ", "ew ", "  w", 'r', Items.redstone, 'e', Items.ender_pearl, 'w', wandcore);
    }
}
