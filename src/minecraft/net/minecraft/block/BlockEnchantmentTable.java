package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer
{
    protected BlockEnchantmentTable()
    {
        super(Material.rock, MapColor.redColor);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public boolean isFullCube()
    {
        return false;
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.randomDisplayTick(worldIn, pos, state, rand);

        for (int lvt_5_1_ = -2; lvt_5_1_ <= 2; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = -2; lvt_6_1_ <= 2; ++lvt_6_1_)
            {
                if (lvt_5_1_ > -2 && lvt_5_1_ < 2 && lvt_6_1_ == -1)
                {
                    lvt_6_1_ = 2;
                }

                if (rand.nextInt(16) == 0)
                {
                    for (int lvt_7_1_ = 0; lvt_7_1_ <= 1; ++lvt_7_1_)
                    {
                        BlockPos lvt_8_1_ = pos.add(lvt_5_1_, lvt_7_1_, lvt_6_1_);

                        if (worldIn.getBlockState(lvt_8_1_).getBlock() == Blocks.bookshelf)
                        {
                            if (!worldIn.isAirBlock(pos.add(lvt_5_1_ / 2, 0, lvt_6_1_ / 2)))
                            {
                                break;
                            }

                            worldIn.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, (double)pos.getX() + 0.5D, (double)pos.getY() + 2.0D, (double)pos.getZ() + 0.5D, (double)((float)lvt_5_1_ + rand.nextFloat()) - 0.5D, (double)((float)lvt_7_1_ - rand.nextFloat() - 1.0F), (double)((float)lvt_6_1_ + rand.nextFloat()) - 0.5D, new int[0]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
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
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityEnchantmentTable();
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

            if (lvt_9_1_ instanceof TileEntityEnchantmentTable)
            {
                playerIn.displayGui((TileEntityEnchantmentTable)lvt_9_1_);
            }

            return true;
        }
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

            if (lvt_6_1_ instanceof TileEntityEnchantmentTable)
            {
                ((TileEntityEnchantmentTable)lvt_6_1_).setCustomName(stack.getDisplayName());
            }
        }
    }
}
