package net.minecraft.client.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;

public class TileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal>
{
    private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random field_147527_e = new Random(31100L);
    FloatBuffer field_147528_b = GLAllocation.createDirectFloatBuffer(16);

    public void renderTileEntityAt(TileEntityEndPortal te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        float lvt_10_1_ = (float)this.rendererDispatcher.entityX;
        float lvt_11_1_ = (float)this.rendererDispatcher.entityY;
        float lvt_12_1_ = (float)this.rendererDispatcher.entityZ;
        GlStateManager.disableLighting();
        field_147527_e.setSeed(31100L);
        float lvt_13_1_ = 0.75F;

        for (int lvt_14_1_ = 0; lvt_14_1_ < 16; ++lvt_14_1_)
        {
            GlStateManager.pushMatrix();
            float lvt_15_1_ = (float)(16 - lvt_14_1_);
            float lvt_16_1_ = 0.0625F;
            float lvt_17_1_ = 1.0F / (lvt_15_1_ + 1.0F);

            if (lvt_14_1_ == 0)
            {
                this.bindTexture(END_SKY_TEXTURE);
                lvt_17_1_ = 0.1F;
                lvt_15_1_ = 65.0F;
                lvt_16_1_ = 0.125F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
            }

            if (lvt_14_1_ >= 1)
            {
                this.bindTexture(END_PORTAL_TEXTURE);
            }

            if (lvt_14_1_ == 1)
            {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
                lvt_16_1_ = 0.5F;
            }

            float lvt_18_1_ = (float)(-(y + (double)lvt_13_1_));
            float lvt_19_1_ = lvt_18_1_ + (float)ActiveRenderInfo.getPosition().yCoord;
            float lvt_20_1_ = lvt_18_1_ + lvt_15_1_ + (float)ActiveRenderInfo.getPosition().yCoord;
            float lvt_21_1_ = lvt_19_1_ / lvt_20_1_;
            lvt_21_1_ = (float)(y + (double)lvt_13_1_) + lvt_21_1_;
            GlStateManager.translate(lvt_10_1_, lvt_21_1_, lvt_12_1_);
            GlStateManager.texGen(GlStateManager.TexGen.S, 9217);
            GlStateManager.texGen(GlStateManager.TexGen.T, 9217);
            GlStateManager.texGen(GlStateManager.TexGen.R, 9217);
            GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGen(GlStateManager.TexGen.S, 9473, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.T, 9473, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.R, 9473, this.func_147525_a(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
            GlStateManager.scale(lvt_16_1_, lvt_16_1_, lvt_16_1_);
            GlStateManager.translate(0.5F, 0.5F, 0.0F);
            GlStateManager.rotate((float)(lvt_14_1_ * lvt_14_1_ * 4321 + lvt_14_1_ * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
            GlStateManager.translate(-lvt_10_1_, -lvt_12_1_, -lvt_11_1_);
            lvt_19_1_ = lvt_18_1_ + (float)ActiveRenderInfo.getPosition().yCoord;
            GlStateManager.translate((float)ActiveRenderInfo.getPosition().xCoord * lvt_15_1_ / lvt_19_1_, (float)ActiveRenderInfo.getPosition().zCoord * lvt_15_1_ / lvt_19_1_, -lvt_11_1_);
            Tessellator lvt_20_2_ = Tessellator.getInstance();
            WorldRenderer lvt_21_2_ = lvt_20_2_.getWorldRenderer();
            lvt_21_2_.begin(7, DefaultVertexFormats.POSITION_COLOR);
            float lvt_22_1_ = (field_147527_e.nextFloat() * 0.5F + 0.1F) * lvt_17_1_;
            float lvt_23_1_ = (field_147527_e.nextFloat() * 0.5F + 0.4F) * lvt_17_1_;
            float lvt_24_1_ = (field_147527_e.nextFloat() * 0.5F + 0.5F) * lvt_17_1_;

            if (lvt_14_1_ == 0)
            {
                lvt_22_1_ = lvt_23_1_ = lvt_24_1_ = 1.0F * lvt_17_1_;
            }

            lvt_21_2_.pos(x, y + (double)lvt_13_1_, z).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
            lvt_21_2_.pos(x, y + (double)lvt_13_1_, z + 1.0D).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
            lvt_21_2_.pos(x + 1.0D, y + (double)lvt_13_1_, z + 1.0D).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
            lvt_21_2_.pos(x + 1.0D, y + (double)lvt_13_1_, z).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
            lvt_20_2_.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            this.bindTexture(END_SKY_TEXTURE);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }

    private FloatBuffer func_147525_a(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_)
    {
        this.field_147528_b.clear();
        this.field_147528_b.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
        this.field_147528_b.flip();
        return this.field_147528_b;
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        this.renderTileEntityAt((TileEntityEndPortal)te, x, y, z, partialTicks, destroyStage);
    }
}
