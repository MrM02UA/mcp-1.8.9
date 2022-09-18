package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenScatteredFeature extends MapGenStructure
{
    private static final List<BiomeGenBase> biomelist = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland});
    private List<BiomeGenBase.SpawnListEntry> scatteredFeatureSpawnList;

    /** the maximum distance between scattered features */
    private int maxDistanceBetweenScatteredFeatures;

    /** the minimum distance between scattered features */
    private int minDistanceBetweenScatteredFeatures;

    public MapGenScatteredFeature()
    {
        this.scatteredFeatureSpawnList = Lists.newArrayList();
        this.maxDistanceBetweenScatteredFeatures = 32;
        this.minDistanceBetweenScatteredFeatures = 8;
        this.scatteredFeatureSpawnList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenScatteredFeature(Map<String, String> p_i2061_1_)
    {
        this();

        for (Entry<String, String> lvt_3_1_ : p_i2061_1_.entrySet())
        {
            if (((String)lvt_3_1_.getKey()).equals("distance"))
            {
                this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax((String)lvt_3_1_.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Temple";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int lvt_3_1_ = chunkX;
        int lvt_4_1_ = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int lvt_5_1_ = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int lvt_6_1_ = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random lvt_7_1_ = this.worldObj.setRandomSeed(lvt_5_1_, lvt_6_1_, 14357617);
        lvt_5_1_ = lvt_5_1_ * this.maxDistanceBetweenScatteredFeatures;
        lvt_6_1_ = lvt_6_1_ * this.maxDistanceBetweenScatteredFeatures;
        lvt_5_1_ = lvt_5_1_ + lvt_7_1_.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
        lvt_6_1_ = lvt_6_1_ + lvt_7_1_.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);

        if (lvt_3_1_ == lvt_5_1_ && lvt_4_1_ == lvt_6_1_)
        {
            BiomeGenBase lvt_8_1_ = this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(lvt_3_1_ * 16 + 8, 0, lvt_4_1_ * 16 + 8));

            if (lvt_8_1_ == null)
            {
                return false;
            }

            for (BiomeGenBase lvt_10_1_ : biomelist)
            {
                if (lvt_8_1_ == lvt_10_1_)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenScatteredFeature.Start(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public boolean func_175798_a(BlockPos p_175798_1_)
    {
        StructureStart lvt_2_1_ = this.func_175797_c(p_175798_1_);

        if (lvt_2_1_ != null && lvt_2_1_ instanceof MapGenScatteredFeature.Start && !lvt_2_1_.components.isEmpty())
        {
            StructureComponent lvt_3_1_ = (StructureComponent)lvt_2_1_.components.getFirst();
            return lvt_3_1_ instanceof ComponentScatteredFeaturePieces.SwampHut;
        }
        else
        {
            return false;
        }
    }

    public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList()
    {
        return this.scatteredFeatureSpawnList;
    }

    public static class Start extends StructureStart
    {
        public Start()
        {
        }

        public Start(World worldIn, Random p_i2060_2_, int p_i2060_3_, int p_i2060_4_)
        {
            super(p_i2060_3_, p_i2060_4_);
            BiomeGenBase lvt_5_1_ = worldIn.getBiomeGenForCoords(new BlockPos(p_i2060_3_ * 16 + 8, 0, p_i2060_4_ * 16 + 8));

            if (lvt_5_1_ != BiomeGenBase.jungle && lvt_5_1_ != BiomeGenBase.jungleHills)
            {
                if (lvt_5_1_ == BiomeGenBase.swampland)
                {
                    ComponentScatteredFeaturePieces.SwampHut lvt_6_2_ = new ComponentScatteredFeaturePieces.SwampHut(p_i2060_2_, p_i2060_3_ * 16, p_i2060_4_ * 16);
                    this.components.add(lvt_6_2_);
                }
                else if (lvt_5_1_ == BiomeGenBase.desert || lvt_5_1_ == BiomeGenBase.desertHills)
                {
                    ComponentScatteredFeaturePieces.DesertPyramid lvt_6_3_ = new ComponentScatteredFeaturePieces.DesertPyramid(p_i2060_2_, p_i2060_3_ * 16, p_i2060_4_ * 16);
                    this.components.add(lvt_6_3_);
                }
            }
            else
            {
                ComponentScatteredFeaturePieces.JunglePyramid lvt_6_1_ = new ComponentScatteredFeaturePieces.JunglePyramid(p_i2060_2_, p_i2060_3_ * 16, p_i2060_4_ * 16);
                this.components.add(lvt_6_1_);
            }

            this.updateBoundingBox();
        }
    }
}
