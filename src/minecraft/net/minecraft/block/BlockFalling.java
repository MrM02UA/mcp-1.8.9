package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockFalling extends Block
{
    public static boolean fallInstantly;

    public BlockFalling()
    {
        super(Material.sand);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public BlockFalling(Material materialIn)
    {
        super(materialIn);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            this.checkFallable(worldIn, pos);
        }
    }

    private void checkFallable(World worldIn, BlockPos pos)
    {
        if (canFallInto(worldIn, pos.down()) && pos.getY() >= 0)
        {
            int lvt_3_1_ = 32;

            if (!fallInstantly && worldIn.isAreaLoaded(pos.add(-lvt_3_1_, -lvt_3_1_, -lvt_3_1_), pos.add(lvt_3_1_, lvt_3_1_, lvt_3_1_)))
            {
                if (!worldIn.isRemote)
                {
                    EntityFallingBlock lvt_4_2_ = new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
                    this.onStartFalling(lvt_4_2_);
                    worldIn.spawnEntityInWorld(lvt_4_2_);
                }
            }
            else
            {
                worldIn.setBlockToAir(pos);
                BlockPos lvt_4_1_;

                for (lvt_4_1_ = pos.down(); canFallInto(worldIn, lvt_4_1_) && lvt_4_1_.getY() > 0; lvt_4_1_ = lvt_4_1_.down())
                {
                    ;
                }

                if (lvt_4_1_.getY() > 0)
                {
                    worldIn.setBlockState(lvt_4_1_.up(), this.getDefaultState());
                }
            }
        }
    }

    protected void onStartFalling(EntityFallingBlock fallingEntity)
    {
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 2;
    }

    public static boolean canFallInto(World worldIn, BlockPos pos)
    {
        Block lvt_2_1_ = worldIn.getBlockState(pos).getBlock();
        Material lvt_3_1_ = lvt_2_1_.blockMaterial;
        return lvt_2_1_ == Blocks.fire || lvt_3_1_ == Material.air || lvt_3_1_ == Material.water || lvt_3_1_ == Material.lava;
    }

    public void onEndFalling(World worldIn, BlockPos pos)
    {
    }
}
