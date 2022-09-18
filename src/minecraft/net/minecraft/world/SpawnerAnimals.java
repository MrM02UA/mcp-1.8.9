package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals
{
    private static final int MOB_COUNT_DIV = (int)Math.pow(17.0D, 2.0D);
    private final Set<ChunkCoordIntPair> eligibleChunksForSpawning = Sets.newHashSet();

    /**
     * adds all chunks within the spawn radius of the players to eligibleChunksForSpawning. pars: the world,
     * hostileCreatures, passiveCreatures. returns number of eligible chunks.
     */
    public int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean p_77192_4_)
    {
        if (!spawnHostileMobs && !spawnPeacefulMobs)
        {
            return 0;
        }
        else
        {
            this.eligibleChunksForSpawning.clear();
            int lvt_5_1_ = 0;

            for (EntityPlayer lvt_7_1_ : worldServerIn.playerEntities)
            {
                if (!lvt_7_1_.isSpectator())
                {
                    int lvt_8_1_ = MathHelper.floor_double(lvt_7_1_.posX / 16.0D);
                    int lvt_9_1_ = MathHelper.floor_double(lvt_7_1_.posZ / 16.0D);
                    int lvt_10_1_ = 8;

                    for (int lvt_11_1_ = -lvt_10_1_; lvt_11_1_ <= lvt_10_1_; ++lvt_11_1_)
                    {
                        for (int lvt_12_1_ = -lvt_10_1_; lvt_12_1_ <= lvt_10_1_; ++lvt_12_1_)
                        {
                            boolean lvt_13_1_ = lvt_11_1_ == -lvt_10_1_ || lvt_11_1_ == lvt_10_1_ || lvt_12_1_ == -lvt_10_1_ || lvt_12_1_ == lvt_10_1_;
                            ChunkCoordIntPair lvt_14_1_ = new ChunkCoordIntPair(lvt_11_1_ + lvt_8_1_, lvt_12_1_ + lvt_9_1_);

                            if (!this.eligibleChunksForSpawning.contains(lvt_14_1_))
                            {
                                ++lvt_5_1_;

                                if (!lvt_13_1_ && worldServerIn.getWorldBorder().contains(lvt_14_1_))
                                {
                                    this.eligibleChunksForSpawning.add(lvt_14_1_);
                                }
                            }
                        }
                    }
                }
            }

            int lvt_6_2_ = 0;
            BlockPos lvt_7_2_ = worldServerIn.getSpawnPoint();

            for (EnumCreatureType lvt_11_2_ : EnumCreatureType.values())
            {
                if ((!lvt_11_2_.getPeacefulCreature() || spawnPeacefulMobs) && (lvt_11_2_.getPeacefulCreature() || spawnHostileMobs) && (!lvt_11_2_.getAnimal() || p_77192_4_))
                {
                    int lvt_12_2_ = worldServerIn.countEntities(lvt_11_2_.getCreatureClass());
                    int lvt_13_2_ = lvt_11_2_.getMaxNumberOfCreature() * lvt_5_1_ / MOB_COUNT_DIV;

                    if (lvt_12_2_ <= lvt_13_2_)
                    {
                        label374:

                        for (ChunkCoordIntPair lvt_15_1_ : this.eligibleChunksForSpawning)
                        {
                            BlockPos lvt_16_1_ = getRandomChunkPosition(worldServerIn, lvt_15_1_.chunkXPos, lvt_15_1_.chunkZPos);
                            int lvt_17_1_ = lvt_16_1_.getX();
                            int lvt_18_1_ = lvt_16_1_.getY();
                            int lvt_19_1_ = lvt_16_1_.getZ();
                            Block lvt_20_1_ = worldServerIn.getBlockState(lvt_16_1_).getBlock();

                            if (!lvt_20_1_.isNormalCube())
                            {
                                int lvt_21_1_ = 0;

                                for (int lvt_22_1_ = 0; lvt_22_1_ < 3; ++lvt_22_1_)
                                {
                                    int lvt_23_1_ = lvt_17_1_;
                                    int lvt_24_1_ = lvt_18_1_;
                                    int lvt_25_1_ = lvt_19_1_;
                                    int lvt_26_1_ = 6;
                                    BiomeGenBase.SpawnListEntry lvt_27_1_ = null;
                                    IEntityLivingData lvt_28_1_ = null;

                                    for (int lvt_29_1_ = 0; lvt_29_1_ < 4; ++lvt_29_1_)
                                    {
                                        lvt_23_1_ += worldServerIn.rand.nextInt(lvt_26_1_) - worldServerIn.rand.nextInt(lvt_26_1_);
                                        lvt_24_1_ += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1);
                                        lvt_25_1_ += worldServerIn.rand.nextInt(lvt_26_1_) - worldServerIn.rand.nextInt(lvt_26_1_);
                                        BlockPos lvt_30_1_ = new BlockPos(lvt_23_1_, lvt_24_1_, lvt_25_1_);
                                        float lvt_31_1_ = (float)lvt_23_1_ + 0.5F;
                                        float lvt_32_1_ = (float)lvt_25_1_ + 0.5F;

                                        if (!worldServerIn.isAnyPlayerWithinRangeAt((double)lvt_31_1_, (double)lvt_24_1_, (double)lvt_32_1_, 24.0D) && lvt_7_2_.distanceSq((double)lvt_31_1_, (double)lvt_24_1_, (double)lvt_32_1_) >= 576.0D)
                                        {
                                            if (lvt_27_1_ == null)
                                            {
                                                lvt_27_1_ = worldServerIn.getSpawnListEntryForTypeAt(lvt_11_2_, lvt_30_1_);

                                                if (lvt_27_1_ == null)
                                                {
                                                    break;
                                                }
                                            }

                                            if (worldServerIn.canCreatureTypeSpawnHere(lvt_11_2_, lvt_27_1_, lvt_30_1_) && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(lvt_27_1_.entityClass), worldServerIn, lvt_30_1_))
                                            {
                                                EntityLiving lvt_33_1_;

                                                try
                                                {
                                                    lvt_33_1_ = (EntityLiving)lvt_27_1_.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {worldServerIn});
                                                }
                                                catch (Exception var35)
                                                {
                                                    var35.printStackTrace();
                                                    return lvt_6_2_;
                                                }

                                                lvt_33_1_.setLocationAndAngles((double)lvt_31_1_, (double)lvt_24_1_, (double)lvt_32_1_, worldServerIn.rand.nextFloat() * 360.0F, 0.0F);

                                                if (lvt_33_1_.getCanSpawnHere() && lvt_33_1_.isNotColliding())
                                                {
                                                    lvt_28_1_ = lvt_33_1_.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(lvt_33_1_)), lvt_28_1_);

                                                    if (lvt_33_1_.isNotColliding())
                                                    {
                                                        ++lvt_21_1_;
                                                        worldServerIn.spawnEntityInWorld(lvt_33_1_);
                                                    }

                                                    if (lvt_21_1_ >= lvt_33_1_.getMaxSpawnedInChunk())
                                                    {
                                                        continue label374;
                                                    }
                                                }

                                                lvt_6_2_ += lvt_21_1_;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return lvt_6_2_;
        }
    }

    protected static BlockPos getRandomChunkPosition(World worldIn, int x, int z)
    {
        Chunk lvt_3_1_ = worldIn.getChunkFromChunkCoords(x, z);
        int lvt_4_1_ = x * 16 + worldIn.rand.nextInt(16);
        int lvt_5_1_ = z * 16 + worldIn.rand.nextInt(16);
        int lvt_6_1_ = MathHelper.roundUp(lvt_3_1_.getHeight(new BlockPos(lvt_4_1_, 0, lvt_5_1_)) + 1, 16);
        int lvt_7_1_ = worldIn.rand.nextInt(lvt_6_1_ > 0 ? lvt_6_1_ : lvt_3_1_.getTopFilledSegment() + 16 - 1);
        return new BlockPos(lvt_4_1_, lvt_7_1_, lvt_5_1_);
    }

    public static boolean canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos)
    {
        if (!worldIn.getWorldBorder().contains(pos))
        {
            return false;
        }
        else
        {
            Block lvt_3_1_ = worldIn.getBlockState(pos).getBlock();

            if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_WATER)
            {
                return lvt_3_1_.getMaterial().isLiquid() && worldIn.getBlockState(pos.down()).getBlock().getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
            }
            else
            {
                BlockPos lvt_4_1_ = pos.down();

                if (!World.doesBlockHaveSolidTopSurface(worldIn, lvt_4_1_))
                {
                    return false;
                }
                else
                {
                    Block lvt_5_1_ = worldIn.getBlockState(lvt_4_1_).getBlock();
                    boolean lvt_6_1_ = lvt_5_1_ != Blocks.bedrock && lvt_5_1_ != Blocks.barrier;
                    return lvt_6_1_ && !lvt_3_1_.isNormalCube() && !lvt_3_1_.getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
                }
            }
        }
    }

    /**
     * Called during chunk generation to spawn initial creatures.
     */
    public static void performWorldGenSpawning(World worldIn, BiomeGenBase biomeIn, int p_77191_2_, int p_77191_3_, int p_77191_4_, int p_77191_5_, Random randomIn)
    {
        List<BiomeGenBase.SpawnListEntry> lvt_7_1_ = biomeIn.getSpawnableList(EnumCreatureType.CREATURE);

        if (!lvt_7_1_.isEmpty())
        {
            while (randomIn.nextFloat() < biomeIn.getSpawningChance())
            {
                BiomeGenBase.SpawnListEntry lvt_8_1_ = (BiomeGenBase.SpawnListEntry)WeightedRandom.getRandomItem(worldIn.rand, lvt_7_1_);
                int lvt_9_1_ = lvt_8_1_.minGroupCount + randomIn.nextInt(1 + lvt_8_1_.maxGroupCount - lvt_8_1_.minGroupCount);
                IEntityLivingData lvt_10_1_ = null;
                int lvt_11_1_ = p_77191_2_ + randomIn.nextInt(p_77191_4_);
                int lvt_12_1_ = p_77191_3_ + randomIn.nextInt(p_77191_5_);
                int lvt_13_1_ = lvt_11_1_;
                int lvt_14_1_ = lvt_12_1_;

                for (int lvt_15_1_ = 0; lvt_15_1_ < lvt_9_1_; ++lvt_15_1_)
                {
                    boolean lvt_16_1_ = false;

                    for (int lvt_17_1_ = 0; !lvt_16_1_ && lvt_17_1_ < 4; ++lvt_17_1_)
                    {
                        BlockPos lvt_18_1_ = worldIn.getTopSolidOrLiquidBlock(new BlockPos(lvt_11_1_, 0, lvt_12_1_));

                        if (canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, worldIn, lvt_18_1_))
                        {
                            EntityLiving lvt_19_1_;

                            try
                            {
                                lvt_19_1_ = (EntityLiving)lvt_8_1_.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {worldIn});
                            }
                            catch (Exception var21)
                            {
                                var21.printStackTrace();
                                continue;
                            }

                            lvt_19_1_.setLocationAndAngles((double)((float)lvt_11_1_ + 0.5F), (double)lvt_18_1_.getY(), (double)((float)lvt_12_1_ + 0.5F), randomIn.nextFloat() * 360.0F, 0.0F);
                            worldIn.spawnEntityInWorld(lvt_19_1_);
                            lvt_10_1_ = lvt_19_1_.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(lvt_19_1_)), lvt_10_1_);
                            lvt_16_1_ = true;
                        }

                        lvt_11_1_ += randomIn.nextInt(5) - randomIn.nextInt(5);

                        for (lvt_12_1_ += randomIn.nextInt(5) - randomIn.nextInt(5); lvt_11_1_ < p_77191_2_ || lvt_11_1_ >= p_77191_2_ + p_77191_4_ || lvt_12_1_ < p_77191_3_ || lvt_12_1_ >= p_77191_3_ + p_77191_4_; lvt_12_1_ = lvt_14_1_ + randomIn.nextInt(5) - randomIn.nextInt(5))
                        {
                            lvt_11_1_ = lvt_13_1_ + randomIn.nextInt(5) - randomIn.nextInt(5);
                        }
                    }
                }
            }
        }
    }
}
