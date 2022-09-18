package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;

public class VisGraph
{
    private static final int field_178616_a = (int)Math.pow(16.0D, 0.0D);
    private static final int field_178614_b = (int)Math.pow(16.0D, 1.0D);
    private static final int field_178615_c = (int)Math.pow(16.0D, 2.0D);
    private final BitSet field_178612_d = new BitSet(4096);
    private static final int[] field_178613_e = new int[1352];
    private int field_178611_f = 4096;

    public void func_178606_a(BlockPos pos)
    {
        this.field_178612_d.set(getIndex(pos), true);
        --this.field_178611_f;
    }

    private static int getIndex(BlockPos pos)
    {
        return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    private static int getIndex(int x, int y, int z)
    {
        return x << 0 | y << 8 | z << 4;
    }

    public SetVisibility computeVisibility()
    {
        SetVisibility lvt_1_1_ = new SetVisibility();

        if (4096 - this.field_178611_f < 256)
        {
            lvt_1_1_.setAllVisible(true);
        }
        else if (this.field_178611_f == 0)
        {
            lvt_1_1_.setAllVisible(false);
        }
        else
        {
            for (int lvt_5_1_ : field_178613_e)
            {
                if (!this.field_178612_d.get(lvt_5_1_))
                {
                    lvt_1_1_.setManyVisible(this.func_178604_a(lvt_5_1_));
                }
            }
        }

        return lvt_1_1_;
    }

    public Set<EnumFacing> func_178609_b(BlockPos pos)
    {
        return this.func_178604_a(getIndex(pos));
    }

    private Set<EnumFacing> func_178604_a(int p_178604_1_)
    {
        Set<EnumFacing> lvt_2_1_ = EnumSet.noneOf(EnumFacing.class);
        Queue<Integer> lvt_3_1_ = Lists.newLinkedList();
        lvt_3_1_.add(IntegerCache.getInteger(p_178604_1_));
        this.field_178612_d.set(p_178604_1_, true);

        while (!((Queue)lvt_3_1_).isEmpty())
        {
            int lvt_4_1_ = ((Integer)lvt_3_1_.poll()).intValue();
            this.func_178610_a(lvt_4_1_, lvt_2_1_);

            for (EnumFacing lvt_8_1_ : EnumFacing.values())
            {
                int lvt_9_1_ = this.func_178603_a(lvt_4_1_, lvt_8_1_);

                if (lvt_9_1_ >= 0 && !this.field_178612_d.get(lvt_9_1_))
                {
                    this.field_178612_d.set(lvt_9_1_, true);
                    lvt_3_1_.add(IntegerCache.getInteger(lvt_9_1_));
                }
            }
        }

        return lvt_2_1_;
    }

    private void func_178610_a(int p_178610_1_, Set<EnumFacing> p_178610_2_)
    {
        int lvt_3_1_ = p_178610_1_ >> 0 & 15;

        if (lvt_3_1_ == 0)
        {
            p_178610_2_.add(EnumFacing.WEST);
        }
        else if (lvt_3_1_ == 15)
        {
            p_178610_2_.add(EnumFacing.EAST);
        }

        int lvt_4_1_ = p_178610_1_ >> 8 & 15;

        if (lvt_4_1_ == 0)
        {
            p_178610_2_.add(EnumFacing.DOWN);
        }
        else if (lvt_4_1_ == 15)
        {
            p_178610_2_.add(EnumFacing.UP);
        }

        int lvt_5_1_ = p_178610_1_ >> 4 & 15;

        if (lvt_5_1_ == 0)
        {
            p_178610_2_.add(EnumFacing.NORTH);
        }
        else if (lvt_5_1_ == 15)
        {
            p_178610_2_.add(EnumFacing.SOUTH);
        }
    }

    private int func_178603_a(int p_178603_1_, EnumFacing p_178603_2_)
    {
        switch (p_178603_2_)
        {
            case DOWN:
                if ((p_178603_1_ >> 8 & 15) == 0)
                {
                    return -1;
                }

                return p_178603_1_ - field_178615_c;

            case UP:
                if ((p_178603_1_ >> 8 & 15) == 15)
                {
                    return -1;
                }

                return p_178603_1_ + field_178615_c;

            case NORTH:
                if ((p_178603_1_ >> 4 & 15) == 0)
                {
                    return -1;
                }

                return p_178603_1_ - field_178614_b;

            case SOUTH:
                if ((p_178603_1_ >> 4 & 15) == 15)
                {
                    return -1;
                }

                return p_178603_1_ + field_178614_b;

            case WEST:
                if ((p_178603_1_ >> 0 & 15) == 0)
                {
                    return -1;
                }

                return p_178603_1_ - field_178616_a;

            case EAST:
                if ((p_178603_1_ >> 0 & 15) == 15)
                {
                    return -1;
                }

                return p_178603_1_ + field_178616_a;

            default:
                return -1;
        }
    }

    static
    {
        int lvt_0_1_ = 0;
        int lvt_1_1_ = 15;
        int lvt_2_1_ = 0;

        for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_)
            {
                for (int lvt_5_1_ = 0; lvt_5_1_ < 16; ++lvt_5_1_)
                {
                    if (lvt_3_1_ == 0 || lvt_3_1_ == 15 || lvt_4_1_ == 0 || lvt_4_1_ == 15 || lvt_5_1_ == 0 || lvt_5_1_ == 15)
                    {
                        field_178613_e[lvt_2_1_++] = getIndex(lvt_3_1_, lvt_4_1_, lvt_5_1_);
                    }
                }
            }
        }
    }
}
