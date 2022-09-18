package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

public class GuiScreenResourcePacks extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private final GuiScreen parentScreen;
    private List<ResourcePackListEntry> availableResourcePacks;
    private List<ResourcePackListEntry> selectedResourcePacks;

    /** List component that contains the available resource packs */
    private GuiResourcePackAvailable availableResourcePacksList;

    /** List component that contains the selected resource packs */
    private GuiResourcePackSelected selectedResourcePacksList;
    private boolean changed = false;

    public GuiScreenResourcePacks(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder", new Object[0])));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done", new Object[0])));

        if (!this.changed)
        {
            this.availableResourcePacks = Lists.newArrayList();
            this.selectedResourcePacks = Lists.newArrayList();
            ResourcePackRepository lvt_1_1_ = this.mc.getResourcePackRepository();
            lvt_1_1_.updateRepositoryEntriesAll();
            List<ResourcePackRepository.Entry> lvt_2_1_ = Lists.newArrayList(lvt_1_1_.getRepositoryEntriesAll());
            lvt_2_1_.removeAll(lvt_1_1_.getRepositoryEntries());

            for (ResourcePackRepository.Entry lvt_4_1_ : lvt_2_1_)
            {
                this.availableResourcePacks.add(new ResourcePackListEntryFound(this, lvt_4_1_));
            }

            for (ResourcePackRepository.Entry lvt_4_2_ : Lists.reverse(lvt_1_1_.getRepositoryEntries()))
            {
                this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, lvt_4_2_));
            }

            this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
        }

        this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
        this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableResourcePacksList.registerScrollButtons(7, 8);
        this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
        this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
        this.selectedResourcePacksList.registerScrollButtons(7, 8);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.selectedResourcePacksList.handleMouseInput();
        this.availableResourcePacksList.handleMouseInput();
    }

    public boolean hasResourcePackEntry(ResourcePackListEntry p_146961_1_)
    {
        return this.selectedResourcePacks.contains(p_146961_1_);
    }

    public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry p_146962_1_)
    {
        return this.hasResourcePackEntry(p_146962_1_) ? this.selectedResourcePacks : this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getAvailableResourcePacks()
    {
        return this.availableResourcePacks;
    }

    public List<ResourcePackListEntry> getSelectedResourcePacks()
    {
        return this.selectedResourcePacks;
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
                File lvt_2_1_ = this.mc.getResourcePackRepository().getDirResourcepacks();
                String lvt_3_1_ = lvt_2_1_.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.OSX)
                {
                    try
                    {
                        logger.info(lvt_3_1_);
                        Runtime.getRuntime().exec(new String[] {"/usr/bin/open", lvt_3_1_});
                        return;
                    }
                    catch (IOException var9)
                    {
                        logger.error("Couldn\'t open file", var9);
                    }
                }
                else if (Util.getOSType() == Util.EnumOS.WINDOWS)
                {
                    String lvt_4_2_ = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] {lvt_3_1_});

                    try
                    {
                        Runtime.getRuntime().exec(lvt_4_2_);
                        return;
                    }
                    catch (IOException var8)
                    {
                        logger.error("Couldn\'t open file", var8);
                    }
                }

                boolean lvt_4_3_ = false;

                try
                {
                    Class<?> lvt_5_2_ = Class.forName("java.awt.Desktop");
                    Object lvt_6_1_ = lvt_5_2_.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    lvt_5_2_.getMethod("browse", new Class[] {URI.class}).invoke(lvt_6_1_, new Object[] {lvt_2_1_.toURI()});
                }
                catch (Throwable var7)
                {
                    logger.error("Couldn\'t open link", var7);
                    lvt_4_3_ = true;
                }

                if (lvt_4_3_)
                {
                    logger.info("Opening via system class!");
                    Sys.openURL("file://" + lvt_3_1_);
                }
            }
            else if (button.id == 1)
            {
                if (this.changed)
                {
                    List<ResourcePackRepository.Entry> lvt_2_2_ = Lists.newArrayList();

                    for (ResourcePackListEntry lvt_4_4_ : this.selectedResourcePacks)
                    {
                        if (lvt_4_4_ instanceof ResourcePackListEntryFound)
                        {
                            lvt_2_2_.add(((ResourcePackListEntryFound)lvt_4_4_).func_148318_i());
                        }
                    }

                    Collections.reverse(lvt_2_2_);
                    this.mc.getResourcePackRepository().setRepositories(lvt_2_2_);
                    this.mc.gameSettings.resourcePacks.clear();
                    this.mc.gameSettings.incompatibleResourcePacks.clear();

                    for (ResourcePackRepository.Entry lvt_4_5_ : lvt_2_2_)
                    {
                        this.mc.gameSettings.resourcePacks.add(lvt_4_5_.getResourcePackName());

                        if (lvt_4_5_.func_183027_f() != 1)
                        {
                            this.mc.gameSettings.incompatibleResourcePacks.add(lvt_4_5_.getResourcePackName());
                        }
                    }

                    this.mc.gameSettings.saveOptions();
                    this.mc.refreshResources();
                }

                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawBackground(0);
        this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title", new Object[0]), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Marks the selected resource packs list as changed to trigger a resource reload when the screen is closed
     */
    public void markChanged()
    {
        this.changed = true;
    }
}
