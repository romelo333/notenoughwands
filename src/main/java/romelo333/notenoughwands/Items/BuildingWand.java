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
        Set<Coordinate> coordinates = findSuitableBlocks(stack, world, side, x, y, z);
        boolean notenough = false;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
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
                Set<Coordinate> coordinates = findSuitableBlocks(wand, player.worldObj, mouseOver.sideHit, mouseOver.blockX, mouseOver.blockY, mouseOver.blockZ);
                renderOutlines(evt, player, coordinates);
            }
        }
    }

    private Set<Coordinate> findSuitableBlocks(ItemStack stack, World world, int sideHit, int x, int y, int z) {
        Set<Coordinate> coordinates = new HashSet<Coordinate>();
        ForgeDirection direction = ForgeDirection.getOrientation(sideHit);
        x = x + direction.offsetX;
        y = y + direction.offsetY;
        z = z + direction.offsetZ;
        coordinates.add(new Coordinate(x,y,z));
        return coordinates;
    }

    private void checkAndAddBlock(World world, int x, int y, int z, Block centerBlock, int centerMeta, Set<Coordinate> coordinates) {
        if (world.getBlock(x, y, z) == centerBlock && world.getBlockMetadata(x, y, z) == centerMeta) {
            coordinates.add(new Coordinate(x, y, z));
        }
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "bb ", "bw ", "  w", 'b', Items.brick, 'w', wandcore);
    }

}
