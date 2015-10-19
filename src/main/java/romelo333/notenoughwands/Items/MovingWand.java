package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovingWand extends GenericWand {
    private float maxHardness = 50;
    private int placeDistance = 4;

    public Map<String,Double> blacklisted = new HashMap<String, Double>();

    public MovingWand() {
        setup("MovingWand", "movingWand").xpUsage(3).availability(AVAILABILITY_NORMAL).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
        placeDistance = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_placeDistance", placeDistance, "Distance at which to place blocks in 'in-air' mode").getInt();

        ConfigCategory category = cfg.getCategory(Config.CATEGORY_MOVINGBLACKLIST);
        if (category.isEmpty()) {
            // Initialize with defaults
            blacklist(cfg, "tile.shieldBlock");
            blacklist(cfg, "tile.shieldBlock2");
            blacklist(cfg, "tile.shieldBlock3");
            blacklist(cfg, "tile.solidShieldBlock");
            blacklist(cfg, "tile.invisibleShieldBlock");
            setCost(cfg, "tile.mobSpawner", 5.0);
            setCost(cfg, "tile.blockAiry", 20.0);
        } else {
            for (Map.Entry<String, Property> entry : category.entrySet()) {
                blacklisted.put(entry.getKey(), entry.getValue().getDouble());
            }
        }
    }

    private void blacklist(Configuration cfg, String name) {
        setCost(cfg, name, -1.0);
    }

    private void setCost(Configuration cfg, String name, double cost) {
        cfg.get(Config.CATEGORY_MOVINGBLACKLIST, name, cost);
        blacklisted.put(name, cost);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (!hasBlock(compound)) {
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

    private boolean hasBlock(NBTTagCompound compound) {
        return compound != null && compound.hasKey("block");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                Vec3 lookVec = player.getLookVec();
                Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                int distance = this.placeDistance;
                Vec3 end = start.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
                MovingObjectPosition position = world.rayTraceBlocks(start, end);
                if (position == null) {
                    place(stack, world, (int) end.xCoord, (int) end.yCoord, (int) end.zCoord, ForgeDirection.UNKNOWN.ordinal());
                }
            }
        }
        return stack;
    }


    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                place(stack, world, x, y, z, side);
            } else {
                pickup(stack, player, world, x, y, z);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        return true;
    }

    private void place(ItemStack stack, World world, int x, int y, int z, int side) {
        int xx = x + ForgeDirection.getOrientation(side).offsetX;
        int yy = y + ForgeDirection.getOrientation(side).offsetY;
        int zz = z + ForgeDirection.getOrientation(side).offsetZ;
        NBTTagCompound tagCompound = stack.getTagCompound();
        int id = tagCompound.getInteger("block");
        Block block = (Block) Block.blockRegistry.getObjectById(id);
        int meta = tagCompound.getInteger("meta");

        world.setBlock(xx, yy, zz, block, meta, 3);
        world.setBlockMetadataWithNotify(xx, yy, zz, meta, 3);
        if (tagCompound.hasKey("tedata")) {
            NBTTagCompound tc = (NBTTagCompound) tagCompound.getTag("tedata");
            TileEntity tileEntity = world.getTileEntity(xx, yy, zz);
            if (tileEntity != null) {
                tc.setInteger("x", xx);
                tc.setInteger("y", yy);
                tc.setInteger("z", zz);
                tileEntity.readFromNBT(tc);
                tileEntity.markDirty();
                world.markBlockForUpdate(xx, yy, zz);
            }
        }

        tagCompound.removeTag("block");
        tagCompound.removeTag("tedata");
        tagCompound.removeTag("meta");
        stack.setTagCompound(tagCompound);
    }

    private void pickup(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        double cost = checkPickup(player, world, x, y, z, block, maxHardness, blacklisted);
        if (cost < 0.0) {
            return;
        }

        if (!checkUsage(stack, player, (float) cost)) {
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

            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
                NBTTagCompound tc = new NBTTagCompound();
                tileEntity.writeToNBT(tc);
                world.removeTileEntity(x, y, z);
                tc.removeTag("x");
                tc.removeTag("y");
                tc.removeTag("z");
                tagCompound.setTag("tedata", tc);
            }
            world.setBlockToAir(x, y, z);

            Tools.notify(player, "You took: " + name);
            registerUsage(stack, player, (float) cost);
        }
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "re ", "ew ", "  w", 'r', Items.redstone, 'e', Items.ender_pearl, 'w', wandcore);
    }
}
