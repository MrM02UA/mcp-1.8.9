package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase
{
    private MapGenStructureData structureData;
    protected Map<Long, StructureStart> structureMap = Maps.newHashMap();

    public abstract String getStructureName();

    /**
     * Recursively called by generate()
     */
    protected final void recursiveGenerate(World worldIn, final int chunkX, final int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn)
    {
        this.initializeStructureData(worldIn);

        if (!this.structureMap.containsKey(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ))))
        {
            this.rand.nextInt();

            try
            {
                if (this.canSpawnStructureAtCoords(chunkX, chunkZ))
                {
                    StructureStart lvt_7_1_ = this.getStructureStart(chunkX, chunkZ);
                    this.structureMap.put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ)), lvt_7_1_);
                    this.setStructureStart(chunkX, chunkZ, lvt_7_1_);
                }
            }
            catch (Throwable var10)
            {
                CrashReport lvt_8_1_ = CrashReport.makeCrashReport(var10, "Exception preparing structure feature");
                CrashReportCategory lvt_9_1_ = lvt_8_1_.makeCategory("Feature being prepared");
                lvt_9_1_.addCrashSectionCallable("Is feature chunk", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return MapGenStructure.this.canSpawnStructureAtCoords(chunkX, chunkZ) ? "True" : "False";
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                lvt_9_1_.addCrashSection("Chunk location", String.format("%d,%d", new Object[] {Integer.valueOf(chunkX), Integer.valueOf(chunkZ)}));
                lvt_9_1_.addCrashSectionCallable("Chunk pos hash", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                lvt_9_1_.addCrashSectionCallable("Structure type", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return MapGenStructure.this.getClass().getCanonicalName();
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                throw new ReportedException(lvt_8_1_);
            }
        }
    }

    public boolean generateStructure(World worldIn, Random randomIn, ChunkCoordIntPair chunkCoord)
    {
        this.initializeStructureData(worldIn);
        int lvt_4_1_ = (chunkCoord.chunkXPos << 4) + 8;
        int lvt_5_1_ = (chunkCoord.chunkZPos << 4) + 8;
        boolean lvt_6_1_ = false;

        for (StructureStart lvt_8_1_ : this.structureMap.values())
        {
            if (lvt_8_1_.isSizeableStructure() && lvt_8_1_.func_175788_a(chunkCoord) && lvt_8_1_.getBoundingBox().intersectsWith(lvt_4_1_, lvt_5_1_, lvt_4_1_ + 15, lvt_5_1_ + 15))
            {
                lvt_8_1_.generateStructure(worldIn, randomIn, new StructureBoundingBox(lvt_4_1_, lvt_5_1_, lvt_4_1_ + 15, lvt_5_1_ + 15));
                lvt_8_1_.func_175787_b(chunkCoord);
                lvt_6_1_ = true;
                this.setStructureStart(lvt_8_1_.getChunkPosX(), lvt_8_1_.getChunkPosZ(), lvt_8_1_);
            }
        }

        return lvt_6_1_;
    }

    public boolean func_175795_b(BlockPos pos)
    {
        this.initializeStructureData(this.worldObj);
        return this.func_175797_c(pos) != null;
    }

    protected StructureStart func_175797_c(BlockPos pos)
    {
        label24:

        for (StructureStart lvt_3_1_ : this.structureMap.values())
        {
            if (lvt_3_1_.isSizeableStructure() && lvt_3_1_.getBoundingBox().isVecInside(pos))
            {
                Iterator<StructureComponent> lvt_4_1_ = lvt_3_1_.getComponents().iterator();

                while (true)
                {
                    if (!lvt_4_1_.hasNext())
                    {
                        continue label24;
                    }

                    StructureComponent lvt_5_1_ = (StructureComponent)lvt_4_1_.next();

                    if (lvt_5_1_.getBoundingBox().isVecInside(pos))
                    {
                        break;
                    }
                }

                return lvt_3_1_;
            }
        }

        return null;
    }

    public boolean isPositionInStructure(World worldIn, BlockPos pos)
    {
        this.initializeStructureData(worldIn);

        for (StructureStart lvt_4_1_ : this.structureMap.values())
        {
            if (lvt_4_1_.isSizeableStructure() && lvt_4_1_.getBoundingBox().isVecInside(pos))
            {
                return true;
            }
        }

        return false;
    }

    public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos)
    {
        this.worldObj = worldIn;
        this.initializeStructureData(worldIn);
        this.rand.setSeed(worldIn.getSeed());
        long lvt_3_1_ = this.rand.nextLong();
        long lvt_5_1_ = this.rand.nextLong();
        long lvt_7_1_ = (long)(pos.getX() >> 4) * lvt_3_1_;
        long lvt_9_1_ = (long)(pos.getZ() >> 4) * lvt_5_1_;
        this.rand.setSeed(lvt_7_1_ ^ lvt_9_1_ ^ worldIn.getSeed());
        this.recursiveGenerate(worldIn, pos.getX() >> 4, pos.getZ() >> 4, 0, 0, (ChunkPrimer)null);
        double lvt_11_1_ = Double.MAX_VALUE;
        BlockPos lvt_13_1_ = null;

        for (StructureStart lvt_15_1_ : this.structureMap.values())
        {
            if (lvt_15_1_.isSizeableStructure())
            {
                StructureComponent lvt_16_1_ = (StructureComponent)lvt_15_1_.getComponents().get(0);
                BlockPos lvt_17_1_ = lvt_16_1_.getBoundingBoxCenter();
                double lvt_18_1_ = lvt_17_1_.distanceSq(pos);

                if (lvt_18_1_ < lvt_11_1_)
                {
                    lvt_11_1_ = lvt_18_1_;
                    lvt_13_1_ = lvt_17_1_;
                }
            }
        }

        if (lvt_13_1_ != null)
        {
            return lvt_13_1_;
        }
        else
        {
            List<BlockPos> lvt_14_2_ = this.getCoordList();

            if (lvt_14_2_ != null)
            {
                BlockPos lvt_15_2_ = null;

                for (BlockPos lvt_17_2_ : lvt_14_2_)
                {
                    double lvt_18_2_ = lvt_17_2_.distanceSq(pos);

                    if (lvt_18_2_ < lvt_11_1_)
                    {
                        lvt_11_1_ = lvt_18_2_;
                        lvt_15_2_ = lvt_17_2_;
                    }
                }

                return lvt_15_2_;
            }
            else
            {
                return null;
            }
        }
    }

    protected List<BlockPos> getCoordList()
    {
        return null;
    }

    private void initializeStructureData(World worldIn)
    {
        if (this.structureData == null)
        {
            this.structureData = (MapGenStructureData)worldIn.loadItemData(MapGenStructureData.class, this.getStructureName());

            if (this.structureData == null)
            {
                this.structureData = new MapGenStructureData(this.getStructureName());
                worldIn.setItemData(this.getStructureName(), this.structureData);
            }
            else
            {
                NBTTagCompound lvt_2_1_ = this.structureData.getTagCompound();

                for (String lvt_4_1_ : lvt_2_1_.getKeySet())
                {
                    NBTBase lvt_5_1_ = lvt_2_1_.getTag(lvt_4_1_);

                    if (lvt_5_1_.getId() == 10)
                    {
                        NBTTagCompound lvt_6_1_ = (NBTTagCompound)lvt_5_1_;

                        if (lvt_6_1_.hasKey("ChunkX") && lvt_6_1_.hasKey("ChunkZ"))
                        {
                            int lvt_7_1_ = lvt_6_1_.getInteger("ChunkX");
                            int lvt_8_1_ = lvt_6_1_.getInteger("ChunkZ");
                            StructureStart lvt_9_1_ = MapGenStructureIO.getStructureStart(lvt_6_1_, worldIn);

                            if (lvt_9_1_ != null)
                            {
                                this.structureMap.put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(lvt_7_1_, lvt_8_1_)), lvt_9_1_);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setStructureStart(int chunkX, int chunkZ, StructureStart start)
    {
        this.structureData.writeInstance(start.writeStructureComponentsToNBT(chunkX, chunkZ), chunkX, chunkZ);
        this.structureData.markDirty();
    }

    protected abstract boolean canSpawnStructureAtCoords(int chunkX, int chunkZ);

    protected abstract StructureStart getStructureStart(int chunkX, int chunkZ);
}
