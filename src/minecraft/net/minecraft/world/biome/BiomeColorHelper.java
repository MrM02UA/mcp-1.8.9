package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper
{
    private static final BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition)
        {
            return biome.getGrassColorAtPos(blockPosition);
        }
    };
    private static final BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition)
        {
            return biome.getFoliageColorAtPos(blockPosition);
        }
    };
    private static final BiomeColorHelper.ColorResolver WATER_COLOR_MULTIPLIER = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition)
        {
            return biome.waterColorMultiplier;
        }
    };

    private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver)
    {
        int lvt_3_1_ = 0;
        int lvt_4_1_ = 0;
        int lvt_5_1_ = 0;

        for (BlockPos.MutableBlockPos lvt_7_1_ : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1)))
        {
            int lvt_8_1_ = colorResolver.getColorAtPos(blockAccess.getBiomeGenForCoords(lvt_7_1_), lvt_7_1_);
            lvt_3_1_ += (lvt_8_1_ & 16711680) >> 16;
            lvt_4_1_ += (lvt_8_1_ & 65280) >> 8;
            lvt_5_1_ += lvt_8_1_ & 255;
        }

        return (lvt_3_1_ / 9 & 255) << 16 | (lvt_4_1_ / 9 & 255) << 8 | lvt_5_1_ / 9 & 255;
    }

    public static int getGrassColorAtPos(IBlockAccess p_180286_0_, BlockPos p_180286_1_)
    {
        return getColorAtPos(p_180286_0_, p_180286_1_, GRASS_COLOR);
    }

    public static int getFoliageColorAtPos(IBlockAccess p_180287_0_, BlockPos p_180287_1_)
    {
        return getColorAtPos(p_180287_0_, p_180287_1_, FOLIAGE_COLOR);
    }

    public static int getWaterColorAtPos(IBlockAccess p_180288_0_, BlockPos p_180288_1_)
    {
        return getColorAtPos(p_180288_0_, p_180288_1_, WATER_COLOR_MULTIPLIER);
    }

    interface ColorResolver
    {
        int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition);
    }
}
