package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

public class RenderTntMinecart extends RenderMinecart<EntityMinecartTNT>
{
    public RenderTntMinecart(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    protected void func_180560_a(EntityMinecartTNT minecart, float partialTicks, IBlockState state)
    {
        int lvt_4_1_ = minecart.getFuseTicks();

        if (lvt_4_1_ > -1 && (float)lvt_4_1_ - partialTicks + 1.0F < 10.0F)
        {
            float lvt_5_1_ = 1.0F - ((float)lvt_4_1_ - partialTicks + 1.0F) / 10.0F;
            lvt_5_1_ = MathHelper.clamp_float(lvt_5_1_, 0.0F, 1.0F);
            lvt_5_1_ = lvt_5_1_ * lvt_5_1_;
            lvt_5_1_ = lvt_5_1_ * lvt_5_1_;
            float lvt_6_1_ = 1.0F + lvt_5_1_ * 0.3F;
            GlStateManager.scale(lvt_6_1_, lvt_6_1_, lvt_6_1_);
        }

        super.func_180560_a(minecart, partialTicks, state);

        if (lvt_4_1_ > -1 && lvt_4_1_ / 5 % 2 == 0)
        {
            BlockRendererDispatcher lvt_5_2_ = Minecraft.getMinecraft().getBlockRendererDispatcher();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 772);
            GlStateManager.color(1.0F, 1.0F, 1.0F, (1.0F - ((float)lvt_4_1_ - partialTicks + 1.0F) / 100.0F) * 0.8F);
            GlStateManager.pushMatrix();
            lvt_5_2_.renderBlockBrightness(Blocks.tnt.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
    }

    protected void func_180560_a(EntityMinecart minecart, float partialTicks, IBlockState state)
    {
        this.func_180560_a((EntityMinecartTNT)minecart, partialTicks, state);
    }
}
