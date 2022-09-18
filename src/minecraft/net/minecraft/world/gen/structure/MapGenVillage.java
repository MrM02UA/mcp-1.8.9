package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure
{
    public static final List<BiomeGenBase> villageSpawnBiomes = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna});

    /** World terrain type, 0 for normal, 1 for flat map */
    private int terrainType;
    private int field_82665_g;
    private int field_82666_h;

    public MapGenVillage()
    {
        this.field_82665_g = 32;
        this.field_82666_h = 8;
    }

    public MapGenVillage(Map<String, String> p_i2093_1_)
    {
        this();

        for (Entry<String, String> lvt_3_1_ : p_i2093_1_.entrySet())
        {
            if (((String)lvt_3_1_.getKey()).equals("size"))
            {
                this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)lvt_3_1_.getValue(), this.terrainType, 0);
            }
            else if (((String)lvt_3_1_.getKey()).equals("distance"))
            {
                this.field_82665_g = MathHelper.parseIntWithDefaultAndMax((String)lvt_3_1_.getValue(), this.field_82665_g, this.field_82666_h + 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Village";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int lvt_3_1_ = chunkX;
        int lvt_4_1_ = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.field_82665_g - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.field_82665_g - 1;
        }

        int lvt_5_1_ = chunkX / this.field_82665_g;
        int lvt_6_1_ = chunkZ / this.field_82665_g;
        Random lvt_7_1_ = this.worldObj.setRandomSeed(lvt_5_1_, lvt_6_1_, 10387312);
        lvt_5_1_ = lvt_5_1_ * this.field_82665_g;
        lvt_6_1_ = lvt_6_1_ * this.field_82665_g;
        lvt_5_1_ = lvt_5_1_ + lvt_7_1_.nextInt(this.field_82665_g - this.field_82666_h);
        lvt_6_1_ = lvt_6_1_ + lvt_7_1_.nextInt(this.field_82665_g - this.field_82666_h);

        if (lvt_3_1_ == lvt_5_1_ && lvt_4_1_ == lvt_6_1_)
        {
            boolean lvt_8_1_ = this.worldObj.getWorldChunkManager().areBiomesViable(lvt_3_1_ * 16 + 8, lvt_4_1_ * 16 + 8, 0, villageSpawnBiomes);

            if (lvt_8_1_)
            {
                return true;
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenVillage.Start(this.worldObj, this.rand, chunkX, chunkZ, this.terrainType);
    }

    public static class Start extends StructureStart
    {
        private boolean hasMoreThanTwoComponents;

        public Start()
        {
        }

        public Start(World worldIn, Random rand, int x, int z, int size)
        {
            super(x, z);
            List<StructureVillagePieces.PieceWeight> lvt_6_1_ = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, size);
            StructureVillagePieces.Start lvt_7_1_ = new StructureVillagePieces.Start(worldIn.getWorldChunkManager(), 0, rand, (x << 4) + 2, (z << 4) + 2, lvt_6_1_, size);
            this.components.add(lvt_7_1_);
            lvt_7_1_.buildComponent(lvt_7_1_, this.components, rand);
            List<StructureComponent> lvt_8_1_ = lvt_7_1_.field_74930_j;
            List<StructureComponent> lvt_9_1_ = lvt_7_1_.field_74932_i;

            while (!lvt_8_1_.isEmpty() || !lvt_9_1_.isEmpty())
            {
                if (lvt_8_1_.isEmpty())
                {
                    int lvt_10_1_ = rand.nextInt(lvt_9_1_.size());
                    StructureComponent lvt_11_1_ = (StructureComponent)lvt_9_1_.remove(lvt_10_1_);
                    lvt_11_1_.buildComponent(lvt_7_1_, this.components, rand);
                }
                else
                {
                    int lvt_10_2_ = rand.nextInt(lvt_8_1_.size());
                    StructureComponent lvt_11_2_ = (StructureComponent)lvt_8_1_.remove(lvt_10_2_);
                    lvt_11_2_.buildComponent(lvt_7_1_, this.components, rand);
                }
            }

            this.updateBoundingBox();
            int lvt_10_3_ = 0;

            for (StructureComponent lvt_12_1_ : this.components)
            {
                if (!(lvt_12_1_ instanceof StructureVillagePieces.Road))
                {
                    ++lvt_10_3_;
                }
            }

            this.hasMoreThanTwoComponents = lvt_10_3_ > 2;
        }

        public boolean isSizeableStructure()
        {
            return this.hasMoreThanTwoComponents;
        }

        public void writeToNBT(NBTTagCompound tagCompound)
        {
            super.writeToNBT(tagCompound);
            tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
        }

        public void readFromNBT(NBTTagCompound tagCompound)
        {
            super.readFromNBT(tagCompound);
            this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
        }
    }
}
