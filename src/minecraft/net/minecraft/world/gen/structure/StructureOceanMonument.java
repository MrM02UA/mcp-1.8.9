package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureOceanMonument extends MapGenStructure
{
    private int field_175800_f;
    private int field_175801_g;
    public static final List<BiomeGenBase> field_175802_d = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver});
    private static final List<BiomeGenBase.SpawnListEntry> field_175803_h = Lists.newArrayList();

    public StructureOceanMonument()
    {
        this.field_175800_f = 32;
        this.field_175801_g = 5;
    }

    public StructureOceanMonument(Map<String, String> p_i45608_1_)
    {
        this();

        for (Entry<String, String> lvt_3_1_ : p_i45608_1_.entrySet())
        {
            if (((String)lvt_3_1_.getKey()).equals("spacing"))
            {
                this.field_175800_f = MathHelper.parseIntWithDefaultAndMax((String)lvt_3_1_.getValue(), this.field_175800_f, 1);
            }
            else if (((String)lvt_3_1_.getKey()).equals("separation"))
            {
                this.field_175801_g = MathHelper.parseIntWithDefaultAndMax((String)lvt_3_1_.getValue(), this.field_175801_g, 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Monument";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int lvt_3_1_ = chunkX;
        int lvt_4_1_ = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.field_175800_f - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.field_175800_f - 1;
        }

        int lvt_5_1_ = chunkX / this.field_175800_f;
        int lvt_6_1_ = chunkZ / this.field_175800_f;
        Random lvt_7_1_ = this.worldObj.setRandomSeed(lvt_5_1_, lvt_6_1_, 10387313);
        lvt_5_1_ = lvt_5_1_ * this.field_175800_f;
        lvt_6_1_ = lvt_6_1_ * this.field_175800_f;
        lvt_5_1_ = lvt_5_1_ + (lvt_7_1_.nextInt(this.field_175800_f - this.field_175801_g) + lvt_7_1_.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
        lvt_6_1_ = lvt_6_1_ + (lvt_7_1_.nextInt(this.field_175800_f - this.field_175801_g) + lvt_7_1_.nextInt(this.field_175800_f - this.field_175801_g)) / 2;

        if (lvt_3_1_ == lvt_5_1_ && lvt_4_1_ == lvt_6_1_)
        {
            if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(lvt_3_1_ * 16 + 8, 64, lvt_4_1_ * 16 + 8), (BiomeGenBase)null) != BiomeGenBase.deepOcean)
            {
                return false;
            }

            boolean lvt_8_1_ = this.worldObj.getWorldChunkManager().areBiomesViable(lvt_3_1_ * 16 + 8, lvt_4_1_ * 16 + 8, 29, field_175802_d);

            if (lvt_8_1_)
            {
                return true;
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList()
    {
        return field_175803_h;
    }

    static
    {
        field_175803_h.add(new BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
    }

    public static class StartMonument extends StructureStart
    {
        private Set<ChunkCoordIntPair> field_175791_c = Sets.newHashSet();
        private boolean field_175790_d;

        public StartMonument()
        {
        }

        public StartMonument(World worldIn, Random p_i45607_2_, int p_i45607_3_, int p_i45607_4_)
        {
            super(p_i45607_3_, p_i45607_4_);
            this.func_175789_b(worldIn, p_i45607_2_, p_i45607_3_, p_i45607_4_);
        }

        private void func_175789_b(World worldIn, Random p_175789_2_, int p_175789_3_, int p_175789_4_)
        {
            p_175789_2_.setSeed(worldIn.getSeed());
            long lvt_5_1_ = p_175789_2_.nextLong();
            long lvt_7_1_ = p_175789_2_.nextLong();
            long lvt_9_1_ = (long)p_175789_3_ * lvt_5_1_;
            long lvt_11_1_ = (long)p_175789_4_ * lvt_7_1_;
            p_175789_2_.setSeed(lvt_9_1_ ^ lvt_11_1_ ^ worldIn.getSeed());
            int lvt_13_1_ = p_175789_3_ * 16 + 8 - 29;
            int lvt_14_1_ = p_175789_4_ * 16 + 8 - 29;
            EnumFacing lvt_15_1_ = EnumFacing.Plane.HORIZONTAL.random(p_175789_2_);
            this.components.add(new StructureOceanMonumentPieces.MonumentBuilding(p_175789_2_, lvt_13_1_, lvt_14_1_, lvt_15_1_));
            this.updateBoundingBox();
            this.field_175790_d = true;
        }

        public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
        {
            if (!this.field_175790_d)
            {
                this.components.clear();
                this.func_175789_b(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
            }

            super.generateStructure(worldIn, rand, structurebb);
        }

        public boolean func_175788_a(ChunkCoordIntPair pair)
        {
            return this.field_175791_c.contains(pair) ? false : super.func_175788_a(pair);
        }

        public void func_175787_b(ChunkCoordIntPair pair)
        {
            super.func_175787_b(pair);
            this.field_175791_c.add(pair);
        }

        public void writeToNBT(NBTTagCompound tagCompound)
        {
            super.writeToNBT(tagCompound);
            NBTTagList lvt_2_1_ = new NBTTagList();

            for (ChunkCoordIntPair lvt_4_1_ : this.field_175791_c)
            {
                NBTTagCompound lvt_5_1_ = new NBTTagCompound();
                lvt_5_1_.setInteger("X", lvt_4_1_.chunkXPos);
                lvt_5_1_.setInteger("Z", lvt_4_1_.chunkZPos);
                lvt_2_1_.appendTag(lvt_5_1_);
            }

            tagCompound.setTag("Processed", lvt_2_1_);
        }

        public void readFromNBT(NBTTagCompound tagCompound)
        {
            super.readFromNBT(tagCompound);

            if (tagCompound.hasKey("Processed", 9))
            {
                NBTTagList lvt_2_1_ = tagCompound.getTagList("Processed", 10);

                for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
                {
                    NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
                    this.field_175791_c.add(new ChunkCoordIntPair(lvt_4_1_.getInteger("X"), lvt_4_1_.getInteger("Z")));
                }
            }
        }
    }
}
