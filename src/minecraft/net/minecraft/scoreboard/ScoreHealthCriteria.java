package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria
{
    public ScoreHealthCriteria(String name)
    {
        super(name);
    }

    public int setScore(List<EntityPlayer> p_96635_1_)
    {
        float lvt_2_1_ = 0.0F;

        for (EntityPlayer lvt_4_1_ : p_96635_1_)
        {
            lvt_2_1_ += lvt_4_1_.getHealth() + lvt_4_1_.getAbsorptionAmount();
        }

        if (p_96635_1_.size() > 0)
        {
            lvt_2_1_ /= (float)p_96635_1_.size();
        }

        return MathHelper.ceiling_float_int(lvt_2_1_);
    }

    public boolean isReadOnly()
    {
        return true;
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType()
    {
        return IScoreObjectiveCriteria.EnumRenderType.HEARTS;
    }
}
