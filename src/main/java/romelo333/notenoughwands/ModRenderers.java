package romelo333.notenoughwands;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import romelo333.notenoughwands.blocks.LightItemRenderer;
import romelo333.notenoughwands.blocks.LightRenderer;
import romelo333.notenoughwands.blocks.LightTE;

public final class ModRenderers {

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(LightTE.class, new LightRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.lightBlock), new LightItemRenderer());
    }

    public static void renderBillboardQuad(double scale, float vAdd1, float vAdd2) {
        GL11.glPushMatrix();

        rotateToPlayer();

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-scale, -scale, 0, 0, 0+vAdd1);
        tessellator.addVertexWithUV(-scale, +scale, 0, 0, 0+vAdd1+vAdd2);
        tessellator.addVertexWithUV(+scale, +scale, 0, 1, 0+vAdd1+vAdd2);
        tessellator.addVertexWithUV(+scale, -scale, 0, 1, 0+vAdd1);
        tessellator.draw();
        GL11.glPopMatrix();
    }

    public static void rotateToPlayer() {
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
    }
}
