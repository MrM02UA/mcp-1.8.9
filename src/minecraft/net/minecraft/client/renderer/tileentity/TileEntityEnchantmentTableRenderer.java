package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnchantmentTableRenderer extends TileEntitySpecialRenderer<TileEntityEnchantmentTable>
{
    /** The texture for the book above the enchantment table. */
    private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private ModelBook field_147541_c = new ModelBook();

    public void renderTileEntityAt(TileEntityEnchantmentTable te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
        float lvt_10_1_ = (float)te.tickCount + partialTicks;
        GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(lvt_10_1_ * 0.1F) * 0.01F, 0.0F);
        float lvt_11_1_;

        for (lvt_11_1_ = te.bookRotation - te.bookRotationPrev; lvt_11_1_ >= (float)Math.PI; lvt_11_1_ -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (lvt_11_1_ < -(float)Math.PI)
        {
            lvt_11_1_ += ((float)Math.PI * 2F);
        }

        float lvt_12_1_ = te.bookRotationPrev + lvt_11_1_ * partialTicks;
        GlStateManager.rotate(-lvt_12_1_ * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(TEXTURE_BOOK);
        float lvt_13_1_ = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
        float lvt_14_1_ = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
        lvt_13_1_ = (lvt_13_1_ - (float)MathHelper.truncateDoubleToInt((double)lvt_13_1_)) * 1.6F - 0.3F;
        lvt_14_1_ = (lvt_14_1_ - (float)MathHelper.truncateDoubleToInt((double)lvt_14_1_)) * 1.6F - 0.3F;

        if (lvt_13_1_ < 0.0F)
        {
            lvt_13_1_ = 0.0F;
        }

        if (lvt_14_1_ < 0.0F)
        {
            lvt_14_1_ = 0.0F;
        }

        if (lvt_13_1_ > 1.0F)
        {
            lvt_13_1_ = 1.0F;
        }

        if (lvt_14_1_ > 1.0F)
        {
            lvt_14_1_ = 1.0F;
        }

        float lvt_15_1_ = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
        GlStateManager.enableCull();
        this.field_147541_c.render((Entity)null, lvt_10_1_, lvt_13_1_, lvt_14_1_, lvt_15_1_, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        this.renderTileEntityAt((TileEntityEnchantmentTable)te, x, y, z, partialTicks, destroyStage);
    }
}
