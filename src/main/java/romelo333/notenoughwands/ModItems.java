package romelo333.notenoughwands;


import romelo333.notenoughwands.Items.SwappingWand;
import romelo333.notenoughwands.Items.WandCore;

public class ModItems {
    public static WandCore wandCore;
    public static SwappingWand swappingWand;

    public static void init() {
        wandCore = new WandCore("WandCore","wandCore");
        swappingWand = new SwappingWand("SwappingWand", "swappingWand");
    }
}
