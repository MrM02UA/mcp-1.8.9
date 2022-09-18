package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer<TileEntityEnderChest>
{
    private static final ResourceLocation ENDER_CHEST_TEXTURE = new ResourceLocation("textures/entity/chest/ender.png");
    private ModelChest field_147521_c = new ModelChest();

    public void renderTileEntityAt(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        int lvt_10_1_ = 0;

        if (te.hasWorldObj())
        {
            lvt_10_1_ = te.getBlockMetadata();
        }

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            this.bindTexture(ENDER_CHEST_TEXTURE);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float)x, (float)y + 1.0F, (float)z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int lvt_11_1_ = 0;

        if (lvt_10_1_ == 2)
        {
            lvt_11_1_ = 180;
        }

        if (lvt_10_1_ == 3)
        {
            lvt_11_1_ = 0;
        }

        if (lvt_10_1_ == 4)
        {
            lvt_11_1_ = 90;
        }

        if (lvt_10_1_ == 5)
        {
            lvt_11_1_ = -90;
        }

        GlStateManager.rotate((float)lvt_11_1_, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float lvt_12_1_ = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
        lvt_12_1_ = 1.0F - lvt_12_1_;
        lvt_12_1_ = 1.0F - lvt_12_1_ * lvt_12_1_ * lvt_12_1_;
        this.field_147521_c.chestLid.rotateAngleX = -(lvt_12_1_ * (float)Math.PI / 2.0F);
        this.field_147521_c.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        this.renderTileEntityAt((TileEntityEnderChest)te, x, y, z, partialTicks, destroyStage);
    }
}
