package romelo333.notenoughwands.Items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.varia.Tools;

import java.util.ArrayList;
import java.util.List;

public class GenericWand extends Item {
    protected int needsxp = 0;
    protected int needsrf = 0;
    protected int maxrf = 0;
    protected int availability = AVAILABILITY_NORMAL;

    public static int AVAILABILITY_NOT = 0;
    public static int AVAILABILITY_CREATIVE = 1;
    public static int AVAILABILITY_ADVANCED = 2;
    public static int AVAILABILITY_NORMAL = 3;

    private static List<GenericWand> wands = new ArrayList<GenericWand>();

    protected GenericWand setup(String name, String texture) {
        if (availability > 0) {
            setMaxStackSize(1);
            setNoRepair();
            setUnlocalizedName(name);
            setCreativeTab(NotEnoughWands.tabNew);
            setTextureName(NotEnoughWands.MODID + ":" + texture);
            GameRegistry.registerItem(this, name);
            wands.add(this);
        }
        return this;
    }

    GenericWand xpUsage(int xp) {
        this.needsxp = xp;
        return this;
    }

    GenericWand rfUsage(int maxrf, int rf) {
        this.maxrf = maxrf;
        this.needsrf = rf;
        return this;
    }

    GenericWand durabilityUsage(int maxdurability) {
        setMaxDamage(maxdurability);
        return this;
    }

    GenericWand availability(int availability) {
        this.availability = availability;
        return this;
    }

    public void initConfig(Configuration cfg) {
        needsxp = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
        needsrf = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
        maxrf = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
        setMaxDamage(cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt());
        availability = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_availability", availability, "Is this wand available? (0=no, 1=not craftable, 2=craftable advanced, 3=craftable normal)").getInt();
    }

    protected boolean checkUsage(ItemStack stack, EntityPlayer player, World world) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        if (needsxp > 0) {
            int experience = Tools.getPlayerXP(player) - needsxp;
            if (experience <= 0) {
                Tools.error(player, "Not enough experience!");
                return false;
            }
        }
        if (isDamageable()) {
            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                Tools.error(player, "This wand can no longer be used!");
                return false;
            }
        }
        return true;
    }

    protected void registerUsage(ItemStack stack, EntityPlayer player, World world) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        if (needsxp > 0) {
            Tools.addPlayerXP(player, -needsxp);
        }
        if (isDamageable()) {
            stack.damageItem(1, player);
        }
    }

    public static void setupCrafting() {
        for (GenericWand wand : wands) {
            if (wand.availability == AVAILABILITY_NORMAL) {
                wand.setupCraftingInt(ModItems.wandCore);
            } else if (wand.availability == AVAILABILITY_ADVANCED) {
                wand.setupCraftingInt(ModItems.advancedWandCore);
            }
        }
    }

    public static void setupConfig(Configuration cfg) {
        for (GenericWand wand : wands) {
            wand.initConfig(cfg);
        }

    }

    protected void setupCraftingInt(Item wandcore) {

    }
}
