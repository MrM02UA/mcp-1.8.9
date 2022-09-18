package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch
{
    private static Map<World, List<BlockRedstoneTorch.Toggle>> toggles = Maps.newHashMap();
    private final boolean isOn;

    private boolean isBurnedOut(World worldIn, BlockPos pos, boolean turnOff)
    {
        if (!toggles.containsKey(worldIn))
        {
            toggles.put(worldIn, Lists.newArrayList());
        }

        List<BlockRedstoneTorch.Toggle> lvt_4_1_ = (List)toggles.get(worldIn);

        if (turnOff)
        {
            lvt_4_1_.add(new BlockRedstoneTorch.Toggle(pos, worldIn.getTotalWorldTime()));
        }

        int lvt_5_1_ = 0;

        for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_4_1_.size(); ++lvt_6_1_)
        {
            BlockRedstoneTorch.Toggle lvt_7_1_ = (BlockRedstoneTorch.Toggle)lvt_4_1_.get(lvt_6_1_);

            if (lvt_7_1_.pos.equals(pos))
            {
                ++lvt_5_1_;

                if (lvt_5_1_ >= 8)
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockRedstoneTorch(boolean isOn)
    {
        this.isOn = isOn;
        this.setTickRandomly(true);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 2;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.isOn)
        {
            for (EnumFacing lvt_7_1_ : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(lvt_7_1_), this);
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.isOn)
        {
            for (EnumFacing lvt_7_1_ : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(lvt_7_1_), this);
            }
        }
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return this.isOn && state.getValue(FACING) != side ? 15 : 0;
    }

    private boolean shouldBeOff(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing lvt_4_1_ = ((EnumFacing)state.getValue(FACING)).getOpposite();
        return worldIn.isSidePowered(pos.offset(lvt_4_1_), lvt_4_1_);
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        boolean lvt_5_1_ = this.shouldBeOff(worldIn, pos, state);
        List<BlockRedstoneTorch.Toggle> lvt_6_1_ = (List)toggles.get(worldIn);

        while (lvt_6_1_ != null && !lvt_6_1_.isEmpty() && worldIn.getTotalWorldTime() - ((BlockRedstoneTorch.Toggle)lvt_6_1_.get(0)).time > 60L)
        {
            lvt_6_1_.remove(0);
        }

        if (this.isOn)
        {
            if (lvt_5_1_)
            {
                worldIn.setBlockState(pos, Blocks.unlit_redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);

                if (this.isBurnedOut(worldIn, pos, true))
                {
                    worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int lvt_7_1_ = 0; lvt_7_1_ < 5; ++lvt_7_1_)
                    {
                        double lvt_8_1_ = (double)pos.getX() + rand.nextDouble() * 0.6D + 0.2D;
                        double lvt_10_1_ = (double)pos.getY() + rand.nextDouble() * 0.6D + 0.2D;
                        double lvt_12_1_ = (double)pos.getZ() + rand.nextDouble() * 0.6D + 0.2D;
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_8_1_, lvt_10_1_, lvt_12_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    }

                    worldIn.scheduleUpdate(pos, worldIn.getBlockState(pos).getBlock(), 160);
                }
            }
        }
        else if (!lvt_5_1_ && !this.isBurnedOut(worldIn, pos, false))
        {
            worldIn.setBlockState(pos, Blocks.redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!this.onNeighborChangeInternal(worldIn, pos, state))
        {
            if (this.isOn == this.shouldBeOff(worldIn, pos, state))
            {
                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            }
        }
    }

    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return side == EnumFacing.DOWN ? this.getWeakPower(worldIn, pos, state, side) : 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isOn)
        {
            double lvt_5_1_ = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            double lvt_7_1_ = (double)pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
            double lvt_9_1_ = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            EnumFacing lvt_11_1_ = (EnumFacing)state.getValue(FACING);

            if (lvt_11_1_.getAxis().isHorizontal())
            {
                EnumFacing lvt_12_1_ = lvt_11_1_.getOpposite();
                double lvt_13_1_ = 0.27D;
                lvt_5_1_ += 0.27D * (double)lvt_12_1_.getFrontOffsetX();
                lvt_7_1_ += 0.22D;
                lvt_9_1_ += 0.27D * (double)lvt_12_1_.getFrontOffsetZ();
            }

            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, lvt_5_1_, lvt_7_1_, lvt_9_1_, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    public boolean isAssociatedBlock(Block other)
    {
        return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
    }

    static class Toggle
    {
        BlockPos pos;
        long time;

        public Toggle(BlockPos pos, long time)
        {
            this.pos = pos;
            this.time = time;
        }
    }
}
