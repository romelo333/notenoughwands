package romelo333.notenoughwands;


import romelo333.notenoughwands.Items.*;

public class ModItems {
    public static WandCore wandCore;
    public static AdvancedWandCore advancedWandCore;
    public static SwappingWand swappingWand;
    public static TeleportationWand teleportationWand;
    public static CapturingWand capturingWand;

    public static void init() {
        wandCore = new WandCore("WandCore", "wandCore");
        advancedWandCore = new AdvancedWandCore("AdvancedWandCore", "advancedWandCore");
        swappingWand = new SwappingWand();
        teleportationWand = new TeleportationWand();
        capturingWand = new CapturingWand();
    }
}
