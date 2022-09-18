package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic
{
    /** The delay to spawn. */
    private int spawnDelay = 20;
    private String mobID = "Pig";
    private final List<MobSpawnerBaseLogic.WeightedRandomMinecart> minecartToSpawn = Lists.newArrayList();
    private MobSpawnerBaseLogic.WeightedRandomMinecart randomEntity;

    /** The rotation of the mob inside the mob spawner */
    private double mobRotation;

    /** the previous rotation of the mob inside the mob spawner */
    private double prevMobRotation;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;

    /** Cached instance of the entity to render inside the spawner. */
    private Entity cachedEntity;
    private int maxNearbyEntities = 6;

    /** The distance from which a player activates the spawner. */
    private int activatingRangeFromPlayer = 16;

    /** The range coefficient for spawning entities around. */
    private int spawnRange = 4;

    /**
     * Gets the entity name that should be spawned.
     */
    private String getEntityNameToSpawn()
    {
        if (this.getRandomEntity() == null)
        {
            if (this.mobID != null && this.mobID.equals("Minecart"))
            {
                this.mobID = "MinecartRideable";
            }

            return this.mobID;
        }
        else
        {
            return this.getRandomEntity().entityType;
        }
    }

    public void setEntityName(String name)
    {
        this.mobID = name;
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    private boolean isActivated()
    {
        BlockPos lvt_1_1_ = this.getSpawnerPosition();
        return this.getSpawnerWorld().isAnyPlayerWithinRangeAt((double)lvt_1_1_.getX() + 0.5D, (double)lvt_1_1_.getY() + 0.5D, (double)lvt_1_1_.getZ() + 0.5D, (double)this.activatingRangeFromPlayer);
    }

    public void updateSpawner()
    {
        if (this.isActivated())
        {
            BlockPos lvt_1_1_ = this.getSpawnerPosition();

            if (this.getSpawnerWorld().isRemote)
            {
                double lvt_2_1_ = (double)((float)lvt_1_1_.getX() + this.getSpawnerWorld().rand.nextFloat());
                double lvt_4_1_ = (double)((float)lvt_1_1_.getY() + this.getSpawnerWorld().rand.nextFloat());
                double lvt_6_1_ = (double)((float)lvt_1_1_.getZ() + this.getSpawnerWorld().rand.nextFloat());
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_2_1_, lvt_4_1_, lvt_6_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, lvt_2_1_, lvt_4_1_, lvt_6_1_, 0.0D, 0.0D, 0.0D, new int[0]);

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                }

                this.prevMobRotation = this.mobRotation;
                this.mobRotation = (this.mobRotation + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
            }
            else
            {
                if (this.spawnDelay == -1)
                {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                    return;
                }

                boolean lvt_2_2_ = false;

                for (int lvt_3_1_ = 0; lvt_3_1_ < this.spawnCount; ++lvt_3_1_)
                {
                    Entity lvt_4_2_ = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());

                    if (lvt_4_2_ == null)
                    {
                        return;
                    }

                    int lvt_5_1_ = this.getSpawnerWorld().getEntitiesWithinAABB(lvt_4_2_.getClass(), (new AxisAlignedBB((double)lvt_1_1_.getX(), (double)lvt_1_1_.getY(), (double)lvt_1_1_.getZ(), (double)(lvt_1_1_.getX() + 1), (double)(lvt_1_1_.getY() + 1), (double)(lvt_1_1_.getZ() + 1))).expand((double)this.spawnRange, (double)this.spawnRange, (double)this.spawnRange)).size();

                    if (lvt_5_1_ >= this.maxNearbyEntities)
                    {
                        this.resetTimer();
                        return;
                    }

                    double lvt_6_2_ = (double)lvt_1_1_.getX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange + 0.5D;
                    double lvt_8_1_ = (double)(lvt_1_1_.getY() + this.getSpawnerWorld().rand.nextInt(3) - 1);
                    double lvt_10_1_ = (double)lvt_1_1_.getZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * (double)this.spawnRange + 0.5D;
                    EntityLiving lvt_12_1_ = lvt_4_2_ instanceof EntityLiving ? (EntityLiving)lvt_4_2_ : null;
                    lvt_4_2_.setLocationAndAngles(lvt_6_2_, lvt_8_1_, lvt_10_1_, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);

                    if (lvt_12_1_ == null || lvt_12_1_.getCanSpawnHere() && lvt_12_1_.isNotColliding())
                    {
                        this.spawnNewEntity(lvt_4_2_, true);
                        this.getSpawnerWorld().playAuxSFX(2004, lvt_1_1_, 0);

                        if (lvt_12_1_ != null)
                        {
                            lvt_12_1_.spawnExplosionParticle();
                        }

                        lvt_2_2_ = true;
                    }
                }

                if (lvt_2_2_)
                {
                    this.resetTimer();
                }
            }
        }
    }

    private Entity spawnNewEntity(Entity entityIn, boolean spawn)
    {
        if (this.getRandomEntity() != null)
        {
            NBTTagCompound lvt_3_1_ = new NBTTagCompound();
            entityIn.writeToNBTOptional(lvt_3_1_);

            for (String lvt_5_1_ : this.getRandomEntity().nbtData.getKeySet())
            {
                NBTBase lvt_6_1_ = this.getRandomEntity().nbtData.getTag(lvt_5_1_);
                lvt_3_1_.setTag(lvt_5_1_, lvt_6_1_.copy());
            }

            entityIn.readFromNBT(lvt_3_1_);

            if (entityIn.worldObj != null && spawn)
            {
                entityIn.worldObj.spawnEntityInWorld(entityIn);
            }

            NBTTagCompound lvt_5_2_;

            for (Entity lvt_4_2_ = entityIn; lvt_3_1_.hasKey("Riding", 10); lvt_3_1_ = lvt_5_2_)
            {
                lvt_5_2_ = lvt_3_1_.getCompoundTag("Riding");
                Entity lvt_6_2_ = EntityList.createEntityByName(lvt_5_2_.getString("id"), entityIn.worldObj);

                if (lvt_6_2_ != null)
                {
                    NBTTagCompound lvt_7_1_ = new NBTTagCompound();
                    lvt_6_2_.writeToNBTOptional(lvt_7_1_);

                    for (String lvt_9_1_ : lvt_5_2_.getKeySet())
                    {
                        NBTBase lvt_10_1_ = lvt_5_2_.getTag(lvt_9_1_);
                        lvt_7_1_.setTag(lvt_9_1_, lvt_10_1_.copy());
                    }

                    lvt_6_2_.readFromNBT(lvt_7_1_);
                    lvt_6_2_.setLocationAndAngles(lvt_4_2_.posX, lvt_4_2_.posY, lvt_4_2_.posZ, lvt_4_2_.rotationYaw, lvt_4_2_.rotationPitch);

                    if (entityIn.worldObj != null && spawn)
                    {
                        entityIn.worldObj.spawnEntityInWorld(lvt_6_2_);
                    }

                    lvt_4_2_.mountEntity(lvt_6_2_);
                }

                lvt_4_2_ = lvt_6_2_;
            }
        }
        else if (entityIn instanceof EntityLivingBase && entityIn.worldObj != null && spawn)
        {
            if (entityIn instanceof EntityLiving)
            {
                ((EntityLiving)entityIn).onInitialSpawn(entityIn.worldObj.getDifficultyForLocation(new BlockPos(entityIn)), (IEntityLivingData)null);
            }

            entityIn.worldObj.spawnEntityInWorld(entityIn);
        }

        return entityIn;
    }

    private void resetTimer()
    {
        if (this.maxSpawnDelay <= this.minSpawnDelay)
        {
            this.spawnDelay = this.minSpawnDelay;
        }
        else
        {
            int var10003 = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(var10003);
        }

        if (this.minecartToSpawn.size() > 0)
        {
            this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
        }

        this.func_98267_a(1);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        this.mobID = nbt.getString("EntityId");
        this.spawnDelay = nbt.getShort("Delay");
        this.minecartToSpawn.clear();

        if (nbt.hasKey("SpawnPotentials", 9))
        {
            NBTTagList lvt_2_1_ = nbt.getTagList("SpawnPotentials", 10);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
            {
                this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(lvt_2_1_.getCompoundTagAt(lvt_3_1_)));
            }
        }

        if (nbt.hasKey("SpawnData", 10))
        {
            this.setRandomEntity(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbt.getCompoundTag("SpawnData"), this.mobID));
        }
        else
        {
            this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
        }

        if (nbt.hasKey("MinSpawnDelay", 99))
        {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.hasKey("MaxNearbyEntities", 99))
        {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.hasKey("SpawnRange", 99))
        {
            this.spawnRange = nbt.getShort("SpawnRange");
        }

        if (this.getSpawnerWorld() != null)
        {
            this.cachedEntity = null;
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        String lvt_2_1_ = this.getEntityNameToSpawn();

        if (!StringUtils.isNullOrEmpty(lvt_2_1_))
        {
            nbt.setString("EntityId", lvt_2_1_);
            nbt.setShort("Delay", (short)this.spawnDelay);
            nbt.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
            nbt.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
            nbt.setShort("SpawnCount", (short)this.spawnCount);
            nbt.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
            nbt.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
            nbt.setShort("SpawnRange", (short)this.spawnRange);

            if (this.getRandomEntity() != null)
            {
                nbt.setTag("SpawnData", this.getRandomEntity().nbtData.copy());
            }

            if (this.getRandomEntity() != null || this.minecartToSpawn.size() > 0)
            {
                NBTTagList lvt_3_1_ = new NBTTagList();

                if (this.minecartToSpawn.size() > 0)
                {
                    for (MobSpawnerBaseLogic.WeightedRandomMinecart lvt_5_1_ : this.minecartToSpawn)
                    {
                        lvt_3_1_.appendTag(lvt_5_1_.toNBT());
                    }
                }
                else
                {
                    lvt_3_1_.appendTag(this.getRandomEntity().toNBT());
                }

                nbt.setTag("SpawnPotentials", lvt_3_1_);
            }
        }
    }

    public Entity func_180612_a(World worldIn)
    {
        if (this.cachedEntity == null)
        {
            Entity lvt_2_1_ = EntityList.createEntityByName(this.getEntityNameToSpawn(), worldIn);

            if (lvt_2_1_ != null)
            {
                lvt_2_1_ = this.spawnNewEntity(lvt_2_1_, false);
                this.cachedEntity = lvt_2_1_;
            }
        }

        return this.cachedEntity;
    }

    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean setDelayToMin(int delay)
    {
        if (delay == 1 && this.getSpawnerWorld().isRemote)
        {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        else
        {
            return false;
        }
    }

    private MobSpawnerBaseLogic.WeightedRandomMinecart getRandomEntity()
    {
        return this.randomEntity;
    }

    public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart p_98277_1_)
    {
        this.randomEntity = p_98277_1_;
    }

    public abstract void func_98267_a(int id);

    public abstract World getSpawnerWorld();

    public abstract BlockPos getSpawnerPosition();

    public double getMobRotation()
    {
        return this.mobRotation;
    }

    public double getPrevMobRotation()
    {
        return this.prevMobRotation;
    }

    public class WeightedRandomMinecart extends WeightedRandom.Item
    {
        private final NBTTagCompound nbtData;
        private final String entityType;

        public WeightedRandomMinecart(NBTTagCompound tagCompound)
        {
            this(tagCompound.getCompoundTag("Properties"), tagCompound.getString("Type"), tagCompound.getInteger("Weight"));
        }

        public WeightedRandomMinecart(NBTTagCompound tagCompound, String type)
        {
            this(tagCompound, type, 1);
        }

        private WeightedRandomMinecart(NBTTagCompound tagCompound, String type, int weight)
        {
            super(weight);

            if (type.equals("Minecart"))
            {
                if (tagCompound != null)
                {
                    type = EntityMinecart.EnumMinecartType.byNetworkID(tagCompound.getInteger("Type")).getName();
                }
                else
                {
                    type = "MinecartRideable";
                }
            }

            this.nbtData = tagCompound;
            this.entityType = type;
        }

        public NBTTagCompound toNBT()
        {
            NBTTagCompound lvt_1_1_ = new NBTTagCompound();
            lvt_1_1_.setTag("Properties", this.nbtData);
            lvt_1_1_.setString("Type", this.entityType);
            lvt_1_1_.setInteger("Weight", this.itemWeight);
            return lvt_1_1_;
        }
    }
}
