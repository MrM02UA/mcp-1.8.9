package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    public static final PropertyBool FLIP = PropertyBool.create("flip");
    public static final PropertyBool ALT = PropertyBool.create("alt");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyInteger UPPER = PropertyInteger.create("upper", 0, 2);
    private final Map<Block, Integer> encouragements = Maps.newIdentityHashMap();
    private final Map<Block, Integer> flammabilities = Maps.newIdentityHashMap();

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int lvt_4_1_ = pos.getX();
        int lvt_5_1_ = pos.getY();
        int lvt_6_1_ = pos.getZ();

        if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !Blocks.fire.canCatchFire(worldIn, pos.down()))
        {
            boolean lvt_7_1_ = (lvt_4_1_ + lvt_5_1_ + lvt_6_1_ & 1) == 1;
            boolean lvt_8_1_ = (lvt_4_1_ / 2 + lvt_5_1_ / 2 + lvt_6_1_ / 2 & 1) == 1;
            int lvt_9_1_ = 0;

            if (this.canCatchFire(worldIn, pos.up()))
            {
                lvt_9_1_ = lvt_7_1_ ? 1 : 2;
            }

            return state.withProperty(NORTH, Boolean.valueOf(this.canCatchFire(worldIn, pos.north()))).withProperty(EAST, Boolean.valueOf(this.canCatchFire(worldIn, pos.east()))).withProperty(SOUTH, Boolean.valueOf(this.canCatchFire(worldIn, pos.south()))).withProperty(WEST, Boolean.valueOf(this.canCatchFire(worldIn, pos.west()))).withProperty(UPPER, Integer.valueOf(lvt_9_1_)).withProperty(FLIP, Boolean.valueOf(lvt_8_1_)).withProperty(ALT, Boolean.valueOf(lvt_7_1_));
        }
        else
        {
            return this.getDefaultState();
        }
    }

    protected BlockFire()
    {
        super(Material.fire);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(FLIP, Boolean.valueOf(false)).withProperty(ALT, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(UPPER, Integer.valueOf(0)));
        this.setTickRandomly(true);
    }

    public static void init()
    {
        Blocks.fire.setFireInfo(Blocks.planks, 5, 20);
        Blocks.fire.setFireInfo(Blocks.double_wooden_slab, 5, 20);
        Blocks.fire.setFireInfo(Blocks.wooden_slab, 5, 20);
        Blocks.fire.setFireInfo(Blocks.oak_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.spruce_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.birch_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.jungle_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.dark_oak_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.acacia_fence_gate, 5, 20);
        Blocks.fire.setFireInfo(Blocks.oak_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.spruce_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.birch_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.jungle_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.dark_oak_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.acacia_fence, 5, 20);
        Blocks.fire.setFireInfo(Blocks.oak_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.birch_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.spruce_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.jungle_stairs, 5, 20);
        Blocks.fire.setFireInfo(Blocks.log, 5, 5);
        Blocks.fire.setFireInfo(Blocks.log2, 5, 5);
        Blocks.fire.setFireInfo(Blocks.leaves, 30, 60);
        Blocks.fire.setFireInfo(Blocks.leaves2, 30, 60);
        Blocks.fire.setFireInfo(Blocks.bookshelf, 30, 20);
        Blocks.fire.setFireInfo(Blocks.tnt, 15, 100);
        Blocks.fire.setFireInfo(Blocks.tallgrass, 60, 100);
        Blocks.fire.setFireInfo(Blocks.double_plant, 60, 100);
        Blocks.fire.setFireInfo(Blocks.yellow_flower, 60, 100);
        Blocks.fire.setFireInfo(Blocks.red_flower, 60, 100);
        Blocks.fire.setFireInfo(Blocks.deadbush, 60, 100);
        Blocks.fire.setFireInfo(Blocks.wool, 30, 60);
        Blocks.fire.setFireInfo(Blocks.vine, 15, 100);
        Blocks.fire.setFireInfo(Blocks.coal_block, 5, 5);
        Blocks.fire.setFireInfo(Blocks.hay_block, 60, 20);
        Blocks.fire.setFireInfo(Blocks.carpet, 60, 20);
    }

    public void setFireInfo(Block blockIn, int encouragement, int flammability)
    {
        this.encouragements.put(blockIn, Integer.valueOf(encouragement));
        this.flammabilities.put(blockIn, Integer.valueOf(flammability));
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 30;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.getGameRules().getBoolean("doFireTick"))
        {
            if (!this.canPlaceBlockAt(worldIn, pos))
            {
                worldIn.setBlockToAir(pos);
            }

            Block lvt_5_1_ = worldIn.getBlockState(pos.down()).getBlock();
            boolean lvt_6_1_ = lvt_5_1_ == Blocks.netherrack;

            if (worldIn.provider instanceof WorldProviderEnd && lvt_5_1_ == Blocks.bedrock)
            {
                lvt_6_1_ = true;
            }

            if (!lvt_6_1_ && worldIn.isRaining() && this.canDie(worldIn, pos))
            {
                worldIn.setBlockToAir(pos);
            }
            else
            {
                int lvt_7_1_ = ((Integer)state.getValue(AGE)).intValue();

                if (lvt_7_1_ < 15)
                {
                    state = state.withProperty(AGE, Integer.valueOf(lvt_7_1_ + rand.nextInt(3) / 2));
                    worldIn.setBlockState(pos, state, 4);
                }

                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));

                if (!lvt_6_1_)
                {
                    if (!this.canNeighborCatchFire(worldIn, pos))
                    {
                        if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || lvt_7_1_ > 3)
                        {
                            worldIn.setBlockToAir(pos);
                        }

                        return;
                    }

                    if (!this.canCatchFire(worldIn, pos.down()) && lvt_7_1_ == 15 && rand.nextInt(4) == 0)
                    {
                        worldIn.setBlockToAir(pos);
                        return;
                    }
                }

                boolean lvt_8_1_ = worldIn.isBlockinHighHumidity(pos);
                int lvt_9_1_ = 0;

                if (lvt_8_1_)
                {
                    lvt_9_1_ = -50;
                }

                this.catchOnFire(worldIn, pos.east(), 300 + lvt_9_1_, rand, lvt_7_1_);
                this.catchOnFire(worldIn, pos.west(), 300 + lvt_9_1_, rand, lvt_7_1_);
                this.catchOnFire(worldIn, pos.down(), 250 + lvt_9_1_, rand, lvt_7_1_);
                this.catchOnFire(worldIn, pos.up(), 250 + lvt_9_1_, rand, lvt_7_1_);
                this.catchOnFire(worldIn, pos.north(), 300 + lvt_9_1_, rand, lvt_7_1_);
                this.catchOnFire(worldIn, pos.south(), 300 + lvt_9_1_, rand, lvt_7_1_);

                for (int lvt_10_1_ = -1; lvt_10_1_ <= 1; ++lvt_10_1_)
                {
                    for (int lvt_11_1_ = -1; lvt_11_1_ <= 1; ++lvt_11_1_)
                    {
                        for (int lvt_12_1_ = -1; lvt_12_1_ <= 4; ++lvt_12_1_)
                        {
                            if (lvt_10_1_ != 0 || lvt_12_1_ != 0 || lvt_11_1_ != 0)
                            {
                                int lvt_13_1_ = 100;

                                if (lvt_12_1_ > 1)
                                {
                                    lvt_13_1_ += (lvt_12_1_ - 1) * 100;
                                }

                                BlockPos lvt_14_1_ = pos.add(lvt_10_1_, lvt_12_1_, lvt_11_1_);
                                int lvt_15_1_ = this.getNeighborEncouragement(worldIn, lvt_14_1_);

                                if (lvt_15_1_ > 0)
                                {
                                    int lvt_16_1_ = (lvt_15_1_ + 40 + worldIn.getDifficulty().getDifficultyId() * 7) / (lvt_7_1_ + 30);

                                    if (lvt_8_1_)
                                    {
                                        lvt_16_1_ /= 2;
                                    }

                                    if (lvt_16_1_ > 0 && rand.nextInt(lvt_13_1_) <= lvt_16_1_ && (!worldIn.isRaining() || !this.canDie(worldIn, lvt_14_1_)))
                                    {
                                        int lvt_17_1_ = lvt_7_1_ + rand.nextInt(5) / 4;

                                        if (lvt_17_1_ > 15)
                                        {
                                            lvt_17_1_ = 15;
                                        }

                                        worldIn.setBlockState(lvt_14_1_, state.withProperty(AGE, Integer.valueOf(lvt_17_1_)), 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean canDie(World worldIn, BlockPos pos)
    {
        return worldIn.isRainingAt(pos) || worldIn.isRainingAt(pos.west()) || worldIn.isRainingAt(pos.east()) || worldIn.isRainingAt(pos.north()) || worldIn.isRainingAt(pos.south());
    }

    public boolean requiresUpdates()
    {
        return false;
    }

    private int getFlammability(Block blockIn)
    {
        Integer lvt_2_1_ = (Integer)this.flammabilities.get(blockIn);
        return lvt_2_1_ == null ? 0 : lvt_2_1_.intValue();
    }

    private int getEncouragement(Block blockIn)
    {
        Integer lvt_2_1_ = (Integer)this.encouragements.get(blockIn);
        return lvt_2_1_ == null ? 0 : lvt_2_1_.intValue();
    }

    private void catchOnFire(World worldIn, BlockPos pos, int chance, Random random, int age)
    {
        int lvt_6_1_ = this.getFlammability(worldIn.getBlockState(pos).getBlock());

        if (random.nextInt(chance) < lvt_6_1_)
        {
            IBlockState lvt_7_1_ = worldIn.getBlockState(pos);

            if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos))
            {
                int lvt_8_1_ = age + random.nextInt(5) / 4;

                if (lvt_8_1_ > 15)
                {
                    lvt_8_1_ = 15;
                }

                worldIn.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(lvt_8_1_)), 3);
            }
            else
            {
                worldIn.setBlockToAir(pos);
            }

            if (lvt_7_1_.getBlock() == Blocks.tnt)
            {
                Blocks.tnt.onBlockDestroyedByPlayer(worldIn, pos, lvt_7_1_.withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
            }
        }
    }

    private boolean canNeighborCatchFire(World worldIn, BlockPos pos)
    {
        for (EnumFacing lvt_6_1_ : EnumFacing.values())
        {
            if (this.canCatchFire(worldIn, pos.offset(lvt_6_1_)))
            {
                return true;
            }
        }

        return false;
    }

    private int getNeighborEncouragement(World worldIn, BlockPos pos)
    {
        if (!worldIn.isAirBlock(pos))
        {
            return 0;
        }
        else
        {
            int lvt_3_1_ = 0;

            for (EnumFacing lvt_7_1_ : EnumFacing.values())
            {
                lvt_3_1_ = Math.max(this.getEncouragement(worldIn.getBlockState(pos.offset(lvt_7_1_)).getBlock()), lvt_3_1_);
            }

            return lvt_3_1_;
        }
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return false;
    }

    /**
     * Checks if the block can be caught on fire
     */
    public boolean canCatchFire(IBlockAccess worldIn, BlockPos pos)
    {
        return this.getEncouragement(worldIn.getBlockState(pos).getBlock()) > 0;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || this.canNeighborCatchFire(worldIn, pos);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !this.canNeighborCatchFire(worldIn, pos))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (worldIn.provider.getDimensionId() > 0 || !Blocks.portal.func_176548_d(worldIn, pos))
        {
            if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !this.canNeighborCatchFire(worldIn, pos))
            {
                worldIn.setBlockToAir(pos);
            }
            else
            {
                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
            }
        }
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (rand.nextInt(24) == 0)
        {
            worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }

        if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !Blocks.fire.canCatchFire(worldIn, pos.down()))
        {
            if (Blocks.fire.canCatchFire(worldIn, pos.west()))
            {
                for (int lvt_5_2_ = 0; lvt_5_2_ < 2; ++lvt_5_2_)
                {
                    double lvt_6_2_ = (double)pos.getX() + rand.nextDouble() * 0.10000000149011612D;
                    double lvt_8_2_ = (double)pos.getY() + rand.nextDouble();
                    double lvt_10_2_ = (double)pos.getZ() + rand.nextDouble();
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, lvt_6_2_, lvt_8_2_, lvt_10_2_, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, pos.east()))
            {
                for (int lvt_5_3_ = 0; lvt_5_3_ < 2; ++lvt_5_3_)
                {
                    double lvt_6_3_ = (double)(pos.getX() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    double lvt_8_3_ = (double)pos.getY() + rand.nextDouble();
                    double lvt_10_3_ = (double)pos.getZ() + rand.nextDouble();
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, lvt_6_3_, lvt_8_3_, lvt_10_3_, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, pos.north()))
            {
                for (int lvt_5_4_ = 0; lvt_5_4_ < 2; ++lvt_5_4_)
                {
                    double lvt_6_4_ = (double)pos.getX() + rand.nextDouble();
                    double lvt_8_4_ = (double)pos.getY() + rand.nextDouble();
                    double lvt_10_4_ = (double)pos.getZ() + rand.nextDouble() * 0.10000000149011612D;
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, lvt_6_4_, lvt_8_4_, lvt_10_4_, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, pos.south()))
            {
                for (int lvt_5_5_ = 0; lvt_5_5_ < 2; ++lvt_5_5_)
                {
                    double lvt_6_5_ = (double)pos.getX() + rand.nextDouble();
                    double lvt_8_5_ = (double)pos.getY() + rand.nextDouble();
                    double lvt_10_5_ = (double)(pos.getZ() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, lvt_6_5_, lvt_8_5_, lvt_10_5_, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, pos.up()))
            {
                for (int lvt_5_6_ = 0; lvt_5_6_ < 2; ++lvt_5_6_)
                {
                    double lvt_6_6_ = (double)pos.getX() + rand.nextDouble();
                    double lvt_8_6_ = (double)(pos.getY() + 1) - rand.nextDouble() * 0.10000000149011612D;
                    double lvt_10_6_ = (double)pos.getZ() + rand.nextDouble();
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, lvt_6_6_, lvt_8_6_, lvt_10_6_, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }
        }
        else
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < 3; ++lvt_5_1_)
            {
                double lvt_6_1_ = (double)pos.getX() + rand.nextDouble();
                double lvt_8_1_ = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
                double lvt_10_1_ = (double)pos.getZ() + rand.nextDouble();
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, lvt_6_1_, lvt_8_1_, lvt_10_1_, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return MapColor.tntColor;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {AGE, NORTH, EAST, SOUTH, WEST, UPPER, FLIP, ALT});
    }
}
