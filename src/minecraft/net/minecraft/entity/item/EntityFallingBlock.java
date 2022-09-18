package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity
{
    private IBlockState fallTile;
    public int fallTime;
    public boolean shouldDropItem = true;
    private boolean canSetAsBlock;
    private boolean hurtEntities;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0F;
    public NBTTagCompound tileEntityData;

    public EntityFallingBlock(World worldIn)
    {
        super(worldIn);
    }

    public EntityFallingBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState)
    {
        super(worldIn);
        this.fallTile = fallingBlockState;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        Block lvt_1_1_ = this.fallTile.getBlock();

        if (lvt_1_1_.getMaterial() == Material.air)
        {
            this.setDead();
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.fallTime++ == 0)
            {
                BlockPos lvt_2_1_ = new BlockPos(this);

                if (this.worldObj.getBlockState(lvt_2_1_).getBlock() == lvt_1_1_)
                {
                    this.worldObj.setBlockToAir(lvt_2_1_);
                }
                else if (!this.worldObj.isRemote)
                {
                    this.setDead();
                    return;
                }
            }

            this.motionY -= 0.03999999910593033D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;

            if (!this.worldObj.isRemote)
            {
                BlockPos lvt_2_2_ = new BlockPos(this);

                if (this.onGround)
                {
                    this.motionX *= 0.699999988079071D;
                    this.motionZ *= 0.699999988079071D;
                    this.motionY *= -0.5D;

                    if (this.worldObj.getBlockState(lvt_2_2_).getBlock() != Blocks.piston_extension)
                    {
                        this.setDead();

                        if (!this.canSetAsBlock)
                        {
                            if (this.worldObj.canBlockBePlaced(lvt_1_1_, lvt_2_2_, true, EnumFacing.UP, (Entity)null, (ItemStack)null) && !BlockFalling.canFallInto(this.worldObj, lvt_2_2_.down()) && this.worldObj.setBlockState(lvt_2_2_, this.fallTile, 3))
                            {
                                if (lvt_1_1_ instanceof BlockFalling)
                                {
                                    ((BlockFalling)lvt_1_1_).onEndFalling(this.worldObj, lvt_2_2_);
                                }

                                if (this.tileEntityData != null && lvt_1_1_ instanceof ITileEntityProvider)
                                {
                                    TileEntity lvt_3_1_ = this.worldObj.getTileEntity(lvt_2_2_);

                                    if (lvt_3_1_ != null)
                                    {
                                        NBTTagCompound lvt_4_1_ = new NBTTagCompound();
                                        lvt_3_1_.writeToNBT(lvt_4_1_);

                                        for (String lvt_6_1_ : this.tileEntityData.getKeySet())
                                        {
                                            NBTBase lvt_7_1_ = this.tileEntityData.getTag(lvt_6_1_);

                                            if (!lvt_6_1_.equals("x") && !lvt_6_1_.equals("y") && !lvt_6_1_.equals("z"))
                                            {
                                                lvt_4_1_.setTag(lvt_6_1_, lvt_7_1_.copy());
                                            }
                                        }

                                        lvt_3_1_.readFromNBT(lvt_4_1_);
                                        lvt_3_1_.markDirty();
                                    }
                                }
                            }
                            else if (this.shouldDropItem && this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                            {
                                this.entityDropItem(new ItemStack(lvt_1_1_, 1, lvt_1_1_.damageDropped(this.fallTile)), 0.0F);
                            }
                        }
                    }
                }
                else if (this.fallTime > 100 && !this.worldObj.isRemote && (lvt_2_2_.getY() < 1 || lvt_2_2_.getY() > 256) || this.fallTime > 600)
                {
                    if (this.shouldDropItem && this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                    {
                        this.entityDropItem(new ItemStack(lvt_1_1_, 1, lvt_1_1_.damageDropped(this.fallTile)), 0.0F);
                    }

                    this.setDead();
                }
            }
        }
    }

    public void fall(float distance, float damageMultiplier)
    {
        Block lvt_3_1_ = this.fallTile.getBlock();

        if (this.hurtEntities)
        {
            int lvt_4_1_ = MathHelper.ceiling_float_int(distance - 1.0F);

            if (lvt_4_1_ > 0)
            {
                List<Entity> lvt_5_1_ = Lists.newArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));
                boolean lvt_6_1_ = lvt_3_1_ == Blocks.anvil;
                DamageSource lvt_7_1_ = lvt_6_1_ ? DamageSource.anvil : DamageSource.fallingBlock;

                for (Entity lvt_9_1_ : lvt_5_1_)
                {
                    lvt_9_1_.attackEntityFrom(lvt_7_1_, (float)Math.min(MathHelper.floor_float((float)lvt_4_1_ * this.fallHurtAmount), this.fallHurtMax));
                }

                if (lvt_6_1_ && (double)this.rand.nextFloat() < 0.05000000074505806D + (double)lvt_4_1_ * 0.05D)
                {
                    int lvt_8_2_ = ((Integer)this.fallTile.getValue(BlockAnvil.DAMAGE)).intValue();
                    ++lvt_8_2_;

                    if (lvt_8_2_ > 2)
                    {
                        this.canSetAsBlock = true;
                    }
                    else
                    {
                        this.fallTile = this.fallTile.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(lvt_8_2_));
                    }
                }
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        Block lvt_2_1_ = this.fallTile != null ? this.fallTile.getBlock() : Blocks.air;
        ResourceLocation lvt_3_1_ = (ResourceLocation)Block.blockRegistry.getNameForObject(lvt_2_1_);
        tagCompound.setString("Block", lvt_3_1_ == null ? "" : lvt_3_1_.toString());
        tagCompound.setByte("Data", (byte)lvt_2_1_.getMetaFromState(this.fallTile));
        tagCompound.setByte("Time", (byte)this.fallTime);
        tagCompound.setBoolean("DropItem", this.shouldDropItem);
        tagCompound.setBoolean("HurtEntities", this.hurtEntities);
        tagCompound.setFloat("FallHurtAmount", this.fallHurtAmount);
        tagCompound.setInteger("FallHurtMax", this.fallHurtMax);

        if (this.tileEntityData != null)
        {
            tagCompound.setTag("TileEntityData", this.tileEntityData);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        int lvt_2_1_ = tagCompund.getByte("Data") & 255;

        if (tagCompund.hasKey("Block", 8))
        {
            this.fallTile = Block.getBlockFromName(tagCompund.getString("Block")).getStateFromMeta(lvt_2_1_);
        }
        else if (tagCompund.hasKey("TileID", 99))
        {
            this.fallTile = Block.getBlockById(tagCompund.getInteger("TileID")).getStateFromMeta(lvt_2_1_);
        }
        else
        {
            this.fallTile = Block.getBlockById(tagCompund.getByte("Tile") & 255).getStateFromMeta(lvt_2_1_);
        }

        this.fallTime = tagCompund.getByte("Time") & 255;
        Block lvt_3_1_ = this.fallTile.getBlock();

        if (tagCompund.hasKey("HurtEntities", 99))
        {
            this.hurtEntities = tagCompund.getBoolean("HurtEntities");
            this.fallHurtAmount = tagCompund.getFloat("FallHurtAmount");
            this.fallHurtMax = tagCompund.getInteger("FallHurtMax");
        }
        else if (lvt_3_1_ == Blocks.anvil)
        {
            this.hurtEntities = true;
        }

        if (tagCompund.hasKey("DropItem", 99))
        {
            this.shouldDropItem = tagCompund.getBoolean("DropItem");
        }

        if (tagCompund.hasKey("TileEntityData", 10))
        {
            this.tileEntityData = tagCompund.getCompoundTag("TileEntityData");
        }

        if (lvt_3_1_ == null || lvt_3_1_.getMaterial() == Material.air)
        {
            this.fallTile = Blocks.sand.getDefaultState();
        }
    }

    public World getWorldObj()
    {
        return this.worldObj;
    }

    public void setHurtEntities(boolean p_145806_1_)
    {
        this.hurtEntities = p_145806_1_;
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    public boolean canRenderOnFire()
    {
        return false;
    }

    public void addEntityCrashInfo(CrashReportCategory category)
    {
        super.addEntityCrashInfo(category);

        if (this.fallTile != null)
        {
            Block lvt_2_1_ = this.fallTile.getBlock();
            category.addCrashSection("Immitating block ID", Integer.valueOf(Block.getIdFromBlock(lvt_2_1_)));
            category.addCrashSection("Immitating block data", Integer.valueOf(lvt_2_1_.getMetaFromState(this.fallTile)));
        }
    }

    public IBlockState getBlock()
    {
        return this.fallTile;
    }
}
