package romelo333.notenoughwands;


import romelo333.notenoughwands.Items.AdvancedWandCore;
import romelo333.notenoughwands.Items.SwappingWand;
import romelo333.notenoughwands.Items.TeleportationWand;
import romelo333.notenoughwands.Items.WandCore;

public class ModItems {
    public static WandCore wandCore;
    public static AdvancedWandCore advancedWandCore;
    public static SwappingWand swappingWand;
    public static TeleportationWand teleportationWand;

    public static void init() {
        wandCore = new WandCore("WandCore", "wandCore");
        advancedWandCore = new AdvancedWandCore("AdvancedWandCore", "advancedWandCore");
        swappingWand = new SwappingWand();
        teleportationWand = new TeleportationWand();
    }
}
