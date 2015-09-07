package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import romelo333.notenoughwands.ModBlocks;

import java.util.List;

public class IlluminationWand extends GenericWand {
    public IlluminationWand() {
        setup("IlluminationWand", "illuminationWand").xpUsage(3).availability(AVAILABILITY_NORMAL).loot(6);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        list.add("Right click on block to spawn light.");
        list.add("Right click on light to remove it again.");
    }


    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            Block block = world.getBlock(x, y, z);
            if (block == ModBlocks.lightBlock) {
                world.setBlockToAir(x, y, z);
                return true;
            }

            if (!checkUsage(stack, player, 1.0f)) {
                return true;
            }

            ForgeDirection direction = ForgeDirection.getOrientation(side);
            x = x + direction.offsetX;
            y = y + direction.offsetY;
            z = z + direction.offsetZ;
            world.setBlock(x, y, z, ModBlocks.lightBlock, 0, 3);

            registerUsage(stack, player, 1.0f);
        }
        return true;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "gg ", "gw ", "  w", 'g', Items.glowstone_dust, 'w', wandcore);
    }

}
