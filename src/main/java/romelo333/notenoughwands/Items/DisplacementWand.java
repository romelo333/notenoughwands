package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.varia.Coordinate;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisplacementWand extends GenericWand{

    private float maxHardness = 50;

    public static final int MODE_FIRST = 0;
    public static final int MODE_3X3 = 0;
    public static final int MODE_5X5 = 1;
    public static final int MODE_7X7 = 2;
    public static final int MODE_SINGLE = 3;
    public static final int MODE_LAST = MODE_SINGLE;

    public static final String[] descriptions = new String[] {
            "3x3", "5x5", "7x7", "single"
    };

    public static final int[] amount = new int[] { 9, 9, 25, 1 };

    public DisplacementWand() {
        setup("DisplacementWand", "displacementWand").xpUsage(4).availability(AVAILABILITY_NORMAL).loot(3);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        list.add(EnumChatFormatting.GREEN + "Mode: " + descriptions[getMode(stack)]);
        list.add("Right click to push blocks forward.");
        list.add("Sneak right click to pull blocks.");
        list.add("Mode key (default '=') to switch mode.");
    }

    @Override
    public void toggleMode(EntityPlayer player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, "Switched to " + descriptions[mode] + " mode");
        Tools.getTagCompound(stack).setInteger("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("mode");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                pullBlocks(stack, player, world, x, y, z, side);
            } else {
                pushBlocks(stack, player, world, x, y, z, side);
            }
            return true;
        }
        return false;
    }

    private void pullBlocks(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<Coordinate> coordinates = findSuitableBlocks(stack, world, side, x, y, z);
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        int cnt = moveBlocks(player, world, coordinates, direction);
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private void pushBlocks(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<Coordinate> coordinates = findSuitableBlocks(stack, world, side, x, y, z);
        ForgeDirection direction = ForgeDirection.getOrientation(side).getOpposite();
        int cnt = moveBlocks(player, world, coordinates, direction);
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private int moveBlocks(EntityPlayer player, World world, Set<Coordinate> coordinates, ForgeDirection direction) {
        int cnt = 0;
        for (Coordinate coordinate : coordinates) {
            int xx = coordinate.getX();
            int yy = coordinate.getY();
            int zz = coordinate.getZ();
            Block block = world.getBlock(xx, yy, zz);
            Coordinate otherC = coordinate.add(direction);
            Block otherBlock = world.getBlock(otherC.getX(), otherC.getY(), otherC.getZ());
            if (otherBlock.isReplaceable(world, otherC.getX(), otherC.getY(), otherC.getZ())) {
                double cost = GenericWand.checkPickup(player, world, xx, yy, zz, block, maxHardness, ModItems.movingWand.blacklisted);
                if (cost >= 0.0) {
                    cnt++;
                    int meta = world.getBlockMetadata(xx, yy, zz);
                    Tools.playSound(world, block.stepSound.getBreakSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                    TileEntity tileEntity = world.getTileEntity(xx, yy, zz);
                    NBTTagCompound tc = null;
                    if (tileEntity != null) {
                        tc = new NBTTagCompound();
                        tileEntity.writeToNBT(tc);
                        world.removeTileEntity(xx, yy, zz);
                    }
                    world.setBlockToAir(xx, yy, zz);

                    xx = otherC.getX();
                    yy = otherC.getY();
                    zz = otherC.getZ();
                    world.setBlock(xx, yy, zz, block, meta, 3);
                    world.setBlockMetadataWithNotify(xx, yy, zz, meta, 3);
                    if (tc != null) {
                        tileEntity = world.getTileEntity(xx, yy, zz);
                        if (tileEntity != null) {
                            tc.setInteger("x", xx);
                            tc.setInteger("y", yy);
                            tc.setInteger("z", zz);
                            tileEntity.readFromNBT(tc);
                            tileEntity.markDirty();
                            world.markBlockForUpdate(xx, yy, zz);
                        }
                    }
                }
            }
        }
        return cnt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityClientPlayerMP player, ItemStack wand) {
        MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver != null) {
            World world = player.worldObj;
            int x = mouseOver.blockX;
            int y = mouseOver.blockY;
            int z = mouseOver.blockZ;
            Block block = world.getBlock(x, y, z);
            if (block != null && block.getMaterial() != Material.air) {
                Set<Coordinate> coordinates = findSuitableBlocks(wand, world, mouseOver.sideHit, x, y, z);
                renderOutlines(evt, player, coordinates, 200, 230, 180);
            }
        }
    }

    private Set<Coordinate> findSuitableBlocks(ItemStack stack, World world, int sideHit, int x, int y, int z) {
        Set<Coordinate> coordinates = new HashSet<Coordinate>();
        int mode = getMode(stack);
        int dim = 0;
        switch (mode) {
            case MODE_SINGLE:
                coordinates.add(new Coordinate(x, y, z));
                return coordinates;
            case MODE_3X3:
                dim = 1;
                break;
            case MODE_5X5:
                dim = 2;
                break;
            case MODE_7X7:
                dim = 3;
                break;
        }
        switch (ForgeDirection.getOrientation(sideHit)) {
            case UP:
            case DOWN:
                for (int dx = x - dim; dx <= x + dim; dx++) {
                    for (int dz = z - dim; dz <= z + dim; dz++) {
                        checkAndAddBlock(world, dx, y, dz, coordinates);
                    }
                }
                break;
            case SOUTH:
            case NORTH:
                for (int dx = x - dim; dx <= x + dim; dx++) {
                    for (int dy = y - dim; dy <= y + dim; dy++) {
                        checkAndAddBlock(world, dx, dy, z, coordinates);
                    }
                }
                break;
            case EAST:
            case WEST:
                for (int dy = y - dim; dy <= y + dim; dy++) {
                    for (int dz = z - dim; dz <= z + dim; dz++) {
                        checkAndAddBlock(world, x, dy, dz, coordinates);
                    }
                }
                break;
        }

        return coordinates;
    }

    private void checkAndAddBlock(World world, int x, int y, int z, Set<Coordinate> coordinates) {
        if (!world.isAirBlock(x, y, z)) {
            coordinates.add(new Coordinate(x, y, z));
        }
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "eb ", "bw ", "  w", 'e', Items.ender_pearl, 'b', Items.brick, 'w', wandcore);
    }

}
