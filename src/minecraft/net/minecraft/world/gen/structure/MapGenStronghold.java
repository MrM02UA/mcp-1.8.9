package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenStronghold extends MapGenStructure
{
    private List<BiomeGenBase> field_151546_e;

    /**
     * is spawned false and set true once the defined BiomeGenBases were compared with the present ones
     */
    private boolean ranBiomeCheck;
    private ChunkCoordIntPair[] structureCoords;
    private double field_82671_h;
    private int field_82672_i;

    public MapGenStronghold()
    {
        this.structureCoords = new ChunkCoordIntPair[3];
        this.field_82671_h = 32.0D;
        this.field_82672_i = 3;
        this.field_151546_e = Lists.newArrayList();

        for (BiomeGenBase lvt_4_1_ : BiomeGenBase.getBiomeGenArray())
        {
            if (lvt_4_1_ != null && lvt_4_1_.minHeight > 0.0F)
            {
                this.field_151546_e.add(lvt_4_1_);
            }
        }
    }

    public MapGenStronghold(Map<String, String> p_i2068_1_)
    {
        this();

        for (Entry<String, String> lvt_3_1_ : p_i2068_1_.entrySet())
        {
            if (((String)lvt_3_1_.getKey()).equals("distance"))
            {
                this.field_82671_h = MathHelper.parseDoubleWithDefaultAndMax((String)lvt_3_1_.getValue(), this.field_82671_h, 1.0D);
            }
            else if (((String)lvt_3_1_.getKey()).equals("count"))
            {
                this.structureCoords = new ChunkCoordIntPair[MathHelper.parseIntWithDefaultAndMax((String)lvt_3_1_.getValue(), this.structureCoords.length, 1)];
            }
            else if (((String)lvt_3_1_.getKey()).equals("spread"))
            {
                this.field_82672_i = MathHelper.parseIntWithDefaultAndMax((String)lvt_3_1_.getValue(), this.field_82672_i, 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Stronghold";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        if (!this.ranBiomeCheck)
        {
            Random lvt_3_1_ = new Random();
            lvt_3_1_.setSeed(this.worldObj.getSeed());
            double lvt_4_1_ = lvt_3_1_.nextDouble() * Math.PI * 2.0D;
            int lvt_6_1_ = 1;

            for (int lvt_7_1_ = 0; lvt_7_1_ < this.structureCoords.length; ++lvt_7_1_)
            {
                double lvt_8_1_ = (1.25D * (double)lvt_6_1_ + lvt_3_1_.nextDouble()) * this.field_82671_h * (double)lvt_6_1_;
                int lvt_10_1_ = (int)Math.round(Math.cos(lvt_4_1_) * lvt_8_1_);
                int lvt_11_1_ = (int)Math.round(Math.sin(lvt_4_1_) * lvt_8_1_);
                BlockPos lvt_12_1_ = this.worldObj.getWorldChunkManager().findBiomePosition((lvt_10_1_ << 4) + 8, (lvt_11_1_ << 4) + 8, 112, this.field_151546_e, lvt_3_1_);

                if (lvt_12_1_ != null)
                {
                    lvt_10_1_ = lvt_12_1_.getX() >> 4;
                    lvt_11_1_ = lvt_12_1_.getZ() >> 4;
                }

                this.structureCoords[lvt_7_1_] = new ChunkCoordIntPair(lvt_10_1_, lvt_11_1_);
                lvt_4_1_ += (Math.PI * 2D) * (double)lvt_6_1_ / (double)this.field_82672_i;

                if (lvt_7_1_ == this.field_82672_i)
                {
                    lvt_6_1_ += 2 + lvt_3_1_.nextInt(5);
                    this.field_82672_i += 1 + lvt_3_1_.nextInt(2);
                }
            }

            this.ranBiomeCheck = true;
        }

        for (ChunkCoordIntPair lvt_6_2_ : this.structureCoords)
        {
            if (chunkX == lvt_6_2_.chunkXPos && chunkZ == lvt_6_2_.chunkZPos)
            {
                return true;
            }
        }

        return false;
    }

    protected List<BlockPos> getCoordList()
    {
        List<BlockPos> lvt_1_1_ = Lists.newArrayList();

        for (ChunkCoordIntPair lvt_5_1_ : this.structureCoords)
        {
            if (lvt_5_1_ != null)
            {
                lvt_1_1_.add(lvt_5_1_.getCenterBlock(64));
            }
        }

        return lvt_1_1_;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        MapGenStronghold.Start lvt_3_1_;

        for (lvt_3_1_ = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ); lvt_3_1_.getComponents().isEmpty() || ((StructureStrongholdPieces.Stairs2)lvt_3_1_.getComponents().get(0)).strongholdPortalRoom == null; lvt_3_1_ = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ))
        {
            ;
        }

        return lvt_3_1_;
    }

    public static class Start extends StructureStart
    {
        public Start()
        {
        }

        public Start(World worldIn, Random p_i2067_2_, int p_i2067_3_, int p_i2067_4_)
        {
            super(p_i2067_3_, p_i2067_4_);
            StructureStrongholdPieces.prepareStructurePieces();
            StructureStrongholdPieces.Stairs2 lvt_5_1_ = new StructureStrongholdPieces.Stairs2(0, p_i2067_2_, (p_i2067_3_ << 4) + 2, (p_i2067_4_ << 4) + 2);
            this.components.add(lvt_5_1_);
            lvt_5_1_.buildComponent(lvt_5_1_, this.components, p_i2067_2_);
            List<StructureComponent> lvt_6_1_ = lvt_5_1_.field_75026_c;

            while (!lvt_6_1_.isEmpty())
            {
                int lvt_7_1_ = p_i2067_2_.nextInt(lvt_6_1_.size());
                StructureComponent lvt_8_1_ = (StructureComponent)lvt_6_1_.remove(lvt_7_1_);
                lvt_8_1_.buildComponent(lvt_5_1_, this.components, p_i2067_2_);
            }

            this.updateBoundingBox();
            this.markAvailableHeight(worldIn, p_i2067_2_, 10);
        }
    }
}
