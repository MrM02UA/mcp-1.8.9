package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite implements ISoundEventAccessor<SoundPoolEntry>
{
    private final List<ISoundEventAccessor<SoundPoolEntry>> soundPool = Lists.newArrayList();
    private final Random rnd = new Random();
    private final ResourceLocation soundLocation;
    private final SoundCategory category;
    private double eventPitch;
    private double eventVolume;

    public SoundEventAccessorComposite(ResourceLocation soundLocation, double pitch, double volume, SoundCategory category)
    {
        this.soundLocation = soundLocation;
        this.eventVolume = volume;
        this.eventPitch = pitch;
        this.category = category;
    }

    public int getWeight()
    {
        int lvt_1_1_ = 0;

        for (ISoundEventAccessor<SoundPoolEntry> lvt_3_1_ : this.soundPool)
        {
            lvt_1_1_ += lvt_3_1_.getWeight();
        }

        return lvt_1_1_;
    }

    public SoundPoolEntry cloneEntry()
    {
        int lvt_1_1_ = this.getWeight();

        if (!this.soundPool.isEmpty() && lvt_1_1_ != 0)
        {
            int lvt_2_1_ = this.rnd.nextInt(lvt_1_1_);

            for (ISoundEventAccessor<SoundPoolEntry> lvt_4_1_ : this.soundPool)
            {
                lvt_2_1_ -= lvt_4_1_.getWeight();

                if (lvt_2_1_ < 0)
                {
                    SoundPoolEntry lvt_5_1_ = (SoundPoolEntry)lvt_4_1_.cloneEntry();
                    lvt_5_1_.setPitch(lvt_5_1_.getPitch() * this.eventPitch);
                    lvt_5_1_.setVolume(lvt_5_1_.getVolume() * this.eventVolume);
                    return lvt_5_1_;
                }
            }

            return SoundHandler.missing_sound;
        }
        else
        {
            return SoundHandler.missing_sound;
        }
    }

    public void addSoundToEventPool(ISoundEventAccessor<SoundPoolEntry> sound)
    {
        this.soundPool.add(sound);
    }

    public ResourceLocation getSoundEventLocation()
    {
        return this.soundLocation;
    }

    public SoundCategory getSoundCategory()
    {
        return this.category;
    }

    public Object cloneEntry()
    {
        return this.cloneEntry();
    }
}
