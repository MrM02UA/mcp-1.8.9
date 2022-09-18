package net.minecraft.client.stream;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.VideoParams;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class TwitchStream implements BroadcastController.BroadcastListener, ChatController.ChatListener, IngestServerTester.IngestTestListener, IStream
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker STREAM_MARKER = MarkerManager.getMarker("STREAM");
    private final BroadcastController broadcastController;
    private final ChatController chatController;
    private String field_176029_e;

    /** the minecraft instance */
    private final Minecraft mc;
    private final IChatComponent twitchComponent = new ChatComponentText("Twitch");
    private final Map<String, ChatUserInfo> field_152955_g = Maps.newHashMap();
    private Framebuffer framebuffer;
    private boolean field_152957_i;

    /** stream's target fps */
    private int targetFPS = 30;
    private long field_152959_k = 0L;
    private boolean field_152960_l = false;
    private boolean loggedIn;
    private boolean field_152962_n;
    private boolean field_152963_o;
    private IStream.AuthFailureReason authFailureReason = IStream.AuthFailureReason.ERROR;
    private static boolean field_152965_q;

    public TwitchStream(Minecraft mcIn, final Property streamProperty)
    {
        this.mc = mcIn;
        this.broadcastController = new BroadcastController();
        this.chatController = new ChatController();
        this.broadcastController.setBroadcastListener(this);
        this.chatController.func_152990_a(this);
        this.broadcastController.func_152842_a("nmt37qblda36pvonovdkbopzfzw3wlq");
        this.chatController.func_152984_a("nmt37qblda36pvonovdkbopzfzw3wlq");
        this.twitchComponent.getChatStyle().setColor(EnumChatFormatting.DARK_PURPLE);

        if (streamProperty != null && !Strings.isNullOrEmpty(streamProperty.getValue()) && OpenGlHelper.framebufferSupported)
        {
            Thread lvt_3_1_ = new Thread("Twitch authenticator")
            {
                public void run()
                {
                    try
                    {
                        URL lvt_1_1_ = new URL("https://api.twitch.tv/kraken?oauth_token=" + URLEncoder.encode(streamProperty.getValue(), "UTF-8"));
                        String lvt_2_1_ = HttpUtil.get(lvt_1_1_);
                        JsonObject lvt_3_1_ = JsonUtils.getJsonObject((new JsonParser()).parse(lvt_2_1_), "Response");
                        JsonObject lvt_4_1_ = JsonUtils.getJsonObject(lvt_3_1_, "token");

                        if (JsonUtils.getBoolean(lvt_4_1_, "valid"))
                        {
                            String lvt_5_1_ = JsonUtils.getString(lvt_4_1_, "user_name");
                            TwitchStream.LOGGER.debug(TwitchStream.STREAM_MARKER, "Authenticated with twitch; username is {}", new Object[] {lvt_5_1_});
                            AuthToken lvt_6_1_ = new AuthToken();
                            lvt_6_1_.data = streamProperty.getValue();
                            TwitchStream.this.broadcastController.func_152818_a(lvt_5_1_, lvt_6_1_);
                            TwitchStream.this.chatController.func_152998_c(lvt_5_1_);
                            TwitchStream.this.chatController.func_152994_a(lvt_6_1_);
                            Runtime.getRuntime().addShutdownHook(new Thread("Twitch shutdown hook")
                            {
                                public void run()
                                {
                                    TwitchStream.this.shutdownStream();
                                }
                            });
                            TwitchStream.this.broadcastController.func_152817_A();
                            TwitchStream.this.chatController.func_175984_n();
                        }
                        else
                        {
                            TwitchStream.this.authFailureReason = IStream.AuthFailureReason.INVALID_TOKEN;
                            TwitchStream.LOGGER.error(TwitchStream.STREAM_MARKER, "Given twitch access token is invalid");
                        }
                    }
                    catch (IOException var7)
                    {
                        TwitchStream.this.authFailureReason = IStream.AuthFailureReason.ERROR;
                        TwitchStream.LOGGER.error(TwitchStream.STREAM_MARKER, "Could not authenticate with twitch", var7);
                    }
                }
            };
            lvt_3_1_.setDaemon(true);
            lvt_3_1_.start();
        }
    }

    /**
     * Shuts down a steam
     */
    public void shutdownStream()
    {
        LOGGER.debug(STREAM_MARKER, "Shutdown streaming");
        this.broadcastController.statCallback();
        this.chatController.func_175988_p();
    }

    public void func_152935_j()
    {
        int lvt_1_1_ = this.mc.gameSettings.streamChatEnabled;
        boolean lvt_2_1_ = this.field_176029_e != null && this.chatController.func_175990_d(this.field_176029_e);
        boolean lvt_3_1_ = this.chatController.func_153000_j() == ChatController.ChatState.Initialized && (this.field_176029_e == null || this.chatController.func_175989_e(this.field_176029_e) == ChatController.EnumChannelState.Disconnected);

        if (lvt_1_1_ == 2)
        {
            if (lvt_2_1_)
            {
                LOGGER.debug(STREAM_MARKER, "Disconnecting from twitch chat per user options");
                this.chatController.func_175991_l(this.field_176029_e);
            }
        }
        else if (lvt_1_1_ == 1)
        {
            if (lvt_3_1_ && this.broadcastController.func_152849_q())
            {
                LOGGER.debug(STREAM_MARKER, "Connecting to twitch chat per user options");
                this.func_152942_I();
            }
        }
        else if (lvt_1_1_ == 0)
        {
            if (lvt_2_1_ && !this.isBroadcasting())
            {
                LOGGER.debug(STREAM_MARKER, "Disconnecting from twitch chat as user is no longer streaming");
                this.chatController.func_175991_l(this.field_176029_e);
            }
            else if (lvt_3_1_ && this.isBroadcasting())
            {
                LOGGER.debug(STREAM_MARKER, "Connecting to twitch chat as user is streaming");
                this.func_152942_I();
            }
        }

        this.broadcastController.func_152821_H();
        this.chatController.func_152997_n();
    }

    protected void func_152942_I()
    {
        ChatController.ChatState lvt_1_1_ = this.chatController.func_153000_j();
        String lvt_2_1_ = this.broadcastController.getChannelInfo().name;
        this.field_176029_e = lvt_2_1_;

        if (lvt_1_1_ != ChatController.ChatState.Initialized)
        {
            LOGGER.warn("Invalid twitch chat state {}", new Object[] {lvt_1_1_});
        }
        else if (this.chatController.func_175989_e(this.field_176029_e) == ChatController.EnumChannelState.Disconnected)
        {
            this.chatController.func_152986_d(lvt_2_1_);
        }
        else
        {
            LOGGER.warn("Invalid twitch chat state {}", new Object[] {lvt_1_1_});
        }
    }

    public void func_152922_k()
    {
        if (this.broadcastController.isBroadcasting() && !this.broadcastController.isBroadcastPaused())
        {
            long lvt_1_1_ = System.nanoTime();
            long lvt_3_1_ = (long)(1000000000 / this.targetFPS);
            long lvt_5_1_ = lvt_1_1_ - this.field_152959_k;
            boolean lvt_7_1_ = lvt_5_1_ >= lvt_3_1_;

            if (lvt_7_1_)
            {
                FrameBuffer lvt_8_1_ = this.broadcastController.func_152822_N();
                Framebuffer lvt_9_1_ = this.mc.getFramebuffer();
                this.framebuffer.bindFramebuffer(true);
                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D, (double)this.framebuffer.framebufferWidth, (double)this.framebuffer.framebufferHeight, 0.0D, 1000.0D, 3000.0D);
                GlStateManager.matrixMode(5888);
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -2000.0F);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.viewport(0, 0, this.framebuffer.framebufferWidth, this.framebuffer.framebufferHeight);
                GlStateManager.enableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                float lvt_10_1_ = (float)this.framebuffer.framebufferWidth;
                float lvt_11_1_ = (float)this.framebuffer.framebufferHeight;
                float lvt_12_1_ = (float)lvt_9_1_.framebufferWidth / (float)lvt_9_1_.framebufferTextureWidth;
                float lvt_13_1_ = (float)lvt_9_1_.framebufferHeight / (float)lvt_9_1_.framebufferTextureHeight;
                lvt_9_1_.bindFramebufferTexture();
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 9729.0F);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 9729.0F);
                Tessellator lvt_14_1_ = Tessellator.getInstance();
                WorldRenderer lvt_15_1_ = lvt_14_1_.getWorldRenderer();
                lvt_15_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
                lvt_15_1_.pos(0.0D, (double)lvt_11_1_, 0.0D).tex(0.0D, (double)lvt_13_1_).endVertex();
                lvt_15_1_.pos((double)lvt_10_1_, (double)lvt_11_1_, 0.0D).tex((double)lvt_12_1_, (double)lvt_13_1_).endVertex();
                lvt_15_1_.pos((double)lvt_10_1_, 0.0D, 0.0D).tex((double)lvt_12_1_, 0.0D).endVertex();
                lvt_15_1_.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
                lvt_14_1_.draw();
                lvt_9_1_.unbindFramebufferTexture();
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5889);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                this.broadcastController.captureFramebuffer(lvt_8_1_);
                this.framebuffer.unbindFramebuffer();
                this.broadcastController.submitStreamFrame(lvt_8_1_);
                this.field_152959_k = lvt_1_1_;
            }
        }
    }

    public boolean func_152936_l()
    {
        return this.broadcastController.func_152849_q();
    }

    public boolean isReadyToBroadcast()
    {
        return this.broadcastController.isReadyToBroadcast();
    }

    public boolean isBroadcasting()
    {
        return this.broadcastController.isBroadcasting();
    }

    public void func_152911_a(Metadata p_152911_1_, long p_152911_2_)
    {
        if (this.isBroadcasting() && this.field_152957_i)
        {
            long lvt_4_1_ = this.broadcastController.getStreamTime();

            if (!this.broadcastController.func_152840_a(p_152911_1_.func_152810_c(), lvt_4_1_ + p_152911_2_, p_152911_1_.func_152809_a(), p_152911_1_.func_152806_b()))
            {
                LOGGER.warn(STREAM_MARKER, "Couldn\'t send stream metadata action at {}: {}", new Object[] {Long.valueOf(lvt_4_1_ + p_152911_2_), p_152911_1_});
            }
            else
            {
                LOGGER.debug(STREAM_MARKER, "Sent stream metadata action at {}: {}", new Object[] {Long.valueOf(lvt_4_1_ + p_152911_2_), p_152911_1_});
            }
        }
    }

    public void func_176026_a(Metadata p_176026_1_, long p_176026_2_, long p_176026_4_)
    {
        if (this.isBroadcasting() && this.field_152957_i)
        {
            long lvt_6_1_ = this.broadcastController.getStreamTime();
            String lvt_8_1_ = p_176026_1_.func_152809_a();
            String lvt_9_1_ = p_176026_1_.func_152806_b();
            long lvt_10_1_ = this.broadcastController.func_177946_b(p_176026_1_.func_152810_c(), lvt_6_1_ + p_176026_2_, lvt_8_1_, lvt_9_1_);

            if (lvt_10_1_ < 0L)
            {
                LOGGER.warn(STREAM_MARKER, "Could not send stream metadata sequence from {} to {}: {}", new Object[] {Long.valueOf(lvt_6_1_ + p_176026_2_), Long.valueOf(lvt_6_1_ + p_176026_4_), p_176026_1_});
            }
            else if (this.broadcastController.func_177947_a(p_176026_1_.func_152810_c(), lvt_6_1_ + p_176026_4_, lvt_10_1_, lvt_8_1_, lvt_9_1_))
            {
                LOGGER.debug(STREAM_MARKER, "Sent stream metadata sequence from {} to {}: {}", new Object[] {Long.valueOf(lvt_6_1_ + p_176026_2_), Long.valueOf(lvt_6_1_ + p_176026_4_), p_176026_1_});
            }
            else
            {
                LOGGER.warn(STREAM_MARKER, "Half-sent stream metadata sequence from {} to {}: {}", new Object[] {Long.valueOf(lvt_6_1_ + p_176026_2_), Long.valueOf(lvt_6_1_ + p_176026_4_), p_176026_1_});
            }
        }
    }

    public boolean isPaused()
    {
        return this.broadcastController.isBroadcastPaused();
    }

    public void requestCommercial()
    {
        if (this.broadcastController.requestCommercial())
        {
            LOGGER.debug(STREAM_MARKER, "Requested commercial from Twitch");
        }
        else
        {
            LOGGER.warn(STREAM_MARKER, "Could not request commercial from Twitch");
        }
    }

    /**
     * pauses a stream
     */
    public void pause()
    {
        this.broadcastController.func_152847_F();
        this.field_152962_n = true;
        this.updateStreamVolume();
    }

    /**
     * unpauses a stream
     */
    public void unpause()
    {
        this.broadcastController.func_152854_G();
        this.field_152962_n = false;
        this.updateStreamVolume();
    }

    public void updateStreamVolume()
    {
        if (this.isBroadcasting())
        {
            float lvt_1_1_ = this.mc.gameSettings.streamGameVolume;
            boolean lvt_2_1_ = this.field_152962_n || lvt_1_1_ <= 0.0F;
            this.broadcastController.setPlaybackDeviceVolume(lvt_2_1_ ? 0.0F : lvt_1_1_);
            this.broadcastController.setRecordingDeviceVolume(this.func_152929_G() ? 0.0F : this.mc.gameSettings.streamMicVolume);
        }
    }

    public void func_152930_t()
    {
        GameSettings lvt_1_1_ = this.mc.gameSettings;
        VideoParams lvt_2_1_ = this.broadcastController.func_152834_a(formatStreamKbps(lvt_1_1_.streamKbps), formatStreamFps(lvt_1_1_.streamFps), formatStreamBps(lvt_1_1_.streamBytesPerPixel), (float)this.mc.displayWidth / (float)this.mc.displayHeight);

        switch (lvt_1_1_.streamCompression)
        {
            case 0:
                lvt_2_1_.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_LOW;
                break;

            case 1:
                lvt_2_1_.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_MEDIUM;
                break;

            case 2:
                lvt_2_1_.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
        }

        if (this.framebuffer == null)
        {
            this.framebuffer = new Framebuffer(lvt_2_1_.outputWidth, lvt_2_1_.outputHeight, false);
        }
        else
        {
            this.framebuffer.createBindFramebuffer(lvt_2_1_.outputWidth, lvt_2_1_.outputHeight);
        }

        if (lvt_1_1_.streamPreferredServer != null && lvt_1_1_.streamPreferredServer.length() > 0)
        {
            for (IngestServer lvt_6_1_ : this.func_152925_v())
            {
                if (lvt_6_1_.serverUrl.equals(lvt_1_1_.streamPreferredServer))
                {
                    this.broadcastController.setIngestServer(lvt_6_1_);
                    break;
                }
            }
        }

        this.targetFPS = lvt_2_1_.targetFps;
        this.field_152957_i = lvt_1_1_.streamSendMetadata;
        this.broadcastController.func_152836_a(lvt_2_1_);
        LOGGER.info(STREAM_MARKER, "Streaming at {}/{} at {} kbps to {}", new Object[] {Integer.valueOf(lvt_2_1_.outputWidth), Integer.valueOf(lvt_2_1_.outputHeight), Integer.valueOf(lvt_2_1_.maxKbps), this.broadcastController.getIngestServer().serverUrl});
        this.broadcastController.func_152828_a((String)null, "Minecraft", (String)null);
    }

    public void stopBroadcasting()
    {
        if (this.broadcastController.stopBroadcasting())
        {
            LOGGER.info(STREAM_MARKER, "Stopped streaming to Twitch");
        }
        else
        {
            LOGGER.warn(STREAM_MARKER, "Could not stop streaming to Twitch");
        }
    }

    public void func_152900_a(ErrorCode p_152900_1_, AuthToken p_152900_2_)
    {
    }

    public void func_152897_a(ErrorCode p_152897_1_)
    {
        if (ErrorCode.succeeded(p_152897_1_))
        {
            LOGGER.debug(STREAM_MARKER, "Login attempt successful");
            this.loggedIn = true;
        }
        else
        {
            LOGGER.warn(STREAM_MARKER, "Login attempt unsuccessful: {} (error code {})", new Object[] {ErrorCode.getString(p_152897_1_), Integer.valueOf(p_152897_1_.getValue())});
            this.loggedIn = false;
        }
    }

    public void func_152898_a(ErrorCode p_152898_1_, GameInfo[] p_152898_2_)
    {
    }

    public void func_152891_a(BroadcastController.BroadcastState p_152891_1_)
    {
        LOGGER.debug(STREAM_MARKER, "Broadcast state changed to {}", new Object[] {p_152891_1_});

        if (p_152891_1_ == BroadcastController.BroadcastState.Initialized)
        {
            this.broadcastController.func_152827_a(BroadcastController.BroadcastState.Authenticated);
        }
    }

    public void func_152895_a()
    {
        LOGGER.info(STREAM_MARKER, "Logged out of twitch");
    }

    public void func_152894_a(StreamInfo p_152894_1_)
    {
        LOGGER.debug(STREAM_MARKER, "Stream info updated; {} viewers on stream ID {}", new Object[] {Integer.valueOf(p_152894_1_.viewers), Long.valueOf(p_152894_1_.streamId)});
    }

    public void func_152896_a(IngestList p_152896_1_)
    {
    }

    public void func_152893_b(ErrorCode p_152893_1_)
    {
        LOGGER.warn(STREAM_MARKER, "Issue submitting frame: {} (Error code {})", new Object[] {ErrorCode.getString(p_152893_1_), Integer.valueOf(p_152893_1_.getValue())});
        this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText("Issue streaming frame: " + p_152893_1_ + " (" + ErrorCode.getString(p_152893_1_) + ")"), 2);
    }

    public void func_152899_b()
    {
        this.updateStreamVolume();
        LOGGER.info(STREAM_MARKER, "Broadcast to Twitch has started");
    }

    public void func_152901_c()
    {
        LOGGER.info(STREAM_MARKER, "Broadcast to Twitch has stopped");
    }

    public void func_152892_c(ErrorCode p_152892_1_)
    {
        if (p_152892_1_ == ErrorCode.TTV_EC_SOUNDFLOWER_NOT_INSTALLED)
        {
            IChatComponent lvt_2_1_ = new ChatComponentTranslation("stream.unavailable.soundflower.chat.link", new Object[0]);
            lvt_2_1_.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://help.mojang.com/customer/portal/articles/1374877-configuring-soundflower-for-streaming-on-apple-computers"));
            lvt_2_1_.getChatStyle().setUnderlined(Boolean.valueOf(true));
            IChatComponent lvt_3_1_ = new ChatComponentTranslation("stream.unavailable.soundflower.chat", new Object[] {lvt_2_1_});
            lvt_3_1_.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
            this.mc.ingameGUI.getChatGUI().printChatMessage(lvt_3_1_);
        }
        else
        {
            IChatComponent lvt_2_2_ = new ChatComponentTranslation("stream.unavailable.unknown.chat", new Object[] {ErrorCode.getString(p_152892_1_)});
            lvt_2_2_.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
            this.mc.ingameGUI.getChatGUI().printChatMessage(lvt_2_2_);
        }
    }

    public void func_152907_a(IngestServerTester p_152907_1_, IngestServerTester.IngestTestState p_152907_2_)
    {
        LOGGER.debug(STREAM_MARKER, "Ingest test state changed to {}", new Object[] {p_152907_2_});

        if (p_152907_2_ == IngestServerTester.IngestTestState.Finished)
        {
            this.field_152960_l = true;
        }
    }

    public static int formatStreamFps(float p_152948_0_)
    {
        return MathHelper.floor_float(10.0F + p_152948_0_ * 50.0F);
    }

    public static int formatStreamKbps(float p_152946_0_)
    {
        return MathHelper.floor_float(230.0F + p_152946_0_ * 3270.0F);
    }

    public static float formatStreamBps(float p_152947_0_)
    {
        return 0.1F + p_152947_0_ * 0.1F;
    }

    public IngestServer[] func_152925_v()
    {
        return this.broadcastController.getIngestList().getServers();
    }

    public void func_152909_x()
    {
        IngestServerTester lvt_1_1_ = this.broadcastController.func_152838_J();

        if (lvt_1_1_ != null)
        {
            lvt_1_1_.func_153042_a(this);
        }
    }

    public IngestServerTester func_152932_y()
    {
        return this.broadcastController.isReady();
    }

    public boolean func_152908_z()
    {
        return this.broadcastController.isIngestTesting();
    }

    public int func_152920_A()
    {
        return this.isBroadcasting() ? this.broadcastController.getStreamInfo().viewers : 0;
    }

    public void func_176023_d(ErrorCode p_176023_1_)
    {
        if (ErrorCode.failed(p_176023_1_))
        {
            LOGGER.error(STREAM_MARKER, "Chat failed to initialize");
        }
    }

    public void func_176022_e(ErrorCode p_176022_1_)
    {
        if (ErrorCode.failed(p_176022_1_))
        {
            LOGGER.error(STREAM_MARKER, "Chat failed to shutdown");
        }
    }

    public void func_176017_a(ChatController.ChatState p_176017_1_)
    {
    }

    public void func_180605_a(String p_180605_1_, ChatRawMessage[] p_180605_2_)
    {
        for (ChatRawMessage lvt_6_1_ : p_180605_2_)
        {
            this.func_176027_a(lvt_6_1_.userName, lvt_6_1_);

            if (this.func_176028_a(lvt_6_1_.modes, lvt_6_1_.subscriptions, this.mc.gameSettings.streamChatUserFilter))
            {
                IChatComponent lvt_7_1_ = new ChatComponentText(lvt_6_1_.userName);
                IChatComponent lvt_8_1_ = new ChatComponentTranslation("chat.stream." + (lvt_6_1_.action ? "emote" : "text"), new Object[] {this.twitchComponent, lvt_7_1_, EnumChatFormatting.getTextWithoutFormattingCodes(lvt_6_1_.message)});

                if (lvt_6_1_.action)
                {
                    lvt_8_1_.getChatStyle().setItalic(Boolean.valueOf(true));
                }

                IChatComponent lvt_9_1_ = new ChatComponentText("");
                lvt_9_1_.appendSibling(new ChatComponentTranslation("stream.userinfo.chatTooltip", new Object[0]));

                for (IChatComponent lvt_11_1_ : GuiTwitchUserMode.func_152328_a(lvt_6_1_.modes, lvt_6_1_.subscriptions, (IStream)null))
                {
                    lvt_9_1_.appendText("\n");
                    lvt_9_1_.appendSibling(lvt_11_1_);
                }

                lvt_7_1_.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, lvt_9_1_));
                lvt_7_1_.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.TWITCH_USER_INFO, lvt_6_1_.userName));
                this.mc.ingameGUI.getChatGUI().printChatMessage(lvt_8_1_);
            }
        }
    }

    public void func_176025_a(String p_176025_1_, ChatTokenizedMessage[] p_176025_2_)
    {
    }

    private void func_176027_a(String p_176027_1_, ChatRawMessage p_176027_2_)
    {
        ChatUserInfo lvt_3_1_ = (ChatUserInfo)this.field_152955_g.get(p_176027_1_);

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = new ChatUserInfo();
            lvt_3_1_.displayName = p_176027_1_;
            this.field_152955_g.put(p_176027_1_, lvt_3_1_);
        }

        lvt_3_1_.subscriptions = p_176027_2_.subscriptions;
        lvt_3_1_.modes = p_176027_2_.modes;
        lvt_3_1_.nameColorARGB = p_176027_2_.nameColorARGB;
    }

    private boolean func_176028_a(Set<ChatUserMode> p_176028_1_, Set<ChatUserSubscription> p_176028_2_, int p_176028_3_)
    {
        return p_176028_1_.contains(ChatUserMode.TTV_CHAT_USERMODE_BANNED) ? false : (p_176028_1_.contains(ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR) ? true : (p_176028_1_.contains(ChatUserMode.TTV_CHAT_USERMODE_MODERATOR) ? true : (p_176028_1_.contains(ChatUserMode.TTV_CHAT_USERMODE_STAFF) ? true : (p_176028_3_ == 0 ? true : (p_176028_3_ == 1 ? p_176028_2_.contains(ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) : false)))));
    }

    public void func_176018_a(String p_176018_1_, ChatUserInfo[] p_176018_2_, ChatUserInfo[] p_176018_3_, ChatUserInfo[] p_176018_4_)
    {
        for (ChatUserInfo lvt_8_1_ : p_176018_3_)
        {
            this.field_152955_g.remove(lvt_8_1_.displayName);
        }

        for (ChatUserInfo lvt_8_2_ : p_176018_4_)
        {
            this.field_152955_g.put(lvt_8_2_.displayName, lvt_8_2_);
        }

        for (ChatUserInfo lvt_8_3_ : p_176018_2_)
        {
            this.field_152955_g.put(lvt_8_3_.displayName, lvt_8_3_);
        }
    }

    public void func_180606_a(String p_180606_1_)
    {
        LOGGER.debug(STREAM_MARKER, "Chat connected");
    }

    public void func_180607_b(String p_180607_1_)
    {
        LOGGER.debug(STREAM_MARKER, "Chat disconnected");
        this.field_152955_g.clear();
    }

    public void func_176019_a(String p_176019_1_, String p_176019_2_)
    {
    }

    public void func_176021_d()
    {
    }

    public void func_176024_e()
    {
    }

    public void func_176016_c(String p_176016_1_)
    {
    }

    public void func_176020_d(String p_176020_1_)
    {
    }

    public boolean func_152927_B()
    {
        return this.field_176029_e != null && this.field_176029_e.equals(this.broadcastController.getChannelInfo().name);
    }

    public String func_152921_C()
    {
        return this.field_176029_e;
    }

    public ChatUserInfo func_152926_a(String p_152926_1_)
    {
        return (ChatUserInfo)this.field_152955_g.get(p_152926_1_);
    }

    public void func_152917_b(String p_152917_1_)
    {
        this.chatController.func_175986_a(this.field_176029_e, p_152917_1_);
    }

    public boolean func_152928_D()
    {
        return field_152965_q && this.broadcastController.func_152858_b();
    }

    public ErrorCode func_152912_E()
    {
        return !field_152965_q ? ErrorCode.TTV_EC_OS_TOO_OLD : this.broadcastController.getErrorCode();
    }

    public boolean func_152913_F()
    {
        return this.loggedIn;
    }

    /**
     * mutes or unmutes the microphone based on the boolean parameter passed into the method
     */
    public void muteMicrophone(boolean p_152910_1_)
    {
        this.field_152963_o = p_152910_1_;
        this.updateStreamVolume();
    }

    public boolean func_152929_G()
    {
        boolean lvt_1_1_ = this.mc.gameSettings.streamMicToggleBehavior == 1;
        return this.field_152962_n || this.mc.gameSettings.streamMicVolume <= 0.0F || lvt_1_1_ != this.field_152963_o;
    }

    public IStream.AuthFailureReason func_152918_H()
    {
        return this.authFailureReason;
    }

    static
    {
        try
        {
            if (Util.getOSType() == Util.EnumOS.WINDOWS)
            {
                System.loadLibrary("avutil-ttv-51");
                System.loadLibrary("swresample-ttv-0");
                System.loadLibrary("libmp3lame-ttv");

                if (System.getProperty("os.arch").contains("64"))
                {
                    System.loadLibrary("libmfxsw64");
                }
                else
                {
                    System.loadLibrary("libmfxsw32");
                }
            }

            field_152965_q = true;
        }
        catch (Throwable var1)
        {
            field_152965_q = false;
        }
    }
}
