package net.minecraft.village;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village
{
    private World worldObj;
    private final List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();

    /**
     * This is the sum of all door coordinates and used to calculate the actual village center by dividing by the number
     * of doors.
     */
    private BlockPos centerHelper = BlockPos.ORIGIN;

    /** This is the actual village center. */
    private BlockPos center = BlockPos.ORIGIN;
    private int villageRadius;
    private int lastAddDoorTimestamp;
    private int tickCounter;
    private int numVillagers;

    /** Timestamp of tick count when villager last bred */
    private int noBreedTicks;
    private TreeMap<String, Integer> playerReputation = new TreeMap();
    private List<Village.VillageAggressor> villageAgressors = Lists.newArrayList();
    private int numIronGolems;

    public Village()
    {
    }

    public Village(World worldIn)
    {
        this.worldObj = worldIn;
    }

    public void setWorld(World worldIn)
    {
        this.worldObj = worldIn;
    }

    /**
     * Called periodically by VillageCollection
     */
    public void tick(int p_75560_1_)
    {
        this.tickCounter = p_75560_1_;
        this.removeDeadAndOutOfRangeDoors();
        this.removeDeadAndOldAgressors();

        if (p_75560_1_ % 20 == 0)
        {
            this.updateNumVillagers();
        }

        if (p_75560_1_ % 30 == 0)
        {
            this.updateNumIronGolems();
        }

        int lvt_2_1_ = this.numVillagers / 10;

        if (this.numIronGolems < lvt_2_1_ && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0)
        {
            Vec3 lvt_3_1_ = this.func_179862_a(this.center, 2, 4, 2);

            if (lvt_3_1_ != null)
            {
                EntityIronGolem lvt_4_1_ = new EntityIronGolem(this.worldObj);
                lvt_4_1_.setPosition(lvt_3_1_.xCoord, lvt_3_1_.yCoord, lvt_3_1_.zCoord);
                this.worldObj.spawnEntityInWorld(lvt_4_1_);
                ++this.numIronGolems;
            }
        }
    }

    private Vec3 func_179862_a(BlockPos p_179862_1_, int p_179862_2_, int p_179862_3_, int p_179862_4_)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < 10; ++lvt_5_1_)
        {
            BlockPos lvt_6_1_ = p_179862_1_.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);

            if (this.func_179866_a(lvt_6_1_) && this.func_179861_a(new BlockPos(p_179862_2_, p_179862_3_, p_179862_4_), lvt_6_1_))
            {
                return new Vec3((double)lvt_6_1_.getX(), (double)lvt_6_1_.getY(), (double)lvt_6_1_.getZ());
            }
        }

        return null;
    }

    private boolean func_179861_a(BlockPos p_179861_1_, BlockPos p_179861_2_)
    {
        if (!World.doesBlockHaveSolidTopSurface(this.worldObj, p_179861_2_.down()))
        {
            return false;
        }
        else
        {
            int lvt_3_1_ = p_179861_2_.getX() - p_179861_1_.getX() / 2;
            int lvt_4_1_ = p_179861_2_.getZ() - p_179861_1_.getZ() / 2;

            for (int lvt_5_1_ = lvt_3_1_; lvt_5_1_ < lvt_3_1_ + p_179861_1_.getX(); ++lvt_5_1_)
            {
                for (int lvt_6_1_ = p_179861_2_.getY(); lvt_6_1_ < p_179861_2_.getY() + p_179861_1_.getY(); ++lvt_6_1_)
                {
                    for (int lvt_7_1_ = lvt_4_1_; lvt_7_1_ < lvt_4_1_ + p_179861_1_.getZ(); ++lvt_7_1_)
                    {
                        if (this.worldObj.getBlockState(new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_)).getBlock().isNormalCube())
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private void updateNumIronGolems()
    {
        List<EntityIronGolem> lvt_1_1_ = this.worldObj.<EntityIronGolem>getEntitiesWithinAABB(EntityIronGolem.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
        this.numIronGolems = lvt_1_1_.size();
    }

    private void updateNumVillagers()
    {
        List<EntityVillager> lvt_1_1_ = this.worldObj.<EntityVillager>getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB((double)(this.center.getX() - this.villageRadius), (double)(this.center.getY() - 4), (double)(this.center.getZ() - this.villageRadius), (double)(this.center.getX() + this.villageRadius), (double)(this.center.getY() + 4), (double)(this.center.getZ() + this.villageRadius)));
        this.numVillagers = lvt_1_1_.size();

        if (this.numVillagers == 0)
        {
            this.playerReputation.clear();
        }
    }

    public BlockPos getCenter()
    {
        return this.center;
    }

    public int getVillageRadius()
    {
        return this.villageRadius;
    }

    /**
     * Actually get num village door info entries, but that boils down to number of doors. Called by
     * EntityAIVillagerMate and VillageSiege
     */
    public int getNumVillageDoors()
    {
        return this.villageDoorInfoList.size();
    }

    public int getTicksSinceLastDoorAdding()
    {
        return this.tickCounter - this.lastAddDoorTimestamp;
    }

    public int getNumVillagers()
    {
        return this.numVillagers;
    }

    public boolean func_179866_a(BlockPos pos)
    {
        return this.center.distanceSq(pos) < (double)(this.villageRadius * this.villageRadius);
    }

    public List<VillageDoorInfo> getVillageDoorInfoList()
    {
        return this.villageDoorInfoList;
    }

    public VillageDoorInfo getNearestDoor(BlockPos pos)
    {
        VillageDoorInfo lvt_2_1_ = null;
        int lvt_3_1_ = Integer.MAX_VALUE;

        for (VillageDoorInfo lvt_5_1_ : this.villageDoorInfoList)
        {
            int lvt_6_1_ = lvt_5_1_.getDistanceToDoorBlockSq(pos);

            if (lvt_6_1_ < lvt_3_1_)
            {
                lvt_2_1_ = lvt_5_1_;
                lvt_3_1_ = lvt_6_1_;
            }
        }

        return lvt_2_1_;
    }

    /**
     * Returns {@link net.minecraft.village.VillageDoorInfo VillageDoorInfo} from given block position
     */
    public VillageDoorInfo getDoorInfo(BlockPos pos)
    {
        VillageDoorInfo lvt_2_1_ = null;
        int lvt_3_1_ = Integer.MAX_VALUE;

        for (VillageDoorInfo lvt_5_1_ : this.villageDoorInfoList)
        {
            int lvt_6_1_ = lvt_5_1_.getDistanceToDoorBlockSq(pos);

            if (lvt_6_1_ > 256)
            {
                lvt_6_1_ = lvt_6_1_ * 1000;
            }
            else
            {
                lvt_6_1_ = lvt_5_1_.getDoorOpeningRestrictionCounter();
            }

            if (lvt_6_1_ < lvt_3_1_)
            {
                lvt_2_1_ = lvt_5_1_;
                lvt_3_1_ = lvt_6_1_;
            }
        }

        return lvt_2_1_;
    }

    /**
     * if door not existed in this village, null will be returned
     */
    public VillageDoorInfo getExistedDoor(BlockPos doorBlock)
    {
        if (this.center.distanceSq(doorBlock) > (double)(this.villageRadius * this.villageRadius))
        {
            return null;
        }
        else
        {
            for (VillageDoorInfo lvt_3_1_ : this.villageDoorInfoList)
            {
                if (lvt_3_1_.getDoorBlockPos().getX() == doorBlock.getX() && lvt_3_1_.getDoorBlockPos().getZ() == doorBlock.getZ() && Math.abs(lvt_3_1_.getDoorBlockPos().getY() - doorBlock.getY()) <= 1)
                {
                    return lvt_3_1_;
                }
            }

            return null;
        }
    }

    public void addVillageDoorInfo(VillageDoorInfo doorInfo)
    {
        this.villageDoorInfoList.add(doorInfo);
        this.centerHelper = this.centerHelper.add(doorInfo.getDoorBlockPos());
        this.updateVillageRadiusAndCenter();
        this.lastAddDoorTimestamp = doorInfo.getInsidePosY();
    }

    /**
     * Returns true, if there is not a single village door left. Called by VillageCollection
     */
    public boolean isAnnihilated()
    {
        return this.villageDoorInfoList.isEmpty();
    }

    public void addOrRenewAgressor(EntityLivingBase entitylivingbaseIn)
    {
        for (Village.VillageAggressor lvt_3_1_ : this.villageAgressors)
        {
            if (lvt_3_1_.agressor == entitylivingbaseIn)
            {
                lvt_3_1_.agressionTime = this.tickCounter;
                return;
            }
        }

        this.villageAgressors.add(new Village.VillageAggressor(entitylivingbaseIn, this.tickCounter));
    }

    public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entitylivingbaseIn)
    {
        double lvt_2_1_ = Double.MAX_VALUE;
        Village.VillageAggressor lvt_4_1_ = null;

        for (int lvt_5_1_ = 0; lvt_5_1_ < this.villageAgressors.size(); ++lvt_5_1_)
        {
            Village.VillageAggressor lvt_6_1_ = (Village.VillageAggressor)this.villageAgressors.get(lvt_5_1_);
            double lvt_7_1_ = lvt_6_1_.agressor.getDistanceSqToEntity(entitylivingbaseIn);

            if (lvt_7_1_ <= lvt_2_1_)
            {
                lvt_4_1_ = lvt_6_1_;
                lvt_2_1_ = lvt_7_1_;
            }
        }

        return lvt_4_1_ != null ? lvt_4_1_.agressor : null;
    }

    public EntityPlayer getNearestTargetPlayer(EntityLivingBase villageDefender)
    {
        double lvt_2_1_ = Double.MAX_VALUE;
        EntityPlayer lvt_4_1_ = null;

        for (String lvt_6_1_ : this.playerReputation.keySet())
        {
            if (this.isPlayerReputationTooLow(lvt_6_1_))
            {
                EntityPlayer lvt_7_1_ = this.worldObj.getPlayerEntityByName(lvt_6_1_);

                if (lvt_7_1_ != null)
                {
                    double lvt_8_1_ = lvt_7_1_.getDistanceSqToEntity(villageDefender);

                    if (lvt_8_1_ <= lvt_2_1_)
                    {
                        lvt_4_1_ = lvt_7_1_;
                        lvt_2_1_ = lvt_8_1_;
                    }
                }
            }
        }

        return lvt_4_1_;
    }

    private void removeDeadAndOldAgressors()
    {
        Iterator<Village.VillageAggressor> lvt_1_1_ = this.villageAgressors.iterator();

        while (lvt_1_1_.hasNext())
        {
            Village.VillageAggressor lvt_2_1_ = (Village.VillageAggressor)lvt_1_1_.next();

            if (!lvt_2_1_.agressor.isEntityAlive() || Math.abs(this.tickCounter - lvt_2_1_.agressionTime) > 300)
            {
                lvt_1_1_.remove();
            }
        }
    }

    private void removeDeadAndOutOfRangeDoors()
    {
        boolean lvt_1_1_ = false;
        boolean lvt_2_1_ = this.worldObj.rand.nextInt(50) == 0;
        Iterator<VillageDoorInfo> lvt_3_1_ = this.villageDoorInfoList.iterator();

        while (lvt_3_1_.hasNext())
        {
            VillageDoorInfo lvt_4_1_ = (VillageDoorInfo)lvt_3_1_.next();

            if (lvt_2_1_)
            {
                lvt_4_1_.resetDoorOpeningRestrictionCounter();
            }

            if (!this.isWoodDoor(lvt_4_1_.getDoorBlockPos()) || Math.abs(this.tickCounter - lvt_4_1_.getInsidePosY()) > 1200)
            {
                this.centerHelper = this.centerHelper.subtract(lvt_4_1_.getDoorBlockPos());
                lvt_1_1_ = true;
                lvt_4_1_.setIsDetachedFromVillageFlag(true);
                lvt_3_1_.remove();
            }
        }

        if (lvt_1_1_)
        {
            this.updateVillageRadiusAndCenter();
        }
    }

    private boolean isWoodDoor(BlockPos pos)
    {
        Block lvt_2_1_ = this.worldObj.getBlockState(pos).getBlock();
        return lvt_2_1_ instanceof BlockDoor ? lvt_2_1_.getMaterial() == Material.wood : false;
    }

    private void updateVillageRadiusAndCenter()
    {
        int lvt_1_1_ = this.villageDoorInfoList.size();

        if (lvt_1_1_ == 0)
        {
            this.center = new BlockPos(0, 0, 0);
            this.villageRadius = 0;
        }
        else
        {
            this.center = new BlockPos(this.centerHelper.getX() / lvt_1_1_, this.centerHelper.getY() / lvt_1_1_, this.centerHelper.getZ() / lvt_1_1_);
            int lvt_2_1_ = 0;

            for (VillageDoorInfo lvt_4_1_ : this.villageDoorInfoList)
            {
                lvt_2_1_ = Math.max(lvt_4_1_.getDistanceToDoorBlockSq(this.center), lvt_2_1_);
            }

            this.villageRadius = Math.max(32, (int)Math.sqrt((double)lvt_2_1_) + 1);
        }
    }

    /**
     * Return the village reputation for a player
     */
    public int getReputationForPlayer(String p_82684_1_)
    {
        Integer lvt_2_1_ = (Integer)this.playerReputation.get(p_82684_1_);
        return lvt_2_1_ != null ? lvt_2_1_.intValue() : 0;
    }

    /**
     * Set the village reputation for a player.
     */
    public int setReputationForPlayer(String p_82688_1_, int p_82688_2_)
    {
        int lvt_3_1_ = this.getReputationForPlayer(p_82688_1_);
        int lvt_4_1_ = MathHelper.clamp_int(lvt_3_1_ + p_82688_2_, -30, 10);
        this.playerReputation.put(p_82688_1_, Integer.valueOf(lvt_4_1_));
        return lvt_4_1_;
    }

    /**
     * Return whether this player has a too low reputation with this village.
     */
    public boolean isPlayerReputationTooLow(String p_82687_1_)
    {
        return this.getReputationForPlayer(p_82687_1_) <= -15;
    }

    /**
     * Read this village's data from NBT.
     */
    public void readVillageDataFromNBT(NBTTagCompound compound)
    {
        this.numVillagers = compound.getInteger("PopSize");
        this.villageRadius = compound.getInteger("Radius");
        this.numIronGolems = compound.getInteger("Golems");
        this.lastAddDoorTimestamp = compound.getInteger("Stable");
        this.tickCounter = compound.getInteger("Tick");
        this.noBreedTicks = compound.getInteger("MTick");
        this.center = new BlockPos(compound.getInteger("CX"), compound.getInteger("CY"), compound.getInteger("CZ"));
        this.centerHelper = new BlockPos(compound.getInteger("ACX"), compound.getInteger("ACY"), compound.getInteger("ACZ"));
        NBTTagList lvt_2_1_ = compound.getTagList("Doors", 10);

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
        {
            NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
            VillageDoorInfo lvt_5_1_ = new VillageDoorInfo(new BlockPos(lvt_4_1_.getInteger("X"), lvt_4_1_.getInteger("Y"), lvt_4_1_.getInteger("Z")), lvt_4_1_.getInteger("IDX"), lvt_4_1_.getInteger("IDZ"), lvt_4_1_.getInteger("TS"));
            this.villageDoorInfoList.add(lvt_5_1_);
        }

        NBTTagList lvt_3_2_ = compound.getTagList("Players", 10);

        for (int lvt_4_2_ = 0; lvt_4_2_ < lvt_3_2_.tagCount(); ++lvt_4_2_)
        {
            NBTTagCompound lvt_5_2_ = lvt_3_2_.getCompoundTagAt(lvt_4_2_);

            if (lvt_5_2_.hasKey("UUID"))
            {
                PlayerProfileCache lvt_6_1_ = MinecraftServer.getServer().getPlayerProfileCache();
                GameProfile lvt_7_1_ = lvt_6_1_.getProfileByUUID(UUID.fromString(lvt_5_2_.getString("UUID")));

                if (lvt_7_1_ != null)
                {
                    this.playerReputation.put(lvt_7_1_.getName(), Integer.valueOf(lvt_5_2_.getInteger("S")));
                }
            }
            else
            {
                this.playerReputation.put(lvt_5_2_.getString("Name"), Integer.valueOf(lvt_5_2_.getInteger("S")));
            }
        }
    }

    /**
     * Write this village's data to NBT.
     */
    public void writeVillageDataToNBT(NBTTagCompound compound)
    {
        compound.setInteger("PopSize", this.numVillagers);
        compound.setInteger("Radius", this.villageRadius);
        compound.setInteger("Golems", this.numIronGolems);
        compound.setInteger("Stable", this.lastAddDoorTimestamp);
        compound.setInteger("Tick", this.tickCounter);
        compound.setInteger("MTick", this.noBreedTicks);
        compound.setInteger("CX", this.center.getX());
        compound.setInteger("CY", this.center.getY());
        compound.setInteger("CZ", this.center.getZ());
        compound.setInteger("ACX", this.centerHelper.getX());
        compound.setInteger("ACY", this.centerHelper.getY());
        compound.setInteger("ACZ", this.centerHelper.getZ());
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (VillageDoorInfo lvt_4_1_ : this.villageDoorInfoList)
        {
            NBTTagCompound lvt_5_1_ = new NBTTagCompound();
            lvt_5_1_.setInteger("X", lvt_4_1_.getDoorBlockPos().getX());
            lvt_5_1_.setInteger("Y", lvt_4_1_.getDoorBlockPos().getY());
            lvt_5_1_.setInteger("Z", lvt_4_1_.getDoorBlockPos().getZ());
            lvt_5_1_.setInteger("IDX", lvt_4_1_.getInsideOffsetX());
            lvt_5_1_.setInteger("IDZ", lvt_4_1_.getInsideOffsetZ());
            lvt_5_1_.setInteger("TS", lvt_4_1_.getInsidePosY());
            lvt_2_1_.appendTag(lvt_5_1_);
        }

        compound.setTag("Doors", lvt_2_1_);
        NBTTagList lvt_3_2_ = new NBTTagList();

        for (String lvt_5_2_ : this.playerReputation.keySet())
        {
            NBTTagCompound lvt_6_1_ = new NBTTagCompound();
            PlayerProfileCache lvt_7_1_ = MinecraftServer.getServer().getPlayerProfileCache();
            GameProfile lvt_8_1_ = lvt_7_1_.getGameProfileForUsername(lvt_5_2_);

            if (lvt_8_1_ != null)
            {
                lvt_6_1_.setString("UUID", lvt_8_1_.getId().toString());
                lvt_6_1_.setInteger("S", ((Integer)this.playerReputation.get(lvt_5_2_)).intValue());
                lvt_3_2_.appendTag(lvt_6_1_);
            }
        }

        compound.setTag("Players", lvt_3_2_);
    }

    /**
     * Prevent villager breeding for a fixed interval of time
     */
    public void endMatingSeason()
    {
        this.noBreedTicks = this.tickCounter;
    }

    /**
     * Return whether villagers mating refractory period has passed
     */
    public boolean isMatingSeason()
    {
        return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
    }

    public void setDefaultPlayerReputation(int p_82683_1_)
    {
        for (String lvt_3_1_ : this.playerReputation.keySet())
        {
            this.setReputationForPlayer(lvt_3_1_, p_82683_1_);
        }
    }

    class VillageAggressor
    {
        public EntityLivingBase agressor;
        public int agressionTime;

        VillageAggressor(EntityLivingBase p_i1674_2_, int p_i1674_3_)
        {
            this.agressor = p_i1674_2_;
            this.agressionTime = p_i1674_3_;
        }
    }
}
