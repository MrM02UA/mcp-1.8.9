package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.MathHelper;

public class GuiSimpleScrolledSelectionListProxy extends GuiSlot
{
    private final RealmsSimpleScrolledSelectionList field_178050_u;

    public GuiSimpleScrolledSelectionListProxy(RealmsSimpleScrolledSelectionList p_i45525_1_, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.field_178050_u = p_i45525_1_;
    }

    protected int getSize()
    {
        return this.field_178050_u.getItemCount();
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
        this.field_178050_u.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return this.field_178050_u.isSelectedItem(slotIndex);
    }

    protected void drawBackground()
    {
        this.field_178050_u.renderBackground();
    }

    protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
    {
        this.field_178050_u.renderItem(entryID, p_180791_2_, p_180791_3_, p_180791_4_, mouseXIn, mouseYIn);
    }

    public int getWidth()
    {
        return super.width;
    }

    public int getMouseY()
    {
        return super.mouseY;
    }

    public int getMouseX()
    {
        return super.mouseX;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.field_178050_u.getMaxPosition();
    }

    protected int getScrollBarX()
    {
        return this.field_178050_u.getScrollbarPosition();
    }

    public void handleMouseInput()
    {
        super.handleMouseInput();
    }

    public void drawScreen(int mouseXIn, int mouseYIn, float p_148128_3_)
    {
        if (this.field_178041_q)
        {
            this.mouseX = mouseXIn;
            this.mouseY = mouseYIn;
            this.drawBackground();
            int lvt_4_1_ = this.getScrollBarX();
            int lvt_5_1_ = lvt_4_1_ + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator lvt_6_1_ = Tessellator.getInstance();
            WorldRenderer lvt_7_1_ = lvt_6_1_.getWorldRenderer();
            int lvt_8_1_ = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int lvt_9_1_ = this.top + 4 - (int)this.amountScrolled;

            if (this.hasListHeader)
            {
                this.drawListHeader(lvt_8_1_, lvt_9_1_, lvt_6_1_);
            }

            this.drawSelectionBox(lvt_8_1_, lvt_9_1_, mouseXIn, mouseYIn);
            GlStateManager.disableDepth();
            int lvt_10_1_ = 4;
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            int lvt_11_1_ = this.func_148135_f();

            if (lvt_11_1_ > 0)
            {
                int lvt_12_1_ = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                lvt_12_1_ = MathHelper.clamp_int(lvt_12_1_, 32, this.bottom - this.top - 8);
                int lvt_13_1_ = (int)this.amountScrolled * (this.bottom - this.top - lvt_12_1_) / lvt_11_1_ + this.top;

                if (lvt_13_1_ < this.top)
                {
                    lvt_13_1_ = this.top;
                }

                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_7_1_.pos((double)lvt_4_1_, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)lvt_4_1_, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                lvt_6_1_.draw();
                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_7_1_.pos((double)lvt_4_1_, (double)(lvt_13_1_ + lvt_12_1_), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)(lvt_13_1_ + lvt_12_1_), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)lvt_13_1_, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_4_1_, (double)lvt_13_1_, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                lvt_6_1_.draw();
                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_7_1_.pos((double)lvt_4_1_, (double)(lvt_13_1_ + lvt_12_1_ - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_5_1_ - 1), (double)(lvt_13_1_ + lvt_12_1_ - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_5_1_ - 1), (double)lvt_13_1_, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
                lvt_7_1_.pos((double)lvt_4_1_, (double)lvt_13_1_, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
                lvt_6_1_.draw();
            }

            this.func_148142_b(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }
}
