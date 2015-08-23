package romelo333.mocs;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.base.ModBase;
import mcjty.gui.GuiStyle;
import mcjty.varia.Logging;
import mcp.mobius.waila.network.NetworkHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;
import romelo333.mocs.proxy.CommonProxy;

import java.io.File;

@Mod(modid = Mocs.MODID, name="Mocs", dependencies =
        "required-after:Forge@["+Mocs.MIN_FORGE_VER+
        ",);required-after:McJtyLib@["+Mocs.MIN_MCJTYLIB_VER+
        ",)",
        version = Mocs.VERSION)
public class Mocs implements ModBase {
    public static final String MODID = "mocs";
    public static final String VERSION = "0.0.1";
    public static final String MIN_FORGE_VER = "10.13.2.1291";
    public static final String MIN_MCJTYLIB_VER = "1.3.0";
    public static final String MIN_ELECCORE_VER = "1.4.130";

    @SidedProxy(clientSide="romelo333.mocs.proxy.ClientProxy", serverSide="romelo333.mocs.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance("deepresonance")
    public static Mocs instance;
    public static Logger logger;
    public static File mainConfigDir;
    public static File modConfigDir;
    public static Configuration config;
    public static NetworkHandler networkHandler;

    public static CreativeTabs tabMocs = new CreativeTabs("Mocs") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.cobblestone);
        }
    };

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        mainConfigDir = e.getModConfigurationDirectory();
        modConfigDir = new File(mainConfigDir.getPath() + File.separator + "deepresonance");
        config = new Configuration(new File(modConfigDir, "main.cfg"));
        proxy.preInit(e);

//        FMLInterModComms.sendMessage("Waila", "register", "mcjty.wailasupport.WailaCompatibility.load");
    }

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        Logging.log("Mocs: server is stopping. Shutting down gracefully");
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void setGuiStyle(EntityPlayerMP entityPlayerMP, GuiStyle guiStyle) {

    }

    @Override
    public GuiStyle getGuiStyle(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    public void openManual(EntityPlayer entityPlayer, int i, String s) {

    }
}
