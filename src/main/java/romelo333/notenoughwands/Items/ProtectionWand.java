package romelo333.notenoughwands.Items;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.network.PacketGetProtectedBlocks;
import romelo333.notenoughwands.network.PacketHandler;
import romelo333.notenoughwands.network.ReturnProtectedBlocksHelper;
import romelo333.notenoughwands.varia.Coordinate;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProtectionWand extends GenericWand{

    public static final int MODE_FIRST = 0;
    public static final int MODE_PROTECT = 0;
    public static final int MODE_UNPROTECT = 1;
    public static final int MODE_LAST = MODE_UNPROTECT;

    public static int blockShowRadius = 10;

    public static final String[] descriptions = new String[] {
            "protect", "unprotect"
    };

    public ProtectionWand() {
        setup("ProtectionWand", "protectionWand").xpUsage(10).availability(AVAILABILITY_CREATIVE).loot(0);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        blockShowRadius = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_blockShowRadius", blockShowRadius, "How far around the player protected blocks will be hilighted").getInt();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        int mode = getMode(stack);
        list.add(EnumChatFormatting.GREEN + "Mode: " + descriptions[mode]);
        list.add("Rigth click to protect or unprotect a block.");
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

    private static long lastTime = 0;

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityClientPlayerMP player, ItemStack wand) {
        if ((System.currentTimeMillis() - lastTime) > 250) {
            lastTime = System.currentTimeMillis();
            PacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlocks());
        }
        renderOutlines(evt, player, ReturnProtectedBlocksHelper.blocks, 200, 230, 180);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        if (!world.isRemote) {
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            if (getMode(stack) == MODE_PROTECT) {
                if (!checkUsage(stack, player, 1.0f)) {
                    return true;
                }
                protectedBlocks.protect(world,x,y,z);
                registerUsage(stack, player, 1.0f);
            } else {
                protectedBlocks.unprotect(world,x,y,z);
            }
        }
        return true;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "re ", "ew ", "  w", 'r', Items.comparator, 'e', Items.ender_eye, 'w', wandcore);
    }

}
