package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid
{
    protected BlockStaticLiquid(Material materialIn)
    {
        super(materialIn);
        this.setTickRandomly(false);

        if (materialIn == Material.lava)
        {
            this.setTickRandomly(true);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!this.checkForMixing(worldIn, pos, state))
        {
            this.updateLiquid(worldIn, pos, state);
        }
    }

    private void updateLiquid(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockDynamicLiquid lvt_4_1_ = getFlowingBlock(this.blockMaterial);
        worldIn.setBlockState(pos, lvt_4_1_.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
        worldIn.scheduleUpdate(pos, lvt_4_1_, this.tickRate(worldIn));
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.blockMaterial == Material.lava)
        {
            if (worldIn.getGameRules().getBoolean("doFireTick"))
            {
                int lvt_5_1_ = rand.nextInt(3);

                if (lvt_5_1_ > 0)
                {
                    BlockPos lvt_6_1_ = pos;

                    for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_5_1_; ++lvt_7_1_)
                    {
                        lvt_6_1_ = lvt_6_1_.add(rand.nextInt(3) - 1, 1, rand.nextInt(3) - 1);
                        Block lvt_8_1_ = worldIn.getBlockState(lvt_6_1_).getBlock();

                        if (lvt_8_1_.blockMaterial == Material.air)
                        {
                            if (this.isSurroundingBlockFlammable(worldIn, lvt_6_1_))
                            {
                                worldIn.setBlockState(lvt_6_1_, Blocks.fire.getDefaultState());
                                return;
                            }
                        }
                        else if (lvt_8_1_.blockMaterial.blocksMovement())
                        {
                            return;
                        }
                    }
                }
                else
                {
                    for (int lvt_6_2_ = 0; lvt_6_2_ < 3; ++lvt_6_2_)
                    {
                        BlockPos lvt_7_2_ = pos.add(rand.nextInt(3) - 1, 0, rand.nextInt(3) - 1);

                        if (worldIn.isAirBlock(lvt_7_2_.up()) && this.getCanBlockBurn(worldIn, lvt_7_2_))
                        {
                            worldIn.setBlockState(lvt_7_2_.up(), Blocks.fire.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    protected boolean isSurroundingBlockFlammable(World worldIn, BlockPos pos)
    {
        for (EnumFacing lvt_6_1_ : EnumFacing.values())
        {
            if (this.getCanBlockBurn(worldIn, pos.offset(lvt_6_1_)))
            {
                return true;
            }
        }

        return false;
    }

    private boolean getCanBlockBurn(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().getMaterial().getCanBurn();
    }
}
