package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot
{
    public GuiListExtended(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return false;
    }

    protected void drawBackground()
    {
    }

    protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
    {
        this.getListEntry(entryID).drawEntry(entryID, p_180791_2_, p_180791_3_, this.getListWidth(), p_180791_4_, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
    }

    protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_)
    {
        this.getListEntry(p_178040_1_).setSelected(p_178040_1_, p_178040_2_, p_178040_3_);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent)
    {
        if (this.isMouseYWithinSlotBounds(mouseY))
        {
            int lvt_4_1_ = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

            if (lvt_4_1_ >= 0)
            {
                int lvt_5_1_ = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int lvt_6_1_ = this.top + 4 - this.getAmountScrolled() + lvt_4_1_ * this.slotHeight + this.headerPadding;
                int lvt_7_1_ = mouseX - lvt_5_1_;
                int lvt_8_1_ = mouseY - lvt_6_1_;

                if (this.getListEntry(lvt_4_1_).mousePressed(lvt_4_1_, mouseX, mouseY, mouseEvent, lvt_7_1_, lvt_8_1_))
                {
                    this.setEnabled(false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(int p_148181_1_, int p_148181_2_, int p_148181_3_)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < this.getSize(); ++lvt_4_1_)
        {
            int lvt_5_1_ = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int lvt_6_1_ = this.top + 4 - this.getAmountScrolled() + lvt_4_1_ * this.slotHeight + this.headerPadding;
            int lvt_7_1_ = p_148181_1_ - lvt_5_1_;
            int lvt_8_1_ = p_148181_2_ - lvt_6_1_;
            this.getListEntry(lvt_4_1_).mouseReleased(lvt_4_1_, p_148181_1_, p_148181_2_, p_148181_3_, lvt_7_1_, lvt_8_1_);
        }

        this.setEnabled(true);
        return false;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public abstract GuiListExtended.IGuiListEntry getListEntry(int index);

    public interface IGuiListEntry
    {
        void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_);

        void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);

        boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_);

        void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
    }
}
