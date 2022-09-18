package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotion extends EntityThrowable
{
    /**
     * The damage value of the thrown potion that this EntityPotion represents.
     */
    private ItemStack potionDamage;

    public EntityPotion(World worldIn)
    {
        super(worldIn);
    }

    public EntityPotion(World worldIn, EntityLivingBase throwerIn, int meta)
    {
        this(worldIn, throwerIn, new ItemStack(Items.potionitem, 1, meta));
    }

    public EntityPotion(World worldIn, EntityLivingBase throwerIn, ItemStack potionDamageIn)
    {
        super(worldIn, throwerIn);
        this.potionDamage = potionDamageIn;
    }

    public EntityPotion(World worldIn, double x, double y, double z, int p_i1791_8_)
    {
        this(worldIn, x, y, z, new ItemStack(Items.potionitem, 1, p_i1791_8_));
    }

    public EntityPotion(World worldIn, double x, double y, double z, ItemStack potionDamageIn)
    {
        super(worldIn, x, y, z);
        this.potionDamage = potionDamageIn;
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.05F;
    }

    protected float getVelocity()
    {
        return 0.5F;
    }

    protected float getInaccuracy()
    {
        return -20.0F;
    }

    /**
     * Sets the PotionEffect by the given id of the potion effect.
     */
    public void setPotionDamage(int potionId)
    {
        if (this.potionDamage == null)
        {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }

        this.potionDamage.setItemDamage(potionId);
    }

    /**
     * Returns the damage value of the thrown potion that this EntityPotion represents.
     */
    public int getPotionDamage()
    {
        if (this.potionDamage == null)
        {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }

        return this.potionDamage.getMetadata();
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition p_70184_1_)
    {
        if (!this.worldObj.isRemote)
        {
            List<PotionEffect> lvt_2_1_ = Items.potionitem.getEffects(this.potionDamage);

            if (lvt_2_1_ != null && !lvt_2_1_.isEmpty())
            {
                AxisAlignedBB lvt_3_1_ = this.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D);
                List<EntityLivingBase> lvt_4_1_ = this.worldObj.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, lvt_3_1_);

                if (!lvt_4_1_.isEmpty())
                {
                    for (EntityLivingBase lvt_6_1_ : lvt_4_1_)
                    {
                        double lvt_7_1_ = this.getDistanceSqToEntity(lvt_6_1_);

                        if (lvt_7_1_ < 16.0D)
                        {
                            double lvt_9_1_ = 1.0D - Math.sqrt(lvt_7_1_) / 4.0D;

                            if (lvt_6_1_ == p_70184_1_.entityHit)
                            {
                                lvt_9_1_ = 1.0D;
                            }

                            for (PotionEffect lvt_12_1_ : lvt_2_1_)
                            {
                                int lvt_13_1_ = lvt_12_1_.getPotionID();

                                if (Potion.potionTypes[lvt_13_1_].isInstant())
                                {
                                    Potion.potionTypes[lvt_13_1_].affectEntity(this, this.getThrower(), lvt_6_1_, lvt_12_1_.getAmplifier(), lvt_9_1_);
                                }
                                else
                                {
                                    int lvt_14_1_ = (int)(lvt_9_1_ * (double)lvt_12_1_.getDuration() + 0.5D);

                                    if (lvt_14_1_ > 20)
                                    {
                                        lvt_6_1_.addPotionEffect(new PotionEffect(lvt_13_1_, lvt_14_1_, lvt_12_1_.getAmplifier()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.worldObj.playAuxSFX(2002, new BlockPos(this), this.getPotionDamage());
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.hasKey("Potion", 10))
        {
            this.potionDamage = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("Potion"));
        }
        else
        {
            this.setPotionDamage(tagCompund.getInteger("potionValue"));
        }

        if (this.potionDamage == null)
        {
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);

        if (this.potionDamage != null)
        {
            tagCompound.setTag("Potion", this.potionDamage.writeToNBT(new NBTTagCompound()));
        }
    }
}
