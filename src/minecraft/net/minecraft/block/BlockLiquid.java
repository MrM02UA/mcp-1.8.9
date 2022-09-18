package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLiquid extends Block
{
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

    protected BlockLiquid(Material materialIn)
    {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setTickRandomly(true);
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return this.blockMaterial != Material.lava;
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return this.blockMaterial == Material.water ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 16777215;
    }

    /**
     * Returns the percentage of the liquid block that is air, based on the given flow decay of the liquid
     */
    public static float getLiquidHeightPercent(int meta)
    {
        if (meta >= 8)
        {
            meta = 0;
        }

        return (float)(meta + 1) / 9.0F;
    }

    protected int getLevel(IBlockAccess worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? ((Integer)worldIn.getBlockState(pos).getValue(LEVEL)).intValue() : -1;
    }

    protected int getEffectiveFlowDecay(IBlockAccess worldIn, BlockPos pos)
    {
        int lvt_3_1_ = this.getLevel(worldIn, pos);
        return lvt_3_1_ >= 8 ? 0 : lvt_3_1_;
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return hitIfLiquid && ((Integer)state.getValue(LEVEL)).intValue() == 0;
    }

    /**
     * Whether this Block is solid on the given Side
     */
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        Material lvt_4_1_ = worldIn.getBlockState(pos).getBlock().getMaterial();
        return lvt_4_1_ == this.blockMaterial ? false : (side == EnumFacing.UP ? true : (lvt_4_1_ == Material.ice ? false : super.isBlockSolid(worldIn, pos, side)));
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? false : (side == EnumFacing.UP ? true : super.shouldSideBeRendered(worldIn, pos, side));
    }

    public boolean shouldRenderSides(IBlockAccess blockAccess, BlockPos pos)
    {
        for (int lvt_3_1_ = -1; lvt_3_1_ <= 1; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = -1; lvt_4_1_ <= 1; ++lvt_4_1_)
            {
                IBlockState lvt_5_1_ = blockAccess.getBlockState(pos.add(lvt_3_1_, 0, lvt_4_1_));
                Block lvt_6_1_ = lvt_5_1_.getBlock();
                Material lvt_7_1_ = lvt_6_1_.getMaterial();

                if (lvt_7_1_ != this.blockMaterial && !lvt_6_1_.isFullBlock())
                {
                    return true;
                }
            }
        }

        return false;
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 1;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    protected Vec3 getFlowVector(IBlockAccess worldIn, BlockPos pos)
    {
        Vec3 lvt_3_1_ = new Vec3(0.0D, 0.0D, 0.0D);
        int lvt_4_1_ = this.getEffectiveFlowDecay(worldIn, pos);

        for (EnumFacing lvt_6_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos lvt_7_1_ = pos.offset(lvt_6_1_);
            int lvt_8_1_ = this.getEffectiveFlowDecay(worldIn, lvt_7_1_);

            if (lvt_8_1_ < 0)
            {
                if (!worldIn.getBlockState(lvt_7_1_).getBlock().getMaterial().blocksMovement())
                {
                    lvt_8_1_ = this.getEffectiveFlowDecay(worldIn, lvt_7_1_.down());

                    if (lvt_8_1_ >= 0)
                    {
                        int lvt_9_1_ = lvt_8_1_ - (lvt_4_1_ - 8);
                        lvt_3_1_ = lvt_3_1_.addVector((double)((lvt_7_1_.getX() - pos.getX()) * lvt_9_1_), (double)((lvt_7_1_.getY() - pos.getY()) * lvt_9_1_), (double)((lvt_7_1_.getZ() - pos.getZ()) * lvt_9_1_));
                    }
                }
            }
            else if (lvt_8_1_ >= 0)
            {
                int lvt_9_2_ = lvt_8_1_ - lvt_4_1_;
                lvt_3_1_ = lvt_3_1_.addVector((double)((lvt_7_1_.getX() - pos.getX()) * lvt_9_2_), (double)((lvt_7_1_.getY() - pos.getY()) * lvt_9_2_), (double)((lvt_7_1_.getZ() - pos.getZ()) * lvt_9_2_));
            }
        }

        if (((Integer)worldIn.getBlockState(pos).getValue(LEVEL)).intValue() >= 8)
        {
            for (EnumFacing lvt_6_2_ : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos lvt_7_2_ = pos.offset(lvt_6_2_);

                if (this.isBlockSolid(worldIn, lvt_7_2_, lvt_6_2_) || this.isBlockSolid(worldIn, lvt_7_2_.up(), lvt_6_2_))
                {
                    lvt_3_1_ = lvt_3_1_.normalize().addVector(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return lvt_3_1_.normalize();
    }

    public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion)
    {
        return motion.add(this.getFlowVector(worldIn, pos));
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.getHasNoSky() ? 10 : 30) : 0);
    }

    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
    {
        int lvt_3_1_ = worldIn.getCombinedLight(pos, 0);
        int lvt_4_1_ = worldIn.getCombinedLight(pos.up(), 0);
        int lvt_5_1_ = lvt_3_1_ & 255;
        int lvt_6_1_ = lvt_4_1_ & 255;
        int lvt_7_1_ = lvt_3_1_ >> 16 & 255;
        int lvt_8_1_ = lvt_4_1_ >> 16 & 255;
        return (lvt_5_1_ > lvt_6_1_ ? lvt_5_1_ : lvt_6_1_) | (lvt_7_1_ > lvt_8_1_ ? lvt_7_1_ : lvt_8_1_) << 16;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return this.blockMaterial == Material.water ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        double lvt_5_1_ = (double)pos.getX();
        double lvt_7_1_ = (double)pos.getY();
        double lvt_9_1_ = (double)pos.getZ();

        if (this.blockMaterial == Material.water)
        {
            int lvt_11_1_ = ((Integer)state.getValue(LEVEL)).intValue();

            if (lvt_11_1_ > 0 && lvt_11_1_ < 8)
            {
                if (rand.nextInt(64) == 0)
                {
                    worldIn.playSound(lvt_5_1_ + 0.5D, lvt_7_1_ + 0.5D, lvt_9_1_ + 0.5D, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F, false);
                }
            }
            else if (rand.nextInt(10) == 0)
            {
                worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, lvt_5_1_ + (double)rand.nextFloat(), lvt_7_1_ + (double)rand.nextFloat(), lvt_9_1_ + (double)rand.nextFloat(), 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }

        if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.up()).getBlock().getMaterial() == Material.air && !worldIn.getBlockState(pos.up()).getBlock().isOpaqueCube())
        {
            if (rand.nextInt(100) == 0)
            {
                double lvt_11_2_ = lvt_5_1_ + (double)rand.nextFloat();
                double lvt_13_1_ = lvt_7_1_ + this.maxY;
                double lvt_15_1_ = lvt_9_1_ + (double)rand.nextFloat();
                worldIn.spawnParticle(EnumParticleTypes.LAVA, lvt_11_2_, lvt_13_1_, lvt_15_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                worldIn.playSound(lvt_11_2_, lvt_13_1_, lvt_15_1_, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
            }

            if (rand.nextInt(200) == 0)
            {
                worldIn.playSound(lvt_5_1_, lvt_7_1_, lvt_9_1_, "liquid.lava", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
            }
        }

        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
        {
            Material lvt_11_3_ = worldIn.getBlockState(pos.down(2)).getBlock().getMaterial();

            if (!lvt_11_3_.blocksMovement() && !lvt_11_3_.isLiquid())
            {
                double lvt_12_1_ = lvt_5_1_ + (double)rand.nextFloat();
                double lvt_14_1_ = lvt_7_1_ - 1.05D;
                double lvt_16_1_ = lvt_9_1_ + (double)rand.nextFloat();

                if (this.blockMaterial == Material.water)
                {
                    worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, lvt_12_1_, lvt_14_1_, lvt_16_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                }
                else
                {
                    worldIn.spawnParticle(EnumParticleTypes.DRIP_LAVA, lvt_12_1_, lvt_14_1_, lvt_16_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }
        }
    }

    public static double getFlowDirection(IBlockAccess worldIn, BlockPos pos, Material materialIn)
    {
        Vec3 lvt_3_1_ = getFlowingBlock(materialIn).getFlowVector(worldIn, pos);
        return lvt_3_1_.xCoord == 0.0D && lvt_3_1_.zCoord == 0.0D ? -1000.0D : MathHelper.atan2(lvt_3_1_.zCoord, lvt_3_1_.xCoord) - (Math.PI / 2D);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.checkForMixing(worldIn, pos, state);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        this.checkForMixing(worldIn, pos, state);
    }

    public boolean checkForMixing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.blockMaterial == Material.lava)
        {
            boolean lvt_4_1_ = false;

            for (EnumFacing lvt_8_1_ : EnumFacing.values())
            {
                if (lvt_8_1_ != EnumFacing.DOWN && worldIn.getBlockState(pos.offset(lvt_8_1_)).getBlock().getMaterial() == Material.water)
                {
                    lvt_4_1_ = true;
                    break;
                }
            }

            if (lvt_4_1_)
            {
                Integer lvt_5_2_ = (Integer)state.getValue(LEVEL);

                if (lvt_5_2_.intValue() == 0)
                {
                    worldIn.setBlockState(pos, Blocks.obsidian.getDefaultState());
                    this.triggerMixEffects(worldIn, pos);
                    return true;
                }

                if (lvt_5_2_.intValue() <= 4)
                {
                    worldIn.setBlockState(pos, Blocks.cobblestone.getDefaultState());
                    this.triggerMixEffects(worldIn, pos);
                    return true;
                }
            }
        }

        return false;
    }

    protected void triggerMixEffects(World worldIn, BlockPos pos)
    {
        double lvt_3_1_ = (double)pos.getX();
        double lvt_5_1_ = (double)pos.getY();
        double lvt_7_1_ = (double)pos.getZ();
        worldIn.playSoundEffect(lvt_3_1_ + 0.5D, lvt_5_1_ + 0.5D, lvt_7_1_ + 0.5D, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

        for (int lvt_9_1_ = 0; lvt_9_1_ < 8; ++lvt_9_1_)
        {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, lvt_3_1_ + Math.random(), lvt_5_1_ + 1.2D, lvt_7_1_ + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LEVEL)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {LEVEL});
    }

    public static BlockDynamicLiquid getFlowingBlock(Material materialIn)
    {
        if (materialIn == Material.water)
        {
            return Blocks.flowing_water;
        }
        else if (materialIn == Material.lava)
        {
            return Blocks.flowing_lava;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public static BlockStaticLiquid getStaticBlock(Material materialIn)
    {
        if (materialIn == Material.water)
        {
            return Blocks.water;
        }
        else if (materialIn == Material.lava)
        {
            return Blocks.lava;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }
}
