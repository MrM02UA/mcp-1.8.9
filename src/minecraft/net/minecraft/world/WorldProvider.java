package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderDebug;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider
{
    public static final float[] moonPhaseFactors = new float[] {1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};

    /** world object being used */
    protected World worldObj;
    private WorldType terrainType;
    private String generatorSettings;

    /** World chunk manager being used to generate chunks */
    protected WorldChunkManager worldChunkMgr;

    /**
     * States whether the Hell world provider is used(true) or if the normal world provider is used(false)
     */
    protected boolean isHellWorld;

    /**
     * A boolean that tells if a world does not have a sky. Used in calculating weather and skylight
     */
    protected boolean hasNoSky;

    /** Light to brightness conversion table */
    protected final float[] lightBrightnessTable = new float[16];

    /** The id for the dimension (ex. -1: Nether, 0: Overworld, 1: The End) */
    protected int dimensionId;

    /** Array for sunrise/sunset colors (RGBA) */
    private final float[] colorsSunriseSunset = new float[4];

    /**
     * associate an existing world with a World provider, and setup its lightbrightness table
     */
    public final void registerWorld(World worldIn)
    {
        this.worldObj = worldIn;
        this.terrainType = worldIn.getWorldInfo().getTerrainType();
        this.generatorSettings = worldIn.getWorldInfo().getGeneratorOptions();
        this.registerWorldChunkManager();
        this.generateLightBrightnessTable();
    }

    /**
     * Creates the light to brightness table
     */
    protected void generateLightBrightnessTable()
    {
        float lvt_1_1_ = 0.0F;

        for (int lvt_2_1_ = 0; lvt_2_1_ <= 15; ++lvt_2_1_)
        {
            float lvt_3_1_ = 1.0F - (float)lvt_2_1_ / 15.0F;
            this.lightBrightnessTable[lvt_2_1_] = (1.0F - lvt_3_1_) / (lvt_3_1_ * 3.0F + 1.0F) * (1.0F - lvt_1_1_) + lvt_1_1_;
        }
    }

    /**
     * creates a new world chunk manager for WorldProvider
     */
    protected void registerWorldChunkManager()
    {
        WorldType lvt_1_1_ = this.worldObj.getWorldInfo().getTerrainType();

        if (lvt_1_1_ == WorldType.FLAT)
        {
            FlatGeneratorInfo lvt_2_1_ = FlatGeneratorInfo.createFlatGeneratorFromString(this.worldObj.getWorldInfo().getGeneratorOptions());
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.getBiomeFromBiomeList(lvt_2_1_.getBiome(), BiomeGenBase.field_180279_ad), 0.5F);
        }
        else if (lvt_1_1_ == WorldType.DEBUG_WORLD)
        {
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.0F);
        }
        else
        {
            this.worldChunkMgr = new WorldChunkManager(this.worldObj);
        }
    }

    /**
     * Returns a new chunk provider which generates chunks for this world
     */
    public IChunkProvider createChunkGenerator()
    {
        return (IChunkProvider)(this.terrainType == WorldType.FLAT ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings) : (this.terrainType == WorldType.DEBUG_WORLD ? new ChunkProviderDebug(this.worldObj) : (this.terrainType == WorldType.CUSTOMIZED ? new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings) : new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings))));
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return this.worldObj.getGroundAboveSeaLevel(new BlockPos(x, 0, z)) == Blocks.grass;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
        int lvt_4_1_ = (int)(worldTime % 24000L);
        float lvt_5_1_ = ((float)lvt_4_1_ + partialTicks) / 24000.0F - 0.25F;

        if (lvt_5_1_ < 0.0F)
        {
            ++lvt_5_1_;
        }

        if (lvt_5_1_ > 1.0F)
        {
            --lvt_5_1_;
        }

        lvt_5_1_ = 1.0F - (float)((Math.cos((double)lvt_5_1_ * Math.PI) + 1.0D) / 2.0D);
        lvt_5_1_ = lvt_5_1_ + (lvt_5_1_ - lvt_5_1_) / 3.0F;
        return lvt_5_1_;
    }

    public int getMoonPhase(long worldTime)
    {
        return (int)(worldTime / 24000L % 8L + 8L) % 8;
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    public boolean isSurfaceWorld()
    {
        return true;
    }

    /**
     * Returns array with sunrise/sunset colors
     */
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks)
    {
        float lvt_3_1_ = 0.4F;
        float lvt_4_1_ = MathHelper.cos(celestialAngle * (float)Math.PI * 2.0F) - 0.0F;
        float lvt_5_1_ = -0.0F;

        if (lvt_4_1_ >= lvt_5_1_ - lvt_3_1_ && lvt_4_1_ <= lvt_5_1_ + lvt_3_1_)
        {
            float lvt_6_1_ = (lvt_4_1_ - lvt_5_1_) / lvt_3_1_ * 0.5F + 0.5F;
            float lvt_7_1_ = 1.0F - (1.0F - MathHelper.sin(lvt_6_1_ * (float)Math.PI)) * 0.99F;
            lvt_7_1_ = lvt_7_1_ * lvt_7_1_;
            this.colorsSunriseSunset[0] = lvt_6_1_ * 0.3F + 0.7F;
            this.colorsSunriseSunset[1] = lvt_6_1_ * lvt_6_1_ * 0.7F + 0.2F;
            this.colorsSunriseSunset[2] = lvt_6_1_ * lvt_6_1_ * 0.0F + 0.2F;
            this.colorsSunriseSunset[3] = lvt_7_1_;
            return this.colorsSunriseSunset;
        }
        else
        {
            return null;
        }
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_)
    {
        float lvt_3_1_ = MathHelper.cos(p_76562_1_ * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
        lvt_3_1_ = MathHelper.clamp_float(lvt_3_1_, 0.0F, 1.0F);
        float lvt_4_1_ = 0.7529412F;
        float lvt_5_1_ = 0.84705883F;
        float lvt_6_1_ = 1.0F;
        lvt_4_1_ = lvt_4_1_ * (lvt_3_1_ * 0.94F + 0.06F);
        lvt_5_1_ = lvt_5_1_ * (lvt_3_1_ * 0.94F + 0.06F);
        lvt_6_1_ = lvt_6_1_ * (lvt_3_1_ * 0.91F + 0.09F);
        return new Vec3((double)lvt_4_1_, (double)lvt_5_1_, (double)lvt_6_1_);
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere()
    {
        return true;
    }

    public static WorldProvider getProviderForDimension(int dimension)
    {
        return (WorldProvider)(dimension == -1 ? new WorldProviderHell() : (dimension == 0 ? new WorldProviderSurface() : (dimension == 1 ? new WorldProviderEnd() : null)));
    }

    /**
     * the y level at which clouds are rendered.
     */
    public float getCloudHeight()
    {
        return 128.0F;
    }

    public boolean isSkyColored()
    {
        return true;
    }

    public BlockPos getSpawnCoordinate()
    {
        return null;
    }

    public int getAverageGroundLevel()
    {
        return this.terrainType == WorldType.FLAT ? 4 : this.worldObj.getSeaLevel() + 1;
    }

    /**
     * Returns a double value representing the Y value relative to the top of the map at which void fog is at its
     * maximum. The default factor of 0.03125 relative to 256, for example, means the void fog will be at its maximum at
     * (256*0.03125), or 8.
     */
    public double getVoidFogYFactor()
    {
        return this.terrainType == WorldType.FLAT ? 1.0D : 0.03125D;
    }

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    public boolean doesXZShowFog(int x, int z)
    {
        return false;
    }

    /**
     * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
     */
    public abstract String getDimensionName();

    public abstract String getInternalNameSuffix();

    public WorldChunkManager getWorldChunkManager()
    {
        return this.worldChunkMgr;
    }

    public boolean doesWaterVaporize()
    {
        return this.isHellWorld;
    }

    public boolean getHasNoSky()
    {
        return this.hasNoSky;
    }

    public float[] getLightBrightnessTable()
    {
        return this.lightBrightnessTable;
    }

    /**
     * Gets the dimension of the provider
     */
    public int getDimensionId()
    {
        return this.dimensionId;
    }

    public WorldBorder getWorldBorder()
    {
        return new WorldBorder();
    }
}
