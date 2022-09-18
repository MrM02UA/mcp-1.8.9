package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public abstract class StructureStart
{
    protected LinkedList<StructureComponent> components = new LinkedList();
    protected StructureBoundingBox boundingBox;
    private int chunkPosX;
    private int chunkPosZ;

    public StructureStart()
    {
    }

    public StructureStart(int chunkX, int chunkZ)
    {
        this.chunkPosX = chunkX;
        this.chunkPosZ = chunkZ;
    }

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public LinkedList<StructureComponent> getComponents()
    {
        return this.components;
    }

    /**
     * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
     */
    public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
    {
        Iterator<StructureComponent> lvt_4_1_ = this.components.iterator();

        while (lvt_4_1_.hasNext())
        {
            StructureComponent lvt_5_1_ = (StructureComponent)lvt_4_1_.next();

            if (lvt_5_1_.getBoundingBox().intersectsWith(structurebb) && !lvt_5_1_.addComponentParts(worldIn, rand, structurebb))
            {
                lvt_4_1_.remove();
            }
        }
    }

    /**
     * Calculates total bounding box based on components' bounding boxes and saves it to boundingBox
     */
    protected void updateBoundingBox()
    {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();

        for (StructureComponent lvt_2_1_ : this.components)
        {
            this.boundingBox.expandTo(lvt_2_1_.getBoundingBox());
        }
    }

    public NBTTagCompound writeStructureComponentsToNBT(int chunkX, int chunkZ)
    {
        NBTTagCompound lvt_3_1_ = new NBTTagCompound();
        lvt_3_1_.setString("id", MapGenStructureIO.getStructureStartName(this));
        lvt_3_1_.setInteger("ChunkX", chunkX);
        lvt_3_1_.setInteger("ChunkZ", chunkZ);
        lvt_3_1_.setTag("BB", this.boundingBox.toNBTTagIntArray());
        NBTTagList lvt_4_1_ = new NBTTagList();

        for (StructureComponent lvt_6_1_ : this.components)
        {
            lvt_4_1_.appendTag(lvt_6_1_.createStructureBaseNBT());
        }

        lvt_3_1_.setTag("Children", lvt_4_1_);
        this.writeToNBT(lvt_3_1_);
        return lvt_3_1_;
    }

    public void writeToNBT(NBTTagCompound tagCompound)
    {
    }

    public void readStructureComponentsFromNBT(World worldIn, NBTTagCompound tagCompound)
    {
        this.chunkPosX = tagCompound.getInteger("ChunkX");
        this.chunkPosZ = tagCompound.getInteger("ChunkZ");

        if (tagCompound.hasKey("BB"))
        {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }

        NBTTagList lvt_3_1_ = tagCompound.getTagList("Children", 10);

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.tagCount(); ++lvt_4_1_)
        {
            this.components.add(MapGenStructureIO.getStructureComponent(lvt_3_1_.getCompoundTagAt(lvt_4_1_), worldIn));
        }

        this.readFromNBT(tagCompound);
    }

    public void readFromNBT(NBTTagCompound tagCompound)
    {
    }

    /**
     * offsets the structure Bounding Boxes up to a certain height, typically 63 - 10
     */
    protected void markAvailableHeight(World worldIn, Random rand, int p_75067_3_)
    {
        int lvt_4_1_ = worldIn.getSeaLevel() - p_75067_3_;
        int lvt_5_1_ = this.boundingBox.getYSize() + 1;

        if (lvt_5_1_ < lvt_4_1_)
        {
            lvt_5_1_ += rand.nextInt(lvt_4_1_ - lvt_5_1_);
        }

        int lvt_6_1_ = lvt_5_1_ - this.boundingBox.maxY;
        this.boundingBox.offset(0, lvt_6_1_, 0);

        for (StructureComponent lvt_8_1_ : this.components)
        {
            lvt_8_1_.func_181138_a(0, lvt_6_1_, 0);
        }
    }

    protected void setRandomHeight(World worldIn, Random rand, int p_75070_3_, int p_75070_4_)
    {
        int lvt_5_1_ = p_75070_4_ - p_75070_3_ + 1 - this.boundingBox.getYSize();
        int lvt_6_1_ = 1;

        if (lvt_5_1_ > 1)
        {
            lvt_6_1_ = p_75070_3_ + rand.nextInt(lvt_5_1_);
        }
        else
        {
            lvt_6_1_ = p_75070_3_;
        }

        int lvt_7_1_ = lvt_6_1_ - this.boundingBox.minY;
        this.boundingBox.offset(0, lvt_7_1_, 0);

        for (StructureComponent lvt_9_1_ : this.components)
        {
            lvt_9_1_.func_181138_a(0, lvt_7_1_, 0);
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return true;
    }

    public boolean func_175788_a(ChunkCoordIntPair pair)
    {
        return true;
    }

    public void func_175787_b(ChunkCoordIntPair pair)
    {
    }

    public int getChunkPosX()
    {
        return this.chunkPosX;
    }

    public int getChunkPosZ()
    {
        return this.chunkPosZ;
    }
}
