package net.minecraft.entity.monster;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob
{
    public EntitySnowman(World worldIn)
    {
        super(worldIn);
        this.setSize(0.7F, 1.9F);
        ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25D, 20, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, true, false, IMob.mobSelector));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote)
        {
            int lvt_1_1_ = MathHelper.floor_double(this.posX);
            int lvt_2_1_ = MathHelper.floor_double(this.posY);
            int lvt_3_1_ = MathHelper.floor_double(this.posZ);

            if (this.isWet())
            {
                this.attackEntityFrom(DamageSource.drown, 1.0F);
            }

            if (this.worldObj.getBiomeGenForCoords(new BlockPos(lvt_1_1_, 0, lvt_3_1_)).getFloatTemperature(new BlockPos(lvt_1_1_, lvt_2_1_, lvt_3_1_)) > 1.0F)
            {
                this.attackEntityFrom(DamageSource.onFire, 1.0F);
            }

            for (int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_)
            {
                lvt_1_1_ = MathHelper.floor_double(this.posX + (double)((float)(lvt_4_1_ % 2 * 2 - 1) * 0.25F));
                lvt_2_1_ = MathHelper.floor_double(this.posY);
                lvt_3_1_ = MathHelper.floor_double(this.posZ + (double)((float)(lvt_4_1_ / 2 % 2 * 2 - 1) * 0.25F));
                BlockPos lvt_5_1_ = new BlockPos(lvt_1_1_, lvt_2_1_, lvt_3_1_);

                if (this.worldObj.getBlockState(lvt_5_1_).getBlock().getMaterial() == Material.air && this.worldObj.getBiomeGenForCoords(new BlockPos(lvt_1_1_, 0, lvt_3_1_)).getFloatTemperature(lvt_5_1_) < 0.8F && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, lvt_5_1_))
                {
                    this.worldObj.setBlockState(lvt_5_1_, Blocks.snow_layer.getDefaultState());
                }
            }
        }
    }

    protected Item getDropItem()
    {
        return Items.snowball;
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
        int lvt_3_1_ = this.rand.nextInt(16);

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_)
        {
            this.dropItem(Items.snowball, 1);
        }
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_)
    {
        EntitySnowball lvt_3_1_ = new EntitySnowball(this.worldObj, this);
        double lvt_4_1_ = target.posY + (double)target.getEyeHeight() - 1.100000023841858D;
        double lvt_6_1_ = target.posX - this.posX;
        double lvt_8_1_ = lvt_4_1_ - lvt_3_1_.posY;
        double lvt_10_1_ = target.posZ - this.posZ;
        float lvt_12_1_ = MathHelper.sqrt_double(lvt_6_1_ * lvt_6_1_ + lvt_10_1_ * lvt_10_1_) * 0.2F;
        lvt_3_1_.setThrowableHeading(lvt_6_1_, lvt_8_1_ + (double)lvt_12_1_, lvt_10_1_, 1.6F, 12.0F);
        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(lvt_3_1_);
    }

    public float getEyeHeight()
    {
        return 1.7F;
    }
}
