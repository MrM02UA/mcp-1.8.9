package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet(new String[] {"http", "https"});
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');

    /** Reference to the Minecraft object. */
    protected Minecraft mc;

    /**
     * Holds a instance of RenderItem, used to draw the achievement icons on screen (is based on ItemStack)
     */
    protected RenderItem itemRender;

    /** The width of the screen object. */
    public int width;

    /** The height of the screen object. */
    public int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;

    /** The FontRenderer used by GuiScreen */
    protected FontRenderer fontRendererObj;

    /** The button that was just pressed. */
    private GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;

    /**
     * Tracks the number of fingers currently on the screen. Prevents subsequent fingers registering as clicks.
     */
    private int touchValue;
    private URI clickedLinkURI;

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < this.buttonList.size(); ++lvt_4_1_)
        {
            ((GuiButton)this.buttonList.get(lvt_4_1_)).drawButton(this.mc, mouseX, mouseY);
        }

        for (int lvt_4_2_ = 0; lvt_4_2_ < this.labelList.size(); ++lvt_4_2_)
        {
            ((GuiLabel)this.labelList.get(lvt_4_2_)).drawLabel(this.mc, mouseX, mouseY);
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }

    /**
     * Returns a string stored in the system clipboard.
     */
    public static String getClipboardString()
    {
        try
        {
            Transferable lvt_0_1_ = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);

            if (lvt_0_1_ != null && lvt_0_1_.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                return (String)lvt_0_1_.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception var1)
        {
            ;
        }

        return "";
    }

    /**
     * Stores the given string in the system clipboard
     */
    public static void setClipboardString(String copyText)
    {
        if (!StringUtils.isEmpty(copyText))
        {
            try
            {
                StringSelection lvt_1_1_ = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(lvt_1_1_, (ClipboardOwner)null);
            }
            catch (Exception var2)
            {
                ;
            }
        }
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        List<String> lvt_4_1_ = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.size(); ++lvt_5_1_)
        {
            if (lvt_5_1_ == 0)
            {
                lvt_4_1_.set(lvt_5_1_, stack.getRarity().rarityColor + (String)lvt_4_1_.get(lvt_5_1_));
            }
            else
            {
                lvt_4_1_.set(lvt_5_1_, EnumChatFormatting.GRAY + (String)lvt_4_1_.get(lvt_5_1_));
            }
        }

        this.drawHoveringText(lvt_4_1_, x, y);
    }

    /**
     * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current
     * mouse x position, current mouse y position.
     */
    protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY)
    {
        this.drawHoveringText(Arrays.asList(new String[] {tabName}), mouseX, mouseY);
    }

    /**
     * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
     */
    protected void drawHoveringText(List<String> textLines, int x, int y)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int lvt_4_1_ = 0;

            for (String lvt_6_1_ : textLines)
            {
                int lvt_7_1_ = this.fontRendererObj.getStringWidth(lvt_6_1_);

                if (lvt_7_1_ > lvt_4_1_)
                {
                    lvt_4_1_ = lvt_7_1_;
                }
            }

            int lvt_5_2_ = x + 12;
            int lvt_6_2_ = y - 12;
            int lvt_8_1_ = 8;

            if (textLines.size() > 1)
            {
                lvt_8_1_ += 2 + (textLines.size() - 1) * 10;
            }

            if (lvt_5_2_ + lvt_4_1_ > this.width)
            {
                lvt_5_2_ -= 28 + lvt_4_1_;
            }

            if (lvt_6_2_ + lvt_8_1_ + 6 > this.height)
            {
                lvt_6_2_ = this.height - lvt_8_1_ - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int lvt_9_1_ = -267386864;
            this.drawGradientRect(lvt_5_2_ - 3, lvt_6_2_ - 4, lvt_5_2_ + lvt_4_1_ + 3, lvt_6_2_ - 3, lvt_9_1_, lvt_9_1_);
            this.drawGradientRect(lvt_5_2_ - 3, lvt_6_2_ + lvt_8_1_ + 3, lvt_5_2_ + lvt_4_1_ + 3, lvt_6_2_ + lvt_8_1_ + 4, lvt_9_1_, lvt_9_1_);
            this.drawGradientRect(lvt_5_2_ - 3, lvt_6_2_ - 3, lvt_5_2_ + lvt_4_1_ + 3, lvt_6_2_ + lvt_8_1_ + 3, lvt_9_1_, lvt_9_1_);
            this.drawGradientRect(lvt_5_2_ - 4, lvt_6_2_ - 3, lvt_5_2_ - 3, lvt_6_2_ + lvt_8_1_ + 3, lvt_9_1_, lvt_9_1_);
            this.drawGradientRect(lvt_5_2_ + lvt_4_1_ + 3, lvt_6_2_ - 3, lvt_5_2_ + lvt_4_1_ + 4, lvt_6_2_ + lvt_8_1_ + 3, lvt_9_1_, lvt_9_1_);
            int lvt_10_1_ = 1347420415;
            int lvt_11_1_ = (lvt_10_1_ & 16711422) >> 1 | lvt_10_1_ & -16777216;
            this.drawGradientRect(lvt_5_2_ - 3, lvt_6_2_ - 3 + 1, lvt_5_2_ - 3 + 1, lvt_6_2_ + lvt_8_1_ + 3 - 1, lvt_10_1_, lvt_11_1_);
            this.drawGradientRect(lvt_5_2_ + lvt_4_1_ + 2, lvt_6_2_ - 3 + 1, lvt_5_2_ + lvt_4_1_ + 3, lvt_6_2_ + lvt_8_1_ + 3 - 1, lvt_10_1_, lvt_11_1_);
            this.drawGradientRect(lvt_5_2_ - 3, lvt_6_2_ - 3, lvt_5_2_ + lvt_4_1_ + 3, lvt_6_2_ - 3 + 1, lvt_10_1_, lvt_10_1_);
            this.drawGradientRect(lvt_5_2_ - 3, lvt_6_2_ + lvt_8_1_ + 2, lvt_5_2_ + lvt_4_1_ + 3, lvt_6_2_ + lvt_8_1_ + 3, lvt_11_1_, lvt_11_1_);

            for (int lvt_12_1_ = 0; lvt_12_1_ < textLines.size(); ++lvt_12_1_)
            {
                String lvt_13_1_ = (String)textLines.get(lvt_12_1_);
                this.fontRendererObj.drawStringWithShadow(lvt_13_1_, (float)lvt_5_2_, (float)lvt_6_2_, -1);

                if (lvt_12_1_ == 0)
                {
                    lvt_6_2_ += 2;
                }

                lvt_6_2_ += 10;
            }

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    /**
     * Draws the hover event specified by the given chat component
     *  
     * @param component The IChatComponent to render
     * @param x The x position where to render
     * @param y The y position where to render
     */
    protected void handleComponentHover(IChatComponent component, int x, int y)
    {
        if (component != null && component.getChatStyle().getChatHoverEvent() != null)
        {
            HoverEvent lvt_4_1_ = component.getChatStyle().getChatHoverEvent();

            if (lvt_4_1_.getAction() == HoverEvent.Action.SHOW_ITEM)
            {
                ItemStack lvt_5_1_ = null;

                try
                {
                    NBTBase lvt_6_1_ = JsonToNBT.getTagFromJson(lvt_4_1_.getValue().getUnformattedText());

                    if (lvt_6_1_ instanceof NBTTagCompound)
                    {
                        lvt_5_1_ = ItemStack.loadItemStackFromNBT((NBTTagCompound)lvt_6_1_);
                    }
                }
                catch (NBTException var11)
                {
                    ;
                }

                if (lvt_5_1_ != null)
                {
                    this.renderToolTip(lvt_5_1_, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
                }
            }
            else if (lvt_4_1_.getAction() == HoverEvent.Action.SHOW_ENTITY)
            {
                if (this.mc.gameSettings.advancedItemTooltips)
                {
                    try
                    {
                        NBTBase lvt_5_2_ = JsonToNBT.getTagFromJson(lvt_4_1_.getValue().getUnformattedText());

                        if (lvt_5_2_ instanceof NBTTagCompound)
                        {
                            List<String> lvt_6_2_ = Lists.newArrayList();
                            NBTTagCompound lvt_7_1_ = (NBTTagCompound)lvt_5_2_;
                            lvt_6_2_.add(lvt_7_1_.getString("name"));

                            if (lvt_7_1_.hasKey("type", 8))
                            {
                                String lvt_8_1_ = lvt_7_1_.getString("type");
                                lvt_6_2_.add("Type: " + lvt_8_1_ + " (" + EntityList.getIDFromString(lvt_8_1_) + ")");
                            }

                            lvt_6_2_.add(lvt_7_1_.getString("id"));
                            this.drawHoveringText(lvt_6_2_, x, y);
                        }
                        else
                        {
                            this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                        }
                    }
                    catch (NBTException var10)
                    {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                    }
                }
            }
            else if (lvt_4_1_.getAction() == HoverEvent.Action.SHOW_TEXT)
            {
                this.drawHoveringText(NEWLINE_SPLITTER.splitToList(lvt_4_1_.getValue().getFormattedText()), x, y);
            }
            else if (lvt_4_1_.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT)
            {
                StatBase lvt_5_4_ = StatList.getOneShotStat(lvt_4_1_.getValue().getUnformattedText());

                if (lvt_5_4_ != null)
                {
                    IChatComponent lvt_6_3_ = lvt_5_4_.getStatName();
                    IChatComponent lvt_7_2_ = new ChatComponentTranslation("stats.tooltip.type." + (lvt_5_4_.isAchievement() ? "achievement" : "statistic"), new Object[0]);
                    lvt_7_2_.getChatStyle().setItalic(Boolean.valueOf(true));
                    String lvt_8_2_ = lvt_5_4_ instanceof Achievement ? ((Achievement)lvt_5_4_).getDescription() : null;
                    List<String> lvt_9_1_ = Lists.newArrayList(new String[] {lvt_6_3_.getFormattedText(), lvt_7_2_.getFormattedText()});

                    if (lvt_8_2_ != null)
                    {
                        lvt_9_1_.addAll(this.fontRendererObj.listFormattedStringToWidth(lvt_8_2_, 150));
                    }

                    this.drawHoveringText(lvt_9_1_, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
                }
            }

            GlStateManager.disableLighting();
        }
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
    }

    /**
     * Executes the click event specified by the given chat component
     *  
     * @param component The ChatComponent to check for click
     */
    protected boolean handleComponentClick(IChatComponent component)
    {
        if (component == null)
        {
            return false;
        }
        else
        {
            ClickEvent lvt_2_1_ = component.getChatStyle().getChatClickEvent();

            if (isShiftKeyDown())
            {
                if (component.getChatStyle().getInsertion() != null)
                {
                    this.setText(component.getChatStyle().getInsertion(), false);
                }
            }
            else if (lvt_2_1_ != null)
            {
                if (lvt_2_1_.getAction() == ClickEvent.Action.OPEN_URL)
                {
                    if (!this.mc.gameSettings.chatLinks)
                    {
                        return false;
                    }

                    try
                    {
                        URI lvt_3_1_ = new URI(lvt_2_1_.getValue());
                        String lvt_4_1_ = lvt_3_1_.getScheme();

                        if (lvt_4_1_ == null)
                        {
                            throw new URISyntaxException(lvt_2_1_.getValue(), "Missing protocol");
                        }

                        if (!PROTOCOLS.contains(lvt_4_1_.toLowerCase()))
                        {
                            throw new URISyntaxException(lvt_2_1_.getValue(), "Unsupported protocol: " + lvt_4_1_.toLowerCase());
                        }

                        if (this.mc.gameSettings.chatLinksPrompt)
                        {
                            this.clickedLinkURI = lvt_3_1_;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, lvt_2_1_.getValue(), 31102009, false));
                        }
                        else
                        {
                            this.openWebLink(lvt_3_1_);
                        }
                    }
                    catch (URISyntaxException var5)
                    {
                        LOGGER.error("Can\'t open url for " + lvt_2_1_, var5);
                    }
                }
                else if (lvt_2_1_.getAction() == ClickEvent.Action.OPEN_FILE)
                {
                    URI lvt_3_3_ = (new File(lvt_2_1_.getValue())).toURI();
                    this.openWebLink(lvt_3_3_);
                }
                else if (lvt_2_1_.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                {
                    this.setText(lvt_2_1_.getValue(), true);
                }
                else if (lvt_2_1_.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    this.sendChatMessage(lvt_2_1_.getValue(), false);
                }
                else if (lvt_2_1_.getAction() == ClickEvent.Action.TWITCH_USER_INFO)
                {
                    ChatUserInfo lvt_3_4_ = this.mc.getTwitchStream().func_152926_a(lvt_2_1_.getValue());

                    if (lvt_3_4_ != null)
                    {
                        this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc.getTwitchStream(), lvt_3_4_));
                    }
                    else
                    {
                        LOGGER.error("Tried to handle twitch user but couldn\'t find them!");
                    }
                }
                else
                {
                    LOGGER.error("Don\'t know how to handle " + lvt_2_1_);
                }

                return true;
            }

            return false;
        }
    }

    /**
     * Used to add chat messages to the client's GuiChat.
     */
    public void sendChatMessage(String msg)
    {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat)
    {
        if (addToChat)
        {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }

        this.mc.thePlayer.sendChatMessage(msg);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < this.buttonList.size(); ++lvt_4_1_)
            {
                GuiButton lvt_5_1_ = (GuiButton)this.buttonList.get(lvt_4_1_);

                if (lvt_5_1_.mousePressed(this.mc, mouseX, mouseY))
                {
                    this.selectedButton = lvt_5_1_;
                    lvt_5_1_.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(lvt_5_1_);
                }
            }
        }
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call
     * Container.validate()
     */
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = mc.fontRendererObj;
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    /**
     * Set the gui to the specified width and height
     *  
     * @param w The width of the screen
     * @param h The height of the screen
     */
    public void setGuiSize(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
    }

    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.handleKeyboardInput();
            }
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        int lvt_1_1_ = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int lvt_2_1_ = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int lvt_3_1_ = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
            {
                return;
            }

            this.eventButton = lvt_3_1_;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(lvt_1_1_, lvt_2_1_, this.eventButton);
        }
        else if (lvt_3_1_ != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(lvt_1_1_, lvt_2_1_, lvt_3_1_);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long lvt_4_1_ = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(lvt_1_1_, lvt_2_1_, this.eventButton, lvt_4_1_);
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput() throws IOException
    {
        if (Keyboard.getEventKeyState())
        {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }

        this.mc.dispatchKeypresses();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int tint)
    {
        if (this.mc.theWorld != null)
        {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else
        {
            this.drawBackground(tint);
        }
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(int tint)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator lvt_2_1_ = Tessellator.getInstance();
        WorldRenderer lvt_3_1_ = lvt_2_1_.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float lvt_4_1_ = 32.0F;
        lvt_3_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        lvt_3_1_.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        lvt_3_1_.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
        lvt_3_1_.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)tint).color(64, 64, 64, 255).endVertex();
        lvt_3_1_.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)tint).color(64, 64, 64, 255).endVertex();
        lvt_2_1_.draw();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean result, int id)
    {
        if (id == 31102009)
        {
            if (result)
            {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI url)
    {
        try
        {
            Class<?> lvt_2_1_ = Class.forName("java.awt.Desktop");
            Object lvt_3_1_ = lvt_2_1_.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            lvt_2_1_.getMethod("browse", new Class[] {URI.class}).invoke(lvt_3_1_, new Object[] {url});
        }
        catch (Throwable var4)
        {
            LOGGER.error("Couldn\'t open link", var4);
        }
    }

    /**
     * Returns true if either windows ctrl key is down or if either mac meta key is down
     */
    public static boolean isCtrlKeyDown()
    {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    /**
     * Returns true if either shift key is down
     */
    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    /**
     * Returns true if either alt key is down
     */
    public static boolean isAltKeyDown()
    {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(int keyID)
    {
        return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID)
    {
        return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID)
    {
        return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID)
    {
        return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    /**
     * Called when the GUI is resized in order to update the world and the resolution
     *  
     * @param w The width of the screen
     * @param h The height of the screen
     */
    public void onResize(Minecraft mcIn, int w, int h)
    {
        this.setWorldAndResolution(mcIn, w, h);
    }
}
