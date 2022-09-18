package net.minecraft.client.gui;

import java.io.IOException;
import java.net.URI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenDemo extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation field_146348_f = new ResourceLocation("textures/gui/demo_background.png");

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        int lvt_1_1_ = -16;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + lvt_1_1_, 114, 20, I18n.format("demo.help.buy", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + lvt_1_1_, 114, 20, I18n.format("demo.help.later", new Object[0])));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 1:
                button.enabled = false;

                try
                {
                    Class<?> lvt_2_1_ = Class.forName("java.awt.Desktop");
                    Object lvt_3_1_ = lvt_2_1_.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    lvt_2_1_.getMethod("browse", new Class[] {URI.class}).invoke(lvt_3_1_, new Object[] {new URI("http://www.minecraft.net/store?source=demo")});
                }
                catch (Throwable var4)
                {
                    logger.error("Couldn\'t open link", var4);
                }

                break;

            case 2:
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground()
    {
        super.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_146348_f);
        int lvt_1_1_ = (this.width - 248) / 2;
        int lvt_2_1_ = (this.height - 166) / 2;
        this.drawTexturedModalRect(lvt_1_1_, lvt_2_1_, 0, 0, 248, 166);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int lvt_4_1_ = (this.width - 248) / 2 + 10;
        int lvt_5_1_ = (this.height - 166) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format("demo.help.title", new Object[0]), lvt_4_1_, lvt_5_1_, 2039583);
        lvt_5_1_ = lvt_5_1_ + 12;
        GameSettings lvt_6_1_ = this.mc.gameSettings;
        this.fontRendererObj.drawString(I18n.format("demo.help.movementShort", new Object[] {GameSettings.getKeyDisplayString(lvt_6_1_.keyBindForward.getKeyCode()), GameSettings.getKeyDisplayString(lvt_6_1_.keyBindLeft.getKeyCode()), GameSettings.getKeyDisplayString(lvt_6_1_.keyBindBack.getKeyCode()), GameSettings.getKeyDisplayString(lvt_6_1_.keyBindRight.getKeyCode())}), lvt_4_1_, lvt_5_1_, 5197647);
        this.fontRendererObj.drawString(I18n.format("demo.help.movementMouse", new Object[0]), lvt_4_1_, lvt_5_1_ + 12, 5197647);
        this.fontRendererObj.drawString(I18n.format("demo.help.jump", new Object[] {GameSettings.getKeyDisplayString(lvt_6_1_.keyBindJump.getKeyCode())}), lvt_4_1_, lvt_5_1_ + 24, 5197647);
        this.fontRendererObj.drawString(I18n.format("demo.help.inventory", new Object[] {GameSettings.getKeyDisplayString(lvt_6_1_.keyBindInventory.getKeyCode())}), lvt_4_1_, lvt_5_1_ + 36, 5197647);
        this.fontRendererObj.drawSplitString(I18n.format("demo.help.fullWrapped", new Object[0]), lvt_4_1_, lvt_5_1_ + 68, 218, 2039583);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
