package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenNetherBridge extends MapGenStructure
{
    private List<BiomeGenBase.SpawnListEntry> spawnList = Lists.newArrayList();

    public MapGenNetherBridge()
    {
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityBlaze.class, 10, 2, 3));
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.spawnList.add(new BiomeGenBase.SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
    }

    public String getStructureName()
    {
        return "Fortress";
    }

    public List<BiomeGenBase.SpawnListEntry> getSpawnList()
    {
        return this.spawnList;
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int lvt_3_1_ = chunkX >> 4;
        int lvt_4_1_ = chunkZ >> 4;
        this.rand.setSeed((long)(lvt_3_1_ ^ lvt_4_1_ << 4) ^ this.worldObj.getSeed());
        this.rand.nextInt();
        return this.rand.nextInt(3) != 0 ? false : (chunkX != (lvt_3_1_ << 4) + 4 + this.rand.nextInt(8) ? false : chunkZ == (lvt_4_1_ << 4) + 4 + this.rand.nextInt(8));
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenNetherBridge.Start(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart
    {
        public Start()
        {
        }

        public Start(World worldIn, Random p_i2040_2_, int p_i2040_3_, int p_i2040_4_)
        {
            super(p_i2040_3_, p_i2040_4_);
            StructureNetherBridgePieces.Start lvt_5_1_ = new StructureNetherBridgePieces.Start(p_i2040_2_, (p_i2040_3_ << 4) + 2, (p_i2040_4_ << 4) + 2);
            this.components.add(lvt_5_1_);
            lvt_5_1_.buildComponent(lvt_5_1_, this.components, p_i2040_2_);
            List<StructureComponent> lvt_6_1_ = lvt_5_1_.field_74967_d;

            while (!lvt_6_1_.isEmpty())
            {
                int lvt_7_1_ = p_i2040_2_.nextInt(lvt_6_1_.size());
                StructureComponent lvt_8_1_ = (StructureComponent)lvt_6_1_.remove(lvt_7_1_);
                lvt_8_1_.buildComponent(lvt_5_1_, this.components, p_i2040_2_);
            }

            this.updateBoundingBox();
            this.setRandomHeight(worldIn, p_i2040_2_, 48, 70);
        }
    }
}
