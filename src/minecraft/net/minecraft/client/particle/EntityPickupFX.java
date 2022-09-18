package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPickupFX extends EntityFX
{
    private Entity field_174840_a;
    private Entity field_174843_ax;
    private int age;
    private int maxAge;
    private float field_174841_aA;
    private RenderManager field_174842_aB = Minecraft.getMinecraft().getRenderManager();

    public EntityPickupFX(World worldIn, Entity p_i1233_2_, Entity p_i1233_3_, float p_i1233_4_)
    {
        super(worldIn, p_i1233_2_.posX, p_i1233_2_.posY, p_i1233_2_.posZ, p_i1233_2_.motionX, p_i1233_2_.motionY, p_i1233_2_.motionZ);
        this.field_174840_a = p_i1233_2_;
        this.field_174843_ax = p_i1233_3_;
        this.maxAge = 3;
        this.field_174841_aA = p_i1233_4_;
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float lvt_9_1_ = ((float)this.age + partialTicks) / (float)this.maxAge;
        lvt_9_1_ = lvt_9_1_ * lvt_9_1_;
        double lvt_10_1_ = this.field_174840_a.posX;
        double lvt_12_1_ = this.field_174840_a.posY;
        double lvt_14_1_ = this.field_174840_a.posZ;
        double lvt_16_1_ = this.field_174843_ax.lastTickPosX + (this.field_174843_ax.posX - this.field_174843_ax.lastTickPosX) * (double)partialTicks;
        double lvt_18_1_ = this.field_174843_ax.lastTickPosY + (this.field_174843_ax.posY - this.field_174843_ax.lastTickPosY) * (double)partialTicks + (double)this.field_174841_aA;
        double lvt_20_1_ = this.field_174843_ax.lastTickPosZ + (this.field_174843_ax.posZ - this.field_174843_ax.lastTickPosZ) * (double)partialTicks;
        double lvt_22_1_ = lvt_10_1_ + (lvt_16_1_ - lvt_10_1_) * (double)lvt_9_1_;
        double lvt_24_1_ = lvt_12_1_ + (lvt_18_1_ - lvt_12_1_) * (double)lvt_9_1_;
        double lvt_26_1_ = lvt_14_1_ + (lvt_20_1_ - lvt_14_1_) * (double)lvt_9_1_;
        int lvt_28_1_ = this.getBrightnessForRender(partialTicks);
        int lvt_29_1_ = lvt_28_1_ % 65536;
        int lvt_30_1_ = lvt_28_1_ / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lvt_29_1_ / 1.0F, (float)lvt_30_1_ / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        lvt_22_1_ = lvt_22_1_ - interpPosX;
        lvt_24_1_ = lvt_24_1_ - interpPosY;
        lvt_26_1_ = lvt_26_1_ - interpPosZ;
        this.field_174842_aB.renderEntityWithPosYaw(this.field_174840_a, (double)((float)lvt_22_1_), (double)((float)lvt_24_1_), (double)((float)lvt_26_1_), this.field_174840_a.rotationYaw, partialTicks);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        ++this.age;

        if (this.age == this.maxAge)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
