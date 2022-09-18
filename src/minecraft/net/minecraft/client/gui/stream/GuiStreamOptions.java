package net.minecraft.client.gui.stream;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;

public class GuiStreamOptions extends GuiScreen
{
    private static final GameSettings.Options[] field_152312_a = new GameSettings.Options[] {GameSettings.Options.STREAM_BYTES_PER_PIXEL, GameSettings.Options.STREAM_FPS, GameSettings.Options.STREAM_KBPS, GameSettings.Options.STREAM_SEND_METADATA, GameSettings.Options.STREAM_VOLUME_MIC, GameSettings.Options.STREAM_VOLUME_SYSTEM, GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR, GameSettings.Options.STREAM_COMPRESSION};
    private static final GameSettings.Options[] field_152316_f = new GameSettings.Options[] {GameSettings.Options.STREAM_CHAT_ENABLED, GameSettings.Options.STREAM_CHAT_USER_FILTER};
    private final GuiScreen parentScreen;
    private final GameSettings field_152318_h;
    private String field_152319_i;
    private String field_152313_r;
    private int field_152314_s;
    private boolean field_152315_t = false;

    public GuiStreamOptions(GuiScreen parentScreenIn, GameSettings p_i1073_2_)
    {
        this.parentScreen = parentScreenIn;
        this.field_152318_h = p_i1073_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        int lvt_1_1_ = 0;
        this.field_152319_i = I18n.format("options.stream.title", new Object[0]);
        this.field_152313_r = I18n.format("options.stream.chat.title", new Object[0]);

        for (GameSettings.Options lvt_5_1_ : field_152312_a)
        {
            if (lvt_5_1_.getEnumFloat())
            {
                this.buttonList.add(new GuiOptionSlider(lvt_5_1_.returnEnumOrdinal(), this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 + 24 * (lvt_1_1_ >> 1), lvt_5_1_));
            }
            else
            {
                this.buttonList.add(new GuiOptionButton(lvt_5_1_.returnEnumOrdinal(), this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 + 24 * (lvt_1_1_ >> 1), lvt_5_1_, this.field_152318_h.getKeyBinding(lvt_5_1_)));
            }

            ++lvt_1_1_;
        }

        if (lvt_1_1_ % 2 == 1)
        {
            ++lvt_1_1_;
        }

        this.field_152314_s = this.height / 6 + 24 * (lvt_1_1_ >> 1) + 6;
        lvt_1_1_ = lvt_1_1_ + 2;

        for (GameSettings.Options lvt_5_2_ : field_152316_f)
        {
            if (lvt_5_2_.getEnumFloat())
            {
                this.buttonList.add(new GuiOptionSlider(lvt_5_2_.returnEnumOrdinal(), this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 + 24 * (lvt_1_1_ >> 1), lvt_5_2_));
            }
            else
            {
                this.buttonList.add(new GuiOptionButton(lvt_5_2_.returnEnumOrdinal(), this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 + 24 * (lvt_1_1_ >> 1), lvt_5_2_, this.field_152318_h.getKeyBinding(lvt_5_2_)));
            }

            ++lvt_1_1_;
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height / 6 + 168, 150, 20, I18n.format("gui.done", new Object[0])));
        GuiButton lvt_2_3_ = new GuiButton(201, this.width / 2 + 5, this.height / 6 + 168, 150, 20, I18n.format("options.stream.ingestSelection", new Object[0]));
        lvt_2_3_.enabled = this.mc.getTwitchStream().isReadyToBroadcast() && this.mc.getTwitchStream().func_152925_v().length > 0 || this.mc.getTwitchStream().func_152908_z();
        this.buttonList.add(lvt_2_3_);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id < 100 && button instanceof GuiOptionButton)
            {
                GameSettings.Options lvt_2_1_ = ((GuiOptionButton)button).returnEnumOptions();
                this.field_152318_h.setOptionValue(lvt_2_1_, 1);
                button.displayString = this.field_152318_h.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));

                if (this.mc.getTwitchStream().isBroadcasting() && lvt_2_1_ != GameSettings.Options.STREAM_CHAT_ENABLED && lvt_2_1_ != GameSettings.Options.STREAM_CHAT_USER_FILTER)
                {
                    this.field_152315_t = true;
                }
            }
            else if (button instanceof GuiOptionSlider)
            {
                if (button.id == GameSettings.Options.STREAM_VOLUME_MIC.returnEnumOrdinal())
                {
                    this.mc.getTwitchStream().updateStreamVolume();
                }
                else if (button.id == GameSettings.Options.STREAM_VOLUME_SYSTEM.returnEnumOrdinal())
                {
                    this.mc.getTwitchStream().updateStreamVolume();
                }
                else if (this.mc.getTwitchStream().isBroadcasting())
                {
                    this.field_152315_t = true;
                }
            }

            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 201)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiIngestServers(this));
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_152319_i, this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.field_152313_r, this.width / 2, this.field_152314_s, 16777215);

        if (this.field_152315_t)
        {
            this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.RED + I18n.format("options.stream.changes", new Object[0]), this.width / 2, 20 + this.fontRendererObj.FONT_HEIGHT, 16777215);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
