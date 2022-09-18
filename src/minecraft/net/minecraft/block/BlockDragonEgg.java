package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block
{
    public BlockDragonEgg()
    {
        super(Material.dragonEgg, MapColor.blackColor);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
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
        this.checkFall(worldIn, pos);
    }

    private void checkFall(World worldIn, BlockPos pos)
    {
        if (BlockFalling.canFallInto(worldIn, pos.down()) && pos.getY() >= 0)
        {
            int lvt_3_1_ = 32;

            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-lvt_3_1_, -lvt_3_1_, -lvt_3_1_), pos.add(lvt_3_1_, lvt_3_1_, lvt_3_1_)))
            {
                worldIn.spawnEntityInWorld(new EntityFallingBlock(worldIn, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), this.getDefaultState()));
            }
            else
            {
                worldIn.setBlockToAir(pos);
                BlockPos lvt_4_1_;

                for (lvt_4_1_ = pos; BlockFalling.canFallInto(worldIn, lvt_4_1_) && lvt_4_1_.getY() > 0; lvt_4_1_ = lvt_4_1_.down())
                {
                    ;
                }

                if (lvt_4_1_.getY() > 0)
                {
                    worldIn.setBlockState(lvt_4_1_, this.getDefaultState(), 2);
                }
            }
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        this.teleport(worldIn, pos);
        return true;
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        this.teleport(worldIn, pos);
    }

    private void teleport(World worldIn, BlockPos pos)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(pos);

        if (lvt_3_1_.getBlock() == this)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < 1000; ++lvt_4_1_)
            {
                BlockPos lvt_5_1_ = pos.add(worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16), worldIn.rand.nextInt(8) - worldIn.rand.nextInt(8), worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16));

                if (worldIn.getBlockState(lvt_5_1_).getBlock().blockMaterial == Material.air)
                {
                    if (worldIn.isRemote)
                    {
                        for (int lvt_6_1_ = 0; lvt_6_1_ < 128; ++lvt_6_1_)
                        {
                            double lvt_7_1_ = worldIn.rand.nextDouble();
                            float lvt_9_1_ = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                            float lvt_10_1_ = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                            float lvt_11_1_ = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                            double lvt_12_1_ = (double)lvt_5_1_.getX() + (double)(pos.getX() - lvt_5_1_.getX()) * lvt_7_1_ + (worldIn.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            double lvt_14_1_ = (double)lvt_5_1_.getY() + (double)(pos.getY() - lvt_5_1_.getY()) * lvt_7_1_ + worldIn.rand.nextDouble() * 1.0D - 0.5D;
                            double lvt_16_1_ = (double)lvt_5_1_.getZ() + (double)(pos.getZ() - lvt_5_1_.getZ()) * lvt_7_1_ + (worldIn.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            worldIn.spawnParticle(EnumParticleTypes.PORTAL, lvt_12_1_, lvt_14_1_, lvt_16_1_, (double)lvt_9_1_, (double)lvt_10_1_, (double)lvt_11_1_, new int[0]);
                        }
                    }
                    else
                    {
                        worldIn.setBlockState(lvt_5_1_, lvt_3_1_, 2);
                        worldIn.setBlockToAir(pos);
                    }

                    return;
                }
            }
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 5;
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

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return null;
    }
}
