package net.minecraft.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity
{
    /** Note to play */
    public byte note;

    /** stores the latest redstone state */
    public boolean previousRedstoneState;

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setByte("note", this.note);
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.note = compound.getByte("note");
        this.note = (byte)MathHelper.clamp_int(this.note, 0, 24);
    }

    /**
     * change pitch by -> (currentPitch + 1) % 25
     */
    public void changePitch()
    {
        this.note = (byte)((this.note + 1) % 25);
        this.markDirty();
    }

    public void triggerNote(World worldIn, BlockPos p_175108_2_)
    {
        if (worldIn.getBlockState(p_175108_2_.up()).getBlock().getMaterial() == Material.air)
        {
            Material lvt_3_1_ = worldIn.getBlockState(p_175108_2_.down()).getBlock().getMaterial();
            int lvt_4_1_ = 0;

            if (lvt_3_1_ == Material.rock)
            {
                lvt_4_1_ = 1;
            }

            if (lvt_3_1_ == Material.sand)
            {
                lvt_4_1_ = 2;
            }

            if (lvt_3_1_ == Material.glass)
            {
                lvt_4_1_ = 3;
            }

            if (lvt_3_1_ == Material.wood)
            {
                lvt_4_1_ = 4;
            }

            worldIn.addBlockEvent(p_175108_2_, Blocks.noteblock, lvt_4_1_, this.note);
        }
    }
}
