package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;

public class RenderLightningBolt extends Render<EntityLightningBolt>
{
    public RenderLightningBolt(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityLightningBolt entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Tessellator lvt_10_1_ = Tessellator.getInstance();
        WorldRenderer lvt_11_1_ = lvt_10_1_.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        double[] lvt_12_1_ = new double[8];
        double[] lvt_13_1_ = new double[8];
        double lvt_14_1_ = 0.0D;
        double lvt_16_1_ = 0.0D;
        Random lvt_18_1_ = new Random(entity.boltVertex);

        for (int lvt_19_1_ = 7; lvt_19_1_ >= 0; --lvt_19_1_)
        {
            lvt_12_1_[lvt_19_1_] = lvt_14_1_;
            lvt_13_1_[lvt_19_1_] = lvt_16_1_;
            lvt_14_1_ += (double)(lvt_18_1_.nextInt(11) - 5);
            lvt_16_1_ += (double)(lvt_18_1_.nextInt(11) - 5);
        }

        for (int lvt_18_2_ = 0; lvt_18_2_ < 4; ++lvt_18_2_)
        {
            Random lvt_19_2_ = new Random(entity.boltVertex);

            for (int lvt_20_1_ = 0; lvt_20_1_ < 3; ++lvt_20_1_)
            {
                int lvt_21_1_ = 7;
                int lvt_22_1_ = 0;

                if (lvt_20_1_ > 0)
                {
                    lvt_21_1_ = 7 - lvt_20_1_;
                }

                if (lvt_20_1_ > 0)
                {
                    lvt_22_1_ = lvt_21_1_ - 2;
                }

                double lvt_23_1_ = lvt_12_1_[lvt_21_1_] - lvt_14_1_;
                double lvt_25_1_ = lvt_13_1_[lvt_21_1_] - lvt_16_1_;

                for (int lvt_27_1_ = lvt_21_1_; lvt_27_1_ >= lvt_22_1_; --lvt_27_1_)
                {
                    double lvt_28_1_ = lvt_23_1_;
                    double lvt_30_1_ = lvt_25_1_;

                    if (lvt_20_1_ == 0)
                    {
                        lvt_23_1_ += (double)(lvt_19_2_.nextInt(11) - 5);
                        lvt_25_1_ += (double)(lvt_19_2_.nextInt(11) - 5);
                    }
                    else
                    {
                        lvt_23_1_ += (double)(lvt_19_2_.nextInt(31) - 15);
                        lvt_25_1_ += (double)(lvt_19_2_.nextInt(31) - 15);
                    }

                    lvt_11_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);
                    float lvt_32_1_ = 0.5F;
                    float lvt_33_1_ = 0.45F;
                    float lvt_34_1_ = 0.45F;
                    float lvt_35_1_ = 0.5F;
                    double lvt_36_1_ = 0.1D + (double)lvt_18_2_ * 0.2D;

                    if (lvt_20_1_ == 0)
                    {
                        lvt_36_1_ *= (double)lvt_27_1_ * 0.1D + 1.0D;
                    }

                    double lvt_38_1_ = 0.1D + (double)lvt_18_2_ * 0.2D;

                    if (lvt_20_1_ == 0)
                    {
                        lvt_38_1_ *= (double)(lvt_27_1_ - 1) * 0.1D + 1.0D;
                    }

                    for (int lvt_40_1_ = 0; lvt_40_1_ < 5; ++lvt_40_1_)
                    {
                        double lvt_41_1_ = x + 0.5D - lvt_36_1_;
                        double lvt_43_1_ = z + 0.5D - lvt_36_1_;

                        if (lvt_40_1_ == 1 || lvt_40_1_ == 2)
                        {
                            lvt_41_1_ += lvt_36_1_ * 2.0D;
                        }

                        if (lvt_40_1_ == 2 || lvt_40_1_ == 3)
                        {
                            lvt_43_1_ += lvt_36_1_ * 2.0D;
                        }

                        double lvt_45_1_ = x + 0.5D - lvt_38_1_;
                        double lvt_47_1_ = z + 0.5D - lvt_38_1_;

                        if (lvt_40_1_ == 1 || lvt_40_1_ == 2)
                        {
                            lvt_45_1_ += lvt_38_1_ * 2.0D;
                        }

                        if (lvt_40_1_ == 2 || lvt_40_1_ == 3)
                        {
                            lvt_47_1_ += lvt_38_1_ * 2.0D;
                        }

                        lvt_11_1_.pos(lvt_45_1_ + lvt_23_1_, y + (double)(lvt_27_1_ * 16), lvt_47_1_ + lvt_25_1_).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
                        lvt_11_1_.pos(lvt_41_1_ + lvt_28_1_, y + (double)((lvt_27_1_ + 1) * 16), lvt_43_1_ + lvt_30_1_).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
                    }

                    lvt_10_1_.draw();
                }
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityLightningBolt entity)
    {
        return null;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityLightningBolt)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityLightningBolt)entity, x, y, z, entityYaw, partialTicks);
    }
}
