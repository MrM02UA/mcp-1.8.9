package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBrewingStand extends GuiContainer
{
    private static final ResourceLocation brewingStandGuiTextures = new ResourceLocation("textures/gui/container/brewing_stand.png");

    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    private IInventory tileBrewingStand;

    public GuiBrewingStand(InventoryPlayer playerInv, IInventory p_i45506_2_)
    {
        super(new ContainerBrewingStand(playerInv, p_i45506_2_));
        this.playerInventory = playerInv;
        this.tileBrewingStand = p_i45506_2_;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String lvt_3_1_ = this.tileBrewingStand.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(lvt_3_1_, this.xSize / 2 - this.fontRendererObj.getStringWidth(lvt_3_1_) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(brewingStandGuiTextures);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
        int lvt_6_1_ = this.tileBrewingStand.getField(0);

        if (lvt_6_1_ > 0)
        {
            int lvt_7_1_ = (int)(28.0F * (1.0F - (float)lvt_6_1_ / 400.0F));

            if (lvt_7_1_ > 0)
            {
                this.drawTexturedModalRect(lvt_4_1_ + 97, lvt_5_1_ + 16, 176, 0, 9, lvt_7_1_);
            }

            int lvt_8_1_ = lvt_6_1_ / 2 % 7;

            switch (lvt_8_1_)
            {
                case 0:
                    lvt_7_1_ = 29;
                    break;

                case 1:
                    lvt_7_1_ = 24;
                    break;

                case 2:
                    lvt_7_1_ = 20;
                    break;

                case 3:
                    lvt_7_1_ = 16;
                    break;

                case 4:
                    lvt_7_1_ = 11;
                    break;

                case 5:
                    lvt_7_1_ = 6;
                    break;

                case 6:
                    lvt_7_1_ = 0;
            }

            if (lvt_7_1_ > 0)
            {
                this.drawTexturedModalRect(lvt_4_1_ + 65, lvt_5_1_ + 14 + 29 - lvt_7_1_, 185, 29 - lvt_7_1_, 12, lvt_7_1_);
            }
        }
    }
}
