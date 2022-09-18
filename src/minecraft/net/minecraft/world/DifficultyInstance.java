package net.minecraft.world;

import net.minecraft.util.MathHelper;

public class DifficultyInstance
{
    private final EnumDifficulty worldDifficulty;
    private final float additionalDifficulty;

    public DifficultyInstance(EnumDifficulty worldDifficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor)
    {
        this.worldDifficulty = worldDifficulty;
        this.additionalDifficulty = this.calculateAdditionalDifficulty(worldDifficulty, worldTime, chunkInhabitedTime, moonPhaseFactor);
    }

    public float getAdditionalDifficulty()
    {
        return this.additionalDifficulty;
    }

    public float getClampedAdditionalDifficulty()
    {
        return this.additionalDifficulty < 2.0F ? 0.0F : (this.additionalDifficulty > 4.0F ? 1.0F : (this.additionalDifficulty - 2.0F) / 2.0F);
    }

    private float calculateAdditionalDifficulty(EnumDifficulty difficulty, long worldTime, long chunkInhabitedTime, float moonPhaseFactor)
    {
        if (difficulty == EnumDifficulty.PEACEFUL)
        {
            return 0.0F;
        }
        else
        {
            boolean lvt_7_1_ = difficulty == EnumDifficulty.HARD;
            float lvt_8_1_ = 0.75F;
            float lvt_9_1_ = MathHelper.clamp_float(((float)worldTime + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
            lvt_8_1_ = lvt_8_1_ + lvt_9_1_;
            float lvt_10_1_ = 0.0F;
            lvt_10_1_ = lvt_10_1_ + MathHelper.clamp_float((float)chunkInhabitedTime / 3600000.0F, 0.0F, 1.0F) * (lvt_7_1_ ? 1.0F : 0.75F);
            lvt_10_1_ = lvt_10_1_ + MathHelper.clamp_float(moonPhaseFactor * 0.25F, 0.0F, lvt_9_1_);

            if (difficulty == EnumDifficulty.EASY)
            {
                lvt_10_1_ *= 0.5F;
            }

            lvt_8_1_ = lvt_8_1_ + lvt_10_1_;
            return (float)difficulty.getDifficultyId() * lvt_8_1_;
        }
    }
}
