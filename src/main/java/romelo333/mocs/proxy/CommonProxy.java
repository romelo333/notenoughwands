package romelo333.mocs.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public abstract class CommonProxy {

    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
//        mainConfig = DeepResonance.config;
//        readMainConfig();
//        DRMessages.registerNetworkMessages();
//        DRFluidRegistry.preInitFluids();
//        ModItems.init();
//        ModBlocks.init();
//        ModCrafting.init();
//        WorldGen.init();
    }

    private void readMainConfig() {
//        Configuration cfg = mainConfig;
//        try {
//            cfg.load();
//            cfg.addCustomCategoryComment(WorldGenConfiguration.CATEGORY_WORLDGEN, "Configuration for wodlgen");
//            cfg.addCustomCategoryComment(GeneratorConfiguration.CATEGORY_GENERATOR, "Configuration for the generator multiblock");
//            cfg.addCustomCategoryComment(ResonatingCrystalConfiguration.CATEGORY_CRYSTALS, "Configuration for the crystals");
//            cfg.addCustomCategoryComment(RadiationConfiguration.CATEGORY_RADIATION, "Configuration for the radiation");
//            WorldGenConfiguration.init(cfg);
//            GeneratorConfiguration.init(cfg);
//            ResonatingCrystalConfiguration.init(cfg);
//            RadiationConfiguration.init(cfg);
//        } catch (Exception e1) {
//            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
//        } finally {
//            if (mainConfig.hasChanged()) {
//                mainConfig.save();
//            }
//        }
    }

    public void init(FMLInitializationEvent e) {
//        NetworkRegistry.INSTANCE.registerGuiHandler(DeepResonance.instance, new GuiProxy());
//        FMLCommonHandler.instance().bus().register(WorldTickHandler.instance);
//        FMLCommonHandler.instance().bus().register(new RadiationTickEvent());
    }

    public void postInit(FMLPostInitializationEvent e) {
//        if (mainConfig.hasChanged()) {
//            mainConfig.save();
//        }
//        mainConfig = null;
//        WrenchChecker.init();
    }

}
