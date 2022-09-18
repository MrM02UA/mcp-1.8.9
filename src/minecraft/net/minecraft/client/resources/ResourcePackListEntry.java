package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class ResourcePackListEntry implements GuiListExtended.IGuiListEntry
{
    private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
    private static final IChatComponent field_183020_d = new ChatComponentTranslation("resourcePack.incompatible", new Object[0]);
    private static final IChatComponent field_183021_e = new ChatComponentTranslation("resourcePack.incompatible.old", new Object[0]);
    private static final IChatComponent field_183022_f = new ChatComponentTranslation("resourcePack.incompatible.new", new Object[0]);
    protected final Minecraft mc;
    protected final GuiScreenResourcePacks resourcePacksGUI;

    public ResourcePackListEntry(GuiScreenResourcePacks resourcePacksGUIIn)
    {
        this.resourcePacksGUI = resourcePacksGUIIn;
        this.mc = Minecraft.getMinecraft();
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
        int lvt_9_1_ = this.func_183019_a();

        if (lvt_9_1_ != 1)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawRect(x - 1, y - 1, x + listWidth - 9, y + slotHeight + 1, -8978432);
        }

        this.func_148313_c();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        String lvt_10_1_ = this.func_148312_b();
        String lvt_11_1_ = this.func_148311_a();

        if ((this.mc.gameSettings.touchscreen || isSelected) && this.func_148310_d())
        {
            this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
            Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int lvt_12_1_ = mouseX - x;
            int lvt_13_1_ = mouseY - y;

            if (lvt_9_1_ < 1)
            {
                lvt_10_1_ = field_183020_d.getFormattedText();
                lvt_11_1_ = field_183021_e.getFormattedText();
            }
            else if (lvt_9_1_ > 1)
            {
                lvt_10_1_ = field_183020_d.getFormattedText();
                lvt_11_1_ = field_183022_f.getFormattedText();
            }

            if (this.func_148309_e())
            {
                if (lvt_12_1_ < 32)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
            else
            {
                if (this.func_148308_f())
                {
                    if (lvt_12_1_ < 16)
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    }
                    else
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148314_g())
                {
                    if (lvt_12_1_ < 32 && lvt_12_1_ > 16 && lvt_13_1_ < 16)
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    }
                    else
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148307_h())
                {
                    if (lvt_12_1_ < 32 && lvt_12_1_ > 16 && lvt_13_1_ > 16)
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    }
                    else
                    {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            }
        }

        int lvt_12_2_ = this.mc.fontRendererObj.getStringWidth(lvt_10_1_);

        if (lvt_12_2_ > 157)
        {
            lvt_10_1_ = this.mc.fontRendererObj.trimStringToWidth(lvt_10_1_, 157 - this.mc.fontRendererObj.getStringWidth("...")) + "...";
        }

        this.mc.fontRendererObj.drawStringWithShadow(lvt_10_1_, (float)(x + 32 + 2), (float)(y + 1), 16777215);
        List<String> lvt_13_2_ = this.mc.fontRendererObj.listFormattedStringToWidth(lvt_11_1_, 157);

        for (int lvt_14_1_ = 0; lvt_14_1_ < 2 && lvt_14_1_ < lvt_13_2_.size(); ++lvt_14_1_)
        {
            this.mc.fontRendererObj.drawStringWithShadow((String)lvt_13_2_.get(lvt_14_1_), (float)(x + 32 + 2), (float)(y + 12 + 10 * lvt_14_1_), 8421504);
        }
    }

    protected abstract int func_183019_a();

    protected abstract String func_148311_a();

    protected abstract String func_148312_b();

    protected abstract void func_148313_c();

    protected boolean func_148310_d()
    {
        return true;
    }

    protected boolean func_148309_e()
    {
        return !this.resourcePacksGUI.hasResourcePackEntry(this);
    }

    protected boolean func_148308_f()
    {
        return this.resourcePacksGUI.hasResourcePackEntry(this);
    }

    protected boolean func_148314_g()
    {
        List<ResourcePackListEntry> lvt_1_1_ = this.resourcePacksGUI.getListContaining(this);
        int lvt_2_1_ = lvt_1_1_.indexOf(this);
        return lvt_2_1_ > 0 && ((ResourcePackListEntry)lvt_1_1_.get(lvt_2_1_ - 1)).func_148310_d();
    }

    protected boolean func_148307_h()
    {
        List<ResourcePackListEntry> lvt_1_1_ = this.resourcePacksGUI.getListContaining(this);
        int lvt_2_1_ = lvt_1_1_.indexOf(this);
        return lvt_2_1_ >= 0 && lvt_2_1_ < lvt_1_1_.size() - 1 && ((ResourcePackListEntry)lvt_1_1_.get(lvt_2_1_ + 1)).func_148310_d();
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        if (this.func_148310_d() && p_148278_5_ <= 32)
        {
            if (this.func_148309_e())
            {
                this.resourcePacksGUI.markChanged();
                int lvt_7_1_ = this.func_183019_a();

                if (lvt_7_1_ != 1)
                {
                    String lvt_8_1_ = I18n.format("resourcePack.incompatible.confirm.title", new Object[0]);
                    String lvt_9_1_ = I18n.format("resourcePack.incompatible.confirm." + (lvt_7_1_ > 1 ? "new" : "old"), new Object[0]);
                    this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
                    {
                        public void confirmClicked(boolean result, int id)
                        {
                            List<ResourcePackListEntry> lvt_3_1_ = ResourcePackListEntry.this.resourcePacksGUI.getListContaining(ResourcePackListEntry.this);
                            ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);

                            if (result)
                            {
                                lvt_3_1_.remove(ResourcePackListEntry.this);
                                ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
                            }
                        }
                    }, lvt_8_1_, lvt_9_1_, 0));
                }
                else
                {
                    this.resourcePacksGUI.getListContaining(this).remove(this);
                    this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
                }

                return true;
            }

            if (p_148278_5_ < 16 && this.func_148308_f())
            {
                this.resourcePacksGUI.getListContaining(this).remove(this);
                this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }

            if (p_148278_5_ > 16 && p_148278_6_ < 16 && this.func_148314_g())
            {
                List<ResourcePackListEntry> lvt_7_2_ = this.resourcePacksGUI.getListContaining(this);
                int lvt_8_2_ = lvt_7_2_.indexOf(this);
                lvt_7_2_.remove(this);
                lvt_7_2_.add(lvt_8_2_ - 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }

            if (p_148278_5_ > 16 && p_148278_6_ > 16 && this.func_148307_h())
            {
                List<ResourcePackListEntry> lvt_7_3_ = this.resourcePacksGUI.getListContaining(this);
                int lvt_8_3_ = lvt_7_3_.indexOf(this);
                lvt_7_3_.remove(this);
                lvt_7_3_.add(lvt_8_3_ + 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
        }

        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
    }
}
