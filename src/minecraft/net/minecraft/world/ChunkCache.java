package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess
{
    protected int chunkX;
    protected int chunkZ;
    protected Chunk[][] chunkArray;

    /** set by !chunk.getAreLevelsEmpty */
    protected boolean hasExtendedLevels;

    /** Reference to the World object. */
    protected World worldObj;

    public ChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
    {
        this.worldObj = worldIn;
        this.chunkX = posFromIn.getX() - subIn >> 4;
        this.chunkZ = posFromIn.getZ() - subIn >> 4;
        int lvt_5_1_ = posToIn.getX() + subIn >> 4;
        int lvt_6_1_ = posToIn.getZ() + subIn >> 4;
        this.chunkArray = new Chunk[lvt_5_1_ - this.chunkX + 1][lvt_6_1_ - this.chunkZ + 1];
        this.hasExtendedLevels = true;

        for (int lvt_7_1_ = this.chunkX; lvt_7_1_ <= lvt_5_1_; ++lvt_7_1_)
        {
            for (int lvt_8_1_ = this.chunkZ; lvt_8_1_ <= lvt_6_1_; ++lvt_8_1_)
            {
                this.chunkArray[lvt_7_1_ - this.chunkX][lvt_8_1_ - this.chunkZ] = worldIn.getChunkFromChunkCoords(lvt_7_1_, lvt_8_1_);
            }
        }

        for (int lvt_7_2_ = posFromIn.getX() >> 4; lvt_7_2_ <= posToIn.getX() >> 4; ++lvt_7_2_)
        {
            for (int lvt_8_2_ = posFromIn.getZ() >> 4; lvt_8_2_ <= posToIn.getZ() >> 4; ++lvt_8_2_)
            {
                Chunk lvt_9_1_ = this.chunkArray[lvt_7_2_ - this.chunkX][lvt_8_2_ - this.chunkZ];

                if (lvt_9_1_ != null && !lvt_9_1_.getAreLevelsEmpty(posFromIn.getY(), posToIn.getY()))
                {
                    this.hasExtendedLevels = false;
                }
            }
        }
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache()
    {
        return this.hasExtendedLevels;
    }

    public TileEntity getTileEntity(BlockPos pos)
    {
        int lvt_2_1_ = (pos.getX() >> 4) - this.chunkX;
        int lvt_3_1_ = (pos.getZ() >> 4) - this.chunkZ;
        return this.chunkArray[lvt_2_1_][lvt_3_1_].getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
    }

    public int getCombinedLight(BlockPos pos, int lightValue)
    {
        int lvt_3_1_ = this.getLightForExt(EnumSkyBlock.SKY, pos);
        int lvt_4_1_ = this.getLightForExt(EnumSkyBlock.BLOCK, pos);

        if (lvt_4_1_ < lightValue)
        {
            lvt_4_1_ = lightValue;
        }

        return lvt_3_1_ << 20 | lvt_4_1_ << 4;
    }

    public IBlockState getBlockState(BlockPos pos)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            int lvt_2_1_ = (pos.getX() >> 4) - this.chunkX;
            int lvt_3_1_ = (pos.getZ() >> 4) - this.chunkZ;

            if (lvt_2_1_ >= 0 && lvt_2_1_ < this.chunkArray.length && lvt_3_1_ >= 0 && lvt_3_1_ < this.chunkArray[lvt_2_1_].length)
            {
                Chunk lvt_4_1_ = this.chunkArray[lvt_2_1_][lvt_3_1_];

                if (lvt_4_1_ != null)
                {
                    return lvt_4_1_.getBlockState(pos);
                }
            }
        }

        return Blocks.air.getDefaultState();
    }

    public BiomeGenBase getBiomeGenForCoords(BlockPos pos)
    {
        return this.worldObj.getBiomeGenForCoords(pos);
    }

    private int getLightForExt(EnumSkyBlock p_175629_1_, BlockPos pos)
    {
        if (p_175629_1_ == EnumSkyBlock.SKY && this.worldObj.provider.getHasNoSky())
        {
            return 0;
        }
        else if (pos.getY() >= 0 && pos.getY() < 256)
        {
            if (this.getBlockState(pos).getBlock().getUseNeighborBrightness())
            {
                int lvt_3_1_ = 0;

                for (EnumFacing lvt_7_1_ : EnumFacing.values())
                {
                    int lvt_8_1_ = this.getLightFor(p_175629_1_, pos.offset(lvt_7_1_));

                    if (lvt_8_1_ > lvt_3_1_)
                    {
                        lvt_3_1_ = lvt_8_1_;
                    }

                    if (lvt_3_1_ >= 15)
                    {
                        return lvt_3_1_;
                    }
                }

                return lvt_3_1_;
            }
            else
            {
                int lvt_3_2_ = (pos.getX() >> 4) - this.chunkX;
                int lvt_4_2_ = (pos.getZ() >> 4) - this.chunkZ;
                return this.chunkArray[lvt_3_2_][lvt_4_2_].getLightFor(p_175629_1_, pos);
            }
        }
        else
        {
            return p_175629_1_.defaultLightValue;
        }
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    public boolean isAirBlock(BlockPos pos)
    {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
    }

    public int getLightFor(EnumSkyBlock p_175628_1_, BlockPos pos)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            int lvt_3_1_ = (pos.getX() >> 4) - this.chunkX;
            int lvt_4_1_ = (pos.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[lvt_3_1_][lvt_4_1_].getLightFor(p_175628_1_, pos);
        }
        else
        {
            return p_175628_1_.defaultLightValue;
        }
    }

    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        IBlockState lvt_3_1_ = this.getBlockState(pos);
        return lvt_3_1_.getBlock().getStrongPower(this, pos, lvt_3_1_, direction);
    }

    public WorldType getWorldType()
    {
        return this.worldObj.getWorldType();
    }
}
