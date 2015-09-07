package romelo333.notenoughwands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import romelo333.notenoughwands.ModRenderers;
import romelo333.notenoughwands.NotEnoughWands;

@SideOnly(Side.CLIENT)
public class LightRenderer extends TileEntitySpecialRenderer {
    ResourceLocation texture = new ResourceLocation(NotEnoughWands.MODID.toLowerCase(), "textures/blocks/light.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time) {
        bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean blending = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.0F, (float) z + 0.5F);


        GL11.glTranslatef(0.0f, 0.5f, 0.0f);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        this.bindTexture(texture);
        ModRenderers.renderBillboardQuad(0.6f);

        GL11.glPopMatrix();

        if (!blending) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
}
