package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntitySilverfish extends EntityMob
{
    private EntitySilverfish.AISummonSilverfish summonSilverfish;

    public EntitySilverfish(World worldIn)
    {
        super(worldIn);
        this.setSize(0.4F, 0.3F);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, this.summonSilverfish = new EntitySilverfish.AISummonSilverfish(this));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(5, new EntitySilverfish.AIHideInStone(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return 0.2D;
    }

    public float getEyeHeight()
    {
        return 0.1F;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.silverfish.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.silverfish.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.silverfish.kill";
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
            if (source instanceof EntityDamageSource || source == DamageSource.magic)
            {
                this.summonSilverfish.func_179462_f();
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound("mob.silverfish.step", 0.15F, 1.0F);
    }

    protected Item getDropItem()
    {
        return null;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    public float getBlockPathWeight(BlockPos pos)
    {
        return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.stone ? 10.0F : super.getBlockPathWeight(pos);
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        if (super.getCanSpawnHere())
        {
            EntityPlayer lvt_1_1_ = this.worldObj.getClosestPlayerToEntity(this, 5.0D);
            return lvt_1_1_ == null;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    static class AIHideInStone extends EntityAIWander
    {
        private final EntitySilverfish silverfish;
        private EnumFacing facing;
        private boolean field_179484_c;

        public AIHideInStone(EntitySilverfish silverfishIn)
        {
            super(silverfishIn, 1.0D, 10);
            this.silverfish = silverfishIn;
            this.setMutexBits(1);
        }

        public boolean shouldExecute()
        {
            if (this.silverfish.getAttackTarget() != null)
            {
                return false;
            }
            else if (!this.silverfish.getNavigator().noPath())
            {
                return false;
            }
            else
            {
                Random lvt_1_1_ = this.silverfish.getRNG();

                if (lvt_1_1_.nextInt(10) == 0)
                {
                    this.facing = EnumFacing.random(lvt_1_1_);
                    BlockPos lvt_2_1_ = (new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5D, this.silverfish.posZ)).offset(this.facing);
                    IBlockState lvt_3_1_ = this.silverfish.worldObj.getBlockState(lvt_2_1_);

                    if (BlockSilverfish.canContainSilverfish(lvt_3_1_))
                    {
                        this.field_179484_c = true;
                        return true;
                    }
                }

                this.field_179484_c = false;
                return super.shouldExecute();
            }
        }

        public boolean continueExecuting()
        {
            return this.field_179484_c ? false : super.continueExecuting();
        }

        public void startExecuting()
        {
            if (!this.field_179484_c)
            {
                super.startExecuting();
            }
            else
            {
                World lvt_1_1_ = this.silverfish.worldObj;
                BlockPos lvt_2_1_ = (new BlockPos(this.silverfish.posX, this.silverfish.posY + 0.5D, this.silverfish.posZ)).offset(this.facing);
                IBlockState lvt_3_1_ = lvt_1_1_.getBlockState(lvt_2_1_);

                if (BlockSilverfish.canContainSilverfish(lvt_3_1_))
                {
                    lvt_1_1_.setBlockState(lvt_2_1_, Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.forModelBlock(lvt_3_1_)), 3);
                    this.silverfish.spawnExplosionParticle();
                    this.silverfish.setDead();
                }
            }
        }
    }

    static class AISummonSilverfish extends EntityAIBase
    {
        private EntitySilverfish silverfish;
        private int field_179463_b;

        public AISummonSilverfish(EntitySilverfish silverfishIn)
        {
            this.silverfish = silverfishIn;
        }

        public void func_179462_f()
        {
            if (this.field_179463_b == 0)
            {
                this.field_179463_b = 20;
            }
        }

        public boolean shouldExecute()
        {
            return this.field_179463_b > 0;
        }

        public void updateTask()
        {
            --this.field_179463_b;

            if (this.field_179463_b <= 0)
            {
                World lvt_1_1_ = this.silverfish.worldObj;
                Random lvt_2_1_ = this.silverfish.getRNG();
                BlockPos lvt_3_1_ = new BlockPos(this.silverfish);

                for (int lvt_4_1_ = 0; lvt_4_1_ <= 5 && lvt_4_1_ >= -5; lvt_4_1_ = lvt_4_1_ <= 0 ? 1 - lvt_4_1_ : 0 - lvt_4_1_)
                {
                    for (int lvt_5_1_ = 0; lvt_5_1_ <= 10 && lvt_5_1_ >= -10; lvt_5_1_ = lvt_5_1_ <= 0 ? 1 - lvt_5_1_ : 0 - lvt_5_1_)
                    {
                        for (int lvt_6_1_ = 0; lvt_6_1_ <= 10 && lvt_6_1_ >= -10; lvt_6_1_ = lvt_6_1_ <= 0 ? 1 - lvt_6_1_ : 0 - lvt_6_1_)
                        {
                            BlockPos lvt_7_1_ = lvt_3_1_.add(lvt_5_1_, lvt_4_1_, lvt_6_1_);
                            IBlockState lvt_8_1_ = lvt_1_1_.getBlockState(lvt_7_1_);

                            if (lvt_8_1_.getBlock() == Blocks.monster_egg)
                            {
                                if (lvt_1_1_.getGameRules().getBoolean("mobGriefing"))
                                {
                                    lvt_1_1_.destroyBlock(lvt_7_1_, true);
                                }
                                else
                                {
                                    lvt_1_1_.setBlockState(lvt_7_1_, ((BlockSilverfish.EnumType)lvt_8_1_.getValue(BlockSilverfish.VARIANT)).getModelBlock(), 3);
                                }

                                if (lvt_2_1_.nextBoolean())
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
