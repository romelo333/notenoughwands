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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.varia.Coordinate;
import romelo333.notenoughwands.varia.Tools;

import java.util.*;

public class BuildingWand extends GenericWand{
    public BuildingWand() {
        setup("BuildingWand", "buildingWand").xpUsage(10).availability(AVAILABILITY_ADVANCED).loot(3);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            int cnt = (compound.hasKey("undo1") ? 1 : 0) + (compound.hasKey("undo2") ? 1 : 0);
            list.add(EnumChatFormatting.GREEN + "Has " + cnt + " undo states");
        }
        list.add("Right click to extend blocks in that direction.");
        list.add("Sneak right click on such a block to undo last");
        list.add("two operations.");
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
            if (Tools.consumeInventoryItem(Item.getItemFromBlock(block), meta, player.inventory)) {
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

        Set<Coordinate> undo1 = checkUndo(player, world, x, y, z, undoTag1);
        Set<Coordinate> undo2 = checkUndo(player, world, x, y, z, undoTag2);
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
                world.setBlockToAir(coordinate.getX(), coordinate.getY(), coordinate.getZ());
                cnt++;
            }
        }
        if (cnt > 0) {
            Tools.giveItem(world, player, block, meta, cnt, x, y, z);
            player.openContainer.detectAndSendChanges();
        }
    }

    private Set<Coordinate> checkUndo(EntityPlayer player, World world, int x, int y, int z, NBTTagCompound undoTag) {
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

                    Set<Coordinate> undo1 = checkUndo(player, world, x, y, z, undoTag1);
                    Set<Coordinate> undo2 = checkUndo(player, world, x, y, z, undoTag2);
                    if (undo1 == null && undo2 == null) {
                        return;
                    }

                    if (undo1 != null && undo1.contains(new Coordinate(x, y, z))) {
                        coordinates = undo1;
                        renderOutlines(evt, player, coordinates, 200, 30, 0);
                    } else if (undo2 != null && undo2.contains(new Coordinate(x, y, z))) {
                        coordinates = undo2;
                        renderOutlines(evt, player, coordinates, 200, 30, 0);
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
        findSuitableBlocks(world, coordinates, done, todo, direction, block, meta);

        return coordinates;
    }

    private void findSuitableBlocks(World world, Set<Coordinate> coordinates, Set<Coordinate> done, Deque<Coordinate> todo, ForgeDirection direction, Block block, int meta) {
        while (!todo.isEmpty() && coordinates.size() < 9) {
            Coordinate base = todo.pollFirst();
            if (!done.contains(base)) {
                done.add(base);
                Coordinate offset = base.add(direction);
                if (world.getBlock(base.getX(), base.getY(), base.getZ()) == block && world.getBlockMetadata(base.getX(), base.getY(), base.getZ()) == meta &&
                        world.isAirBlock(offset.getX(), offset.getY(), offset.getZ())) {
                    coordinates.add(offset);
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


    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "bb ", "bw ", "  w", 'b', Items.brick, 'w', wandcore);
    }

}
