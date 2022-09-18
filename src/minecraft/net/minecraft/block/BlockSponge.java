package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

public class BlockSponge extends Block
{
    public static final PropertyBool WET = PropertyBool.create("wet");

    protected BlockSponge()
    {
        super(Material.sponge);
        this.setDefaultState(this.blockState.getBaseState().withProperty(WET, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".dry.name");
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((Boolean)state.getValue(WET)).booleanValue() ? 1 : 0;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.tryAbsorb(worldIn, pos, state);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        this.tryAbsorb(worldIn, pos, state);
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
    }

    protected void tryAbsorb(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!((Boolean)state.getValue(WET)).booleanValue() && this.absorb(worldIn, pos))
        {
            worldIn.setBlockState(pos, state.withProperty(WET, Boolean.valueOf(true)), 2);
            worldIn.playAuxSFX(2001, pos, Block.getIdFromBlock(Blocks.water));
        }
    }

    private boolean absorb(World worldIn, BlockPos pos)
    {
        Queue<Tuple<BlockPos, Integer>> lvt_3_1_ = Lists.newLinkedList();
        ArrayList<BlockPos> lvt_4_1_ = Lists.newArrayList();
        lvt_3_1_.add(new Tuple(pos, Integer.valueOf(0)));
        int lvt_5_1_ = 0;

        while (!((Queue)lvt_3_1_).isEmpty())
        {
            Tuple<BlockPos, Integer> lvt_6_1_ = (Tuple)lvt_3_1_.poll();
            BlockPos lvt_7_1_ = (BlockPos)lvt_6_1_.getFirst();
            int lvt_8_1_ = ((Integer)lvt_6_1_.getSecond()).intValue();

            for (EnumFacing lvt_12_1_ : EnumFacing.values())
            {
                BlockPos lvt_13_1_ = lvt_7_1_.offset(lvt_12_1_);

                if (worldIn.getBlockState(lvt_13_1_).getBlock().getMaterial() == Material.water)
                {
                    worldIn.setBlockState(lvt_13_1_, Blocks.air.getDefaultState(), 2);
                    lvt_4_1_.add(lvt_13_1_);
                    ++lvt_5_1_;

                    if (lvt_8_1_ < 6)
                    {
                        lvt_3_1_.add(new Tuple(lvt_13_1_, Integer.valueOf(lvt_8_1_ + 1)));
                    }
                }
            }

            if (lvt_5_1_ > 64)
            {
                break;
            }
        }

        for (BlockPos lvt_7_2_ : lvt_4_1_)
        {
            worldIn.notifyNeighborsOfStateChange(lvt_7_2_, Blocks.air);
        }

        return lvt_5_1_ > 0;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(WET, Boolean.valueOf((meta & 1) == 1));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Boolean)state.getValue(WET)).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {WET});
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (((Boolean)state.getValue(WET)).booleanValue())
        {
            EnumFacing lvt_5_1_ = EnumFacing.random(rand);

            if (lvt_5_1_ != EnumFacing.UP && !World.doesBlockHaveSolidTopSurface(worldIn, pos.offset(lvt_5_1_)))
            {
                double lvt_6_1_ = (double)pos.getX();
                double lvt_8_1_ = (double)pos.getY();
                double lvt_10_1_ = (double)pos.getZ();

                if (lvt_5_1_ == EnumFacing.DOWN)
                {
                    lvt_8_1_ = lvt_8_1_ - 0.05D;
                    lvt_6_1_ += rand.nextDouble();
                    lvt_10_1_ += rand.nextDouble();
                }
                else
                {
                    lvt_8_1_ = lvt_8_1_ + rand.nextDouble() * 0.8D;

                    if (lvt_5_1_.getAxis() == EnumFacing.Axis.X)
                    {
                        lvt_10_1_ += rand.nextDouble();

                        if (lvt_5_1_ == EnumFacing.EAST)
                        {
                            ++lvt_6_1_;
                        }
                        else
                        {
                            lvt_6_1_ += 0.05D;
                        }
                    }
                    else
                    {
                        lvt_6_1_ += rand.nextDouble();

                        if (lvt_5_1_ == EnumFacing.SOUTH)
                        {
                            ++lvt_10_1_;
                        }
                        else
                        {
                            lvt_10_1_ += 0.05D;
                        }
                    }
                }

                worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, lvt_6_1_, lvt_8_1_, lvt_10_1_, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }
}
