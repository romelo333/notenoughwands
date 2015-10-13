package romelo333.notenoughwands;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ForgeEventHandlers {
    @SubscribeEvent
    public void onBlockBreakEvent (BlockEvent.BreakEvent event){
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.world);
        if (protectedBlocks.isProtected(event.world,event.x,event.y,event.z)){
            event.setCanceled(true);
        }
    }
}
