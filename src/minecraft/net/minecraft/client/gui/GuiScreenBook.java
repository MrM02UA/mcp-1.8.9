package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");

    /** The player editing the book */
    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;

    /** Whether the book is signed or can still be edited */
    private final boolean bookIsUnsigned;

    /**
     * Whether the book's title or contents has been modified since being opened
     */
    private boolean bookIsModified;

    /** Determines if the signing screen is open */
    private boolean bookGettingSigned;

    /** Update ticks since the gui was opened */
    private int updateCount;
    private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    private int bookTotalPages = 1;
    private int currPage;
    private NBTTagList bookPages;
    private String bookTitle = "";
    private List<IChatComponent> field_175386_A;
    private int field_175387_B = -1;
    private GuiScreenBook.NextPageButton buttonNextPage;
    private GuiScreenBook.NextPageButton buttonPreviousPage;
    private GuiButton buttonDone;

    /** The GuiButton to sign this book. */
    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;

    public GuiScreenBook(EntityPlayer player, ItemStack book, boolean isUnsigned)
    {
        this.editingPlayer = player;
        this.bookObj = book;
        this.bookIsUnsigned = isUnsigned;

        if (book.hasTagCompound())
        {
            NBTTagCompound lvt_4_1_ = book.getTagCompound();
            this.bookPages = lvt_4_1_.getTagList("pages", 8);

            if (this.bookPages != null)
            {
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();

                if (this.bookTotalPages < 1)
                {
                    this.bookTotalPages = 1;
                }
            }
        }

        if (this.bookPages == null && isUnsigned)
        {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCount;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        if (this.bookIsUnsigned)
        {
            this.buttonList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton", new Object[0])));
            this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done", new Object[0])));
            this.buttonList.add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
            this.buttonList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel", new Object[0])));
        }
        else
        {
            this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done", new Object[0])));
        }

        int lvt_1_1_ = (this.width - this.bookImageWidth) / 2;
        int lvt_2_1_ = 2;
        this.buttonList.add(this.buttonNextPage = new GuiScreenBook.NextPageButton(1, lvt_1_1_ + 120, lvt_2_1_ + 154, true));
        this.buttonList.add(this.buttonPreviousPage = new GuiScreenBook.NextPageButton(2, lvt_1_1_ + 38, lvt_2_1_ + 154, false));
        this.updateButtons();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons()
    {
        this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
        this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
        this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;

        if (this.bookIsUnsigned)
        {
            this.buttonSign.visible = !this.bookGettingSigned;
            this.buttonCancel.visible = this.bookGettingSigned;
            this.buttonFinalize.visible = this.bookGettingSigned;
            this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
        }
    }

    private void sendBookToServer(boolean publish) throws IOException
    {
        if (this.bookIsUnsigned && this.bookIsModified)
        {
            if (this.bookPages != null)
            {
                while (this.bookPages.tagCount() > 1)
                {
                    String lvt_2_1_ = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);

                    if (lvt_2_1_.length() != 0)
                    {
                        break;
                    }

                    this.bookPages.removeTag(this.bookPages.tagCount() - 1);
                }

                if (this.bookObj.hasTagCompound())
                {
                    NBTTagCompound lvt_2_2_ = this.bookObj.getTagCompound();
                    lvt_2_2_.setTag("pages", this.bookPages);
                }
                else
                {
                    this.bookObj.setTagInfo("pages", this.bookPages);
                }

                String lvt_2_3_ = "MC|BEdit";

                if (publish)
                {
                    lvt_2_3_ = "MC|BSign";
                    this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
                    this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));

                    for (int lvt_3_1_ = 0; lvt_3_1_ < this.bookPages.tagCount(); ++lvt_3_1_)
                    {
                        String lvt_4_1_ = this.bookPages.getStringTagAt(lvt_3_1_);
                        IChatComponent lvt_5_1_ = new ChatComponentText(lvt_4_1_);
                        lvt_4_1_ = IChatComponent.Serializer.componentToJson(lvt_5_1_);
                        this.bookPages.set(lvt_3_1_, new NBTTagString(lvt_4_1_));
                    }

                    this.bookObj.setItem(Items.written_book);
                }

                PacketBuffer lvt_3_2_ = new PacketBuffer(Unpooled.buffer());
                lvt_3_2_.writeItemStackToBuffer(this.bookObj);
                this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(lvt_2_3_, lvt_3_2_));
            }
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.sendBookToServer(false);
            }
            else if (button.id == 3 && this.bookIsUnsigned)
            {
                this.bookGettingSigned = true;
            }
            else if (button.id == 1)
            {
                if (this.currPage < this.bookTotalPages - 1)
                {
                    ++this.currPage;
                }
                else if (this.bookIsUnsigned)
                {
                    this.addNewPage();

                    if (this.currPage < this.bookTotalPages - 1)
                    {
                        ++this.currPage;
                    }
                }
            }
            else if (button.id == 2)
            {
                if (this.currPage > 0)
                {
                    --this.currPage;
                }
            }
            else if (button.id == 5 && this.bookGettingSigned)
            {
                this.sendBookToServer(true);
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (button.id == 4 && this.bookGettingSigned)
            {
                this.bookGettingSigned = false;
            }

            this.updateButtons();
        }
    }

    private void addNewPage()
    {
        if (this.bookPages != null && this.bookPages.tagCount() < 50)
        {
            this.bookPages.appendTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookIsModified = true;
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        if (this.bookIsUnsigned)
        {
            if (this.bookGettingSigned)
            {
                this.keyTypedInTitle(typedChar, keyCode);
            }
            else
            {
                this.keyTypedInBook(typedChar, keyCode);
            }
        }
    }

    /**
     * Processes keystrokes when editing the text of a book
     */
    private void keyTypedInBook(char typedChar, int keyCode)
    {
        if (GuiScreen.isKeyComboCtrlV(keyCode))
        {
            this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
        }
        else
        {
            switch (keyCode)
            {
                case 14:
                    String lvt_3_1_ = this.pageGetCurrent();

                    if (lvt_3_1_.length() > 0)
                    {
                        this.pageSetCurrent(lvt_3_1_.substring(0, lvt_3_1_.length() - 1));
                    }

                    return;

                case 28:
                case 156:
                    this.pageInsertIntoCurrent("\n");
                    return;

                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
                    {
                        this.pageInsertIntoCurrent(Character.toString(typedChar));
                    }
            }
        }
    }

    /**
     * Processes keystrokes when editing the title of a book
     */
    private void keyTypedInTitle(char p_146460_1_, int p_146460_2_) throws IOException
    {
        switch (p_146460_2_)
        {
            case 14:
                if (!this.bookTitle.isEmpty())
                {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }

                return;

            case 28:
            case 156:
                if (!this.bookTitle.isEmpty())
                {
                    this.sendBookToServer(true);
                    this.mc.displayGuiScreen((GuiScreen)null);
                }

                return;

            default:
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(p_146460_1_))
                {
                    this.bookTitle = this.bookTitle + Character.toString(p_146460_1_);
                    this.updateButtons();
                    this.bookIsModified = true;
                }
        }
    }

    /**
     * Returns the entire text of the current page as determined by currPage
     */
    private String pageGetCurrent()
    {
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
    }

    /**
     * Sets the text of the current page as determined by currPage
     */
    private void pageSetCurrent(String p_146457_1_)
    {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount())
        {
            this.bookPages.set(this.currPage, new NBTTagString(p_146457_1_));
            this.bookIsModified = true;
        }
    }

    /**
     * Processes any text getting inserted into the current page, enforcing the page size limit
     */
    private void pageInsertIntoCurrent(String p_146459_1_)
    {
        String lvt_2_1_ = this.pageGetCurrent();
        String lvt_3_1_ = lvt_2_1_ + p_146459_1_;
        int lvt_4_1_ = this.fontRendererObj.splitStringWidth(lvt_3_1_ + "" + EnumChatFormatting.BLACK + "_", 118);

        if (lvt_4_1_ <= 128 && lvt_3_1_.length() < 256)
        {
            this.pageSetCurrent(lvt_3_1_);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bookGuiTextures);
        int lvt_4_1_ = (this.width - this.bookImageWidth) / 2;
        int lvt_5_1_ = 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.bookImageWidth, this.bookImageHeight);

        if (this.bookGettingSigned)
        {
            String lvt_6_1_ = this.bookTitle;

            if (this.bookIsUnsigned)
            {
                if (this.updateCount / 6 % 2 == 0)
                {
                    lvt_6_1_ = lvt_6_1_ + "" + EnumChatFormatting.BLACK + "_";
                }
                else
                {
                    lvt_6_1_ = lvt_6_1_ + "" + EnumChatFormatting.GRAY + "_";
                }
            }

            String lvt_7_1_ = I18n.format("book.editTitle", new Object[0]);
            int lvt_8_1_ = this.fontRendererObj.getStringWidth(lvt_7_1_);
            this.fontRendererObj.drawString(lvt_7_1_, lvt_4_1_ + 36 + (116 - lvt_8_1_) / 2, lvt_5_1_ + 16 + 16, 0);
            int lvt_9_1_ = this.fontRendererObj.getStringWidth(lvt_6_1_);
            this.fontRendererObj.drawString(lvt_6_1_, lvt_4_1_ + 36 + (116 - lvt_9_1_) / 2, lvt_5_1_ + 48, 0);
            String lvt_10_1_ = I18n.format("book.byAuthor", new Object[] {this.editingPlayer.getName()});
            int lvt_11_1_ = this.fontRendererObj.getStringWidth(lvt_10_1_);
            this.fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + lvt_10_1_, lvt_4_1_ + 36 + (116 - lvt_11_1_) / 2, lvt_5_1_ + 48 + 10, 0);
            String lvt_12_1_ = I18n.format("book.finalizeWarning", new Object[0]);
            this.fontRendererObj.drawSplitString(lvt_12_1_, lvt_4_1_ + 36, lvt_5_1_ + 80, 116, 0);
        }
        else
        {
            String lvt_6_2_ = I18n.format("book.pageIndicator", new Object[] {Integer.valueOf(this.currPage + 1), Integer.valueOf(this.bookTotalPages)});
            String lvt_7_2_ = "";

            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount())
            {
                lvt_7_2_ = this.bookPages.getStringTagAt(this.currPage);
            }

            if (this.bookIsUnsigned)
            {
                if (this.fontRendererObj.getBidiFlag())
                {
                    lvt_7_2_ = lvt_7_2_ + "_";
                }
                else if (this.updateCount / 6 % 2 == 0)
                {
                    lvt_7_2_ = lvt_7_2_ + "" + EnumChatFormatting.BLACK + "_";
                }
                else
                {
                    lvt_7_2_ = lvt_7_2_ + "" + EnumChatFormatting.GRAY + "_";
                }
            }
            else if (this.field_175387_B != this.currPage)
            {
                if (ItemEditableBook.validBookTagContents(this.bookObj.getTagCompound()))
                {
                    try
                    {
                        IChatComponent lvt_8_2_ = IChatComponent.Serializer.jsonToComponent(lvt_7_2_);
                        this.field_175386_A = lvt_8_2_ != null ? GuiUtilRenderComponents.splitText(lvt_8_2_, 116, this.fontRendererObj, true, true) : null;
                    }
                    catch (JsonParseException var13)
                    {
                        this.field_175386_A = null;
                    }
                }
                else
                {
                    ChatComponentText lvt_8_4_ = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
                    this.field_175386_A = Lists.newArrayList(lvt_8_4_);
                }

                this.field_175387_B = this.currPage;
            }

            int lvt_8_5_ = this.fontRendererObj.getStringWidth(lvt_6_2_);
            this.fontRendererObj.drawString(lvt_6_2_, lvt_4_1_ - lvt_8_5_ + this.bookImageWidth - 44, lvt_5_1_ + 16, 0);

            if (this.field_175386_A == null)
            {
                this.fontRendererObj.drawSplitString(lvt_7_2_, lvt_4_1_ + 36, lvt_5_1_ + 16 + 16, 116, 0);
            }
            else
            {
                int lvt_9_2_ = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());

                for (int lvt_10_2_ = 0; lvt_10_2_ < lvt_9_2_; ++lvt_10_2_)
                {
                    IChatComponent lvt_11_2_ = (IChatComponent)this.field_175386_A.get(lvt_10_2_);
                    this.fontRendererObj.drawString(lvt_11_2_.getUnformattedText(), lvt_4_1_ + 36, lvt_5_1_ + 16 + 16 + lvt_10_2_ * this.fontRendererObj.FONT_HEIGHT, 0);
                }

                IChatComponent lvt_10_3_ = this.func_175385_b(mouseX, mouseY);

                if (lvt_10_3_ != null)
                {
                    this.handleComponentHover(lvt_10_3_, mouseX, mouseY);
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            IChatComponent lvt_4_1_ = this.func_175385_b(mouseX, mouseY);

            if (this.handleComponentClick(lvt_4_1_))
            {
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Executes the click event specified by the given chat component
     *  
     * @param component The ChatComponent to check for click
     */
    protected boolean handleComponentClick(IChatComponent component)
    {
        ClickEvent lvt_2_1_ = component == null ? null : component.getChatStyle().getChatClickEvent();

        if (lvt_2_1_ == null)
        {
            return false;
        }
        else if (lvt_2_1_.getAction() == ClickEvent.Action.CHANGE_PAGE)
        {
            String lvt_3_1_ = lvt_2_1_.getValue();

            try
            {
                int lvt_4_1_ = Integer.parseInt(lvt_3_1_) - 1;

                if (lvt_4_1_ >= 0 && lvt_4_1_ < this.bookTotalPages && lvt_4_1_ != this.currPage)
                {
                    this.currPage = lvt_4_1_;
                    this.updateButtons();
                    return true;
                }
            }
            catch (Throwable var5)
            {
                ;
            }

            return false;
        }
        else
        {
            boolean lvt_3_2_ = super.handleComponentClick(component);

            if (lvt_3_2_ && lvt_2_1_.getAction() == ClickEvent.Action.RUN_COMMAND)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }

            return lvt_3_2_;
        }
    }

    public IChatComponent func_175385_b(int p_175385_1_, int p_175385_2_)
    {
        if (this.field_175386_A == null)
        {
            return null;
        }
        else
        {
            int lvt_3_1_ = p_175385_1_ - (this.width - this.bookImageWidth) / 2 - 36;
            int lvt_4_1_ = p_175385_2_ - 2 - 16 - 16;

            if (lvt_3_1_ >= 0 && lvt_4_1_ >= 0)
            {
                int lvt_5_1_ = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());

                if (lvt_3_1_ <= 116 && lvt_4_1_ < this.mc.fontRendererObj.FONT_HEIGHT * lvt_5_1_ + lvt_5_1_)
                {
                    int lvt_6_1_ = lvt_4_1_ / this.mc.fontRendererObj.FONT_HEIGHT;

                    if (lvt_6_1_ >= 0 && lvt_6_1_ < this.field_175386_A.size())
                    {
                        IChatComponent lvt_7_1_ = (IChatComponent)this.field_175386_A.get(lvt_6_1_);
                        int lvt_8_1_ = 0;

                        for (IChatComponent lvt_10_1_ : lvt_7_1_)
                        {
                            if (lvt_10_1_ instanceof ChatComponentText)
                            {
                                lvt_8_1_ += this.mc.fontRendererObj.getStringWidth(((ChatComponentText)lvt_10_1_).getChatComponentText_TextValue());

                                if (lvt_8_1_ > lvt_3_1_)
                                {
                                    return lvt_10_1_;
                                }
                            }
                        }
                    }

                    return null;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }

    static class NextPageButton extends GuiButton
    {
        private final boolean field_146151_o;

        public NextPageButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3_, boolean p_i46316_4_)
        {
            super(p_i46316_1_, p_i46316_2_, p_i46316_3_, 23, 13, "");
            this.field_146151_o = p_i46316_4_;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                boolean lvt_4_1_ = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
                int lvt_5_1_ = 0;
                int lvt_6_1_ = 192;

                if (lvt_4_1_)
                {
                    lvt_5_1_ += 23;
                }

                if (!this.field_146151_o)
                {
                    lvt_6_1_ += 13;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, lvt_5_1_, lvt_6_1_, 23, 13);
            }
        }
    }
}
