package net.minecraft.client.renderer;

import java.util.Collection;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class InventoryEffectRenderer extends GuiContainer
{
    /** True if there is some potion effect to display */
    private boolean hasActivePotionEffects;

    public InventoryEffectRenderer(Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.updateActivePotionEffects();
    }

    protected void updateActivePotionEffects()
    {
        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty())
        {
            this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            this.hasActivePotionEffects = true;
        }
        else
        {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hasActivePotionEffects)
        {
            this.drawActivePotionEffects();
        }
    }

    /**
     * Display the potion effects list
     */
    private void drawActivePotionEffects()
    {
        int lvt_1_1_ = this.guiLeft - 124;
        int lvt_2_1_ = this.guiTop;
        int lvt_3_1_ = 166;
        Collection<PotionEffect> lvt_4_1_ = this.mc.thePlayer.getActivePotionEffects();

        if (!lvt_4_1_.isEmpty())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int lvt_5_1_ = 33;

            if (lvt_4_1_.size() > 5)
            {
                lvt_5_1_ = 132 / (lvt_4_1_.size() - 1);
            }

            for (PotionEffect lvt_7_1_ : this.mc.thePlayer.getActivePotionEffects())
            {
                Potion lvt_8_1_ = Potion.potionTypes[lvt_7_1_.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(inventoryBackground);
                this.drawTexturedModalRect(lvt_1_1_, lvt_2_1_, 0, 166, 140, 32);

                if (lvt_8_1_.hasStatusIcon())
                {
                    int lvt_9_1_ = lvt_8_1_.getStatusIconIndex();
                    this.drawTexturedModalRect(lvt_1_1_ + 6, lvt_2_1_ + 7, 0 + lvt_9_1_ % 8 * 18, 198 + lvt_9_1_ / 8 * 18, 18, 18);
                }

                String lvt_9_2_ = I18n.format(lvt_8_1_.getName(), new Object[0]);

                if (lvt_7_1_.getAmplifier() == 1)
                {
                    lvt_9_2_ = lvt_9_2_ + " " + I18n.format("enchantment.level.2", new Object[0]);
                }
                else if (lvt_7_1_.getAmplifier() == 2)
                {
                    lvt_9_2_ = lvt_9_2_ + " " + I18n.format("enchantment.level.3", new Object[0]);
                }
                else if (lvt_7_1_.getAmplifier() == 3)
                {
                    lvt_9_2_ = lvt_9_2_ + " " + I18n.format("enchantment.level.4", new Object[0]);
                }

                this.fontRendererObj.drawStringWithShadow(lvt_9_2_, (float)(lvt_1_1_ + 10 + 18), (float)(lvt_2_1_ + 6), 16777215);
                String lvt_10_1_ = Potion.getDurationString(lvt_7_1_);
                this.fontRendererObj.drawStringWithShadow(lvt_10_1_, (float)(lvt_1_1_ + 10 + 18), (float)(lvt_2_1_ + 6 + 10), 8355711);
                lvt_2_1_ += lvt_5_1_;
            }
        }
    }
}
