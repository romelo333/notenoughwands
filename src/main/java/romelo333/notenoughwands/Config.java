package romelo333.notenoughwands;


import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Items.GenericWand;
import romelo333.notenoughwands.ModItems;

public class Config {
    public static String CATEGORY_WANDS = "wands";
    public static String CATEGORY_MOVINGBLACKLIST = "movingblacklist";

    public static void init(Configuration cfg) {
        GenericWand.setupConfig(cfg);
    }
}
