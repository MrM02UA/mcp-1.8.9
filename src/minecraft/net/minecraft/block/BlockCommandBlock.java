package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer
{
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");

    public BlockCommandBlock()
    {
        super(Material.iron, MapColor.adobeColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, Boolean.valueOf(false)));
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityCommandBlock();
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            boolean lvt_5_1_ = worldIn.isBlockPowered(pos);
            boolean lvt_6_1_ = ((Boolean)state.getValue(TRIGGERED)).booleanValue();

            if (lvt_5_1_ && !lvt_6_1_)
            {
                worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            }
            else if (!lvt_5_1_ && lvt_6_1_)
            {
                worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        TileEntity lvt_5_1_ = worldIn.getTileEntity(pos);

        if (lvt_5_1_ instanceof TileEntityCommandBlock)
        {
            ((TileEntityCommandBlock)lvt_5_1_).getCommandBlockLogic().trigger(worldIn);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 1;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity lvt_9_1_ = worldIn.getTileEntity(pos);
        return lvt_9_1_ instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)lvt_9_1_).getCommandBlockLogic().tryOpenEditCommandBlock(playerIn) : false;
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        TileEntity lvt_3_1_ = worldIn.getTileEntity(pos);
        return lvt_3_1_ instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)lvt_3_1_).getCommandBlockLogic().getSuccessCount() : 0;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity lvt_6_1_ = worldIn.getTileEntity(pos);

        if (lvt_6_1_ instanceof TileEntityCommandBlock)
        {
            CommandBlockLogic lvt_7_1_ = ((TileEntityCommandBlock)lvt_6_1_).getCommandBlockLogic();

            if (stack.hasDisplayName())
            {
                lvt_7_1_.setName(stack.getDisplayName());
            }

            if (!worldIn.isRemote)
            {
                lvt_7_1_.setTrackOutput(worldIn.getGameRules().getBoolean("sendCommandFeedback"));
            }
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf((meta & 1) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;

        if (((Boolean)state.getValue(TRIGGERED)).booleanValue())
        {
            lvt_2_1_ |= 1;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {TRIGGERED});
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf(false));
    }
}
