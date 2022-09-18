package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry
{
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final GuiMultiplayer owner;
    private final Minecraft mc;
    private final ServerData server;
    private final ResourceLocation serverIcon;
    private String field_148299_g;
    private DynamicTexture field_148305_h;
    private long field_148298_f;

    protected ServerListEntryNormal(GuiMultiplayer p_i45048_1_, ServerData serverIn)
    {
        this.owner = p_i45048_1_;
        this.server = serverIn;
        this.mc = Minecraft.getMinecraft();
        this.serverIcon = new ResourceLocation("servers/" + serverIn.serverIP + "/icon");
        this.field_148305_h = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
        if (!this.server.field_78841_f)
        {
            this.server.field_78841_f = true;
            this.server.pingToServer = -2L;
            this.server.serverMOTD = "";
            this.server.populationInfo = "";
            field_148302_b.submit(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        ServerListEntryNormal.this.owner.getOldServerPinger().ping(ServerListEntryNormal.this.server);
                    }
                    catch (UnknownHostException var2)
                    {
                        ServerListEntryNormal.this.server.pingToServer = -1L;
                        ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t resolve hostname";
                    }
                    catch (Exception var3)
                    {
                        ServerListEntryNormal.this.server.pingToServer = -1L;
                        ServerListEntryNormal.this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
                    }
                }
            });
        }

        boolean lvt_9_1_ = this.server.version > 47;
        boolean lvt_10_1_ = this.server.version < 47;
        boolean lvt_11_1_ = lvt_9_1_ || lvt_10_1_;
        this.mc.fontRendererObj.drawString(this.server.serverName, x + 32 + 3, y + 1, 16777215);
        List<String> lvt_12_1_ = this.mc.fontRendererObj.listFormattedStringToWidth(this.server.serverMOTD, listWidth - 32 - 2);

        for (int lvt_13_1_ = 0; lvt_13_1_ < Math.min(lvt_12_1_.size(), 2); ++lvt_13_1_)
        {
            this.mc.fontRendererObj.drawString((String)lvt_12_1_.get(lvt_13_1_), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * lvt_13_1_, 8421504);
        }

        String lvt_13_2_ = lvt_11_1_ ? EnumChatFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
        int lvt_14_1_ = this.mc.fontRendererObj.getStringWidth(lvt_13_2_);
        this.mc.fontRendererObj.drawString(lvt_13_2_, x + listWidth - lvt_14_1_ - 15 - 2, y + 1, 8421504);
        int lvt_15_1_ = 0;
        String lvt_17_1_ = null;
        int lvt_16_1_;
        String lvt_18_1_;

        if (lvt_11_1_)
        {
            lvt_16_1_ = 5;
            lvt_18_1_ = lvt_9_1_ ? "Client out of date!" : "Server out of date!";
            lvt_17_1_ = this.server.playerList;
        }
        else if (this.server.field_78841_f && this.server.pingToServer != -2L)
        {
            if (this.server.pingToServer < 0L)
            {
                lvt_16_1_ = 5;
            }
            else if (this.server.pingToServer < 150L)
            {
                lvt_16_1_ = 0;
            }
            else if (this.server.pingToServer < 300L)
            {
                lvt_16_1_ = 1;
            }
            else if (this.server.pingToServer < 600L)
            {
                lvt_16_1_ = 2;
            }
            else if (this.server.pingToServer < 1000L)
            {
                lvt_16_1_ = 3;
            }
            else
            {
                lvt_16_1_ = 4;
            }

            if (this.server.pingToServer < 0L)
            {
                lvt_18_1_ = "(no connection)";
            }
            else
            {
                lvt_18_1_ = this.server.pingToServer + "ms";
                lvt_17_1_ = this.server.playerList;
            }
        }
        else
        {
            lvt_15_1_ = 1;
            lvt_16_1_ = (int)(Minecraft.getSystemTime() / 100L + (long)(slotIndex * 2) & 7L);

            if (lvt_16_1_ > 4)
            {
                lvt_16_1_ = 8 - lvt_16_1_;
            }

            lvt_18_1_ = "Pinging...";
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, (float)(lvt_15_1_ * 10), (float)(176 + lvt_16_1_ * 8), 10, 8, 256.0F, 256.0F);

        if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.field_148299_g))
        {
            this.field_148299_g = this.server.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.owner.getServerList().saveServerList();
        }

        if (this.field_148305_h != null)
        {
            this.drawTextureAt(x, y, this.serverIcon);
        }
        else
        {
            this.drawTextureAt(x, y, UNKNOWN_SERVER);
        }

        int lvt_19_1_ = mouseX - x;
        int lvt_20_1_ = mouseY - y;

        if (lvt_19_1_ >= listWidth - 15 && lvt_19_1_ <= listWidth - 5 && lvt_20_1_ >= 0 && lvt_20_1_ <= 8)
        {
            this.owner.setHoveringText(lvt_18_1_);
        }
        else if (lvt_19_1_ >= listWidth - lvt_14_1_ - 15 - 2 && lvt_19_1_ <= listWidth - 15 - 2 && lvt_20_1_ >= 0 && lvt_20_1_ <= 8)
        {
            this.owner.setHoveringText(lvt_17_1_);
        }

        if (this.mc.gameSettings.touchscreen || isSelected)
        {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int lvt_21_1_ = mouseX - x;
            int lvt_22_1_ = mouseY - y;

            if (this.func_178013_b())
            {
                if (lvt_21_1_ < 32 && lvt_21_1_ > 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.owner.func_175392_a(this, slotIndex))
            {
                if (lvt_21_1_ < 16 && lvt_22_1_ < 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (this.owner.func_175394_b(this, slotIndex))
            {
                if (lvt_21_1_ < 16 && lvt_22_1_ > 16)
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }
    }

    protected void drawTextureAt(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_)
    {
        this.mc.getTextureManager().bindTexture(p_178012_3_);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b()
    {
        return true;
    }

    private void prepareServerIcon()
    {
        if (this.server.getBase64EncodedIconData() == null)
        {
            this.mc.getTextureManager().deleteTexture(this.serverIcon);
            this.field_148305_h = null;
        }
        else
        {
            ByteBuf lvt_2_1_ = Unpooled.copiedBuffer(this.server.getBase64EncodedIconData(), Charsets.UTF_8);
            ByteBuf lvt_3_1_ = Base64.decode(lvt_2_1_);
            BufferedImage lvt_1_1_;
            label101:
            {
                try
                {
                    lvt_1_1_ = TextureUtil.readBufferedImage(new ByteBufInputStream(lvt_3_1_));
                    Validate.validState(lvt_1_1_.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(lvt_1_1_.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break label101;
                }
                catch (Throwable var8)
                {
                    logger.error("Invalid icon for server " + this.server.serverName + " (" + this.server.serverIP + ")", var8);
                    this.server.setBase64EncodedIconData((String)null);
                }
                finally
                {
                    lvt_2_1_.release();
                    lvt_3_1_.release();
                }

                return;
            }

            if (this.field_148305_h == null)
            {
                this.field_148305_h = new DynamicTexture(lvt_1_1_.getWidth(), lvt_1_1_.getHeight());
                this.mc.getTextureManager().loadTexture(this.serverIcon, this.field_148305_h);
            }

            lvt_1_1_.getRGB(0, 0, lvt_1_1_.getWidth(), lvt_1_1_.getHeight(), this.field_148305_h.getTextureData(), 0, lvt_1_1_.getWidth());
            this.field_148305_h.updateDynamicTexture();
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        if (p_148278_5_ <= 32)
        {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b())
            {
                this.owner.selectServer(slotIndex);
                this.owner.connectToSelected();
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ < 16 && this.owner.func_175392_a(this, slotIndex))
            {
                this.owner.func_175391_a(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ > 16 && this.owner.func_175394_b(this, slotIndex))
            {
                this.owner.func_175393_b(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }
        }

        this.owner.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.field_148298_f < 250L)
        {
            this.owner.connectToSelected();
        }

        this.field_148298_f = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
    }

    public ServerData getServerData()
    {
        return this.server;
    }
}
