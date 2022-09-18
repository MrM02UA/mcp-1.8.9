package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiMerchant extends GuiContainer
{
    private static final Logger logger = LogManager.getLogger();

    /** The GUI texture for the villager merchant GUI. */
    private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");

    /** The current IMerchant instance in use for this specific merchant. */
    private IMerchant merchant;

    /** The button which proceeds to the next available merchant recipe. */
    private GuiMerchant.MerchantButton nextButton;

    /** Returns to the previous Merchant recipe if one is applicable. */
    private GuiMerchant.MerchantButton previousButton;

    /**
     * The integer value corresponding to the currently selected merchant recipe.
     */
    private int selectedMerchantRecipe;

    /** The chat component utilized by this GuiMerchant instance. */
    private IChatComponent chatComponent;

    public GuiMerchant(InventoryPlayer p_i45500_1_, IMerchant p_i45500_2_, World worldIn)
    {
        super(new ContainerMerchant(p_i45500_1_, p_i45500_2_, worldIn));
        this.merchant = p_i45500_2_;
        this.chatComponent = p_i45500_2_.getDisplayName();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        int lvt_1_1_ = (this.width - this.xSize) / 2;
        int lvt_2_1_ = (this.height - this.ySize) / 2;
        this.buttonList.add(this.nextButton = new GuiMerchant.MerchantButton(1, lvt_1_1_ + 120 + 27, lvt_2_1_ + 24 - 1, true));
        this.buttonList.add(this.previousButton = new GuiMerchant.MerchantButton(2, lvt_1_1_ + 36 - 19, lvt_2_1_ + 24 - 1, false));
        this.nextButton.enabled = false;
        this.previousButton.enabled = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String lvt_3_1_ = this.chatComponent.getUnformattedText();
        this.fontRendererObj.drawString(lvt_3_1_, this.xSize / 2 - this.fontRendererObj.getStringWidth(lvt_3_1_) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        MerchantRecipeList lvt_1_1_ = this.merchant.getRecipes(this.mc.thePlayer);

        if (lvt_1_1_ != null)
        {
            this.nextButton.enabled = this.selectedMerchantRecipe < lvt_1_1_.size() - 1;
            this.previousButton.enabled = this.selectedMerchantRecipe > 0;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        boolean lvt_2_1_ = false;

        if (button == this.nextButton)
        {
            ++this.selectedMerchantRecipe;
            MerchantRecipeList lvt_3_1_ = this.merchant.getRecipes(this.mc.thePlayer);

            if (lvt_3_1_ != null && this.selectedMerchantRecipe >= lvt_3_1_.size())
            {
                this.selectedMerchantRecipe = lvt_3_1_.size() - 1;
            }

            lvt_2_1_ = true;
        }
        else if (button == this.previousButton)
        {
            --this.selectedMerchantRecipe;

            if (this.selectedMerchantRecipe < 0)
            {
                this.selectedMerchantRecipe = 0;
            }

            lvt_2_1_ = true;
        }

        if (lvt_2_1_)
        {
            ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
            PacketBuffer lvt_3_2_ = new PacketBuffer(Unpooled.buffer());
            lvt_3_2_.writeInt(this.selectedMerchantRecipe);
            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", lvt_3_2_));
        }
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
        MerchantRecipeList lvt_6_1_ = this.merchant.getRecipes(this.mc.thePlayer);

        if (lvt_6_1_ != null && !lvt_6_1_.isEmpty())
        {
            int lvt_7_1_ = this.selectedMerchantRecipe;

            if (lvt_7_1_ < 0 || lvt_7_1_ >= lvt_6_1_.size())
            {
                return;
            }

            MerchantRecipe lvt_8_1_ = (MerchantRecipe)lvt_6_1_.get(lvt_7_1_);

            if (lvt_8_1_.isRecipeDisabled())
            {
                this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableLighting();
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        MerchantRecipeList lvt_4_1_ = this.merchant.getRecipes(this.mc.thePlayer);

        if (lvt_4_1_ != null && !lvt_4_1_.isEmpty())
        {
            int lvt_5_1_ = (this.width - this.xSize) / 2;
            int lvt_6_1_ = (this.height - this.ySize) / 2;
            int lvt_7_1_ = this.selectedMerchantRecipe;
            MerchantRecipe lvt_8_1_ = (MerchantRecipe)lvt_4_1_.get(lvt_7_1_);
            ItemStack lvt_9_1_ = lvt_8_1_.getItemToBuy();
            ItemStack lvt_10_1_ = lvt_8_1_.getSecondItemToBuy();
            ItemStack lvt_11_1_ = lvt_8_1_.getItemToSell();
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.itemRender.zLevel = 100.0F;
            this.itemRender.renderItemAndEffectIntoGUI(lvt_9_1_, lvt_5_1_ + 36, lvt_6_1_ + 24);
            this.itemRender.renderItemOverlays(this.fontRendererObj, lvt_9_1_, lvt_5_1_ + 36, lvt_6_1_ + 24);

            if (lvt_10_1_ != null)
            {
                this.itemRender.renderItemAndEffectIntoGUI(lvt_10_1_, lvt_5_1_ + 62, lvt_6_1_ + 24);
                this.itemRender.renderItemOverlays(this.fontRendererObj, lvt_10_1_, lvt_5_1_ + 62, lvt_6_1_ + 24);
            }

            this.itemRender.renderItemAndEffectIntoGUI(lvt_11_1_, lvt_5_1_ + 120, lvt_6_1_ + 24);
            this.itemRender.renderItemOverlays(this.fontRendererObj, lvt_11_1_, lvt_5_1_ + 120, lvt_6_1_ + 24);
            this.itemRender.zLevel = 0.0F;
            GlStateManager.disableLighting();

            if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY) && lvt_9_1_ != null)
            {
                this.renderToolTip(lvt_9_1_, mouseX, mouseY);
            }
            else if (lvt_10_1_ != null && this.isPointInRegion(62, 24, 16, 16, mouseX, mouseY) && lvt_10_1_ != null)
            {
                this.renderToolTip(lvt_10_1_, mouseX, mouseY);
            }
            else if (lvt_11_1_ != null && this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY) && lvt_11_1_ != null)
            {
                this.renderToolTip(lvt_11_1_, mouseX, mouseY);
            }
            else if (lvt_8_1_.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isPointInRegion(83, 51, 28, 21, mouseX, mouseY)))
            {
                this.drawCreativeTabHoveringText(I18n.format("merchant.deprecated", new Object[0]), mouseX, mouseY);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public IMerchant getMerchant()
    {
        return this.merchant;
    }

    static class MerchantButton extends GuiButton
    {
        private final boolean field_146157_o;

        public MerchantButton(int buttonID, int x, int y, boolean p_i1095_4_)
        {
            super(buttonID, x, y, 12, 19, "");
            this.field_146157_o = p_i1095_4_;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                boolean lvt_4_1_ = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                int lvt_5_1_ = 0;
                int lvt_6_1_ = 176;

                if (!this.enabled)
                {
                    lvt_6_1_ += this.width * 2;
                }
                else if (lvt_4_1_)
                {
                    lvt_6_1_ += this.width;
                }

                if (!this.field_146157_o)
                {
                    lvt_5_1_ += this.height;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, lvt_6_1_, lvt_5_1_, this.width, this.height);
            }
        }
    }
}
