package romelo333.notenoughwands;


import romelo333.notenoughwands.Items.SwappingWand;

public class ModItems {
    public static SwappingWand swappingWand;

    public static void init() {
        swappingWand = new SwappingWand("SwappingWand", "swappingWand");
    }
}
