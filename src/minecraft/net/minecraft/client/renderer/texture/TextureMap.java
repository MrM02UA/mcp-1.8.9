package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject
{
    private static final Logger logger = LogManager.getLogger();
    public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
    public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
    private final List<TextureAtlasSprite> listAnimatedSprites;
    private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
    private final Map<String, TextureAtlasSprite> mapUploadedSprites;
    private final String basePath;
    private final IIconCreator iconCreator;
    private int mipmapLevels;
    private final TextureAtlasSprite missingImage;

    public TextureMap(String p_i46099_1_)
    {
        this(p_i46099_1_, (IIconCreator)null);
    }

    public TextureMap(String p_i46100_1_, IIconCreator iconCreatorIn)
    {
        this.listAnimatedSprites = Lists.newArrayList();
        this.mapRegisteredSprites = Maps.newHashMap();
        this.mapUploadedSprites = Maps.newHashMap();
        this.missingImage = new TextureAtlasSprite("missingno");
        this.basePath = p_i46100_1_;
        this.iconCreator = iconCreatorIn;
    }

    private void initMissingImage()
    {
        int[] lvt_1_1_ = TextureUtil.missingTextureData;
        this.missingImage.setIconWidth(16);
        this.missingImage.setIconHeight(16);
        int[][] lvt_2_1_ = new int[this.mipmapLevels + 1][];
        lvt_2_1_[0] = lvt_1_1_;
        this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][] {lvt_2_1_}));
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        if (this.iconCreator != null)
        {
            this.loadSprites(resourceManager, this.iconCreator);
        }
    }

    public void loadSprites(IResourceManager resourceManager, IIconCreator p_174943_2_)
    {
        this.mapRegisteredSprites.clear();
        p_174943_2_.registerSprites(this);
        this.initMissingImage();
        this.deleteGlTexture();
        this.loadTextureAtlas(resourceManager);
    }

    public void loadTextureAtlas(IResourceManager resourceManager)
    {
        int lvt_2_1_ = Minecraft.getGLMaximumTextureSize();
        Stitcher lvt_3_1_ = new Stitcher(lvt_2_1_, lvt_2_1_, true, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int lvt_4_1_ = Integer.MAX_VALUE;
        int lvt_5_1_ = 1 << this.mipmapLevels;

        for (Entry<String, TextureAtlasSprite> lvt_7_1_ : this.mapRegisteredSprites.entrySet())
        {
            TextureAtlasSprite lvt_8_1_ = (TextureAtlasSprite)lvt_7_1_.getValue();
            ResourceLocation lvt_9_1_ = new ResourceLocation(lvt_8_1_.getIconName());
            ResourceLocation lvt_10_1_ = this.completeResourceLocation(lvt_9_1_, 0);

            try
            {
                IResource lvt_11_1_ = resourceManager.getResource(lvt_10_1_);
                BufferedImage[] lvt_12_1_ = new BufferedImage[1 + this.mipmapLevels];
                lvt_12_1_[0] = TextureUtil.readBufferedImage(lvt_11_1_.getInputStream());
                TextureMetadataSection lvt_13_1_ = (TextureMetadataSection)lvt_11_1_.getMetadata("texture");

                if (lvt_13_1_ != null)
                {
                    List<Integer> lvt_14_1_ = lvt_13_1_.getListMipmaps();

                    if (!lvt_14_1_.isEmpty())
                    {
                        int lvt_15_1_ = lvt_12_1_[0].getWidth();
                        int lvt_16_1_ = lvt_12_1_[0].getHeight();

                        if (MathHelper.roundUpToPowerOfTwo(lvt_15_1_) != lvt_15_1_ || MathHelper.roundUpToPowerOfTwo(lvt_16_1_) != lvt_16_1_)
                        {
                            throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                        }
                    }

                    Iterator lvt_15_2_ = lvt_14_1_.iterator();

                    while (lvt_15_2_.hasNext())
                    {
                        int lvt_16_2_ = ((Integer)lvt_15_2_.next()).intValue();

                        if (lvt_16_2_ > 0 && lvt_16_2_ < lvt_12_1_.length - 1 && lvt_12_1_[lvt_16_2_] == null)
                        {
                            ResourceLocation lvt_17_1_ = this.completeResourceLocation(lvt_9_1_, lvt_16_2_);

                            try
                            {
                                lvt_12_1_[lvt_16_2_] = TextureUtil.readBufferedImage(resourceManager.getResource(lvt_17_1_).getInputStream());
                            }
                            catch (IOException var22)
                            {
                                logger.error("Unable to load miplevel {} from: {}", new Object[] {Integer.valueOf(lvt_16_2_), lvt_17_1_, var22});
                            }
                        }
                    }
                }

                AnimationMetadataSection lvt_14_2_ = (AnimationMetadataSection)lvt_11_1_.getMetadata("animation");
                lvt_8_1_.loadSprite(lvt_12_1_, lvt_14_2_);
            }
            catch (RuntimeException var23)
            {
                logger.error("Unable to parse metadata from " + lvt_10_1_, var23);
                continue;
            }
            catch (IOException var24)
            {
                logger.error("Using missing texture, unable to load " + lvt_10_1_, var24);
                continue;
            }

            lvt_4_1_ = Math.min(lvt_4_1_, Math.min(lvt_8_1_.getIconWidth(), lvt_8_1_.getIconHeight()));
            int lvt_11_4_ = Math.min(Integer.lowestOneBit(lvt_8_1_.getIconWidth()), Integer.lowestOneBit(lvt_8_1_.getIconHeight()));

            if (lvt_11_4_ < lvt_5_1_)
            {
                logger.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[] {lvt_10_1_, Integer.valueOf(lvt_8_1_.getIconWidth()), Integer.valueOf(lvt_8_1_.getIconHeight()), Integer.valueOf(MathHelper.calculateLogBaseTwo(lvt_5_1_)), Integer.valueOf(MathHelper.calculateLogBaseTwo(lvt_11_4_))});
                lvt_5_1_ = lvt_11_4_;
            }

            lvt_3_1_.addSprite(lvt_8_1_);
        }

        int lvt_6_2_ = Math.min(lvt_4_1_, lvt_5_1_);
        int lvt_7_2_ = MathHelper.calculateLogBaseTwo(lvt_6_2_);

        if (lvt_7_2_ < this.mipmapLevels)
        {
            logger.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[] {this.basePath, Integer.valueOf(this.mipmapLevels), Integer.valueOf(lvt_7_2_), Integer.valueOf(lvt_6_2_)});
            this.mipmapLevels = lvt_7_2_;
        }

        for (final TextureAtlasSprite lvt_9_2_ : this.mapRegisteredSprites.values())
        {
            try
            {
                lvt_9_2_.generateMipmaps(this.mipmapLevels);
            }
            catch (Throwable var21)
            {
                CrashReport lvt_11_5_ = CrashReport.makeCrashReport(var21, "Applying mipmap");
                CrashReportCategory lvt_12_2_ = lvt_11_5_.makeCategory("Sprite being mipmapped");
                lvt_12_2_.addCrashSectionCallable("Sprite name", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return lvt_9_2_.getIconName();
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                lvt_12_2_.addCrashSectionCallable("Sprite size", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return lvt_9_2_.getIconWidth() + " x " + lvt_9_2_.getIconHeight();
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                lvt_12_2_.addCrashSectionCallable("Sprite frames", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return lvt_9_2_.getFrameCount() + " frames";
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                lvt_12_2_.addCrashSection("Mipmap levels", Integer.valueOf(this.mipmapLevels));
                throw new ReportedException(lvt_11_5_);
            }
        }

        this.missingImage.generateMipmaps(this.mipmapLevels);
        lvt_3_1_.addSprite(this.missingImage);

        try
        {
            lvt_3_1_.doStitch();
        }
        catch (StitcherException var20)
        {
            throw var20;
        }

        logger.info("Created: {}x{} {}-atlas", new Object[] {Integer.valueOf(lvt_3_1_.getCurrentWidth()), Integer.valueOf(lvt_3_1_.getCurrentHeight()), this.basePath});
        TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, lvt_3_1_.getCurrentWidth(), lvt_3_1_.getCurrentHeight());
        Map<String, TextureAtlasSprite> lvt_8_4_ = Maps.newHashMap(this.mapRegisteredSprites);

        for (TextureAtlasSprite lvt_10_3_ : lvt_3_1_.getStichSlots())
        {
            String lvt_11_6_ = lvt_10_3_.getIconName();
            lvt_8_4_.remove(lvt_11_6_);
            this.mapUploadedSprites.put(lvt_11_6_, lvt_10_3_);

            try
            {
                TextureUtil.uploadTextureMipmap(lvt_10_3_.getFrameTextureData(0), lvt_10_3_.getIconWidth(), lvt_10_3_.getIconHeight(), lvt_10_3_.getOriginX(), lvt_10_3_.getOriginY(), false, false);
            }
            catch (Throwable var19)
            {
                CrashReport lvt_13_2_ = CrashReport.makeCrashReport(var19, "Stitching texture atlas");
                CrashReportCategory lvt_14_3_ = lvt_13_2_.makeCategory("Texture being stitched together");
                lvt_14_3_.addCrashSection("Atlas path", this.basePath);
                lvt_14_3_.addCrashSection("Sprite", lvt_10_3_);
                throw new ReportedException(lvt_13_2_);
            }

            if (lvt_10_3_.hasAnimationMetadata())
            {
                this.listAnimatedSprites.add(lvt_10_3_);
            }
        }

        for (TextureAtlasSprite lvt_10_4_ : lvt_8_4_.values())
        {
            lvt_10_4_.copyFrom(this.missingImage);
        }
    }

    private ResourceLocation completeResourceLocation(ResourceLocation location, int p_147634_2_)
    {
        return p_147634_2_ == 0 ? new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", new Object[] {this.basePath, location.getResourcePath(), ".png"})): new ResourceLocation(location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", new Object[] {this.basePath, location.getResourcePath(), Integer.valueOf(p_147634_2_), ".png"}));
    }

    public TextureAtlasSprite getAtlasSprite(String iconName)
    {
        TextureAtlasSprite lvt_2_1_ = (TextureAtlasSprite)this.mapUploadedSprites.get(iconName);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = this.missingImage;
        }

        return lvt_2_1_;
    }

    public void updateAnimations()
    {
        TextureUtil.bindTexture(this.getGlTextureId());

        for (TextureAtlasSprite lvt_2_1_ : this.listAnimatedSprites)
        {
            lvt_2_1_.updateAnimation();
        }
    }

    public TextureAtlasSprite registerSprite(ResourceLocation location)
    {
        if (location == null)
        {
            throw new IllegalArgumentException("Location cannot be null!");
        }
        else
        {
            TextureAtlasSprite lvt_2_1_ = (TextureAtlasSprite)this.mapRegisteredSprites.get(location);

            if (lvt_2_1_ == null)
            {
                lvt_2_1_ = TextureAtlasSprite.makeAtlasSprite(location);
                this.mapRegisteredSprites.put(location.toString(), lvt_2_1_);
            }

            return lvt_2_1_;
        }
    }

    public void tick()
    {
        this.updateAnimations();
    }

    public void setMipmapLevels(int mipmapLevelsIn)
    {
        this.mipmapLevels = mipmapLevelsIn;
    }

    public TextureAtlasSprite getMissingSprite()
    {
        return this.missingImage;
    }
}
