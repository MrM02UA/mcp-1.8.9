package net.minecraft.client.gui.inventory;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiBeacon extends GuiContainer
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation beaconGuiTextures = new ResourceLocation("textures/gui/container/beacon.png");
    private IInventory tileBeacon;
    private GuiBeacon.ConfirmButton beaconConfirmButton;
    private boolean buttonsNotDrawn;

    public GuiBeacon(InventoryPlayer playerInventory, IInventory tileBeaconIn)
    {
        super(new ContainerBeacon(playerInventory, tileBeaconIn));
        this.tileBeacon = tileBeaconIn;
        this.xSize = 230;
        this.ySize = 219;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107));
        this.buttonList.add(new GuiBeacon.CancelButton(-2, this.guiLeft + 190, this.guiTop + 107));
        this.buttonsNotDrawn = true;
        this.beaconConfirmButton.enabled = false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        int lvt_1_1_ = this.tileBeacon.getField(0);
        int lvt_2_1_ = this.tileBeacon.getField(1);
        int lvt_3_1_ = this.tileBeacon.getField(2);

        if (this.buttonsNotDrawn && lvt_1_1_ >= 0)
        {
            this.buttonsNotDrawn = false;

            for (int lvt_4_1_ = 0; lvt_4_1_ <= 2; ++lvt_4_1_)
            {
                int lvt_5_1_ = TileEntityBeacon.effectsList[lvt_4_1_].length;
                int lvt_6_1_ = lvt_5_1_ * 22 + (lvt_5_1_ - 1) * 2;

                for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_5_1_; ++lvt_7_1_)
                {
                    int lvt_8_1_ = TileEntityBeacon.effectsList[lvt_4_1_][lvt_7_1_].id;
                    GuiBeacon.PowerButton lvt_9_1_ = new GuiBeacon.PowerButton(lvt_4_1_ << 8 | lvt_8_1_, this.guiLeft + 76 + lvt_7_1_ * 24 - lvt_6_1_ / 2, this.guiTop + 22 + lvt_4_1_ * 25, lvt_8_1_, lvt_4_1_);
                    this.buttonList.add(lvt_9_1_);

                    if (lvt_4_1_ >= lvt_1_1_)
                    {
                        lvt_9_1_.enabled = false;
                    }
                    else if (lvt_8_1_ == lvt_2_1_)
                    {
                        lvt_9_1_.func_146140_b(true);
                    }
                }
            }

            int lvt_4_2_ = 3;
            int lvt_5_2_ = TileEntityBeacon.effectsList[lvt_4_2_].length + 1;
            int lvt_6_2_ = lvt_5_2_ * 22 + (lvt_5_2_ - 1) * 2;

            for (int lvt_7_2_ = 0; lvt_7_2_ < lvt_5_2_ - 1; ++lvt_7_2_)
            {
                int lvt_8_2_ = TileEntityBeacon.effectsList[lvt_4_2_][lvt_7_2_].id;
                GuiBeacon.PowerButton lvt_9_2_ = new GuiBeacon.PowerButton(lvt_4_2_ << 8 | lvt_8_2_, this.guiLeft + 167 + lvt_7_2_ * 24 - lvt_6_2_ / 2, this.guiTop + 47, lvt_8_2_, lvt_4_2_);
                this.buttonList.add(lvt_9_2_);

                if (lvt_4_2_ >= lvt_1_1_)
                {
                    lvt_9_2_.enabled = false;
                }
                else if (lvt_8_2_ == lvt_3_1_)
                {
                    lvt_9_2_.func_146140_b(true);
                }
            }

            if (lvt_2_1_ > 0)
            {
                GuiBeacon.PowerButton lvt_7_3_ = new GuiBeacon.PowerButton(lvt_4_2_ << 8 | lvt_2_1_, this.guiLeft + 167 + (lvt_5_2_ - 1) * 24 - lvt_6_2_ / 2, this.guiTop + 47, lvt_2_1_, lvt_4_2_);
                this.buttonList.add(lvt_7_3_);

                if (lvt_4_2_ >= lvt_1_1_)
                {
                    lvt_7_3_.enabled = false;
                }
                else if (lvt_2_1_ == lvt_3_1_)
                {
                    lvt_7_3_.func_146140_b(true);
                }
            }
        }

        this.beaconConfirmButton.enabled = this.tileBeacon.getStackInSlot(0) != null && lvt_2_1_ > 0;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == -2)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (button.id == -1)
        {
            String lvt_2_1_ = "MC|Beacon";
            PacketBuffer lvt_3_1_ = new PacketBuffer(Unpooled.buffer());
            lvt_3_1_.writeInt(this.tileBeacon.getField(1));
            lvt_3_1_.writeInt(this.tileBeacon.getField(2));
            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(lvt_2_1_, lvt_3_1_));
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (button instanceof GuiBeacon.PowerButton)
        {
            if (((GuiBeacon.PowerButton)button).func_146141_c())
            {
                return;
            }

            int lvt_2_2_ = button.id;
            int lvt_3_2_ = lvt_2_2_ & 255;
            int lvt_4_1_ = lvt_2_2_ >> 8;

            if (lvt_4_1_ < 3)
            {
                this.tileBeacon.setField(1, lvt_3_2_);
            }
            else
            {
                this.tileBeacon.setField(2, lvt_3_2_);
            }

            this.buttonList.clear();
            this.initGui();
            this.updateScreen();
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        RenderHelper.disableStandardItemLighting();
        this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.primary", new Object[0]), 62, 10, 14737632);
        this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.secondary", new Object[0]), 169, 10, 14737632);

        for (GuiButton lvt_4_1_ : this.buttonList)
        {
            if (lvt_4_1_.isMouseOver())
            {
                lvt_4_1_.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(beaconGuiTextures);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
        this.itemRender.zLevel = 100.0F;
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.emerald), lvt_4_1_ + 42, lvt_5_1_ + 109);
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.diamond), lvt_4_1_ + 42 + 22, lvt_5_1_ + 109);
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.gold_ingot), lvt_4_1_ + 42 + 44, lvt_5_1_ + 109);
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.iron_ingot), lvt_4_1_ + 42 + 66, lvt_5_1_ + 109);
        this.itemRender.zLevel = 0.0F;
    }

    static class Button extends GuiButton
    {
        private final ResourceLocation field_146145_o;
        private final int field_146144_p;
        private final int field_146143_q;
        private boolean field_146142_r;

        protected Button(int p_i1077_1_, int p_i1077_2_, int p_i1077_3_, ResourceLocation p_i1077_4_, int p_i1077_5_, int p_i1077_6_)
        {
            super(p_i1077_1_, p_i1077_2_, p_i1077_3_, 22, 22, "");
            this.field_146145_o = p_i1077_4_;
            this.field_146144_p = p_i1077_5_;
            this.field_146143_q = p_i1077_6_;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                mc.getTextureManager().bindTexture(GuiBeacon.beaconGuiTextures);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                int lvt_4_1_ = 219;
                int lvt_5_1_ = 0;

                if (!this.enabled)
                {
                    lvt_5_1_ += this.width * 2;
                }
                else if (this.field_146142_r)
                {
                    lvt_5_1_ += this.width * 1;
                }
                else if (this.hovered)
                {
                    lvt_5_1_ += this.width * 3;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, lvt_5_1_, lvt_4_1_, this.width, this.height);

                if (!GuiBeacon.beaconGuiTextures.equals(this.field_146145_o))
                {
                    mc.getTextureManager().bindTexture(this.field_146145_o);
                }

                this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.field_146144_p, this.field_146143_q, 18, 18);
            }
        }

        public boolean func_146141_c()
        {
            return this.field_146142_r;
        }

        public void func_146140_b(boolean p_146140_1_)
        {
            this.field_146142_r = p_146140_1_;
        }
    }

    class CancelButton extends GuiBeacon.Button
    {
        public CancelButton(int p_i1074_2_, int p_i1074_3_, int p_i1074_4_)
        {
            super(p_i1074_2_, p_i1074_3_, p_i1074_4_, GuiBeacon.beaconGuiTextures, 112, 220);
        }

        public void drawButtonForegroundLayer(int mouseX, int mouseY)
        {
            GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.cancel", new Object[0]), mouseX, mouseY);
        }
    }

    class ConfirmButton extends GuiBeacon.Button
    {
        public ConfirmButton(int p_i1075_2_, int p_i1075_3_, int p_i1075_4_)
        {
            super(p_i1075_2_, p_i1075_3_, p_i1075_4_, GuiBeacon.beaconGuiTextures, 90, 220);
        }

        public void drawButtonForegroundLayer(int mouseX, int mouseY)
        {
            GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.done", new Object[0]), mouseX, mouseY);
        }
    }

    class PowerButton extends GuiBeacon.Button
    {
        private final int field_146149_p;
        private final int field_146148_q;

        public PowerButton(int p_i1076_2_, int p_i1076_3_, int p_i1076_4_, int p_i1076_5_, int p_i1076_6_)
        {
            super(p_i1076_2_, p_i1076_3_, p_i1076_4_, GuiContainer.inventoryBackground, 0 + Potion.potionTypes[p_i1076_5_].getStatusIconIndex() % 8 * 18, 198 + Potion.potionTypes[p_i1076_5_].getStatusIconIndex() / 8 * 18);
            this.field_146149_p = p_i1076_5_;
            this.field_146148_q = p_i1076_6_;
        }

        public void drawButtonForegroundLayer(int mouseX, int mouseY)
        {
            String lvt_3_1_ = I18n.format(Potion.potionTypes[this.field_146149_p].getName(), new Object[0]);

            if (this.field_146148_q >= 3 && this.field_146149_p != Potion.regeneration.id)
            {
                lvt_3_1_ = lvt_3_1_ + " II";
            }

            GuiBeacon.this.drawCreativeTabHoveringText(lvt_3_1_, mouseX, mouseY);
        }
    }
}
