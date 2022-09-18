package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader
{
    public static ChunkLoader.AnvilConverterData load(NBTTagCompound nbt)
    {
        int lvt_1_1_ = nbt.getInteger("xPos");
        int lvt_2_1_ = nbt.getInteger("zPos");
        ChunkLoader.AnvilConverterData lvt_3_1_ = new ChunkLoader.AnvilConverterData(lvt_1_1_, lvt_2_1_);
        lvt_3_1_.blocks = nbt.getByteArray("Blocks");
        lvt_3_1_.data = new NibbleArrayReader(nbt.getByteArray("Data"), 7);
        lvt_3_1_.skyLight = new NibbleArrayReader(nbt.getByteArray("SkyLight"), 7);
        lvt_3_1_.blockLight = new NibbleArrayReader(nbt.getByteArray("BlockLight"), 7);
        lvt_3_1_.heightmap = nbt.getByteArray("HeightMap");
        lvt_3_1_.terrainPopulated = nbt.getBoolean("TerrainPopulated");
        lvt_3_1_.entities = nbt.getTagList("Entities", 10);
        lvt_3_1_.tileEntities = nbt.getTagList("TileEntities", 10);
        lvt_3_1_.tileTicks = nbt.getTagList("TileTicks", 10);

        try
        {
            lvt_3_1_.lastUpdated = nbt.getLong("LastUpdate");
        }
        catch (ClassCastException var5)
        {
            lvt_3_1_.lastUpdated = (long)nbt.getInteger("LastUpdate");
        }

        return lvt_3_1_;
    }

    public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData p_76690_0_, NBTTagCompound compound, WorldChunkManager p_76690_2_)
    {
        compound.setInteger("xPos", p_76690_0_.x);
        compound.setInteger("zPos", p_76690_0_.z);
        compound.setLong("LastUpdate", p_76690_0_.lastUpdated);
        int[] lvt_3_1_ = new int[p_76690_0_.heightmap.length];

        for (int lvt_4_1_ = 0; lvt_4_1_ < p_76690_0_.heightmap.length; ++lvt_4_1_)
        {
            lvt_3_1_[lvt_4_1_] = p_76690_0_.heightmap[lvt_4_1_];
        }

        compound.setIntArray("HeightMap", lvt_3_1_);
        compound.setBoolean("TerrainPopulated", p_76690_0_.terrainPopulated);
        NBTTagList lvt_4_2_ = new NBTTagList();

        for (int lvt_5_1_ = 0; lvt_5_1_ < 8; ++lvt_5_1_)
        {
            boolean lvt_6_1_ = true;

            for (int lvt_7_1_ = 0; lvt_7_1_ < 16 && lvt_6_1_; ++lvt_7_1_)
            {
                for (int lvt_8_1_ = 0; lvt_8_1_ < 16 && lvt_6_1_; ++lvt_8_1_)
                {
                    for (int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_)
                    {
                        int lvt_10_1_ = lvt_7_1_ << 11 | lvt_9_1_ << 7 | lvt_8_1_ + (lvt_5_1_ << 4);
                        int lvt_11_1_ = p_76690_0_.blocks[lvt_10_1_];

                        if (lvt_11_1_ != 0)
                        {
                            lvt_6_1_ = false;
                            break;
                        }
                    }
                }
            }

            if (!lvt_6_1_)
            {
                byte[] lvt_7_2_ = new byte[4096];
                NibbleArray lvt_8_2_ = new NibbleArray();
                NibbleArray lvt_9_2_ = new NibbleArray();
                NibbleArray lvt_10_2_ = new NibbleArray();

                for (int lvt_11_2_ = 0; lvt_11_2_ < 16; ++lvt_11_2_)
                {
                    for (int lvt_12_1_ = 0; lvt_12_1_ < 16; ++lvt_12_1_)
                    {
                        for (int lvt_13_1_ = 0; lvt_13_1_ < 16; ++lvt_13_1_)
                        {
                            int lvt_14_1_ = lvt_11_2_ << 11 | lvt_13_1_ << 7 | lvt_12_1_ + (lvt_5_1_ << 4);
                            int lvt_15_1_ = p_76690_0_.blocks[lvt_14_1_];
                            lvt_7_2_[lvt_12_1_ << 8 | lvt_13_1_ << 4 | lvt_11_2_] = (byte)(lvt_15_1_ & 255);
                            lvt_8_2_.set(lvt_11_2_, lvt_12_1_, lvt_13_1_, p_76690_0_.data.get(lvt_11_2_, lvt_12_1_ + (lvt_5_1_ << 4), lvt_13_1_));
                            lvt_9_2_.set(lvt_11_2_, lvt_12_1_, lvt_13_1_, p_76690_0_.skyLight.get(lvt_11_2_, lvt_12_1_ + (lvt_5_1_ << 4), lvt_13_1_));
                            lvt_10_2_.set(lvt_11_2_, lvt_12_1_, lvt_13_1_, p_76690_0_.blockLight.get(lvt_11_2_, lvt_12_1_ + (lvt_5_1_ << 4), lvt_13_1_));
                        }
                    }
                }

                NBTTagCompound lvt_11_3_ = new NBTTagCompound();
                lvt_11_3_.setByte("Y", (byte)(lvt_5_1_ & 255));
                lvt_11_3_.setByteArray("Blocks", lvt_7_2_);
                lvt_11_3_.setByteArray("Data", lvt_8_2_.getData());
                lvt_11_3_.setByteArray("SkyLight", lvt_9_2_.getData());
                lvt_11_3_.setByteArray("BlockLight", lvt_10_2_.getData());
                lvt_4_2_.appendTag(lvt_11_3_);
            }
        }

        compound.setTag("Sections", lvt_4_2_);
        byte[] lvt_5_2_ = new byte[256];
        BlockPos.MutableBlockPos lvt_6_2_ = new BlockPos.MutableBlockPos();

        for (int lvt_7_3_ = 0; lvt_7_3_ < 16; ++lvt_7_3_)
        {
            for (int lvt_8_3_ = 0; lvt_8_3_ < 16; ++lvt_8_3_)
            {
                lvt_6_2_.set(p_76690_0_.x << 4 | lvt_7_3_, 0, p_76690_0_.z << 4 | lvt_8_3_);
                lvt_5_2_[lvt_8_3_ << 4 | lvt_7_3_] = (byte)(p_76690_2_.getBiomeGenerator(lvt_6_2_, BiomeGenBase.field_180279_ad).biomeID & 255);
            }
        }

        compound.setByteArray("Biomes", lvt_5_2_);
        compound.setTag("Entities", p_76690_0_.entities);
        compound.setTag("TileEntities", p_76690_0_.tileEntities);

        if (p_76690_0_.tileTicks != null)
        {
            compound.setTag("TileTicks", p_76690_0_.tileTicks);
        }
    }

    public static class AnvilConverterData
    {
        public long lastUpdated;
        public boolean terrainPopulated;
        public byte[] heightmap;
        public NibbleArrayReader blockLight;
        public NibbleArrayReader skyLight;
        public NibbleArrayReader data;
        public byte[] blocks;
        public NBTTagList entities;
        public NBTTagList tileEntities;
        public NBTTagList tileTicks;
        public final int x;
        public final int z;

        public AnvilConverterData(int xIn, int zIn)
        {
            this.x = xIn;
            this.z = zIn;
        }
    }
}
