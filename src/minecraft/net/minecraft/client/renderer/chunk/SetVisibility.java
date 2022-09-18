package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility
{
    private static final int COUNT_FACES = EnumFacing.values().length;
    private final BitSet bitSet;

    public SetVisibility()
    {
        this.bitSet = new BitSet(COUNT_FACES * COUNT_FACES);
    }

    public void setManyVisible(Set<EnumFacing> p_178620_1_)
    {
        for (EnumFacing lvt_3_1_ : p_178620_1_)
        {
            for (EnumFacing lvt_5_1_ : p_178620_1_)
            {
                this.setVisible(lvt_3_1_, lvt_5_1_, true);
            }
        }
    }

    public void setVisible(EnumFacing facing, EnumFacing facing2, boolean p_178619_3_)
    {
        this.bitSet.set(facing.ordinal() + facing2.ordinal() * COUNT_FACES, p_178619_3_);
        this.bitSet.set(facing2.ordinal() + facing.ordinal() * COUNT_FACES, p_178619_3_);
    }

    public void setAllVisible(boolean visible)
    {
        this.bitSet.set(0, this.bitSet.size(), visible);
    }

    public boolean isVisible(EnumFacing facing, EnumFacing facing2)
    {
        return this.bitSet.get(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
    }

    public String toString()
    {
        StringBuilder lvt_1_1_ = new StringBuilder();
        lvt_1_1_.append(' ');

        for (EnumFacing lvt_5_1_ : EnumFacing.values())
        {
            lvt_1_1_.append(' ').append(lvt_5_1_.toString().toUpperCase().charAt(0));
        }

        lvt_1_1_.append('\n');

        for (EnumFacing lvt_5_2_ : EnumFacing.values())
        {
            lvt_1_1_.append(lvt_5_2_.toString().toUpperCase().charAt(0));

            for (EnumFacing lvt_9_1_ : EnumFacing.values())
            {
                if (lvt_5_2_ == lvt_9_1_)
                {
                    lvt_1_1_.append("  ");
                }
                else
                {
                    boolean lvt_10_1_ = this.isVisible(lvt_5_2_, lvt_9_1_);
                    lvt_1_1_.append(' ').append((char)(lvt_10_1_ ? 'Y' : 'n'));
                }
            }

            lvt_1_1_.append('\n');
        }

        return lvt_1_1_.toString();
    }
}
