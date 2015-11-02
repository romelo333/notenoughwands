package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.varia.Coordinate;
import romelo333.notenoughwands.varia.Tools;

import java.util.*;

public class BuildingWand extends GenericWand{

    public static final int MODE_FIRST = 0;
    public static final int MODE_9 = 0;
    public static final int MODE_9ROW = 1;
    public static final int MODE_25 = 2;
    public static final int MODE_SINGLE = 3;
    public static final int MODE_LAST = MODE_SINGLE;

    public static final String[] descriptions = new String[] {
            "9 blocks", "9 blocks row", "25 blocks", "single"
    };

    public static final int[] amount = new int[] { 9, 9, 25, 1 };

    public BuildingWand() {
        setup("BuildingWand", "buildingWand").xpUsage(4).availability(AVAILABILITY_ADVANCED).loot(3);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            int cnt = (compound.hasKey("undo1") ? 1 : 0) + (compound.hasKey("undo2") ? 1 : 0);
            list.add(EnumChatFormatting.GREEN + "Has " + cnt + " undo states");
            list.add(EnumChatFormatting.GREEN + "Mode: " + descriptions[compound.getInteger("mode")]);
        }
        list.add("Right click to extend blocks in that direction.");
        list.add("Sneak right click on such a block to undo one of");
        list.add("the last two operations.");
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                undoPlaceBlock(stack, player, world, x, y, z);
            } else {
                placeBlock(stack, player, world, x, y, z, side);
            }
        }
        return true;
    }

    private void placeBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        boolean notenough = false;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Set<Coordinate> coordinates = findSuitableBlocks(stack, world, side, x, y, z, block, meta);
        Set<Coordinate> undo = new HashSet<Coordinate>();
        for (Coordinate coordinate : coordinates) {
            if (!checkUsage(stack, player, 1.0f)) {
                break;
            }
            if (Tools.consumeInventoryItem(Item.getItemFromBlock(block), meta, player.inventory,player)) {
                Tools.playSound(world, block.stepSound.getBreakSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                world.setBlock(coordinate.getX(), coordinate.getY(), coordinate.getZ(), block, meta, 2);
                player.openContainer.detectAndSendChanges();
                registerUsage(stack, player, 1.0f);
                undo.add(coordinate);
            } else {
                notenough = true;
            }
        }
        if (notenough) {
            Tools.error(player, "You don't have the right block");
        }

        registerUndo(stack, block, meta, world, undo);
    }

    private void registerUndo(ItemStack stack, Block block, int meta, World world, Set<Coordinate> undo) {
        NBTTagCompound undoTag = new NBTTagCompound();
        undoTag.setInteger("block", Block.blockRegistry.getIDForObject(block));
        undoTag.setInteger("meta", meta);
        undoTag.setInteger("dimension", world.provider.dimensionId);
        int[] undoX = new int[undo.size()];
        int[] undoY = new int[undo.size()];
        int[] undoZ = new int[undo.size()];
        int idx = 0;
        for (Coordinate coordinate : undo) {
            undoX[idx] = coordinate.getX();
            undoY[idx] = coordinate.getY();
            undoZ[idx] = coordinate.getZ();
            idx++;
        }

        undoTag.setIntArray("x", undoX);
        undoTag.setIntArray("y", undoY);
        undoTag.setIntArray("z", undoZ);
        NBTTagCompound wandTag = Tools.getTagCompound(stack);
        if (wandTag.hasKey("undo1")) {
            wandTag.setTag("undo2", wandTag.getTag("undo1"));
        }
        wandTag.setTag("undo1", undoTag);
    }

    private void undoPlaceBlock(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        NBTTagCompound wandTag = Tools.getTagCompound(stack);
        NBTTagCompound undoTag1 = (NBTTagCompound) wandTag.getTag("undo1");
        NBTTagCompound undoTag2 = (NBTTagCompound) wandTag.getTag("undo2");

        Set<Coordinate> undo1 = checkUndo(player, world, undoTag1);
        Set<Coordinate> undo2 = checkUndo(player, world, undoTag2);
        if (undo1 == null && undo2 == null) {
            Tools.error(player, "Nothing to undo!");
            return;
        }

        if (undo1 != null && undo1.contains(new Coordinate(x, y, z))) {
            performUndo(stack, player, world, x, y, z, undoTag1, undo1);
            if (wandTag.hasKey("undo2")) {
                wandTag.setTag("undo1", wandTag.getTag("undo2"));
                wandTag.removeTag("undo2");
            } else {
                wandTag.removeTag("undo1");
            }
            return;
        }
        if (undo2 != null && undo2.contains(new Coordinate(x, y, z))) {
            performUndo(stack, player, world, x, y, z, undoTag2, undo2);
            wandTag.removeTag("undo2");
            return;
        }

        Tools.error(player, "Select at least one block of the area you want to undo!");
    }

    private void performUndo(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, NBTTagCompound undoTag, Set<Coordinate> undo) {
        Block block = (Block) Block.blockRegistry.getObjectById(undoTag.getInteger("block"));
        int meta = undoTag.getInteger("meta");

        int cnt = 0;
        for (Coordinate coordinate : undo) {
            Block testBlock = world.getBlock(coordinate.getX(), coordinate.getY(), coordinate.getZ());
            int testMeta = world.getBlockMetadata(coordinate.getX(), coordinate.getY(), coordinate.getZ());
            if (testBlock == block && testMeta == meta) {
                Tools.playSound(world, block.stepSound.getBreakSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                world.setBlockToAir(coordinate.getX(), coordinate.getY(), coordinate.getZ());
                cnt++;
            }
        }
        if (cnt > 0) {
            if (!player.capabilities.isCreativeMode) {
                Tools.giveItem(world, player, block, meta, cnt, x, y, z);
                player.openContainer.detectAndSendChanges();
            }
        }
    }

    private Set<Coordinate> checkUndo(EntityPlayer player, World world, NBTTagCompound undoTag) {
        if (undoTag == null) {
            return null;
        }
        int dimension = undoTag.getInteger("dimension");
        if (dimension != world.provider.dimensionId) {
            Tools.error(player, "Select at least one block of the area you want to undo!");
            return null;
        }

        int[] undoX = undoTag.getIntArray("x");
        int[] undoY = undoTag.getIntArray("y");
        int[] undoZ = undoTag.getIntArray("z");
        Set<Coordinate> undo = new HashSet<Coordinate>();
        for (int i = 0 ; i < undoX.length ; i++) {
            undo.add(new Coordinate(undoX[i], undoY[i], undoZ[i]));
        }
        return undo;
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
                Set<Coordinate> coordinates;
                int meta = world.getBlockMetadata(x, y, z);

                if (player.isSneaking()) {
                    NBTTagCompound wandTag = Tools.getTagCompound(wand);
                    NBTTagCompound undoTag1 = (NBTTagCompound) wandTag.getTag("undo1");
                    NBTTagCompound undoTag2 = (NBTTagCompound) wandTag.getTag("undo2");

                    Set<Coordinate> undo1 = checkUndo(player, world, undoTag1);
                    Set<Coordinate> undo2 = checkUndo(player, world, undoTag2);
                    if (undo1 == null && undo2 == null) {
                        return;
                    }

                    if (undo1 != null && undo1.contains(new Coordinate(x, y, z))) {
                        coordinates = undo1;
                        renderOutlines(evt, player, coordinates, 240, 30, 0);
                    } else if (undo2 != null && undo2.contains(new Coordinate(x, y, z))) {
                        coordinates = undo2;
                        renderOutlines(evt, player, coordinates, 240, 30, 0);
                    }
                } else {
                    coordinates = findSuitableBlocks(wand, world, mouseOver.sideHit, x, y, z, block, meta);
                    renderOutlines(evt, player, coordinates, 200, 230, 180);
                }
            }
        }
    }

    private Set<Coordinate> findSuitableBlocks(ItemStack stack, World world, int sideHit, int x, int y, int z, Block block, int meta) {
        ForgeDirection direction = ForgeDirection.getOrientation(sideHit);
        Coordinate base = new Coordinate(x, y, z);

        Set<Coordinate> coordinates = new HashSet<Coordinate>();
        Set<Coordinate> done = new HashSet<Coordinate>();
        Deque<Coordinate> todo = new ArrayDeque<Coordinate>();
        todo.addLast(base);
        findSuitableBlocks(world, coordinates, done, todo, direction, block, meta, amount[getMode(stack)], getMode(stack) == MODE_9ROW);

        return coordinates;
    }

    private void findSuitableBlocks(World world, Set<Coordinate> coordinates, Set<Coordinate> done, Deque<Coordinate> todo, ForgeDirection direction, Block block, int meta, int maxAmount,
                                    boolean rowMode) {

        ForgeDirection dirA = null;
        ForgeDirection dirB = null;
        if (rowMode) {
            Coordinate base = todo.getFirst();
            Coordinate offset = base.add(direction);
            dirA = dir1(direction);
            dirB = dirA.getOpposite();
            if (!isSuitable(world, block, meta, base.add(dirA), offset.add(dirA)) ||
                !isSuitable(world, block, meta, base.add(dirB), offset.add(dirB))) {
                dirA = dir2(direction);
                dirB = dirA.getOpposite();
                if (!isSuitable(world, block, meta, base.add(dirA), offset.add(dirA)) ||
                        !isSuitable(world, block, meta, base.add(dirB), offset.add(dirB))) {
                    dirA = dir3(direction);
                    dirB = dirA.getOpposite();
                }
            }
        }

        while (!todo.isEmpty() && coordinates.size() < maxAmount) {
            Coordinate base = todo.pollFirst();
            if (!done.contains(base)) {
                done.add(base);
                Coordinate offset = base.add(direction);
                if (isSuitable(world, block, meta, base, offset)) {
                    coordinates.add(offset);
                    if (rowMode) {
                        todo.addLast(base.add(dirA));
                        todo.addLast(base.add(dirB));
                    } else {
                        todo.addLast(base.add(dir1(direction)));
                        todo.addLast(base.add(dir1(direction).getOpposite()));
                        todo.addLast(base.add(dir2(direction)));
                        todo.addLast(base.add(dir2(direction).getOpposite()));
                        todo.addLast(base.add(dir1(direction)).add(dir2(direction)));
                        todo.addLast(base.add(dir1(direction)).add(dir2(direction).getOpposite()));
                        todo.addLast(base.add(dir1(direction).getOpposite()).add(dir2(direction)));
                        todo.addLast(base.add(dir1(direction).getOpposite()).add(dir2(direction).getOpposite()));
                    }
                }
            }
        }
    }

    private boolean isSuitable(World world, Block block, int meta, Coordinate base, Coordinate offset) {
        Block destBlock = world.getBlock(offset.getX(), offset.getY(), offset.getZ());
        if (destBlock == null) {
            destBlock = Blocks.air;
        }
        return world.getBlock(base.getX(), base.getY(), base.getZ()) == block && world.getBlockMetadata(base.getX(), base.getY(), base.getZ()) == meta &&
                destBlock.isReplaceable(world, offset.getX(), offset.getY(), offset.getZ());
    }

    private ForgeDirection dir1(ForgeDirection direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return ForgeDirection.EAST;
            case NORTH:
            case SOUTH:
                return ForgeDirection.EAST;
            case WEST:
            case EAST:
                return ForgeDirection.DOWN;
        }
        return null;
    }

    private ForgeDirection dir2(ForgeDirection direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return ForgeDirection.SOUTH;
            case NORTH:
            case SOUTH:
                return ForgeDirection.DOWN;
            case WEST:
            case EAST:
                return ForgeDirection.SOUTH;
        }
        return null;
    }

    private ForgeDirection dir3(ForgeDirection direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return ForgeDirection.SOUTH;
            case NORTH:
            case SOUTH:
                return ForgeDirection.WEST;
            case WEST:
            case EAST:
                return ForgeDirection.SOUTH;
        }
        return null;
    }


    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "bb ", "bw ", "  w", 'b', Items.brick, 'w', wandcore);
    }

}
