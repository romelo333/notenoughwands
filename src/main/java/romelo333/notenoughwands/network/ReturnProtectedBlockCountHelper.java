package romelo333.notenoughwands.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnProtectedBlockCountHelper {
    public static int count = 0;

    public static void setProtectedBlocks(PacketReturnProtectedBlockCount message) {
        count = message.getCount();
    }
}
