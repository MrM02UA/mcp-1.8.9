package net.minecraft.block.state.pattern;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class BlockPattern
{
    private final Predicate<BlockWorldState>[][][] blockMatches;
    private final int fingerLength;
    private final int thumbLength;
    private final int palmLength;

    public BlockPattern(Predicate<BlockWorldState>[][][] predicatesIn)
    {
        this.blockMatches = predicatesIn;
        this.fingerLength = predicatesIn.length;

        if (this.fingerLength > 0)
        {
            this.thumbLength = predicatesIn[0].length;

            if (this.thumbLength > 0)
            {
                this.palmLength = predicatesIn[0][0].length;
            }
            else
            {
                this.palmLength = 0;
            }
        }
        else
        {
            this.thumbLength = 0;
            this.palmLength = 0;
        }
    }

    public int getThumbLength()
    {
        return this.thumbLength;
    }

    public int getPalmLength()
    {
        return this.palmLength;
    }

    /**
     * checks that the given pattern & rotation is at the block co-ordinates.
     */
    private BlockPattern.PatternHelper checkPatternAt(BlockPos pos, EnumFacing finger, EnumFacing thumb, LoadingCache<BlockPos, BlockWorldState> lcache)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < this.palmLength; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < this.thumbLength; ++lvt_6_1_)
            {
                for (int lvt_7_1_ = 0; lvt_7_1_ < this.fingerLength; ++lvt_7_1_)
                {
                    if (!this.blockMatches[lvt_7_1_][lvt_6_1_][lvt_5_1_].apply(lcache.getUnchecked(translateOffset(pos, finger, thumb, lvt_5_1_, lvt_6_1_, lvt_7_1_))))
                    {
                        return null;
                    }
                }
            }
        }

        return new BlockPattern.PatternHelper(pos, finger, thumb, lcache, this.palmLength, this.thumbLength, this.fingerLength);
    }

    /**
     * Calculates whether the given world position matches the pattern. Warning, fairly heavy function. @return a
     * BlockPattern.PatternHelper if found, null otherwise.
     */
    public BlockPattern.PatternHelper match(World worldIn, BlockPos pos)
    {
        LoadingCache<BlockPos, BlockWorldState> lvt_3_1_ = func_181627_a(worldIn, false);
        int lvt_4_1_ = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);

        for (BlockPos lvt_6_1_ : BlockPos.getAllInBox(pos, pos.add(lvt_4_1_ - 1, lvt_4_1_ - 1, lvt_4_1_ - 1)))
        {
            for (EnumFacing lvt_10_1_ : EnumFacing.values())
            {
                for (EnumFacing lvt_14_1_ : EnumFacing.values())
                {
                    if (lvt_14_1_ != lvt_10_1_ && lvt_14_1_ != lvt_10_1_.getOpposite())
                    {
                        BlockPattern.PatternHelper lvt_15_1_ = this.checkPatternAt(lvt_6_1_, lvt_10_1_, lvt_14_1_, lvt_3_1_);

                        if (lvt_15_1_ != null)
                        {
                            return lvt_15_1_;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static LoadingCache<BlockPos, BlockWorldState> func_181627_a(World p_181627_0_, boolean p_181627_1_)
    {
        return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(p_181627_0_, p_181627_1_));
    }

    /**
     * Offsets the position of pos in the direction of finger and thumb facing by offset amounts, follows the right-hand
     * rule for cross products (finger, thumb, palm) @return A new BlockPos offset in the facing directions
     */
    protected static BlockPos translateOffset(BlockPos pos, EnumFacing finger, EnumFacing thumb, int palmOffset, int thumbOffset, int fingerOffset)
    {
        if (finger != thumb && finger != thumb.getOpposite())
        {
            Vec3i lvt_6_1_ = new Vec3i(finger.getFrontOffsetX(), finger.getFrontOffsetY(), finger.getFrontOffsetZ());
            Vec3i lvt_7_1_ = new Vec3i(thumb.getFrontOffsetX(), thumb.getFrontOffsetY(), thumb.getFrontOffsetZ());
            Vec3i lvt_8_1_ = lvt_6_1_.crossProduct(lvt_7_1_);
            return pos.add(lvt_7_1_.getX() * -thumbOffset + lvt_8_1_.getX() * palmOffset + lvt_6_1_.getX() * fingerOffset, lvt_7_1_.getY() * -thumbOffset + lvt_8_1_.getY() * palmOffset + lvt_6_1_.getY() * fingerOffset, lvt_7_1_.getZ() * -thumbOffset + lvt_8_1_.getZ() * palmOffset + lvt_6_1_.getZ() * fingerOffset);
        }
        else
        {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
    }

    static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, BlockWorldState>
    {
        private final World world;
        private final boolean field_181626_b;

        public CacheLoader(World worldIn, boolean p_i46460_2_)
        {
            this.world = worldIn;
            this.field_181626_b = p_i46460_2_;
        }

        public BlockWorldState load(BlockPos p_load_1_) throws Exception
        {
            return new BlockWorldState(this.world, p_load_1_, this.field_181626_b);
        }

        public Object load(Object p_load_1_) throws Exception
        {
            return this.load((BlockPos)p_load_1_);
        }
    }

    public static class PatternHelper
    {
        private final BlockPos pos;
        private final EnumFacing finger;
        private final EnumFacing thumb;
        private final LoadingCache<BlockPos, BlockWorldState> lcache;
        private final int field_181120_e;
        private final int field_181121_f;
        private final int field_181122_g;

        public PatternHelper(BlockPos posIn, EnumFacing fingerIn, EnumFacing thumbIn, LoadingCache<BlockPos, BlockWorldState> lcacheIn, int p_i46378_5_, int p_i46378_6_, int p_i46378_7_)
        {
            this.pos = posIn;
            this.finger = fingerIn;
            this.thumb = thumbIn;
            this.lcache = lcacheIn;
            this.field_181120_e = p_i46378_5_;
            this.field_181121_f = p_i46378_6_;
            this.field_181122_g = p_i46378_7_;
        }

        public BlockPos getPos()
        {
            return this.pos;
        }

        public EnumFacing getFinger()
        {
            return this.finger;
        }

        public EnumFacing getThumb()
        {
            return this.thumb;
        }

        public int func_181118_d()
        {
            return this.field_181120_e;
        }

        public int func_181119_e()
        {
            return this.field_181121_f;
        }

        public BlockWorldState translateOffset(int palmOffset, int thumbOffset, int fingerOffset)
        {
            return (BlockWorldState)this.lcache.getUnchecked(BlockPattern.translateOffset(this.pos, this.getFinger(), this.getThumb(), palmOffset, thumbOffset, fingerOffset));
        }

        public String toString()
        {
            return Objects.toStringHelper(this).add("up", this.thumb).add("forwards", this.finger).add("frontTopLeft", this.pos).toString();
        }
    }
}
