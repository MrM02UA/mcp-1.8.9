package net.minecraft.client.gui.stream;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IStream;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class GuiTwitchUserMode extends GuiScreen
{
    private static final EnumChatFormatting field_152331_a = EnumChatFormatting.DARK_GREEN;
    private static final EnumChatFormatting field_152335_f = EnumChatFormatting.RED;
    private static final EnumChatFormatting field_152336_g = EnumChatFormatting.DARK_PURPLE;
    private final ChatUserInfo field_152337_h;
    private final IChatComponent field_152338_i;
    private final List<IChatComponent> field_152332_r = Lists.newArrayList();
    private final IStream stream;
    private int field_152334_t;

    public GuiTwitchUserMode(IStream streamIn, ChatUserInfo p_i1064_2_)
    {
        this.stream = streamIn;
        this.field_152337_h = p_i1064_2_;
        this.field_152338_i = new ChatComponentText(p_i1064_2_.displayName);
        this.field_152332_r.addAll(func_152328_a(p_i1064_2_.modes, p_i1064_2_.subscriptions, streamIn));
    }

    public static List<IChatComponent> func_152328_a(Set<ChatUserMode> p_152328_0_, Set<ChatUserSubscription> p_152328_1_, IStream p_152328_2_)
    {
        String lvt_3_1_ = p_152328_2_ == null ? null : p_152328_2_.func_152921_C();
        boolean lvt_4_1_ = p_152328_2_ != null && p_152328_2_.func_152927_B();
        List<IChatComponent> lvt_5_1_ = Lists.newArrayList();

        for (ChatUserMode lvt_7_1_ : p_152328_0_)
        {
            IChatComponent lvt_8_1_ = func_152329_a(lvt_7_1_, lvt_3_1_, lvt_4_1_);

            if (lvt_8_1_ != null)
            {
                IChatComponent lvt_9_1_ = new ChatComponentText("- ");
                lvt_9_1_.appendSibling(lvt_8_1_);
                lvt_5_1_.add(lvt_9_1_);
            }
        }

        for (ChatUserSubscription lvt_7_2_ : p_152328_1_)
        {
            IChatComponent lvt_8_2_ = func_152330_a(lvt_7_2_, lvt_3_1_, lvt_4_1_);

            if (lvt_8_2_ != null)
            {
                IChatComponent lvt_9_2_ = new ChatComponentText("- ");
                lvt_9_2_.appendSibling(lvt_8_2_);
                lvt_5_1_.add(lvt_9_2_);
            }
        }

        return lvt_5_1_;
    }

    public static IChatComponent func_152330_a(ChatUserSubscription p_152330_0_, String p_152330_1_, boolean p_152330_2_)
    {
        IChatComponent lvt_3_1_ = null;

        if (p_152330_0_ == ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER)
        {
            if (p_152330_1_ == null)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.subscription.subscriber", new Object[0]);
            }
            else if (p_152330_2_)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.subscription.subscriber.self", new Object[0]);
            }
            else
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.subscription.subscriber.other", new Object[] {p_152330_1_});
            }

            lvt_3_1_.getChatStyle().setColor(field_152331_a);
        }
        else if (p_152330_0_ == ChatUserSubscription.TTV_CHAT_USERSUB_TURBO)
        {
            lvt_3_1_ = new ChatComponentTranslation("stream.user.subscription.turbo", new Object[0]);
            lvt_3_1_.getChatStyle().setColor(field_152336_g);
        }

        return lvt_3_1_;
    }

    public static IChatComponent func_152329_a(ChatUserMode p_152329_0_, String p_152329_1_, boolean p_152329_2_)
    {
        IChatComponent lvt_3_1_ = null;

        if (p_152329_0_ == ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR)
        {
            lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.administrator", new Object[0]);
            lvt_3_1_.getChatStyle().setColor(field_152336_g);
        }
        else if (p_152329_0_ == ChatUserMode.TTV_CHAT_USERMODE_BANNED)
        {
            if (p_152329_1_ == null)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.banned", new Object[0]);
            }
            else if (p_152329_2_)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.banned.self", new Object[0]);
            }
            else
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.banned.other", new Object[] {p_152329_1_});
            }

            lvt_3_1_.getChatStyle().setColor(field_152335_f);
        }
        else if (p_152329_0_ == ChatUserMode.TTV_CHAT_USERMODE_BROADCASTER)
        {
            if (p_152329_1_ == null)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.broadcaster", new Object[0]);
            }
            else if (p_152329_2_)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.broadcaster.self", new Object[0]);
            }
            else
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.broadcaster.other", new Object[0]);
            }

            lvt_3_1_.getChatStyle().setColor(field_152331_a);
        }
        else if (p_152329_0_ == ChatUserMode.TTV_CHAT_USERMODE_MODERATOR)
        {
            if (p_152329_1_ == null)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.moderator", new Object[0]);
            }
            else if (p_152329_2_)
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.moderator.self", new Object[0]);
            }
            else
            {
                lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.moderator.other", new Object[] {p_152329_1_});
            }

            lvt_3_1_.getChatStyle().setColor(field_152331_a);
        }
        else if (p_152329_0_ == ChatUserMode.TTV_CHAT_USERMODE_STAFF)
        {
            lvt_3_1_ = new ChatComponentTranslation("stream.user.mode.staff", new Object[0]);
            lvt_3_1_.getChatStyle().setColor(field_152336_g);
        }

        return lvt_3_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        int lvt_1_1_ = this.width / 3;
        int lvt_2_1_ = lvt_1_1_ - 130;
        this.buttonList.add(new GuiButton(1, lvt_1_1_ * 0 + lvt_2_1_ / 2, this.height - 70, 130, 20, I18n.format("stream.userinfo.timeout", new Object[0])));
        this.buttonList.add(new GuiButton(0, lvt_1_1_ * 1 + lvt_2_1_ / 2, this.height - 70, 130, 20, I18n.format("stream.userinfo.ban", new Object[0])));
        this.buttonList.add(new GuiButton(2, lvt_1_1_ * 2 + lvt_2_1_ / 2, this.height - 70, 130, 20, I18n.format("stream.userinfo.mod", new Object[0])));
        this.buttonList.add(new GuiButton(5, lvt_1_1_ * 0 + lvt_2_1_ / 2, this.height - 45, 130, 20, I18n.format("gui.cancel", new Object[0])));
        this.buttonList.add(new GuiButton(3, lvt_1_1_ * 1 + lvt_2_1_ / 2, this.height - 45, 130, 20, I18n.format("stream.userinfo.unban", new Object[0])));
        this.buttonList.add(new GuiButton(4, lvt_1_1_ * 2 + lvt_2_1_ / 2, this.height - 45, 130, 20, I18n.format("stream.userinfo.unmod", new Object[0])));
        int lvt_3_1_ = 0;

        for (IChatComponent lvt_5_1_ : this.field_152332_r)
        {
            lvt_3_1_ = Math.max(lvt_3_1_, this.fontRendererObj.getStringWidth(lvt_5_1_.getFormattedText()));
        }

        this.field_152334_t = this.width / 2 - lvt_3_1_ / 2;
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
                this.stream.func_152917_b("/ban " + this.field_152337_h.displayName);
            }
            else if (button.id == 3)
            {
                this.stream.func_152917_b("/unban " + this.field_152337_h.displayName);
            }
            else if (button.id == 2)
            {
                this.stream.func_152917_b("/mod " + this.field_152337_h.displayName);
            }
            else if (button.id == 4)
            {
                this.stream.func_152917_b("/unmod " + this.field_152337_h.displayName);
            }
            else if (button.id == 1)
            {
                this.stream.func_152917_b("/timeout " + this.field_152337_h.displayName);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_152338_i.getUnformattedText(), this.width / 2, 70, 16777215);
        int lvt_4_1_ = 80;

        for (IChatComponent lvt_6_1_ : this.field_152332_r)
        {
            this.drawString(this.fontRendererObj, lvt_6_1_.getFormattedText(), this.field_152334_t, lvt_4_1_, 16777215);
            lvt_4_1_ += this.fontRendererObj.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
