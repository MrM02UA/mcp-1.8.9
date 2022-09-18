package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;

public class GuiCreateFlatWorld extends GuiScreen
{
    private final GuiCreateWorld createWorldGui;
    private FlatGeneratorInfo theFlatGeneratorInfo = FlatGeneratorInfo.getDefaultFlatGenerator();

    /** The title given to the flat world currently in creation */
    private String flatWorldTitle;
    private String field_146394_i;
    private String field_146391_r;
    private GuiCreateFlatWorld.Details createFlatWorldListSlotGui;
    private GuiButton field_146389_t;
    private GuiButton field_146388_u;
    private GuiButton field_146386_v;

    public GuiCreateFlatWorld(GuiCreateWorld createWorldGuiIn, String p_i1029_2_)
    {
        this.createWorldGui = createWorldGuiIn;
        this.func_146383_a(p_i1029_2_);
    }

    public String func_146384_e()
    {
        return this.theFlatGeneratorInfo.toString();
    }

    public void func_146383_a(String p_146383_1_)
    {
        this.theFlatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(p_146383_1_);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.flatWorldTitle = I18n.format("createWorld.customize.flat.title", new Object[0]);
        this.field_146394_i = I18n.format("createWorld.customize.flat.tile", new Object[0]);
        this.field_146391_r = I18n.format("createWorld.customize.flat.height", new Object[0]);
        this.createFlatWorldListSlotGui = new GuiCreateFlatWorld.Details();
        this.buttonList.add(this.field_146389_t = new GuiButton(2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.addLayer", new Object[0]) + " (NYI)"));
        this.buttonList.add(this.field_146388_u = new GuiButton(3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.editLayer", new Object[0]) + " (NYI)"));
        this.buttonList.add(this.field_146386_v = new GuiButton(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));
        this.field_146389_t.visible = this.field_146388_u.visible = false;
        this.theFlatGeneratorInfo.func_82645_d();
        this.func_146375_g();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.createFlatWorldListSlotGui.handleMouseInput();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        int lvt_2_1_ = this.theFlatGeneratorInfo.getFlatLayers().size() - this.createFlatWorldListSlotGui.field_148228_k - 1;

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(this.createWorldGui);
        }
        else if (button.id == 0)
        {
            this.createWorldGui.chunkProviderSettingsJson = this.func_146384_e();
            this.mc.displayGuiScreen(this.createWorldGui);
        }
        else if (button.id == 5)
        {
            this.mc.displayGuiScreen(new GuiFlatPresets(this));
        }
        else if (button.id == 4 && this.func_146382_i())
        {
            this.theFlatGeneratorInfo.getFlatLayers().remove(lvt_2_1_);
            this.createFlatWorldListSlotGui.field_148228_k = Math.min(this.createFlatWorldListSlotGui.field_148228_k, this.theFlatGeneratorInfo.getFlatLayers().size() - 1);
        }

        this.theFlatGeneratorInfo.func_82645_d();
        this.func_146375_g();
    }

    public void func_146375_g()
    {
        boolean lvt_1_1_ = this.func_146382_i();
        this.field_146386_v.enabled = lvt_1_1_;
        this.field_146388_u.enabled = lvt_1_1_;
        this.field_146388_u.enabled = false;
        this.field_146389_t.enabled = false;
    }

    private boolean func_146382_i()
    {
        return this.createFlatWorldListSlotGui.field_148228_k > -1 && this.createFlatWorldListSlotGui.field_148228_k < this.theFlatGeneratorInfo.getFlatLayers().size();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.flatWorldTitle, this.width / 2, 8, 16777215);
        int lvt_4_1_ = this.width / 2 - 92 - 16;
        this.drawString(this.fontRendererObj, this.field_146394_i, lvt_4_1_, 32, 16777215);
        this.drawString(this.fontRendererObj, this.field_146391_r, lvt_4_1_ + 2 + 213 - this.fontRendererObj.getStringWidth(this.field_146391_r), 32, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class Details extends GuiSlot
    {
        public int field_148228_k = -1;

        public Details()
        {
            super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
        }

        private void func_148225_a(int p_148225_1_, int p_148225_2_, ItemStack p_148225_3_)
        {
            this.func_148226_e(p_148225_1_ + 1, p_148225_2_ + 1);
            GlStateManager.enableRescaleNormal();

            if (p_148225_3_ != null && p_148225_3_.getItem() != null)
            {
                RenderHelper.enableGUIStandardItemLighting();
                GuiCreateFlatWorld.this.itemRender.renderItemIntoGUI(p_148225_3_, p_148225_1_ + 2, p_148225_2_ + 2);
                RenderHelper.disableStandardItemLighting();
            }

            GlStateManager.disableRescaleNormal();
        }

        private void func_148226_e(int p_148226_1_, int p_148226_2_)
        {
            this.func_148224_c(p_148226_1_, p_148226_2_, 0, 0);
        }

        private void func_148224_c(int p_148224_1_, int p_148224_2_, int p_148224_3_, int p_148224_4_)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(Gui.statIcons);
            float lvt_5_1_ = 0.0078125F;
            float lvt_6_1_ = 0.0078125F;
            int lvt_7_1_ = 18;
            int lvt_8_1_ = 18;
            Tessellator lvt_9_1_ = Tessellator.getInstance();
            WorldRenderer lvt_10_1_ = lvt_9_1_.getWorldRenderer();
            lvt_10_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_10_1_.pos((double)(p_148224_1_ + 0), (double)(p_148224_2_ + 18), (double)GuiCreateFlatWorld.this.zLevel).tex((double)((float)(p_148224_3_ + 0) * 0.0078125F), (double)((float)(p_148224_4_ + 18) * 0.0078125F)).endVertex();
            lvt_10_1_.pos((double)(p_148224_1_ + 18), (double)(p_148224_2_ + 18), (double)GuiCreateFlatWorld.this.zLevel).tex((double)((float)(p_148224_3_ + 18) * 0.0078125F), (double)((float)(p_148224_4_ + 18) * 0.0078125F)).endVertex();
            lvt_10_1_.pos((double)(p_148224_1_ + 18), (double)(p_148224_2_ + 0), (double)GuiCreateFlatWorld.this.zLevel).tex((double)((float)(p_148224_3_ + 18) * 0.0078125F), (double)((float)(p_148224_4_ + 0) * 0.0078125F)).endVertex();
            lvt_10_1_.pos((double)(p_148224_1_ + 0), (double)(p_148224_2_ + 0), (double)GuiCreateFlatWorld.this.zLevel).tex((double)((float)(p_148224_3_ + 0) * 0.0078125F), (double)((float)(p_148224_4_ + 0) * 0.0078125F)).endVertex();
            lvt_9_1_.draw();
        }

        protected int getSize()
        {
            return GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            this.field_148228_k = slotIndex;
            GuiCreateFlatWorld.this.func_146375_g();
        }

        protected boolean isSelected(int slotIndex)
        {
            return slotIndex == this.field_148228_k;
        }

        protected void drawBackground()
        {
        }

        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
        {
            FlatLayerInfo lvt_7_1_ = (FlatLayerInfo)GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().get(GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - entryID - 1);
            IBlockState lvt_8_1_ = lvt_7_1_.getLayerMaterial();
            Block lvt_9_1_ = lvt_8_1_.getBlock();
            Item lvt_10_1_ = Item.getItemFromBlock(lvt_9_1_);
            ItemStack lvt_11_1_ = lvt_9_1_ != Blocks.air && lvt_10_1_ != null ? new ItemStack(lvt_10_1_, 1, lvt_9_1_.getMetaFromState(lvt_8_1_)) : null;
            String lvt_12_1_ = lvt_11_1_ == null ? "Air" : lvt_10_1_.getItemStackDisplayName(lvt_11_1_);

            if (lvt_10_1_ == null)
            {
                if (lvt_9_1_ != Blocks.water && lvt_9_1_ != Blocks.flowing_water)
                {
                    if (lvt_9_1_ == Blocks.lava || lvt_9_1_ == Blocks.flowing_lava)
                    {
                        lvt_10_1_ = Items.lava_bucket;
                    }
                }
                else
                {
                    lvt_10_1_ = Items.water_bucket;
                }

                if (lvt_10_1_ != null)
                {
                    lvt_11_1_ = new ItemStack(lvt_10_1_, 1, lvt_9_1_.getMetaFromState(lvt_8_1_));
                    lvt_12_1_ = lvt_9_1_.getLocalizedName();
                }
            }

            this.func_148225_a(p_180791_2_, p_180791_3_, lvt_11_1_);
            GuiCreateFlatWorld.this.fontRendererObj.drawString(lvt_12_1_, p_180791_2_ + 18 + 5, p_180791_3_ + 3, 16777215);
            String lvt_13_1_;

            if (entryID == 0)
            {
                lvt_13_1_ = I18n.format("createWorld.customize.flat.layer.top", new Object[] {Integer.valueOf(lvt_7_1_.getLayerCount())});
            }
            else if (entryID == GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - 1)
            {
                lvt_13_1_ = I18n.format("createWorld.customize.flat.layer.bottom", new Object[] {Integer.valueOf(lvt_7_1_.getLayerCount())});
            }
            else
            {
                lvt_13_1_ = I18n.format("createWorld.customize.flat.layer", new Object[] {Integer.valueOf(lvt_7_1_.getLayerCount())});
            }

            GuiCreateFlatWorld.this.fontRendererObj.drawString(lvt_13_1_, p_180791_2_ + 2 + 213 - GuiCreateFlatWorld.this.fontRendererObj.getStringWidth(lvt_13_1_), p_180791_3_ + 3, 16777215);
        }

        protected int getScrollBarX()
        {
            return this.width - 70;
        }
    }
}
