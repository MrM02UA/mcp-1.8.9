package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob
{
    private static final UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier attackingSpeedBoostModifier = (new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost", 0.15000000596046448D, 0)).setSaved(false);
    private static final Set<Block> carriableBlocks = Sets.newIdentityHashSet();
    private boolean isAggressive;

    public EntityEnderman(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 2.9F);
        this.stepHeight = 1.0F;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0D, false));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.tasks.addTask(10, new EntityEnderman.AIPlaceBlock(this));
        this.tasks.addTask(11, new EntityEnderman.AITakeBlock(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityEnderman.AIFindPlayer(this));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityEndermite.class, 10, true, false, new Predicate<EntityEndermite>()
        {
            public boolean apply(EntityEndermite p_apply_1_)
            {
                return p_apply_1_.isSpawnedByPlayer();
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((EntityEndermite)p_apply_1_);
            }
        }));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Short((short)0));
        this.dataWatcher.addObject(17, new Byte((byte)0));
        this.dataWatcher.addObject(18, new Byte((byte)0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        IBlockState lvt_2_1_ = this.getHeldBlockState();
        tagCompound.setShort("carried", (short)Block.getIdFromBlock(lvt_2_1_.getBlock()));
        tagCompound.setShort("carriedData", (short)lvt_2_1_.getBlock().getMetaFromState(lvt_2_1_));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        IBlockState lvt_2_1_;

        if (tagCompund.hasKey("carried", 8))
        {
            lvt_2_1_ = Block.getBlockFromName(tagCompund.getString("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
        }
        else
        {
            lvt_2_1_ = Block.getBlockById(tagCompund.getShort("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
        }

        this.setHeldBlockState(lvt_2_1_);
    }

    /**
     * Checks to see if this enderman should be attacking this player
     */
    private boolean shouldAttackPlayer(EntityPlayer player)
    {
        ItemStack lvt_2_1_ = player.inventory.armorInventory[3];

        if (lvt_2_1_ != null && lvt_2_1_.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
        {
            return false;
        }
        else
        {
            Vec3 lvt_3_1_ = player.getLook(1.0F).normalize();
            Vec3 lvt_4_1_ = new Vec3(this.posX - player.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - (player.posY + (double)player.getEyeHeight()), this.posZ - player.posZ);
            double lvt_5_1_ = lvt_4_1_.lengthVector();
            lvt_4_1_ = lvt_4_1_.normalize();
            double lvt_7_1_ = lvt_3_1_.dotProduct(lvt_4_1_);
            return lvt_7_1_ > 1.0D - 0.025D / lvt_5_1_ ? player.canEntityBeSeen(this) : false;
        }
    }

    public float getEyeHeight()
    {
        return 2.55F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.worldObj.isRemote)
        {
            for (int lvt_1_1_ = 0; lvt_1_1_ < 2; ++lvt_1_1_)
            {
                this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
            }
        }

        this.isJumping = false;
        super.onLivingUpdate();
    }

    protected void updateAITasks()
    {
        if (this.isWet())
        {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        }

        if (this.isScreaming() && !this.isAggressive && this.rand.nextInt(100) == 0)
        {
            this.setScreaming(false);
        }

        if (this.worldObj.isDaytime())
        {
            float lvt_1_1_ = this.getBrightness(1.0F);

            if (lvt_1_1_ > 0.5F && this.worldObj.canSeeSky(new BlockPos(this)) && this.rand.nextFloat() * 30.0F < (lvt_1_1_ - 0.4F) * 2.0F)
            {
                this.setAttackTarget((EntityLivingBase)null);
                this.setScreaming(false);
                this.isAggressive = false;
                this.teleportRandomly();
            }
        }

        super.updateAITasks();
    }

    /**
     * Teleport the enderman to a random nearby position
     */
    protected boolean teleportRandomly()
    {
        double lvt_1_1_ = this.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
        double lvt_3_1_ = this.posY + (double)(this.rand.nextInt(64) - 32);
        double lvt_5_1_ = this.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
        return this.teleportTo(lvt_1_1_, lvt_3_1_, lvt_5_1_);
    }

    /**
     * Teleport the enderman to another entity
     */
    protected boolean teleportToEntity(Entity p_70816_1_)
    {
        Vec3 lvt_2_1_ = new Vec3(this.posX - p_70816_1_.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - p_70816_1_.posY + (double)p_70816_1_.getEyeHeight(), this.posZ - p_70816_1_.posZ);
        lvt_2_1_ = lvt_2_1_.normalize();
        double lvt_3_1_ = 16.0D;
        double lvt_5_1_ = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - lvt_2_1_.xCoord * lvt_3_1_;
        double lvt_7_1_ = this.posY + (double)(this.rand.nextInt(16) - 8) - lvt_2_1_.yCoord * lvt_3_1_;
        double lvt_9_1_ = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - lvt_2_1_.zCoord * lvt_3_1_;
        return this.teleportTo(lvt_5_1_, lvt_7_1_, lvt_9_1_);
    }

    /**
     * Teleport the enderman
     */
    protected boolean teleportTo(double x, double y, double z)
    {
        double lvt_7_1_ = this.posX;
        double lvt_9_1_ = this.posY;
        double lvt_11_1_ = this.posZ;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        boolean lvt_13_1_ = false;
        BlockPos lvt_14_1_ = new BlockPos(this.posX, this.posY, this.posZ);

        if (this.worldObj.isBlockLoaded(lvt_14_1_))
        {
            boolean lvt_15_1_ = false;

            while (!lvt_15_1_ && lvt_14_1_.getY() > 0)
            {
                BlockPos lvt_16_1_ = lvt_14_1_.down();
                Block lvt_17_1_ = this.worldObj.getBlockState(lvt_16_1_).getBlock();

                if (lvt_17_1_.getMaterial().blocksMovement())
                {
                    lvt_15_1_ = true;
                }
                else
                {
                    --this.posY;
                    lvt_14_1_ = lvt_16_1_;
                }
            }

            if (lvt_15_1_)
            {
                super.setPositionAndUpdate(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox()))
                {
                    lvt_13_1_ = true;
                }
            }
        }

        if (!lvt_13_1_)
        {
            this.setPosition(lvt_7_1_, lvt_9_1_, lvt_11_1_);
            return false;
        }
        else
        {
            int lvt_15_2_ = 128;

            for (int lvt_16_2_ = 0; lvt_16_2_ < lvt_15_2_; ++lvt_16_2_)
            {
                double lvt_17_2_ = (double)lvt_16_2_ / ((double)lvt_15_2_ - 1.0D);
                float lvt_19_1_ = (this.rand.nextFloat() - 0.5F) * 0.2F;
                float lvt_20_1_ = (this.rand.nextFloat() - 0.5F) * 0.2F;
                float lvt_21_1_ = (this.rand.nextFloat() - 0.5F) * 0.2F;
                double lvt_22_1_ = lvt_7_1_ + (this.posX - lvt_7_1_) * lvt_17_2_ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
                double lvt_24_1_ = lvt_9_1_ + (this.posY - lvt_9_1_) * lvt_17_2_ + this.rand.nextDouble() * (double)this.height;
                double lvt_26_1_ = lvt_11_1_ + (this.posZ - lvt_11_1_) * lvt_17_2_ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
                this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, lvt_22_1_, lvt_24_1_, lvt_26_1_, (double)lvt_19_1_, (double)lvt_20_1_, (double)lvt_21_1_, new int[0]);
            }

            this.worldObj.playSoundEffect(lvt_7_1_, lvt_9_1_, lvt_11_1_, "mob.endermen.portal", 1.0F, 1.0F);
            this.playSound("mob.endermen.portal", 1.0F, 1.0F);
            return true;
        }
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return this.isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.endermen.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.endermen.death";
    }

    protected Item getDropItem()
    {
        return Items.ender_pearl;
    }

    /**
     * Drop 0-2 items of this living's type
     *  
     * @param wasRecentlyHit true if this this entity was recently hit by appropriate entity (generally only if player
     * or tameable)
     * @param lootingModifier level of enchanment to be applied to this drop
     */
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier)
    {
        Item lvt_3_1_ = this.getDropItem();

        if (lvt_3_1_ != null)
        {
            int lvt_4_1_ = this.rand.nextInt(2 + lootingModifier);

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_; ++lvt_5_1_)
            {
                this.dropItem(lvt_3_1_, 1);
            }
        }
    }

    /**
     * Sets this enderman's held block state
     */
    public void setHeldBlockState(IBlockState state)
    {
        this.dataWatcher.updateObject(16, Short.valueOf((short)(Block.getStateId(state) & 65535)));
    }

    /**
     * Gets this enderman's held block state
     */
    public IBlockState getHeldBlockState()
    {
        return Block.getStateById(this.dataWatcher.getWatchableObjectShort(16) & 65535);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            if (source.getEntity() == null || !(source.getEntity() instanceof EntityEndermite))
            {
                if (!this.worldObj.isRemote)
                {
                    this.setScreaming(true);
                }

                if (source instanceof EntityDamageSource && source.getEntity() instanceof EntityPlayer)
                {
                    if (source.getEntity() instanceof EntityPlayerMP && ((EntityPlayerMP)source.getEntity()).theItemInWorldManager.isCreative())
                    {
                        this.setScreaming(false);
                    }
                    else
                    {
                        this.isAggressive = true;
                    }
                }

                if (source instanceof EntityDamageSourceIndirect)
                {
                    this.isAggressive = false;

                    for (int lvt_3_1_ = 0; lvt_3_1_ < 64; ++lvt_3_1_)
                    {
                        if (this.teleportRandomly())
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }

            boolean lvt_3_2_ = super.attackEntityFrom(source, amount);

            if (source.isUnblockable() && this.rand.nextInt(10) != 0)
            {
                this.teleportRandomly();
            }

            return lvt_3_2_;
        }
    }

    public boolean isScreaming()
    {
        return this.dataWatcher.getWatchableObjectByte(18) > 0;
    }

    public void setScreaming(boolean screaming)
    {
        this.dataWatcher.updateObject(18, Byte.valueOf((byte)(screaming ? 1 : 0)));
    }

    static
    {
        carriableBlocks.add(Blocks.grass);
        carriableBlocks.add(Blocks.dirt);
        carriableBlocks.add(Blocks.sand);
        carriableBlocks.add(Blocks.gravel);
        carriableBlocks.add(Blocks.yellow_flower);
        carriableBlocks.add(Blocks.red_flower);
        carriableBlocks.add(Blocks.brown_mushroom);
        carriableBlocks.add(Blocks.red_mushroom);
        carriableBlocks.add(Blocks.tnt);
        carriableBlocks.add(Blocks.cactus);
        carriableBlocks.add(Blocks.clay);
        carriableBlocks.add(Blocks.pumpkin);
        carriableBlocks.add(Blocks.melon_block);
        carriableBlocks.add(Blocks.mycelium);
    }

    static class AIFindPlayer extends EntityAINearestAttackableTarget
    {
        private EntityPlayer player;
        private int field_179450_h;
        private int field_179451_i;
        private EntityEnderman enderman;

        public AIFindPlayer(EntityEnderman p_i45842_1_)
        {
            super(p_i45842_1_, EntityPlayer.class, true);
            this.enderman = p_i45842_1_;
        }

        public boolean shouldExecute()
        {
            double lvt_1_1_ = this.getTargetDistance();
            List<EntityPlayer> lvt_3_1_ = this.taskOwner.worldObj.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, this.taskOwner.getEntityBoundingBox().expand(lvt_1_1_, 4.0D, lvt_1_1_), this.targetEntitySelector);
            Collections.sort(lvt_3_1_, this.theNearestAttackableTargetSorter);

            if (lvt_3_1_.isEmpty())
            {
                return false;
            }
            else
            {
                this.player = (EntityPlayer)lvt_3_1_.get(0);
                return true;
            }
        }

        public void startExecuting()
        {
            this.field_179450_h = 5;
            this.field_179451_i = 0;
        }

        public void resetTask()
        {
            this.player = null;
            this.enderman.setScreaming(false);
            IAttributeInstance lvt_1_1_ = this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            lvt_1_1_.removeModifier(EntityEnderman.attackingSpeedBoostModifier);
            super.resetTask();
        }

        public boolean continueExecuting()
        {
            if (this.player != null)
            {
                if (!this.enderman.shouldAttackPlayer(this.player))
                {
                    return false;
                }
                else
                {
                    this.enderman.isAggressive = true;
                    this.enderman.faceEntity(this.player, 10.0F, 10.0F);
                    return true;
                }
            }
            else
            {
                return super.continueExecuting();
            }
        }

        public void updateTask()
        {
            if (this.player != null)
            {
                if (--this.field_179450_h <= 0)
                {
                    this.targetEntity = this.player;
                    this.player = null;
                    super.startExecuting();
                    this.enderman.playSound("mob.endermen.stare", 1.0F, 1.0F);
                    this.enderman.setScreaming(true);
                    IAttributeInstance lvt_1_1_ = this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                    lvt_1_1_.applyModifier(EntityEnderman.attackingSpeedBoostModifier);
                }
            }
            else
            {
                if (this.targetEntity != null)
                {
                    if (this.targetEntity instanceof EntityPlayer && this.enderman.shouldAttackPlayer((EntityPlayer)this.targetEntity))
                    {
                        if (this.targetEntity.getDistanceSqToEntity(this.enderman) < 16.0D)
                        {
                            this.enderman.teleportRandomly();
                        }

                        this.field_179451_i = 0;
                    }
                    else if (this.targetEntity.getDistanceSqToEntity(this.enderman) > 256.0D && this.field_179451_i++ >= 30 && this.enderman.teleportToEntity(this.targetEntity))
                    {
                        this.field_179451_i = 0;
                    }
                }

                super.updateTask();
            }
        }
    }

    static class AIPlaceBlock extends EntityAIBase
    {
        private EntityEnderman enderman;

        public AIPlaceBlock(EntityEnderman p_i45843_1_)
        {
            this.enderman = p_i45843_1_;
        }

        public boolean shouldExecute()
        {
            return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing") ? false : (this.enderman.getHeldBlockState().getBlock().getMaterial() == Material.air ? false : this.enderman.getRNG().nextInt(2000) == 0);
        }

        public void updateTask()
        {
            Random lvt_1_1_ = this.enderman.getRNG();
            World lvt_2_1_ = this.enderman.worldObj;
            int lvt_3_1_ = MathHelper.floor_double(this.enderman.posX - 1.0D + lvt_1_1_.nextDouble() * 2.0D);
            int lvt_4_1_ = MathHelper.floor_double(this.enderman.posY + lvt_1_1_.nextDouble() * 2.0D);
            int lvt_5_1_ = MathHelper.floor_double(this.enderman.posZ - 1.0D + lvt_1_1_.nextDouble() * 2.0D);
            BlockPos lvt_6_1_ = new BlockPos(lvt_3_1_, lvt_4_1_, lvt_5_1_);
            Block lvt_7_1_ = lvt_2_1_.getBlockState(lvt_6_1_).getBlock();
            Block lvt_8_1_ = lvt_2_1_.getBlockState(lvt_6_1_.down()).getBlock();

            if (this.func_179474_a(lvt_2_1_, lvt_6_1_, this.enderman.getHeldBlockState().getBlock(), lvt_7_1_, lvt_8_1_))
            {
                lvt_2_1_.setBlockState(lvt_6_1_, this.enderman.getHeldBlockState(), 3);
                this.enderman.setHeldBlockState(Blocks.air.getDefaultState());
            }
        }

        private boolean func_179474_a(World worldIn, BlockPos p_179474_2_, Block p_179474_3_, Block p_179474_4_, Block p_179474_5_)
        {
            return !p_179474_3_.canPlaceBlockAt(worldIn, p_179474_2_) ? false : (p_179474_4_.getMaterial() != Material.air ? false : (p_179474_5_.getMaterial() == Material.air ? false : p_179474_5_.isFullCube()));
        }
    }

    static class AITakeBlock extends EntityAIBase
    {
        private EntityEnderman enderman;

        public AITakeBlock(EntityEnderman p_i45841_1_)
        {
            this.enderman = p_i45841_1_;
        }

        public boolean shouldExecute()
        {
            return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing") ? false : (this.enderman.getHeldBlockState().getBlock().getMaterial() != Material.air ? false : this.enderman.getRNG().nextInt(20) == 0);
        }

        public void updateTask()
        {
            Random lvt_1_1_ = this.enderman.getRNG();
            World lvt_2_1_ = this.enderman.worldObj;
            int lvt_3_1_ = MathHelper.floor_double(this.enderman.posX - 2.0D + lvt_1_1_.nextDouble() * 4.0D);
            int lvt_4_1_ = MathHelper.floor_double(this.enderman.posY + lvt_1_1_.nextDouble() * 3.0D);
            int lvt_5_1_ = MathHelper.floor_double(this.enderman.posZ - 2.0D + lvt_1_1_.nextDouble() * 4.0D);
            BlockPos lvt_6_1_ = new BlockPos(lvt_3_1_, lvt_4_1_, lvt_5_1_);
            IBlockState lvt_7_1_ = lvt_2_1_.getBlockState(lvt_6_1_);
            Block lvt_8_1_ = lvt_7_1_.getBlock();

            if (EntityEnderman.carriableBlocks.contains(lvt_8_1_))
            {
                this.enderman.setHeldBlockState(lvt_7_1_);
                lvt_2_1_.setBlockState(lvt_6_1_, Blocks.air.getDefaultState());
            }
        }
    }
}
