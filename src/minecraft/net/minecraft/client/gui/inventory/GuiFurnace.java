package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer
{
    private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");

    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    private IInventory tileFurnace;

    public GuiFurnace(InventoryPlayer playerInv, IInventory furnaceInv)
    {
        super(new ContainerFurnace(playerInv, furnaceInv));
        this.playerInventory = playerInv;
        this.tileFurnace = furnaceInv;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String lvt_3_1_ = this.tileFurnace.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(lvt_3_1_, this.xSize / 2 - this.fontRendererObj.getStringWidth(lvt_3_1_) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);

        if (TileEntityFurnace.isBurning(this.tileFurnace))
        {
            int lvt_6_1_ = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(lvt_4_1_ + 56, lvt_5_1_ + 36 + 12 - lvt_6_1_, 176, 12 - lvt_6_1_, 14, lvt_6_1_ + 1);
        }

        int lvt_6_2_ = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(lvt_4_1_ + 79, lvt_5_1_ + 34, 176, 14, lvt_6_2_ + 1, 16);
    }

    private int getCookProgressScaled(int pixels)
    {
        int lvt_2_1_ = this.tileFurnace.getField(2);
        int lvt_3_1_ = this.tileFurnace.getField(3);
        return lvt_3_1_ != 0 && lvt_2_1_ != 0 ? lvt_2_1_ * pixels / lvt_3_1_ : 0;
    }

    private int getBurnLeftScaled(int pixels)
    {
        int lvt_2_1_ = this.tileFurnace.getField(1);

        if (lvt_2_1_ == 0)
        {
            lvt_2_1_ = 200;
        }

        return this.tileFurnace.getField(0) * pixels / lvt_2_1_;
    }
}
