package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class BlockBeacon extends BlockContainer
{
    public BlockBeacon()
    {
        super(Material.glass, MapColor.diamondColor);
        this.setHardness(3.0F);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityBeacon();
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity lvt_9_1_ = worldIn.getTileEntity(pos);

            if (lvt_9_1_ instanceof TileEntityBeacon)
            {
                playerIn.displayGUIChest((TileEntityBeacon)lvt_9_1_);
                playerIn.triggerAchievement(StatList.field_181730_N);
            }

            return true;
        }
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
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (stack.hasDisplayName())
        {
            TileEntity lvt_6_1_ = worldIn.getTileEntity(pos);

            if (lvt_6_1_ instanceof TileEntityBeacon)
            {
                ((TileEntityBeacon)lvt_6_1_).setName(stack.getDisplayName());
            }
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        TileEntity lvt_5_1_ = worldIn.getTileEntity(pos);

        if (lvt_5_1_ instanceof TileEntityBeacon)
        {
            ((TileEntityBeacon)lvt_5_1_).updateBeacon();
            worldIn.addBlockEvent(pos, this, 1, 0);
        }
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public static void updateColorAsync(final World worldIn, final BlockPos glassPos)
    {
        HttpUtil.field_180193_a.submit(new Runnable()
        {
            public void run()
            {
                Chunk lvt_1_1_ = worldIn.getChunkFromBlockCoords(glassPos);

                for (int lvt_2_1_ = glassPos.getY() - 1; lvt_2_1_ >= 0; --lvt_2_1_)
                {
                    final BlockPos lvt_3_1_ = new BlockPos(glassPos.getX(), lvt_2_1_, glassPos.getZ());

                    if (!lvt_1_1_.canSeeSky(lvt_3_1_))
                    {
                        break;
                    }

                    IBlockState lvt_4_1_ = worldIn.getBlockState(lvt_3_1_);

                    if (lvt_4_1_.getBlock() == Blocks.beacon)
                    {
                        ((WorldServer)worldIn).addScheduledTask(new Runnable()
                        {
                            public void run()
                            {
                                TileEntity lvt_1_1_ = worldIn.getTileEntity(lvt_3_1_);

                                if (lvt_1_1_ instanceof TileEntityBeacon)
                                {
                                    ((TileEntityBeacon)lvt_1_1_).updateBeacon();
                                    worldIn.addBlockEvent(lvt_3_1_, Blocks.beacon, 1, 0);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
