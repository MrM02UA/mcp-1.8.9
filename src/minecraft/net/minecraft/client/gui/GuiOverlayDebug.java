package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug extends Gui
{
    private final Minecraft mc;
    private final FontRenderer fontRenderer;

    public GuiOverlayDebug(Minecraft mc)
    {
        this.mc = mc;
        this.fontRenderer = mc.fontRendererObj;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn)
    {
        this.mc.mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(scaledResolutionIn);
        GlStateManager.popMatrix();

        if (this.mc.gameSettings.showLagometer)
        {
            this.renderLagometer();
        }

        this.mc.mcProfiler.endSection();
    }

    private boolean isReducedDebug()
    {
        return this.mc.thePlayer.hasReducedDebug() || this.mc.gameSettings.reducedDebugInfo;
    }

    protected void renderDebugInfoLeft()
    {
        List<String> lvt_1_1_ = this.call();

        for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.size(); ++lvt_2_1_)
        {
            String lvt_3_1_ = (String)lvt_1_1_.get(lvt_2_1_);

            if (!Strings.isNullOrEmpty(lvt_3_1_))
            {
                int lvt_4_1_ = this.fontRenderer.FONT_HEIGHT;
                int lvt_5_1_ = this.fontRenderer.getStringWidth(lvt_3_1_);
                int lvt_6_1_ = 2;
                int lvt_7_1_ = 2 + lvt_4_1_ * lvt_2_1_;
                drawRect(1, lvt_7_1_ - 1, 2 + lvt_5_1_ + 1, lvt_7_1_ + lvt_4_1_ - 1, -1873784752);
                this.fontRenderer.drawString(lvt_3_1_, 2, lvt_7_1_, 14737632);
            }
        }
    }

    protected void renderDebugInfoRight(ScaledResolution scaledRes)
    {
        List<String> lvt_2_1_ = this.getDebugInfoRight();

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_)
        {
            String lvt_4_1_ = (String)lvt_2_1_.get(lvt_3_1_);

            if (!Strings.isNullOrEmpty(lvt_4_1_))
            {
                int lvt_5_1_ = this.fontRenderer.FONT_HEIGHT;
                int lvt_6_1_ = this.fontRenderer.getStringWidth(lvt_4_1_);
                int lvt_7_1_ = scaledRes.getScaledWidth() - 2 - lvt_6_1_;
                int lvt_8_1_ = 2 + lvt_5_1_ * lvt_3_1_;
                drawRect(lvt_7_1_ - 1, lvt_8_1_ - 1, lvt_7_1_ + lvt_6_1_ + 1, lvt_8_1_ + lvt_5_1_ - 1, -1873784752);
                this.fontRenderer.drawString(lvt_4_1_, lvt_7_1_, lvt_8_1_, 14737632);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected List<String> call()
    {
        BlockPos lvt_1_1_ = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

        if (this.isReducedDebug())
        {
            return Lists.newArrayList(new String[] {"Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(), this.mc.theWorld.getProviderName(), "", String.format("Chunk-relative: %d %d %d", new Object[]{Integer.valueOf(lvt_1_1_.getX() & 15), Integer.valueOf(lvt_1_1_.getY() & 15), Integer.valueOf(lvt_1_1_.getZ() & 15)})});
        }
        else
        {
            Entity lvt_2_1_ = this.mc.getRenderViewEntity();
            EnumFacing lvt_3_1_ = lvt_2_1_.getHorizontalFacing();
            String lvt_4_1_ = "Invalid";

            switch (lvt_3_1_)
            {
                case NORTH:
                    lvt_4_1_ = "Towards negative Z";
                    break;

                case SOUTH:
                    lvt_4_1_ = "Towards positive Z";
                    break;

                case WEST:
                    lvt_4_1_ = "Towards negative X";
                    break;

                case EAST:
                    lvt_4_1_ = "Towards positive X";
            }

            List<String> lvt_5_1_ = Lists.newArrayList(new String[] {"Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(), this.mc.theWorld.getProviderName(), "", String.format("XYZ: %.3f / %.5f / %.3f", new Object[]{Double.valueOf(this.mc.getRenderViewEntity().posX), Double.valueOf(this.mc.getRenderViewEntity().getEntityBoundingBox().minY), Double.valueOf(this.mc.getRenderViewEntity().posZ)}), String.format("Block: %d %d %d", new Object[]{Integer.valueOf(lvt_1_1_.getX()), Integer.valueOf(lvt_1_1_.getY()), Integer.valueOf(lvt_1_1_.getZ())}), String.format("Chunk: %d %d %d in %d %d %d", new Object[]{Integer.valueOf(lvt_1_1_.getX() & 15), Integer.valueOf(lvt_1_1_.getY() & 15), Integer.valueOf(lvt_1_1_.getZ() & 15), Integer.valueOf(lvt_1_1_.getX() >> 4), Integer.valueOf(lvt_1_1_.getY() >> 4), Integer.valueOf(lvt_1_1_.getZ() >> 4)}), String.format("Facing: %s (%s) (%.1f / %.1f)", new Object[]{lvt_3_1_, lvt_4_1_, Float.valueOf(MathHelper.wrapAngleTo180_float(lvt_2_1_.rotationYaw)), Float.valueOf(MathHelper.wrapAngleTo180_float(lvt_2_1_.rotationPitch))})});

            if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(lvt_1_1_))
            {
                Chunk lvt_6_1_ = this.mc.theWorld.getChunkFromBlockCoords(lvt_1_1_);
                lvt_5_1_.add("Biome: " + lvt_6_1_.getBiome(lvt_1_1_, this.mc.theWorld.getWorldChunkManager()).biomeName);
                lvt_5_1_.add("Light: " + lvt_6_1_.getLightSubtracted(lvt_1_1_, 0) + " (" + lvt_6_1_.getLightFor(EnumSkyBlock.SKY, lvt_1_1_) + " sky, " + lvt_6_1_.getLightFor(EnumSkyBlock.BLOCK, lvt_1_1_) + " block)");
                DifficultyInstance lvt_7_1_ = this.mc.theWorld.getDifficultyForLocation(lvt_1_1_);

                if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null)
                {
                    EntityPlayerMP lvt_8_1_ = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());

                    if (lvt_8_1_ != null)
                    {
                        lvt_7_1_ = lvt_8_1_.worldObj.getDifficultyForLocation(new BlockPos(lvt_8_1_));
                    }
                }

                lvt_5_1_.add(String.format("Local Difficulty: %.2f (Day %d)", new Object[] {Float.valueOf(lvt_7_1_.getAdditionalDifficulty()), Long.valueOf(this.mc.theWorld.getWorldTime() / 24000L)}));
            }

            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive())
            {
                lvt_5_1_.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
            }

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos lvt_6_2_ = this.mc.objectMouseOver.getBlockPos();
                lvt_5_1_.add(String.format("Looking at: %d %d %d", new Object[] {Integer.valueOf(lvt_6_2_.getX()), Integer.valueOf(lvt_6_2_.getY()), Integer.valueOf(lvt_6_2_.getZ())}));
            }

            return lvt_5_1_;
        }
    }

    protected List<String> getDebugInfoRight()
    {
        long lvt_1_1_ = Runtime.getRuntime().maxMemory();
        long lvt_3_1_ = Runtime.getRuntime().totalMemory();
        long lvt_5_1_ = Runtime.getRuntime().freeMemory();
        long lvt_7_1_ = lvt_3_1_ - lvt_5_1_;
        List<String> lvt_9_1_ = Lists.newArrayList(new String[] {String.format("Java: %s %dbit", new Object[]{System.getProperty("java.version"), Integer.valueOf(this.mc.isJava64bit() ? 64 : 32)}), String.format("Mem: % 2d%% %03d/%03dMB", new Object[]{Long.valueOf(lvt_7_1_ * 100L / lvt_1_1_), Long.valueOf(bytesToMb(lvt_7_1_)), Long.valueOf(bytesToMb(lvt_1_1_))}), String.format("Allocated: % 2d%% %03dMB", new Object[]{Long.valueOf(lvt_3_1_ * 100L / lvt_1_1_), Long.valueOf(bytesToMb(lvt_3_1_))}), "", String.format("CPU: %s", new Object[]{OpenGlHelper.getCpu()}), "", String.format("Display: %dx%d (%s)", new Object[]{Integer.valueOf(Display.getWidth()), Integer.valueOf(Display.getHeight()), GL11.glGetString(GL11.GL_VENDOR)}), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION)});

        if (this.isReducedDebug())
        {
            return lvt_9_1_;
        }
        else
        {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos lvt_10_1_ = this.mc.objectMouseOver.getBlockPos();
                IBlockState lvt_11_1_ = this.mc.theWorld.getBlockState(lvt_10_1_);

                if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD)
                {
                    lvt_11_1_ = lvt_11_1_.getBlock().getActualState(lvt_11_1_, this.mc.theWorld, lvt_10_1_);
                }

                lvt_9_1_.add("");
                lvt_9_1_.add(String.valueOf(Block.blockRegistry.getNameForObject(lvt_11_1_.getBlock())));

                for (Entry<IProperty, Comparable> lvt_13_1_ : lvt_11_1_.getProperties().entrySet())
                {
                    String lvt_14_1_ = ((Comparable)lvt_13_1_.getValue()).toString();

                    if (lvt_13_1_.getValue() == Boolean.TRUE)
                    {
                        lvt_14_1_ = EnumChatFormatting.GREEN + lvt_14_1_;
                    }
                    else if (lvt_13_1_.getValue() == Boolean.FALSE)
                    {
                        lvt_14_1_ = EnumChatFormatting.RED + lvt_14_1_;
                    }

                    lvt_9_1_.add(((IProperty)lvt_13_1_.getKey()).getName() + ": " + lvt_14_1_);
                }
            }

            return lvt_9_1_;
        }
    }

    private void renderLagometer()
    {
        GlStateManager.disableDepth();
        FrameTimer lvt_1_1_ = this.mc.getFrameTimer();
        int lvt_2_1_ = lvt_1_1_.getLastIndex();
        int lvt_3_1_ = lvt_1_1_.getIndex();
        long[] lvt_4_1_ = lvt_1_1_.getFrames();
        ScaledResolution lvt_5_1_ = new ScaledResolution(this.mc);
        int lvt_6_1_ = lvt_2_1_;
        int lvt_7_1_ = 0;
        drawRect(0, lvt_5_1_.getScaledHeight() - 60, 240, lvt_5_1_.getScaledHeight(), -1873784752);

        while (lvt_6_1_ != lvt_3_1_)
        {
            int lvt_8_1_ = lvt_1_1_.getLagometerValue(lvt_4_1_[lvt_6_1_], 30);
            int lvt_9_1_ = this.getFrameColor(MathHelper.clamp_int(lvt_8_1_, 0, 60), 0, 30, 60);
            this.drawVerticalLine(lvt_7_1_, lvt_5_1_.getScaledHeight(), lvt_5_1_.getScaledHeight() - lvt_8_1_, lvt_9_1_);
            ++lvt_7_1_;
            lvt_6_1_ = lvt_1_1_.parseIndex(lvt_6_1_ + 1);
        }

        drawRect(1, lvt_5_1_.getScaledHeight() - 30 + 1, 14, lvt_5_1_.getScaledHeight() - 30 + 10, -1873784752);
        this.fontRenderer.drawString("60", 2, lvt_5_1_.getScaledHeight() - 30 + 2, 14737632);
        this.drawHorizontalLine(0, 239, lvt_5_1_.getScaledHeight() - 30, -1);
        drawRect(1, lvt_5_1_.getScaledHeight() - 60 + 1, 14, lvt_5_1_.getScaledHeight() - 60 + 10, -1873784752);
        this.fontRenderer.drawString("30", 2, lvt_5_1_.getScaledHeight() - 60 + 2, 14737632);
        this.drawHorizontalLine(0, 239, lvt_5_1_.getScaledHeight() - 60, -1);
        this.drawHorizontalLine(0, 239, lvt_5_1_.getScaledHeight() - 1, -1);
        this.drawVerticalLine(0, lvt_5_1_.getScaledHeight() - 60, lvt_5_1_.getScaledHeight(), -1);
        this.drawVerticalLine(239, lvt_5_1_.getScaledHeight() - 60, lvt_5_1_.getScaledHeight(), -1);

        if (this.mc.gameSettings.limitFramerate <= 120)
        {
            this.drawHorizontalLine(0, 239, lvt_5_1_.getScaledHeight() - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
        }

        GlStateManager.enableDepth();
    }

    private int getFrameColor(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_)
    {
        return p_181552_1_ < p_181552_3_ ? this.blendColors(-16711936, -256, (float)p_181552_1_ / (float)p_181552_3_) : this.blendColors(-256, -65536, (float)(p_181552_1_ - p_181552_3_) / (float)(p_181552_4_ - p_181552_3_));
    }

    private int blendColors(int p_181553_1_, int p_181553_2_, float p_181553_3_)
    {
        int lvt_4_1_ = p_181553_1_ >> 24 & 255;
        int lvt_5_1_ = p_181553_1_ >> 16 & 255;
        int lvt_6_1_ = p_181553_1_ >> 8 & 255;
        int lvt_7_1_ = p_181553_1_ & 255;
        int lvt_8_1_ = p_181553_2_ >> 24 & 255;
        int lvt_9_1_ = p_181553_2_ >> 16 & 255;
        int lvt_10_1_ = p_181553_2_ >> 8 & 255;
        int lvt_11_1_ = p_181553_2_ & 255;
        int lvt_12_1_ = MathHelper.clamp_int((int)((float)lvt_4_1_ + (float)(lvt_8_1_ - lvt_4_1_) * p_181553_3_), 0, 255);
        int lvt_13_1_ = MathHelper.clamp_int((int)((float)lvt_5_1_ + (float)(lvt_9_1_ - lvt_5_1_) * p_181553_3_), 0, 255);
        int lvt_14_1_ = MathHelper.clamp_int((int)((float)lvt_6_1_ + (float)(lvt_10_1_ - lvt_6_1_) * p_181553_3_), 0, 255);
        int lvt_15_1_ = MathHelper.clamp_int((int)((float)lvt_7_1_ + (float)(lvt_11_1_ - lvt_7_1_) * p_181553_3_), 0, 255);
        return lvt_12_1_ << 24 | lvt_13_1_ << 16 | lvt_14_1_ << 8 | lvt_15_1_;
    }

    private static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }
}
