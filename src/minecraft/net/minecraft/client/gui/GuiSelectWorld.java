package net.minecraft.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback
{
    private static final Logger logger = LogManager.getLogger();
    private final DateFormat field_146633_h = new SimpleDateFormat();
    protected GuiScreen parentScreen;
    protected String screenTitle = "Select world";
    private boolean field_146634_i;

    /** The list index of the currently-selected world */
    private int selectedIndex;
    private java.util.List<SaveFormatComparator> field_146639_s;
    private GuiSelectWorld.List availableWorlds;
    private String field_146637_u;
    private String field_146636_v;
    private String[] field_146635_w = new String[4];
    private boolean confirmingDelete;
    private GuiButton deleteButton;
    private GuiButton selectButton;
    private GuiButton renameButton;
    private GuiButton recreateButton;

    public GuiSelectWorld(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.screenTitle = I18n.format("selectWorld.title", new Object[0]);

        try
        {
            this.loadLevelList();
        }
        catch (AnvilConverterException var2)
        {
            logger.error("Couldn\'t load level list", var2);
            this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", var2.getMessage()));
            return;
        }

        this.field_146637_u = I18n.format("selectWorld.world", new Object[0]);
        this.field_146636_v = I18n.format("selectWorld.conversion", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator", new Object[0]);
        this.availableWorlds = new GuiSelectWorld.List(this.mc);
        this.availableWorlds.registerScrollButtons(4, 5);
        this.addWorldSelectionButtons();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.availableWorlds.handleMouseInput();
    }

    /**
     * Load the existing world saves for display
     */
    private void loadLevelList() throws AnvilConverterException
    {
        ISaveFormat lvt_1_1_ = this.mc.getSaveLoader();
        this.field_146639_s = lvt_1_1_.getSaveList();
        Collections.sort(this.field_146639_s);
        this.selectedIndex = -1;
    }

    protected String func_146621_a(int p_146621_1_)
    {
        return ((SaveFormatComparator)this.field_146639_s.get(p_146621_1_)).getFileName();
    }

    protected String func_146614_d(int p_146614_1_)
    {
        String lvt_2_1_ = ((SaveFormatComparator)this.field_146639_s.get(p_146614_1_)).getDisplayName();

        if (StringUtils.isEmpty(lvt_2_1_))
        {
            lvt_2_1_ = I18n.format("selectWorld.world", new Object[0]) + " " + (p_146614_1_ + 1);
        }

        return lvt_2_1_;
    }

    public void addWorldSelectionButtons()
    {
        this.buttonList.add(this.selectButton = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select", new Object[0])));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create", new Object[0])));
        this.buttonList.add(this.renameButton = new GuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename", new Object[0])));
        this.buttonList.add(this.deleteButton = new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete", new Object[0])));
        this.buttonList.add(this.recreateButton = new GuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel", new Object[0])));
        this.selectButton.enabled = false;
        this.deleteButton.enabled = false;
        this.renameButton.enabled = false;
        this.recreateButton.enabled = false;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 2)
            {
                String lvt_2_1_ = this.func_146614_d(this.selectedIndex);

                if (lvt_2_1_ != null)
                {
                    this.confirmingDelete = true;
                    GuiYesNo lvt_3_1_ = makeDeleteWorldYesNo(this, lvt_2_1_, this.selectedIndex);
                    this.mc.displayGuiScreen(lvt_3_1_);
                }
            }
            else if (button.id == 1)
            {
                this.func_146615_e(this.selectedIndex);
            }
            else if (button.id == 3)
            {
                this.mc.displayGuiScreen(new GuiCreateWorld(this));
            }
            else if (button.id == 6)
            {
                this.mc.displayGuiScreen(new GuiRenameWorld(this, this.func_146621_a(this.selectedIndex)));
            }
            else if (button.id == 0)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 7)
            {
                GuiCreateWorld lvt_2_2_ = new GuiCreateWorld(this);
                ISaveHandler lvt_3_2_ = this.mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.selectedIndex), false);
                WorldInfo lvt_4_1_ = lvt_3_2_.loadWorldInfo();
                lvt_3_2_.flush();
                lvt_2_2_.recreateFromExistingWorld(lvt_4_1_);
                this.mc.displayGuiScreen(lvt_2_2_);
            }
            else
            {
                this.availableWorlds.actionPerformed(button);
            }
        }
    }

    public void func_146615_e(int p_146615_1_)
    {
        this.mc.displayGuiScreen((GuiScreen)null);

        if (!this.field_146634_i)
        {
            this.field_146634_i = true;
            String lvt_2_1_ = this.func_146621_a(p_146615_1_);

            if (lvt_2_1_ == null)
            {
                lvt_2_1_ = "World" + p_146615_1_;
            }

            String lvt_3_1_ = this.func_146614_d(p_146615_1_);

            if (lvt_3_1_ == null)
            {
                lvt_3_1_ = "World" + p_146615_1_;
            }

            if (this.mc.getSaveLoader().canLoadWorld(lvt_2_1_))
            {
                this.mc.launchIntegratedServer(lvt_2_1_, lvt_3_1_, (WorldSettings)null);
            }
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        if (this.confirmingDelete)
        {
            this.confirmingDelete = false;

            if (result)
            {
                ISaveFormat lvt_3_1_ = this.mc.getSaveLoader();
                lvt_3_1_.flushCache();
                lvt_3_1_.deleteWorldDirectory(this.func_146621_a(id));

                try
                {
                    this.loadLevelList();
                }
                catch (AnvilConverterException var5)
                {
                    logger.error("Couldn\'t load level list", var5);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Generate a GuiYesNo asking for confirmation to delete a world
     *  
     * Called when user selects the "Delete" button.
     *  
     * @param selectWorld A reference back to the GuiSelectWorld spawning the GuiYesNo
     * @param name The name of the world selected for deletion
     * @param id An arbitrary integer passed back to selectWorld's confirmClicked method
     */
    public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback selectWorld, String name, int id)
    {
        String lvt_3_1_ = I18n.format("selectWorld.deleteQuestion", new Object[0]);
        String lvt_4_1_ = "\'" + name + "\' " + I18n.format("selectWorld.deleteWarning", new Object[0]);
        String lvt_5_1_ = I18n.format("selectWorld.deleteButton", new Object[0]);
        String lvt_6_1_ = I18n.format("gui.cancel", new Object[0]);
        GuiYesNo lvt_7_1_ = new GuiYesNo(selectWorld, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_, id);
        return lvt_7_1_;
    }

    class List extends GuiSlot
    {
        public List(Minecraft mcIn)
        {
            super(mcIn, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
        }

        protected int getSize()
        {
            return GuiSelectWorld.this.field_146639_s.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            GuiSelectWorld.this.selectedIndex = slotIndex;
            boolean lvt_5_1_ = GuiSelectWorld.this.selectedIndex >= 0 && GuiSelectWorld.this.selectedIndex < this.getSize();
            GuiSelectWorld.this.selectButton.enabled = lvt_5_1_;
            GuiSelectWorld.this.deleteButton.enabled = lvt_5_1_;
            GuiSelectWorld.this.renameButton.enabled = lvt_5_1_;
            GuiSelectWorld.this.recreateButton.enabled = lvt_5_1_;

            if (isDoubleClick && lvt_5_1_)
            {
                GuiSelectWorld.this.func_146615_e(slotIndex);
            }
        }

        protected boolean isSelected(int slotIndex)
        {
            return slotIndex == GuiSelectWorld.this.selectedIndex;
        }

        protected int getContentHeight()
        {
            return GuiSelectWorld.this.field_146639_s.size() * 36;
        }

        protected void drawBackground()
        {
            GuiSelectWorld.this.drawDefaultBackground();
        }

        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
        {
            SaveFormatComparator lvt_7_1_ = (SaveFormatComparator)GuiSelectWorld.this.field_146639_s.get(entryID);
            String lvt_8_1_ = lvt_7_1_.getDisplayName();

            if (StringUtils.isEmpty(lvt_8_1_))
            {
                lvt_8_1_ = GuiSelectWorld.this.field_146637_u + " " + (entryID + 1);
            }

            String lvt_9_1_ = lvt_7_1_.getFileName();
            lvt_9_1_ = lvt_9_1_ + " (" + GuiSelectWorld.this.field_146633_h.format(new Date(lvt_7_1_.getLastTimePlayed()));
            lvt_9_1_ = lvt_9_1_ + ")";
            String lvt_10_1_ = "";

            if (lvt_7_1_.requiresConversion())
            {
                lvt_10_1_ = GuiSelectWorld.this.field_146636_v + " " + lvt_10_1_;
            }
            else
            {
                lvt_10_1_ = GuiSelectWorld.this.field_146635_w[lvt_7_1_.getEnumGameType().getID()];

                if (lvt_7_1_.isHardcoreModeEnabled())
                {
                    lvt_10_1_ = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore", new Object[0]) + EnumChatFormatting.RESET;
                }

                if (lvt_7_1_.getCheatsEnabled())
                {
                    lvt_10_1_ = lvt_10_1_ + ", " + I18n.format("selectWorld.cheats", new Object[0]);
                }
            }

            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, lvt_8_1_, p_180791_2_ + 2, p_180791_3_ + 1, 16777215);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, lvt_9_1_, p_180791_2_ + 2, p_180791_3_ + 12, 8421504);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, lvt_10_1_, p_180791_2_ + 2, p_180791_3_ + 12 + 10, 8421504);
        }
    }
}
