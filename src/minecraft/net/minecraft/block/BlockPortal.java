package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable
{
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis", EnumFacing.Axis.class, new EnumFacing.Axis[] {EnumFacing.Axis.X, EnumFacing.Axis.Z});

    public BlockPortal()
    {
        super(Material.portal, false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
        this.setTickRandomly(true);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);

        if (worldIn.provider.isSurfaceWorld() && worldIn.getGameRules().getBoolean("doMobSpawning") && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId())
        {
            int lvt_5_1_ = pos.getY();
            BlockPos lvt_6_1_;

            for (lvt_6_1_ = pos; !World.doesBlockHaveSolidTopSurface(worldIn, lvt_6_1_) && lvt_6_1_.getY() > 0; lvt_6_1_ = lvt_6_1_.down())
            {
                ;
            }

            if (lvt_5_1_ > 0 && !worldIn.getBlockState(lvt_6_1_.up()).getBlock().isNormalCube())
            {
                Entity lvt_7_1_ = ItemMonsterPlacer.spawnCreature(worldIn, 57, (double)lvt_6_1_.getX() + 0.5D, (double)lvt_6_1_.getY() + 1.1D, (double)lvt_6_1_.getZ() + 0.5D);

                if (lvt_7_1_ != null)
                {
                    lvt_7_1_.timeUntilPortal = lvt_7_1_.getPortalCooldown();
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing.Axis lvt_3_1_ = (EnumFacing.Axis)worldIn.getBlockState(pos).getValue(AXIS);
        float lvt_4_1_ = 0.125F;
        float lvt_5_1_ = 0.125F;

        if (lvt_3_1_ == EnumFacing.Axis.X)
        {
            lvt_4_1_ = 0.5F;
        }

        if (lvt_3_1_ == EnumFacing.Axis.Z)
        {
            lvt_5_1_ = 0.5F;
        }

        this.setBlockBounds(0.5F - lvt_4_1_, 0.0F, 0.5F - lvt_5_1_, 0.5F + lvt_4_1_, 1.0F, 0.5F + lvt_5_1_);
    }

    public static int getMetaForAxis(EnumFacing.Axis axis)
    {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
    }

    public boolean isFullCube()
    {
        return false;
    }

    public boolean func_176548_d(World worldIn, BlockPos p_176548_2_)
    {
        BlockPortal.Size lvt_3_1_ = new BlockPortal.Size(worldIn, p_176548_2_, EnumFacing.Axis.X);

        if (lvt_3_1_.func_150860_b() && lvt_3_1_.field_150864_e == 0)
        {
            lvt_3_1_.func_150859_c();
            return true;
        }
        else
        {
            BlockPortal.Size lvt_4_1_ = new BlockPortal.Size(worldIn, p_176548_2_, EnumFacing.Axis.Z);

            if (lvt_4_1_.func_150860_b() && lvt_4_1_.field_150864_e == 0)
            {
                lvt_4_1_.func_150859_c();
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        EnumFacing.Axis lvt_5_1_ = (EnumFacing.Axis)state.getValue(AXIS);

        if (lvt_5_1_ == EnumFacing.Axis.X)
        {
            BlockPortal.Size lvt_6_1_ = new BlockPortal.Size(worldIn, pos, EnumFacing.Axis.X);

            if (!lvt_6_1_.func_150860_b() || lvt_6_1_.field_150864_e < lvt_6_1_.field_150868_h * lvt_6_1_.field_150862_g)
            {
                worldIn.setBlockState(pos, Blocks.air.getDefaultState());
            }
        }
        else if (lvt_5_1_ == EnumFacing.Axis.Z)
        {
            BlockPortal.Size lvt_6_2_ = new BlockPortal.Size(worldIn, pos, EnumFacing.Axis.Z);

            if (!lvt_6_2_.func_150860_b() || lvt_6_2_.field_150864_e < lvt_6_2_.field_150868_h * lvt_6_2_.field_150862_g)
            {
                worldIn.setBlockState(pos, Blocks.air.getDefaultState());
            }
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        EnumFacing.Axis lvt_4_1_ = null;
        IBlockState lvt_5_1_ = worldIn.getBlockState(pos);

        if (worldIn.getBlockState(pos).getBlock() == this)
        {
            lvt_4_1_ = (EnumFacing.Axis)lvt_5_1_.getValue(AXIS);

            if (lvt_4_1_ == null)
            {
                return false;
            }

            if (lvt_4_1_ == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST)
            {
                return false;
            }

            if (lvt_4_1_ == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH)
            {
                return false;
            }
        }

        boolean lvt_6_1_ = worldIn.getBlockState(pos.west()).getBlock() == this && worldIn.getBlockState(pos.west(2)).getBlock() != this;
        boolean lvt_7_1_ = worldIn.getBlockState(pos.east()).getBlock() == this && worldIn.getBlockState(pos.east(2)).getBlock() != this;
        boolean lvt_8_1_ = worldIn.getBlockState(pos.north()).getBlock() == this && worldIn.getBlockState(pos.north(2)).getBlock() != this;
        boolean lvt_9_1_ = worldIn.getBlockState(pos.south()).getBlock() == this && worldIn.getBlockState(pos.south(2)).getBlock() != this;
        boolean lvt_10_1_ = lvt_6_1_ || lvt_7_1_ || lvt_4_1_ == EnumFacing.Axis.X;
        boolean lvt_11_1_ = lvt_8_1_ || lvt_9_1_ || lvt_4_1_ == EnumFacing.Axis.Z;
        return lvt_10_1_ && side == EnumFacing.WEST ? true : (lvt_10_1_ && side == EnumFacing.EAST ? true : (lvt_11_1_ && side == EnumFacing.NORTH ? true : lvt_11_1_ && side == EnumFacing.SOUTH));
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null)
        {
            entityIn.setPortal(pos);
        }
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (rand.nextInt(100) == 0)
        {
            worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_)
        {
            double lvt_6_1_ = (double)((float)pos.getX() + rand.nextFloat());
            double lvt_8_1_ = (double)((float)pos.getY() + rand.nextFloat());
            double lvt_10_1_ = (double)((float)pos.getZ() + rand.nextFloat());
            double lvt_12_1_ = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double lvt_14_1_ = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            double lvt_16_1_ = ((double)rand.nextFloat() - 0.5D) * 0.5D;
            int lvt_18_1_ = rand.nextInt(2) * 2 - 1;

            if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this)
            {
                lvt_6_1_ = (double)pos.getX() + 0.5D + 0.25D * (double)lvt_18_1_;
                lvt_12_1_ = (double)(rand.nextFloat() * 2.0F * (float)lvt_18_1_);
            }
            else
            {
                lvt_10_1_ = (double)pos.getZ() + 0.5D + 0.25D * (double)lvt_18_1_;
                lvt_16_1_ = (double)(rand.nextFloat() * 2.0F * (float)lvt_18_1_);
            }

            worldIn.spawnParticle(EnumParticleTypes.PORTAL, lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_, lvt_14_1_, lvt_16_1_, new int[0]);
        }
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return null;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return getMetaForAxis((EnumFacing.Axis)state.getValue(AXIS));
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {AXIS});
    }

    public BlockPattern.PatternHelper func_181089_f(World p_181089_1_, BlockPos p_181089_2_)
    {
        EnumFacing.Axis lvt_3_1_ = EnumFacing.Axis.Z;
        BlockPortal.Size lvt_4_1_ = new BlockPortal.Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.X);
        LoadingCache<BlockPos, BlockWorldState> lvt_5_1_ = BlockPattern.func_181627_a(p_181089_1_, true);

        if (!lvt_4_1_.func_150860_b())
        {
            lvt_3_1_ = EnumFacing.Axis.X;
            lvt_4_1_ = new BlockPortal.Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.Z);
        }

        if (!lvt_4_1_.func_150860_b())
        {
            return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, lvt_5_1_, 1, 1, 1);
        }
        else
        {
            int[] lvt_6_1_ = new int[EnumFacing.AxisDirection.values().length];
            EnumFacing lvt_7_1_ = lvt_4_1_.field_150866_c.rotateYCCW();
            BlockPos lvt_8_1_ = lvt_4_1_.field_150861_f.up(lvt_4_1_.func_181100_a() - 1);

            for (EnumFacing.AxisDirection lvt_12_1_ : EnumFacing.AxisDirection.values())
            {
                BlockPattern.PatternHelper lvt_13_1_ = new BlockPattern.PatternHelper(lvt_7_1_.getAxisDirection() == lvt_12_1_ ? lvt_8_1_ : lvt_8_1_.offset(lvt_4_1_.field_150866_c, lvt_4_1_.func_181101_b() - 1), EnumFacing.getFacingFromAxis(lvt_12_1_, lvt_3_1_), EnumFacing.UP, lvt_5_1_, lvt_4_1_.func_181101_b(), lvt_4_1_.func_181100_a(), 1);

                for (int lvt_14_1_ = 0; lvt_14_1_ < lvt_4_1_.func_181101_b(); ++lvt_14_1_)
                {
                    for (int lvt_15_1_ = 0; lvt_15_1_ < lvt_4_1_.func_181100_a(); ++lvt_15_1_)
                    {
                        BlockWorldState lvt_16_1_ = lvt_13_1_.translateOffset(lvt_14_1_, lvt_15_1_, 1);

                        if (lvt_16_1_.getBlockState() != null && lvt_16_1_.getBlockState().getBlock().getMaterial() != Material.air)
                        {
                            ++lvt_6_1_[lvt_12_1_.ordinal()];
                        }
                    }
                }
            }

            EnumFacing.AxisDirection lvt_9_2_ = EnumFacing.AxisDirection.POSITIVE;

            for (EnumFacing.AxisDirection lvt_13_2_ : EnumFacing.AxisDirection.values())
            {
                if (lvt_6_1_[lvt_13_2_.ordinal()] < lvt_6_1_[lvt_9_2_.ordinal()])
                {
                    lvt_9_2_ = lvt_13_2_;
                }
            }

            return new BlockPattern.PatternHelper(lvt_7_1_.getAxisDirection() == lvt_9_2_ ? lvt_8_1_ : lvt_8_1_.offset(lvt_4_1_.field_150866_c, lvt_4_1_.func_181101_b() - 1), EnumFacing.getFacingFromAxis(lvt_9_2_, lvt_3_1_), EnumFacing.UP, lvt_5_1_, lvt_4_1_.func_181101_b(), lvt_4_1_.func_181100_a(), 1);
        }
    }

    public static class Size
    {
        private final World world;
        private final EnumFacing.Axis axis;
        private final EnumFacing field_150866_c;
        private final EnumFacing field_150863_d;
        private int field_150864_e = 0;
        private BlockPos field_150861_f;
        private int field_150862_g;
        private int field_150868_h;

        public Size(World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_)
        {
            this.world = worldIn;
            this.axis = p_i45694_3_;

            if (p_i45694_3_ == EnumFacing.Axis.X)
            {
                this.field_150863_d = EnumFacing.EAST;
                this.field_150866_c = EnumFacing.WEST;
            }
            else
            {
                this.field_150863_d = EnumFacing.NORTH;
                this.field_150866_c = EnumFacing.SOUTH;
            }

            for (BlockPos lvt_4_1_ = p_i45694_2_; p_i45694_2_.getY() > lvt_4_1_.getY() - 21 && p_i45694_2_.getY() > 0 && this.func_150857_a(worldIn.getBlockState(p_i45694_2_.down()).getBlock()); p_i45694_2_ = p_i45694_2_.down())
            {
                ;
            }

            int lvt_5_1_ = this.func_180120_a(p_i45694_2_, this.field_150863_d) - 1;

            if (lvt_5_1_ >= 0)
            {
                this.field_150861_f = p_i45694_2_.offset(this.field_150863_d, lvt_5_1_);
                this.field_150868_h = this.func_180120_a(this.field_150861_f, this.field_150866_c);

                if (this.field_150868_h < 2 || this.field_150868_h > 21)
                {
                    this.field_150861_f = null;
                    this.field_150868_h = 0;
                }
            }

            if (this.field_150861_f != null)
            {
                this.field_150862_g = this.func_150858_a();
            }
        }

        protected int func_180120_a(BlockPos p_180120_1_, EnumFacing p_180120_2_)
        {
            int lvt_3_1_;

            for (lvt_3_1_ = 0; lvt_3_1_ < 22; ++lvt_3_1_)
            {
                BlockPos lvt_4_1_ = p_180120_1_.offset(p_180120_2_, lvt_3_1_);

                if (!this.func_150857_a(this.world.getBlockState(lvt_4_1_).getBlock()) || this.world.getBlockState(lvt_4_1_.down()).getBlock() != Blocks.obsidian)
                {
                    break;
                }
            }

            Block lvt_4_2_ = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, lvt_3_1_)).getBlock();
            return lvt_4_2_ == Blocks.obsidian ? lvt_3_1_ : 0;
        }

        public int func_181100_a()
        {
            return this.field_150862_g;
        }

        public int func_181101_b()
        {
            return this.field_150868_h;
        }

        protected int func_150858_a()
        {
            label24:

            for (this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g)
            {
                for (int lvt_1_1_ = 0; lvt_1_1_ < this.field_150868_h; ++lvt_1_1_)
                {
                    BlockPos lvt_2_1_ = this.field_150861_f.offset(this.field_150866_c, lvt_1_1_).up(this.field_150862_g);
                    Block lvt_3_1_ = this.world.getBlockState(lvt_2_1_).getBlock();

                    if (!this.func_150857_a(lvt_3_1_))
                    {
                        break label24;
                    }

                    if (lvt_3_1_ == Blocks.portal)
                    {
                        ++this.field_150864_e;
                    }

                    if (lvt_1_1_ == 0)
                    {
                        lvt_3_1_ = this.world.getBlockState(lvt_2_1_.offset(this.field_150863_d)).getBlock();

                        if (lvt_3_1_ != Blocks.obsidian)
                        {
                            break label24;
                        }
                    }
                    else if (lvt_1_1_ == this.field_150868_h - 1)
                    {
                        lvt_3_1_ = this.world.getBlockState(lvt_2_1_.offset(this.field_150866_c)).getBlock();

                        if (lvt_3_1_ != Blocks.obsidian)
                        {
                            break label24;
                        }
                    }
                }
            }

            for (int lvt_1_2_ = 0; lvt_1_2_ < this.field_150868_h; ++lvt_1_2_)
            {
                if (this.world.getBlockState(this.field_150861_f.offset(this.field_150866_c, lvt_1_2_).up(this.field_150862_g)).getBlock() != Blocks.obsidian)
                {
                    this.field_150862_g = 0;
                    break;
                }
            }

            if (this.field_150862_g <= 21 && this.field_150862_g >= 3)
            {
                return this.field_150862_g;
            }
            else
            {
                this.field_150861_f = null;
                this.field_150868_h = 0;
                this.field_150862_g = 0;
                return 0;
            }
        }

        protected boolean func_150857_a(Block p_150857_1_)
        {
            return p_150857_1_.blockMaterial == Material.air || p_150857_1_ == Blocks.fire || p_150857_1_ == Blocks.portal;
        }

        public boolean func_150860_b()
        {
            return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
        }

        public void func_150859_c()
        {
            for (int lvt_1_1_ = 0; lvt_1_1_ < this.field_150868_h; ++lvt_1_1_)
            {
                BlockPos lvt_2_1_ = this.field_150861_f.offset(this.field_150866_c, lvt_1_1_);

                for (int lvt_3_1_ = 0; lvt_3_1_ < this.field_150862_g; ++lvt_3_1_)
                {
                    this.world.setBlockState(lvt_2_1_.up(lvt_3_1_), Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
                }
            }
        }
    }
}
