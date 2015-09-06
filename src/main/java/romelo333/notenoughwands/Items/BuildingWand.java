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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.varia.Coordinate;
import romelo333.notenoughwands.varia.Tools;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class BuildingWand extends GenericWand{
    public BuildingWand() {
        setup("BuildingWand", "buildingWand").xpUsage(10).availability(AVAILABILITY_ADVANCED).loot(3);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
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
        for (Coordinate coordinate : coordinates) {
            if (!checkUsage(stack, player, 1.0f)) {
                return;
            }
            if (Tools.consumeInventoryItem(Item.getItemFromBlock(block), meta, player.inventory)) {
                Tools.playSound(world, block.stepSound.getBreakSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                world.setBlock(coordinate.getX(), coordinate.getY(), coordinate.getZ(), block, meta, 2);
                player.openContainer.detectAndSendChanges();
                registerUsage(stack, player, 1.0f);
            } else {
                notenough = true;
            }
        }
        if (notenough) {
            Tools.error(player, "You don't have the right block");
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityClientPlayerMP player, ItemStack wand) {
        MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver != null) {
            Block block = player.worldObj.getBlock(mouseOver.blockX, mouseOver.blockY, mouseOver.blockZ);
            if (block != null && block.getMaterial() != Material.air) {
                int meta = player.worldObj.getBlockMetadata(mouseOver.blockX, mouseOver.blockY, mouseOver.blockZ);
                Set<Coordinate> coordinates = findSuitableBlocks(wand, player.worldObj, mouseOver.sideHit, mouseOver.blockX, mouseOver.blockY, mouseOver.blockZ, block, meta);
                renderOutlines(evt, player, coordinates);
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
