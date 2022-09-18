package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements IResourceManagerReloadListener, ITickable
{
    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final ParameterizedType TYPE = new ParameterizedType()
    {
        public Type[] getActualTypeArguments()
        {
            return new Type[] {String.class, SoundList.class};
        }
        public Type getRawType()
        {
            return Map.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };
    public static final SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0D, 0.0D, false);
    private final SoundRegistry sndRegistry = new SoundRegistry();
    private final SoundManager sndManager;
    private final IResourceManager mcResourceManager;

    public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn)
    {
        this.mcResourceManager = manager;
        this.sndManager = new SoundManager(this, gameSettingsIn);
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.sndManager.reloadSoundSystem();
        this.sndRegistry.clearMap();

        for (String lvt_3_1_ : resourceManager.getResourceDomains())
        {
            try
            {
                for (IResource lvt_6_1_ : resourceManager.getAllResources(new ResourceLocation(lvt_3_1_, "sounds.json")))
                {
                    try
                    {
                        Map<String, SoundList> lvt_7_1_ = this.getSoundMap(lvt_6_1_.getInputStream());

                        for (Entry<String, SoundList> lvt_9_1_ : lvt_7_1_.entrySet())
                        {
                            this.loadSoundResource(new ResourceLocation(lvt_3_1_, (String)lvt_9_1_.getKey()), (SoundList)lvt_9_1_.getValue());
                        }
                    }
                    catch (RuntimeException var10)
                    {
                        logger.warn("Invalid sounds.json", var10);
                    }
                }
            }
            catch (IOException var11)
            {
                ;
            }
        }
    }

    protected Map<String, SoundList> getSoundMap(InputStream stream)
    {
        Map var2;

        try
        {
            var2 = (Map)GSON.fromJson(new InputStreamReader(stream), TYPE);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }

        return var2;
    }

    private void loadSoundResource(ResourceLocation location, SoundList sounds)
    {
        boolean lvt_4_1_ = !this.sndRegistry.containsKey(location);
        SoundEventAccessorComposite lvt_3_2_;

        if (!lvt_4_1_ && !sounds.canReplaceExisting())
        {
            lvt_3_2_ = (SoundEventAccessorComposite)this.sndRegistry.getObject(location);
        }
        else
        {
            if (!lvt_4_1_)
            {
                logger.debug("Replaced sound event location {}", new Object[] {location});
            }

            lvt_3_2_ = new SoundEventAccessorComposite(location, 1.0D, 1.0D, sounds.getSoundCategory());
            this.sndRegistry.registerSound(lvt_3_2_);
        }

        for (final SoundList.SoundEntry lvt_6_1_ : sounds.getSoundList())
        {
            String lvt_7_1_ = lvt_6_1_.getSoundEntryName();
            ResourceLocation lvt_8_1_ = new ResourceLocation(lvt_7_1_);
            final String lvt_9_1_ = lvt_7_1_.contains(":") ? lvt_8_1_.getResourceDomain() : location.getResourceDomain();
            ISoundEventAccessor<SoundPoolEntry> lvt_10_2_;

            switch (lvt_6_1_.getSoundEntryType())
            {
                case FILE:
                    ResourceLocation lvt_11_1_ = new ResourceLocation(lvt_9_1_, "sounds/" + lvt_8_1_.getResourcePath() + ".ogg");
                    InputStream lvt_12_1_ = null;

                    try
                    {
                        lvt_12_1_ = this.mcResourceManager.getResource(lvt_11_1_).getInputStream();
                    }
                    catch (FileNotFoundException var18)
                    {
                        logger.warn("File {} does not exist, cannot add it to event {}", new Object[] {lvt_11_1_, location});
                        continue;
                    }
                    catch (IOException var19)
                    {
                        logger.warn("Could not load sound file " + lvt_11_1_ + ", cannot add it to event " + location, var19);
                        continue;
                    }
                    finally
                    {
                        IOUtils.closeQuietly(lvt_12_1_);
                    }

                    lvt_10_2_ = new SoundEventAccessor(new SoundPoolEntry(lvt_11_1_, (double)lvt_6_1_.getSoundEntryPitch(), (double)lvt_6_1_.getSoundEntryVolume(), lvt_6_1_.isStreaming()), lvt_6_1_.getSoundEntryWeight());
                    break;

                case SOUND_EVENT:
                    lvt_10_2_ = new ISoundEventAccessor<SoundPoolEntry>()
                    {
                        final ResourceLocation field_148726_a = new ResourceLocation(lvt_9_1_, lvt_6_1_.getSoundEntryName());
                        public int getWeight()
                        {
                            SoundEventAccessorComposite lvt_1_1_ = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return lvt_1_1_ == null ? 0 : lvt_1_1_.getWeight();
                        }
                        public SoundPoolEntry cloneEntry()
                        {
                            SoundEventAccessorComposite lvt_1_1_ = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return lvt_1_1_ == null ? SoundHandler.missing_sound : lvt_1_1_.cloneEntry();
                        }
                        public Object cloneEntry()
                        {
                            return this.cloneEntry();
                        }
                    };

                    break;
                default:
                    throw new IllegalStateException("IN YOU FACE");
            }

            lvt_3_2_.addSoundToEventPool(lvt_10_2_);
        }
    }

    public SoundEventAccessorComposite getSound(ResourceLocation location)
    {
        return (SoundEventAccessorComposite)this.sndRegistry.getObject(location);
    }

    /**
     * Play a sound
     */
    public void playSound(ISound sound)
    {
        this.sndManager.playSound(sound);
    }

    /**
     * Plays the sound in n ticks
     */
    public void playDelayedSound(ISound sound, int delay)
    {
        this.sndManager.playDelayedSound(sound, delay);
    }

    public void setListener(EntityPlayer player, float p_147691_2_)
    {
        this.sndManager.setListener(player, p_147691_2_);
    }

    public void pauseSounds()
    {
        this.sndManager.pauseAllSounds();
    }

    public void stopSounds()
    {
        this.sndManager.stopAllSounds();
    }

    public void unloadSounds()
    {
        this.sndManager.unloadSoundSystem();
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        this.sndManager.updateAllSounds();
    }

    public void resumeSounds()
    {
        this.sndManager.resumeAllSounds();
    }

    public void setSoundLevel(SoundCategory category, float volume)
    {
        if (category == SoundCategory.MASTER && volume <= 0.0F)
        {
            this.stopSounds();
        }

        this.sndManager.setSoundCategoryVolume(category, volume);
    }

    public void stopSound(ISound p_147683_1_)
    {
        this.sndManager.stopSound(p_147683_1_);
    }

    /**
     * Returns a random sound from one or more categories
     */
    public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory... categories)
    {
        List<SoundEventAccessorComposite> lvt_2_1_ = Lists.newArrayList();

        for (ResourceLocation lvt_4_1_ : this.sndRegistry.getKeys())
        {
            SoundEventAccessorComposite lvt_5_1_ = (SoundEventAccessorComposite)this.sndRegistry.getObject(lvt_4_1_);

            if (ArrayUtils.contains(categories, lvt_5_1_.getSoundCategory()))
            {
                lvt_2_1_.add(lvt_5_1_);
            }
        }

        if (lvt_2_1_.isEmpty())
        {
            return null;
        }
        else
        {
            return (SoundEventAccessorComposite)lvt_2_1_.get((new Random()).nextInt(lvt_2_1_.size()));
        }
    }

    public boolean isSoundPlaying(ISound sound)
    {
        return this.sndManager.isSoundPlaying(sound);
    }
}
