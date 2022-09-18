package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData
{
    private World worldObj;
    private final List<BlockPos> villagerPositionsList = Lists.newArrayList();
    private final List<VillageDoorInfo> newDoors = Lists.newArrayList();
    private final List<Village> villageList = Lists.newArrayList();
    private int tickCounter;

    public VillageCollection(String name)
    {
        super(name);
    }

    public VillageCollection(World worldIn)
    {
        super(fileNameForProvider(worldIn.provider));
        this.worldObj = worldIn;
        this.markDirty();
    }

    public void setWorldsForAll(World worldIn)
    {
        this.worldObj = worldIn;

        for (Village lvt_3_1_ : this.villageList)
        {
            lvt_3_1_.setWorld(worldIn);
        }
    }

    public void addToVillagerPositionList(BlockPos pos)
    {
        if (this.villagerPositionsList.size() <= 64)
        {
            if (!this.positionInList(pos))
            {
                this.villagerPositionsList.add(pos);
            }
        }
    }

    /**
     * Runs a single tick for the village collection
     */
    public void tick()
    {
        ++this.tickCounter;

        for (Village lvt_2_1_ : this.villageList)
        {
            lvt_2_1_.tick(this.tickCounter);
        }

        this.removeAnnihilatedVillages();
        this.dropOldestVillagerPosition();
        this.addNewDoorsToVillageOrCreateVillage();

        if (this.tickCounter % 400 == 0)
        {
            this.markDirty();
        }
    }

    private void removeAnnihilatedVillages()
    {
        Iterator<Village> lvt_1_1_ = this.villageList.iterator();

        while (lvt_1_1_.hasNext())
        {
            Village lvt_2_1_ = (Village)lvt_1_1_.next();

            if (lvt_2_1_.isAnnihilated())
            {
                lvt_1_1_.remove();
                this.markDirty();
            }
        }
    }

    public List<Village> getVillageList()
    {
        return this.villageList;
    }

    public Village getNearestVillage(BlockPos doorBlock, int radius)
    {
        Village lvt_3_1_ = null;
        double lvt_4_1_ = 3.4028234663852886E38D;

        for (Village lvt_7_1_ : this.villageList)
        {
            double lvt_8_1_ = lvt_7_1_.getCenter().distanceSq(doorBlock);

            if (lvt_8_1_ < lvt_4_1_)
            {
                float lvt_10_1_ = (float)(radius + lvt_7_1_.getVillageRadius());

                if (lvt_8_1_ <= (double)(lvt_10_1_ * lvt_10_1_))
                {
                    lvt_3_1_ = lvt_7_1_;
                    lvt_4_1_ = lvt_8_1_;
                }
            }
        }

        return lvt_3_1_;
    }

    private void dropOldestVillagerPosition()
    {
        if (!this.villagerPositionsList.isEmpty())
        {
            this.addDoorsAround((BlockPos)this.villagerPositionsList.remove(0));
        }
    }

    private void addNewDoorsToVillageOrCreateVillage()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.newDoors.size(); ++lvt_1_1_)
        {
            VillageDoorInfo lvt_2_1_ = (VillageDoorInfo)this.newDoors.get(lvt_1_1_);
            Village lvt_3_1_ = this.getNearestVillage(lvt_2_1_.getDoorBlockPos(), 32);

            if (lvt_3_1_ == null)
            {
                lvt_3_1_ = new Village(this.worldObj);
                this.villageList.add(lvt_3_1_);
                this.markDirty();
            }

            lvt_3_1_.addVillageDoorInfo(lvt_2_1_);
        }

        this.newDoors.clear();
    }

    private void addDoorsAround(BlockPos central)
    {
        int lvt_2_1_ = 16;
        int lvt_3_1_ = 4;
        int lvt_4_1_ = 16;

        for (int lvt_5_1_ = -lvt_2_1_; lvt_5_1_ < lvt_2_1_; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = -lvt_3_1_; lvt_6_1_ < lvt_3_1_; ++lvt_6_1_)
            {
                for (int lvt_7_1_ = -lvt_4_1_; lvt_7_1_ < lvt_4_1_; ++lvt_7_1_)
                {
                    BlockPos lvt_8_1_ = central.add(lvt_5_1_, lvt_6_1_, lvt_7_1_);

                    if (this.isWoodDoor(lvt_8_1_))
                    {
                        VillageDoorInfo lvt_9_1_ = this.checkDoorExistence(lvt_8_1_);

                        if (lvt_9_1_ == null)
                        {
                            this.addToNewDoorsList(lvt_8_1_);
                        }
                        else
                        {
                            lvt_9_1_.func_179849_a(this.tickCounter);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns the VillageDoorInfo if it exists in any village or in the newDoor list, otherwise returns null
     */
    private VillageDoorInfo checkDoorExistence(BlockPos doorBlock)
    {
        for (VillageDoorInfo lvt_3_1_ : this.newDoors)
        {
            if (lvt_3_1_.getDoorBlockPos().getX() == doorBlock.getX() && lvt_3_1_.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(lvt_3_1_.getDoorBlockPos().getY() - doorBlock.getY()) <= 1)
            {
                return lvt_3_1_;
            }
        }

        for (Village lvt_3_2_ : this.villageList)
        {
            VillageDoorInfo lvt_4_1_ = lvt_3_2_.getExistedDoor(doorBlock);

            if (lvt_4_1_ != null)
            {
                return lvt_4_1_;
            }
        }

        return null;
    }

    private void addToNewDoorsList(BlockPos doorBlock)
    {
        EnumFacing lvt_2_1_ = BlockDoor.getFacing(this.worldObj, doorBlock);
        EnumFacing lvt_3_1_ = lvt_2_1_.getOpposite();
        int lvt_4_1_ = this.countBlocksCanSeeSky(doorBlock, lvt_2_1_, 5);
        int lvt_5_1_ = this.countBlocksCanSeeSky(doorBlock, lvt_3_1_, lvt_4_1_ + 1);

        if (lvt_4_1_ != lvt_5_1_)
        {
            this.newDoors.add(new VillageDoorInfo(doorBlock, lvt_4_1_ < lvt_5_1_ ? lvt_2_1_ : lvt_3_1_, this.tickCounter));
        }
    }

    /**
     * Check five blocks in the direction. The centerPos will not be checked.
     */
    private int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation)
    {
        int lvt_4_1_ = 0;

        for (int lvt_5_1_ = 1; lvt_5_1_ <= 5; ++lvt_5_1_)
        {
            if (this.worldObj.canSeeSky(centerPos.offset(direction, lvt_5_1_)))
            {
                ++lvt_4_1_;

                if (lvt_4_1_ >= limitation)
                {
                    return lvt_4_1_;
                }
            }
        }

        return lvt_4_1_;
    }

    private boolean positionInList(BlockPos pos)
    {
        for (BlockPos lvt_3_1_ : this.villagerPositionsList)
        {
            if (lvt_3_1_.equals(pos))
            {
                return true;
            }
        }

        return false;
    }

    private boolean isWoodDoor(BlockPos doorPos)
    {
        Block lvt_2_1_ = this.worldObj.getBlockState(doorPos).getBlock();
        return lvt_2_1_ instanceof BlockDoor ? lvt_2_1_.getMaterial() == Material.wood : false;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.tickCounter = nbt.getInteger("Tick");
        NBTTagList lvt_2_1_ = nbt.getTagList("Villages", 10);

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
        {
            NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
            Village lvt_5_1_ = new Village();
            lvt_5_1_.readVillageDataFromNBT(lvt_4_1_);
            this.villageList.add(lvt_5_1_);
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("Tick", this.tickCounter);
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (Village lvt_4_1_ : this.villageList)
        {
            NBTTagCompound lvt_5_1_ = new NBTTagCompound();
            lvt_4_1_.writeVillageDataToNBT(lvt_5_1_);
            lvt_2_1_.appendTag(lvt_5_1_);
        }

        nbt.setTag("Villages", lvt_2_1_);
    }

    public static String fileNameForProvider(WorldProvider provider)
    {
        return "villages" + provider.getInternalNameSuffix();
    }
}
