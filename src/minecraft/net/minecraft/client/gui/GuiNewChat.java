package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiNewChat extends Gui
{
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    private int scrollPos;
    private boolean isScrolled;

    public GuiNewChat(Minecraft mcIn)
    {
        this.mc = mcIn;
    }

    public void drawChat(int updateCounter)
    {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            int lvt_2_1_ = this.getLineCount();
            boolean lvt_3_1_ = false;
            int lvt_4_1_ = 0;
            int lvt_5_1_ = this.drawnChatLines.size();
            float lvt_6_1_ = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (lvt_5_1_ > 0)
            {
                if (this.getChatOpen())
                {
                    lvt_3_1_ = true;
                }

                float lvt_7_1_ = this.getChatScale();
                int lvt_8_1_ = MathHelper.ceiling_float_int((float)this.getChatWidth() / lvt_7_1_);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(lvt_7_1_, lvt_7_1_, 1.0F);

                for (int lvt_9_1_ = 0; lvt_9_1_ + this.scrollPos < this.drawnChatLines.size() && lvt_9_1_ < lvt_2_1_; ++lvt_9_1_)
                {
                    ChatLine lvt_10_1_ = (ChatLine)this.drawnChatLines.get(lvt_9_1_ + this.scrollPos);

                    if (lvt_10_1_ != null)
                    {
                        int lvt_11_1_ = updateCounter - lvt_10_1_.getUpdatedCounter();

                        if (lvt_11_1_ < 200 || lvt_3_1_)
                        {
                            double lvt_12_1_ = (double)lvt_11_1_ / 200.0D;
                            lvt_12_1_ = 1.0D - lvt_12_1_;
                            lvt_12_1_ = lvt_12_1_ * 10.0D;
                            lvt_12_1_ = MathHelper.clamp_double(lvt_12_1_, 0.0D, 1.0D);
                            lvt_12_1_ = lvt_12_1_ * lvt_12_1_;
                            int lvt_14_1_ = (int)(255.0D * lvt_12_1_);

                            if (lvt_3_1_)
                            {
                                lvt_14_1_ = 255;
                            }

                            lvt_14_1_ = (int)((float)lvt_14_1_ * lvt_6_1_);
                            ++lvt_4_1_;

                            if (lvt_14_1_ > 3)
                            {
                                int lvt_15_1_ = 0;
                                int lvt_16_1_ = -lvt_9_1_ * 9;
                                drawRect(lvt_15_1_, lvt_16_1_ - 9, lvt_15_1_ + lvt_8_1_ + 4, lvt_16_1_, lvt_14_1_ / 2 << 24);
                                String lvt_17_1_ = lvt_10_1_.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(lvt_17_1_, (float)lvt_15_1_, (float)(lvt_16_1_ - 8), 16777215 + (lvt_14_1_ << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (lvt_3_1_)
                {
                    int lvt_9_2_ = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int lvt_10_2_ = lvt_5_1_ * lvt_9_2_ + lvt_5_1_;
                    int lvt_11_2_ = lvt_4_1_ * lvt_9_2_ + lvt_4_1_;
                    int lvt_12_2_ = this.scrollPos * lvt_11_2_ / lvt_5_1_;
                    int lvt_13_1_ = lvt_11_2_ * lvt_11_2_ / lvt_10_2_;

                    if (lvt_10_2_ != lvt_11_2_)
                    {
                        int lvt_14_2_ = lvt_12_2_ > 0 ? 170 : 96;
                        int lvt_15_2_ = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -lvt_12_2_, 2, -lvt_12_2_ - lvt_13_1_, lvt_15_2_ + (lvt_14_2_ << 24));
                        drawRect(2, -lvt_12_2_, 1, -lvt_12_2_ - lvt_13_1_, 13421772 + (lvt_14_2_ << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Clears the chat.
     */
    public void clearChatMessages()
    {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(IChatComponent chatComponent)
    {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId)
    {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + chatComponent.getUnformattedText());
    }

    private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly)
    {
        if (chatLineId != 0)
        {
            this.deleteChatLine(chatLineId);
        }

        int lvt_5_1_ = MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale());
        List<IChatComponent> lvt_6_1_ = GuiUtilRenderComponents.splitText(chatComponent, lvt_5_1_, this.mc.fontRendererObj, false, false);
        boolean lvt_7_1_ = this.getChatOpen();

        for (IChatComponent lvt_9_1_ : lvt_6_1_)
        {
            if (lvt_7_1_ && this.scrollPos > 0)
            {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.drawnChatLines.add(0, new ChatLine(updateCounter, lvt_9_1_, chatLineId));
        }

        while (this.drawnChatLines.size() > 100)
        {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!displayOnly)
        {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

            while (this.chatLines.size() > 100)
            {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat()
    {
        this.drawnChatLines.clear();
        this.resetScroll();

        for (int lvt_1_1_ = this.chatLines.size() - 1; lvt_1_1_ >= 0; --lvt_1_1_)
        {
            ChatLine lvt_2_1_ = (ChatLine)this.chatLines.get(lvt_1_1_);
            this.setChatLine(lvt_2_1_.getChatComponent(), lvt_2_1_.getChatLineID(), lvt_2_1_.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages()
    {
        return this.sentMessages;
    }

    /**
     * Adds this string to the list of sent messages, for recall using the up/down arrow keys
     *  
     * @param message The message to add in the sendMessage List
     */
    public void addToSentMessages(String message)
    {
        if (this.sentMessages.isEmpty() || !((String)this.sentMessages.get(this.sentMessages.size() - 1)).equals(message))
        {
            this.sentMessages.add(message);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    public void resetScroll()
    {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    /**
     * Scrolls the chat by the given number of lines.
     *  
     * @param amount The amount to scroll
     */
    public void scroll(int amount)
    {
        this.scrollPos += amount;
        int lvt_2_1_ = this.drawnChatLines.size();

        if (this.scrollPos > lvt_2_1_ - this.getLineCount())
        {
            this.scrollPos = lvt_2_1_ - this.getLineCount();
        }

        if (this.scrollPos <= 0)
        {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    /**
     * Gets the chat component under the mouse
     *  
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     */
    public IChatComponent getChatComponent(int mouseX, int mouseY)
    {
        if (!this.getChatOpen())
        {
            return null;
        }
        else
        {
            ScaledResolution lvt_3_1_ = new ScaledResolution(this.mc);
            int lvt_4_1_ = lvt_3_1_.getScaleFactor();
            float lvt_5_1_ = this.getChatScale();
            int lvt_6_1_ = mouseX / lvt_4_1_ - 3;
            int lvt_7_1_ = mouseY / lvt_4_1_ - 27;
            lvt_6_1_ = MathHelper.floor_float((float)lvt_6_1_ / lvt_5_1_);
            lvt_7_1_ = MathHelper.floor_float((float)lvt_7_1_ / lvt_5_1_);

            if (lvt_6_1_ >= 0 && lvt_7_1_ >= 0)
            {
                int lvt_8_1_ = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (lvt_6_1_ <= MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale()) && lvt_7_1_ < this.mc.fontRendererObj.FONT_HEIGHT * lvt_8_1_ + lvt_8_1_)
                {
                    int lvt_9_1_ = lvt_7_1_ / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (lvt_9_1_ >= 0 && lvt_9_1_ < this.drawnChatLines.size())
                    {
                        ChatLine lvt_10_1_ = (ChatLine)this.drawnChatLines.get(lvt_9_1_);
                        int lvt_11_1_ = 0;

                        for (IChatComponent lvt_13_1_ : lvt_10_1_.getChatComponent())
                        {
                            if (lvt_13_1_ instanceof ChatComponentText)
                            {
                                lvt_11_1_ += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)lvt_13_1_).getChatComponentText_TextValue(), false));

                                if (lvt_11_1_ > lvt_6_1_)
                                {
                                    return lvt_13_1_;
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

    /**
     * Returns true if the chat GUI is open
     */
    public boolean getChatOpen()
    {
        return this.mc.currentScreen instanceof GuiChat;
    }

    /**
     * finds and deletes a Chat line by ID
     *  
     * @param id The ChatLine's id to delete
     */
    public void deleteChatLine(int id)
    {
        Iterator<ChatLine> lvt_2_1_ = this.drawnChatLines.iterator();

        while (lvt_2_1_.hasNext())
        {
            ChatLine lvt_3_1_ = (ChatLine)lvt_2_1_.next();

            if (lvt_3_1_.getChatLineID() == id)
            {
                lvt_2_1_.remove();
            }
        }

        lvt_2_1_ = this.chatLines.iterator();

        while (lvt_2_1_.hasNext())
        {
            ChatLine lvt_3_2_ = (ChatLine)lvt_2_1_.next();

            if (lvt_3_2_.getChatLineID() == id)
            {
                lvt_2_1_.remove();
                break;
            }
        }
    }

    public int getChatWidth()
    {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight()
    {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    /**
     * Returns the chatscale from mc.gameSettings.chatScale
     */
    public float getChatScale()
    {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale)
    {
        int lvt_1_1_ = 320;
        int lvt_2_1_ = 40;
        return MathHelper.floor_float(scale * (float)(lvt_1_1_ - lvt_2_1_) + (float)lvt_2_1_);
    }

    public static int calculateChatboxHeight(float scale)
    {
        int lvt_1_1_ = 180;
        int lvt_2_1_ = 20;
        return MathHelper.floor_float(scale * (float)(lvt_1_1_ - lvt_2_1_) + (float)lvt_2_1_);
    }

    public int getLineCount()
    {
        return this.getChatHeight() / 9;
    }
}
