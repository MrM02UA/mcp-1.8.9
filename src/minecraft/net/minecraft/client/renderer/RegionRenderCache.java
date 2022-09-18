package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class RegionRenderCache extends ChunkCache
{
    private static final IBlockState DEFAULT_STATE = Blocks.air.getDefaultState();
    private final BlockPos position;
    private int[] combinedLights;
    private IBlockState[] blockStates;

    public RegionRenderCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
    {
        super(worldIn, posFromIn, posToIn, subIn);
        this.position = posFromIn.subtract(new Vec3i(subIn, subIn, subIn));
        int lvt_5_1_ = 8000;
        this.combinedLights = new int[8000];
        Arrays.fill(this.combinedLights, -1);
        this.blockStates = new IBlockState[8000];
    }

    public TileEntity getTileEntity(BlockPos pos)
    {
        int lvt_2_1_ = (pos.getX() >> 4) - this.chunkX;
        int lvt_3_1_ = (pos.getZ() >> 4) - this.chunkZ;
        return this.chunkArray[lvt_2_1_][lvt_3_1_].getTileEntity(pos, Chunk.EnumCreateEntityType.QUEUED);
    }

    public int getCombinedLight(BlockPos pos, int lightValue)
    {
        int lvt_3_1_ = this.getPositionIndex(pos);
        int lvt_4_1_ = this.combinedLights[lvt_3_1_];

        if (lvt_4_1_ == -1)
        {
            lvt_4_1_ = super.getCombinedLight(pos, lightValue);
            this.combinedLights[lvt_3_1_] = lvt_4_1_;
        }

        return lvt_4_1_;
    }

    public IBlockState getBlockState(BlockPos pos)
    {
        int lvt_2_1_ = this.getPositionIndex(pos);
        IBlockState lvt_3_1_ = this.blockStates[lvt_2_1_];

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = this.getBlockStateRaw(pos);
            this.blockStates[lvt_2_1_] = lvt_3_1_;
        }

        return lvt_3_1_;
    }

    private IBlockState getBlockStateRaw(BlockPos pos)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            int lvt_2_1_ = (pos.getX() >> 4) - this.chunkX;
            int lvt_3_1_ = (pos.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[lvt_2_1_][lvt_3_1_].getBlockState(pos);
        }
        else
        {
            return DEFAULT_STATE;
        }
    }

    private int getPositionIndex(BlockPos p_175630_1_)
    {
        int lvt_2_1_ = p_175630_1_.getX() - this.position.getX();
        int lvt_3_1_ = p_175630_1_.getY() - this.position.getY();
        int lvt_4_1_ = p_175630_1_.getZ() - this.position.getZ();
        return lvt_2_1_ * 400 + lvt_4_1_ * 20 + lvt_3_1_;
    }
}
