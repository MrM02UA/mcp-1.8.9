package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityRainFX extends EntityFX
{
    protected EntityRainFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.30000001192092896D;
        this.motionY = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
        this.motionZ *= 0.30000001192092896D;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(19 + this.rand.nextInt(4));
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.particleMaxAge-- <= 0)
        {
            this.setDead();
        }

        if (this.onGround)
        {
            if (Math.random() < 0.5D)
            {
                this.setDead();
            }

            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

        BlockPos lvt_1_1_ = new BlockPos(this);
        IBlockState lvt_2_1_ = this.worldObj.getBlockState(lvt_1_1_);
        Block lvt_3_1_ = lvt_2_1_.getBlock();
        lvt_3_1_.setBlockBoundsBasedOnState(this.worldObj, lvt_1_1_);
        Material lvt_4_1_ = lvt_2_1_.getBlock().getMaterial();

        if (lvt_4_1_.isLiquid() || lvt_4_1_.isSolid())
        {
            double lvt_5_1_ = 0.0D;

            if (lvt_2_1_.getBlock() instanceof BlockLiquid)
            {
                lvt_5_1_ = (double)(1.0F - BlockLiquid.getLiquidHeightPercent(((Integer)lvt_2_1_.getValue(BlockLiquid.LEVEL)).intValue()));
            }
            else
            {
                lvt_5_1_ = lvt_3_1_.getBlockBoundsMaxY();
            }

            double lvt_7_1_ = (double)MathHelper.floor_double(this.posY) + lvt_5_1_;

            if (this.posY < lvt_7_1_)
            {
                this.setDead();
            }
        }
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntityRainFX(worldIn, xCoordIn, yCoordIn, zCoordIn);
        }
    }
}
