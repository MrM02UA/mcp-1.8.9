package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public abstract class GuiSlot
{
    protected final Minecraft mc;
    protected int width;
    protected int height;

    /** The top of the slot container. Affects the overlays and scrolling. */
    protected int top;

    /** The bottom of the slot container. Affects the overlays and scrolling. */
    protected int bottom;
    protected int right;
    protected int left;

    /** The height of a slot. */
    protected final int slotHeight;

    /** The buttonID of the button used to scroll up */
    private int scrollUpButtonID;

    /** The buttonID of the button used to scroll down */
    private int scrollDownButtonID;
    protected int mouseX;
    protected int mouseY;
    protected boolean field_148163_i = true;

    /** Where the mouse was in the window when you first clicked to scroll */
    protected int initialClickY = -2;

    /**
     * What to multiply the amount you moved your mouse by (used for slowing down scrolling when over the items and not
     * on the scroll bar)
     */
    protected float scrollMultiplier;

    /** How far down this slot has been scrolled */
    protected float amountScrolled;

    /** The element in the list that was selected */
    protected int selectedElement = -1;

    /** The time when this button was last clicked. */
    protected long lastClicked;
    protected boolean field_178041_q = true;

    /**
     * Set to true if a selected element in this gui will show an outline box
     */
    protected boolean showSelectionBox = true;
    protected boolean hasListHeader;
    protected int headerPadding;
    private boolean enabled = true;

    public GuiSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn)
    {
        this.mc = mcIn;
        this.width = width;
        this.height = height;
        this.top = topIn;
        this.bottom = bottomIn;
        this.slotHeight = slotHeightIn;
        this.left = 0;
        this.right = width;
    }

    public void setDimensions(int widthIn, int heightIn, int topIn, int bottomIn)
    {
        this.width = widthIn;
        this.height = heightIn;
        this.top = topIn;
        this.bottom = bottomIn;
        this.left = 0;
        this.right = widthIn;
    }

    public void setShowSelectionBox(boolean showSelectionBoxIn)
    {
        this.showSelectionBox = showSelectionBoxIn;
    }

    /**
     * Sets hasListHeader and headerHeight. Params: hasListHeader, headerHeight. If hasListHeader is false headerHeight
     * is set to 0.
     */
    protected void setHasListHeader(boolean hasListHeaderIn, int headerPaddingIn)
    {
        this.hasListHeader = hasListHeaderIn;
        this.headerPadding = headerPaddingIn;

        if (!hasListHeaderIn)
        {
            this.headerPadding = 0;
        }
    }

    protected abstract int getSize();

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected abstract void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY);

    /**
     * Returns true if the element passed in is currently selected
     */
    protected abstract boolean isSelected(int slotIndex);

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.headerPadding;
    }

    protected abstract void drawBackground();

    protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_)
    {
    }

    protected abstract void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn);

    /**
     * Handles drawing a list's header row.
     */
    protected void drawListHeader(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_)
    {
    }

    protected void func_148132_a(int p_148132_1_, int p_148132_2_)
    {
    }

    protected void func_148142_b(int p_148142_1_, int p_148142_2_)
    {
    }

    public int getSlotIndexFromScreenCoords(int p_148124_1_, int p_148124_2_)
    {
        int lvt_3_1_ = this.left + this.width / 2 - this.getListWidth() / 2;
        int lvt_4_1_ = this.left + this.width / 2 + this.getListWidth() / 2;
        int lvt_5_1_ = p_148124_2_ - this.top - this.headerPadding + (int)this.amountScrolled - 4;
        int lvt_6_1_ = lvt_5_1_ / this.slotHeight;
        return p_148124_1_ < this.getScrollBarX() && p_148124_1_ >= lvt_3_1_ && p_148124_1_ <= lvt_4_1_ && lvt_6_1_ >= 0 && lvt_5_1_ >= 0 && lvt_6_1_ < this.getSize() ? lvt_6_1_ : -1;
    }

    /**
     * Registers the IDs that can be used for the scrollbar's up/down buttons.
     */
    public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn)
    {
        this.scrollUpButtonID = scrollUpButtonIDIn;
        this.scrollDownButtonID = scrollDownButtonIDIn;
    }

    /**
     * Stop the thing from scrolling out of bounds
     */
    protected void bindAmountScrolled()
    {
        this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0F, (float)this.func_148135_f());
    }

    public int func_148135_f()
    {
        return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
    }

    /**
     * Returns the amountScrolled field as an integer.
     */
    public int getAmountScrolled()
    {
        return (int)this.amountScrolled;
    }

    public boolean isMouseYWithinSlotBounds(int p_148141_1_)
    {
        return p_148141_1_ >= this.top && p_148141_1_ <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
    }

    /**
     * Scrolls the slot by the given amount. A positive value scrolls down, and a negative value scrolls up.
     */
    public void scrollBy(int amount)
    {
        this.amountScrolled += (float)amount;
        this.bindAmountScrolled();
        this.initialClickY = -2;
    }

    public void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == this.scrollUpButtonID)
            {
                this.amountScrolled -= (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            }
            else if (button.id == this.scrollDownButtonID)
            {
                this.amountScrolled += (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            }
        }
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
            this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float lvt_8_1_ = 32.0F;
            lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            lvt_7_1_.pos((double)this.left, (double)this.bottom, 0.0D).tex((double)((float)this.left / lvt_8_1_), (double)((float)(this.bottom + (int)this.amountScrolled) / lvt_8_1_)).color(32, 32, 32, 255).endVertex();
            lvt_7_1_.pos((double)this.right, (double)this.bottom, 0.0D).tex((double)((float)this.right / lvt_8_1_), (double)((float)(this.bottom + (int)this.amountScrolled) / lvt_8_1_)).color(32, 32, 32, 255).endVertex();
            lvt_7_1_.pos((double)this.right, (double)this.top, 0.0D).tex((double)((float)this.right / lvt_8_1_), (double)((float)(this.top + (int)this.amountScrolled) / lvt_8_1_)).color(32, 32, 32, 255).endVertex();
            lvt_7_1_.pos((double)this.left, (double)this.top, 0.0D).tex((double)((float)this.left / lvt_8_1_), (double)((float)(this.top + (int)this.amountScrolled) / lvt_8_1_)).color(32, 32, 32, 255).endVertex();
            lvt_6_1_.draw();
            int lvt_9_1_ = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int lvt_10_1_ = this.top + 4 - (int)this.amountScrolled;

            if (this.hasListHeader)
            {
                this.drawListHeader(lvt_9_1_, lvt_10_1_, lvt_6_1_);
            }

            this.drawSelectionBox(lvt_9_1_, lvt_10_1_, mouseXIn, mouseYIn);
            GlStateManager.disableDepth();
            int lvt_11_1_ = 4;
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            lvt_7_1_.pos((double)this.left, (double)(this.top + lvt_11_1_), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
            lvt_7_1_.pos((double)this.right, (double)(this.top + lvt_11_1_), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
            lvt_7_1_.pos((double)this.right, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            lvt_7_1_.pos((double)this.left, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            lvt_6_1_.draw();
            lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            lvt_7_1_.pos((double)this.left, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            lvt_7_1_.pos((double)this.right, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            lvt_7_1_.pos((double)this.right, (double)(this.bottom - lvt_11_1_), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
            lvt_7_1_.pos((double)this.left, (double)(this.bottom - lvt_11_1_), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
            lvt_6_1_.draw();
            int lvt_12_1_ = this.func_148135_f();

            if (lvt_12_1_ > 0)
            {
                int lvt_13_1_ = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                lvt_13_1_ = MathHelper.clamp_int(lvt_13_1_, 32, this.bottom - this.top - 8);
                int lvt_14_1_ = (int)this.amountScrolled * (this.bottom - this.top - lvt_13_1_) / lvt_12_1_ + this.top;

                if (lvt_14_1_ < this.top)
                {
                    lvt_14_1_ = this.top;
                }

                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_7_1_.pos((double)lvt_4_1_, (double)this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)lvt_4_1_, (double)this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                lvt_6_1_.draw();
                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_7_1_.pos((double)lvt_4_1_, (double)(lvt_14_1_ + lvt_13_1_), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)(lvt_14_1_ + lvt_13_1_), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_5_1_, (double)lvt_14_1_, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_4_1_, (double)lvt_14_1_, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                lvt_6_1_.draw();
                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_7_1_.pos((double)lvt_4_1_, (double)(lvt_14_1_ + lvt_13_1_ - 1), 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_5_1_ - 1), (double)(lvt_14_1_ + lvt_13_1_ - 1), 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_5_1_ - 1), (double)lvt_14_1_, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
                lvt_7_1_.pos((double)lvt_4_1_, (double)lvt_14_1_, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
                lvt_6_1_.draw();
            }

            this.func_148142_b(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void handleMouseInput()
    {
        if (this.isMouseYWithinSlotBounds(this.mouseY))
        {
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom)
            {
                int lvt_1_1_ = (this.width - this.getListWidth()) / 2;
                int lvt_2_1_ = (this.width + this.getListWidth()) / 2;
                int lvt_3_1_ = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                int lvt_4_1_ = lvt_3_1_ / this.slotHeight;

                if (lvt_4_1_ < this.getSize() && this.mouseX >= lvt_1_1_ && this.mouseX <= lvt_2_1_ && lvt_4_1_ >= 0 && lvt_3_1_ >= 0)
                {
                    this.elementClicked(lvt_4_1_, false, this.mouseX, this.mouseY);
                    this.selectedElement = lvt_4_1_;
                }
                else if (this.mouseX >= lvt_1_1_ && this.mouseX <= lvt_2_1_ && lvt_3_1_ < 0)
                {
                    this.func_148132_a(this.mouseX - lvt_1_1_, this.mouseY - this.top + (int)this.amountScrolled - 4);
                }
            }

            if (Mouse.isButtonDown(0) && this.getEnabled())
            {
                if (this.initialClickY == -1)
                {
                    boolean lvt_1_2_ = true;

                    if (this.mouseY >= this.top && this.mouseY <= this.bottom)
                    {
                        int lvt_2_2_ = (this.width - this.getListWidth()) / 2;
                        int lvt_3_2_ = (this.width + this.getListWidth()) / 2;
                        int lvt_4_2_ = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                        int lvt_5_1_ = lvt_4_2_ / this.slotHeight;

                        if (lvt_5_1_ < this.getSize() && this.mouseX >= lvt_2_2_ && this.mouseX <= lvt_3_2_ && lvt_5_1_ >= 0 && lvt_4_2_ >= 0)
                        {
                            boolean lvt_6_1_ = lvt_5_1_ == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                            this.elementClicked(lvt_5_1_, lvt_6_1_, this.mouseX, this.mouseY);
                            this.selectedElement = lvt_5_1_;
                            this.lastClicked = Minecraft.getSystemTime();
                        }
                        else if (this.mouseX >= lvt_2_2_ && this.mouseX <= lvt_3_2_ && lvt_4_2_ < 0)
                        {
                            this.func_148132_a(this.mouseX - lvt_2_2_, this.mouseY - this.top + (int)this.amountScrolled - 4);
                            lvt_1_2_ = false;
                        }

                        int lvt_6_2_ = this.getScrollBarX();
                        int lvt_7_1_ = lvt_6_2_ + 6;

                        if (this.mouseX >= lvt_6_2_ && this.mouseX <= lvt_7_1_)
                        {
                            this.scrollMultiplier = -1.0F;
                            int lvt_8_1_ = this.func_148135_f();

                            if (lvt_8_1_ < 1)
                            {
                                lvt_8_1_ = 1;
                            }

                            int lvt_9_1_ = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
                            lvt_9_1_ = MathHelper.clamp_int(lvt_9_1_, 32, this.bottom - this.top - 8);
                            this.scrollMultiplier /= (float)(this.bottom - this.top - lvt_9_1_) / (float)lvt_8_1_;
                        }
                        else
                        {
                            this.scrollMultiplier = 1.0F;
                        }

                        if (lvt_1_2_)
                        {
                            this.initialClickY = this.mouseY;
                        }
                        else
                        {
                            this.initialClickY = -2;
                        }
                    }
                    else
                    {
                        this.initialClickY = -2;
                    }
                }
                else if (this.initialClickY >= 0)
                {
                    this.amountScrolled -= (float)(this.mouseY - this.initialClickY) * this.scrollMultiplier;
                    this.initialClickY = this.mouseY;
                }
            }
            else
            {
                this.initialClickY = -1;
            }

            int lvt_1_3_ = Mouse.getEventDWheel();

            if (lvt_1_3_ != 0)
            {
                if (lvt_1_3_ > 0)
                {
                    lvt_1_3_ = -1;
                }
                else if (lvt_1_3_ < 0)
                {
                    lvt_1_3_ = 1;
                }

                this.amountScrolled += (float)(lvt_1_3_ * this.slotHeight / 2);
            }
        }
    }

    public void setEnabled(boolean enabledIn)
    {
        this.enabled = enabledIn;
    }

    public boolean getEnabled()
    {
        return this.enabled;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return 220;
    }

    /**
     * Draws the selection box around the selected slot element.
     */
    protected void drawSelectionBox(int p_148120_1_, int p_148120_2_, int mouseXIn, int mouseYIn)
    {
        int lvt_5_1_ = this.getSize();
        Tessellator lvt_6_1_ = Tessellator.getInstance();
        WorldRenderer lvt_7_1_ = lvt_6_1_.getWorldRenderer();

        for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_5_1_; ++lvt_8_1_)
        {
            int lvt_9_1_ = p_148120_2_ + lvt_8_1_ * this.slotHeight + this.headerPadding;
            int lvt_10_1_ = this.slotHeight - 4;

            if (lvt_9_1_ > this.bottom || lvt_9_1_ + lvt_10_1_ < this.top)
            {
                this.func_178040_a(lvt_8_1_, p_148120_1_, lvt_9_1_);
            }

            if (this.showSelectionBox && this.isSelected(lvt_8_1_))
            {
                int lvt_11_1_ = this.left + (this.width / 2 - this.getListWidth() / 2);
                int lvt_12_1_ = this.left + this.width / 2 + this.getListWidth() / 2;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableTexture2D();
                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_7_1_.pos((double)lvt_11_1_, (double)(lvt_9_1_ + lvt_10_1_ + 2), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_12_1_, (double)(lvt_9_1_ + lvt_10_1_ + 2), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_12_1_, (double)(lvt_9_1_ - 2), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)lvt_11_1_, (double)(lvt_9_1_ - 2), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_11_1_ + 1), (double)(lvt_9_1_ + lvt_10_1_ + 1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_12_1_ - 1), (double)(lvt_9_1_ + lvt_10_1_ + 1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_12_1_ - 1), (double)(lvt_9_1_ - 1), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_1_.pos((double)(lvt_11_1_ + 1), (double)(lvt_9_1_ - 1), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                lvt_6_1_.draw();
                GlStateManager.enableTexture2D();
            }

            this.drawSlot(lvt_8_1_, p_148120_1_, lvt_9_1_, lvt_10_1_, mouseXIn, mouseYIn);
        }
    }

    protected int getScrollBarX()
    {
        return this.width / 2 + 124;
    }

    /**
     * Overlays the background to hide scrolled items
     */
    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha)
    {
        Tessellator lvt_5_1_ = Tessellator.getInstance();
        WorldRenderer lvt_6_1_ = lvt_5_1_.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float lvt_7_1_ = 32.0F;
        lvt_6_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        lvt_6_1_.pos((double)this.left, (double)endY, 0.0D).tex(0.0D, (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
        lvt_6_1_.pos((double)(this.left + this.width), (double)endY, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
        lvt_6_1_.pos((double)(this.left + this.width), (double)startY, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
        lvt_6_1_.pos((double)this.left, (double)startY, 0.0D).tex(0.0D, (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
        lvt_5_1_.draw();
    }

    /**
     * Sets the left and right bounds of the slot. Param is the left bound, right is calculated as left + width.
     */
    public void setSlotXBoundsFromLeft(int leftIn)
    {
        this.left = leftIn;
        this.right = leftIn + this.width;
    }

    public int getSlotHeight()
    {
        return this.slotHeight;
    }
}
