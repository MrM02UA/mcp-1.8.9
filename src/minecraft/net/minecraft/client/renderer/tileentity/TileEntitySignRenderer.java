package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySignRenderer extends TileEntitySpecialRenderer<TileEntitySign>
{
    private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");

    /** The ModelSign instance for use in this renderer */
    private final ModelSign model = new ModelSign();

    public void renderTileEntityAt(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        Block lvt_10_1_ = te.getBlockType();
        GlStateManager.pushMatrix();
        float lvt_11_1_ = 0.6666667F;

        if (lvt_10_1_ == Blocks.standing_sign)
        {
            GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * lvt_11_1_, (float)z + 0.5F);
            float lvt_12_1_ = (float)(te.getBlockMetadata() * 360) / 16.0F;
            GlStateManager.rotate(-lvt_12_1_, 0.0F, 1.0F, 0.0F);
            this.model.signStick.showModel = true;
        }
        else
        {
            int lvt_12_2_ = te.getBlockMetadata();
            float lvt_13_1_ = 0.0F;

            if (lvt_12_2_ == 2)
            {
                lvt_13_1_ = 180.0F;
            }

            if (lvt_12_2_ == 4)
            {
                lvt_13_1_ = 90.0F;
            }

            if (lvt_12_2_ == 5)
            {
                lvt_13_1_ = -90.0F;
            }

            GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * lvt_11_1_, (float)z + 0.5F);
            GlStateManager.rotate(-lvt_13_1_, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            this.model.signStick.showModel = false;
        }

        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            this.bindTexture(SIGN_TEXTURE);
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scale(lvt_11_1_, -lvt_11_1_, -lvt_11_1_);
        this.model.renderSign();
        GlStateManager.popMatrix();
        FontRenderer lvt_12_3_ = this.getFontRenderer();
        float lvt_13_2_ = 0.015625F * lvt_11_1_;
        GlStateManager.translate(0.0F, 0.5F * lvt_11_1_, 0.07F * lvt_11_1_);
        GlStateManager.scale(lvt_13_2_, -lvt_13_2_, lvt_13_2_);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * lvt_13_2_);
        GlStateManager.depthMask(false);
        int lvt_14_1_ = 0;

        if (destroyStage < 0)
        {
            for (int lvt_15_1_ = 0; lvt_15_1_ < te.signText.length; ++lvt_15_1_)
            {
                if (te.signText[lvt_15_1_] != null)
                {
                    IChatComponent lvt_16_1_ = te.signText[lvt_15_1_];
                    List<IChatComponent> lvt_17_1_ = GuiUtilRenderComponents.splitText(lvt_16_1_, 90, lvt_12_3_, false, true);
                    String lvt_18_1_ = lvt_17_1_ != null && lvt_17_1_.size() > 0 ? ((IChatComponent)lvt_17_1_.get(0)).getFormattedText() : "";

                    if (lvt_15_1_ == te.lineBeingEdited)
                    {
                        lvt_18_1_ = "> " + lvt_18_1_ + " <";
                        lvt_12_3_.drawString(lvt_18_1_, -lvt_12_3_.getStringWidth(lvt_18_1_) / 2, lvt_15_1_ * 10 - te.signText.length * 5, lvt_14_1_);
                    }
                    else
                    {
                        lvt_12_3_.drawString(lvt_18_1_, -lvt_12_3_.getStringWidth(lvt_18_1_) / 2, lvt_15_1_ * 10 - te.signText.length * 5, lvt_14_1_);
                    }
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        this.renderTileEntityAt((TileEntitySign)te, x, y, z, partialTicks, destroyStage);
    }
}
