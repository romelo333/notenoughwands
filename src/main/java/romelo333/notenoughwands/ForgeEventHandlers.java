package romelo333.notenoughwands;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.List;

public class ForgeEventHandlers {
    @SubscribeEvent
    public void onBlockBreakEvent (BlockEvent.BreakEvent event){
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.world);
        if (protectedBlocks.isProtected(event.world,event.x,event.y,event.z)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDetonate(ExplosionEvent.Detonate event) {
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.world);
        if (!protectedBlocks.hasProtections()) {
            return;
        }


        List<ChunkPosition> affectedBlocks = event.getAffectedBlocks();

        int i = 0;
        while (i < affectedBlocks.size()) {
            ChunkPosition block = affectedBlocks.get(i);
            if (protectedBlocks.isProtected(event.world, block.chunkPosX, block.chunkPosY, block.chunkPosZ)) {
                affectedBlocks.remove(i);
            } else {
                i++;
            }
        }
    }


}
