package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer
{
    private static final List<String> INSTRUMENTS = Lists.newArrayList(new String[] {"harp", "bd", "snare", "hat", "bassattack"});

    public BlockNote()
    {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        boolean lvt_5_1_ = worldIn.isBlockPowered(pos);
        TileEntity lvt_6_1_ = worldIn.getTileEntity(pos);

        if (lvt_6_1_ instanceof TileEntityNote)
        {
            TileEntityNote lvt_7_1_ = (TileEntityNote)lvt_6_1_;

            if (lvt_7_1_.previousRedstoneState != lvt_5_1_)
            {
                if (lvt_5_1_)
                {
                    lvt_7_1_.triggerNote(worldIn, pos);
                }

                lvt_7_1_.previousRedstoneState = lvt_5_1_;
            }
        }
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

            if (lvt_9_1_ instanceof TileEntityNote)
            {
                TileEntityNote lvt_10_1_ = (TileEntityNote)lvt_9_1_;
                lvt_10_1_.changePitch();
                lvt_10_1_.triggerNote(worldIn, pos);
                playerIn.triggerAchievement(StatList.field_181735_S);
            }

            return true;
        }
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        if (!worldIn.isRemote)
        {
            TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);

            if (lvt_4_1_ instanceof TileEntityNote)
            {
                ((TileEntityNote)lvt_4_1_).triggerNote(worldIn, pos);
                playerIn.triggerAchievement(StatList.field_181734_R);
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityNote();
    }

    private String getInstrument(int id)
    {
        if (id < 0 || id >= INSTRUMENTS.size())
        {
            id = 0;
        }

        return (String)INSTRUMENTS.get(id);
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        float lvt_6_1_ = (float)Math.pow(2.0D, (double)(eventParam - 12) / 12.0D);
        worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "note." + this.getInstrument(eventID), 3.0F, lvt_6_1_);
        worldIn.spawnParticle(EnumParticleTypes.NOTE, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.2D, (double)pos.getZ() + 0.5D, (double)eventParam / 24.0D, 0.0D, 0.0D, new int[0]);
        return true;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }
}
