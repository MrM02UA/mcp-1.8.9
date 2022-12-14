package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiScreenHorseInventory extends GuiContainer
{
    private static final ResourceLocation horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");

    /** The player inventory bound to this GUI. */
    private IInventory playerInventory;

    /** The horse inventory bound to this GUI. */
    private IInventory horseInventory;

    /** The EntityHorse whose inventory is currently being accessed. */
    private EntityHorse horseEntity;

    /** The mouse x-position recorded during the last rendered frame. */
    private float mousePosx;

    /** The mouse y-position recorded during the last renderered frame. */
    private float mousePosY;

    public GuiScreenHorseInventory(IInventory playerInv, IInventory horseInv, EntityHorse horse)
    {
        super(new ContainerHorseInventory(playerInv, horseInv, horse, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = playerInv;
        this.horseInventory = horseInv;
        this.horseEntity = horse;
        this.allowUserInput = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(horseGuiTextures);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);

        if (this.horseEntity.isChested())
        {
            this.drawTexturedModalRect(lvt_4_1_ + 79, lvt_5_1_ + 17, 0, this.ySize, 90, 54);
        }

        if (this.horseEntity.canWearArmor())
        {
            this.drawTexturedModalRect(lvt_4_1_ + 7, lvt_5_1_ + 35, 0, this.ySize + 54, 18, 18);
        }

        GuiInventory.drawEntityOnScreen(lvt_4_1_ + 51, lvt_5_1_ + 60, 17, (float)(lvt_4_1_ + 51) - this.mousePosx, (float)(lvt_5_1_ + 75 - 50) - this.mousePosY, this.horseEntity);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.mousePosx = (float)mouseX;
        this.mousePosY = (float)mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
