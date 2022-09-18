package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager
{
    /** The marker used for logging */
    private static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
    private static final Logger logger = LogManager.getLogger();

    /** A reference to the sound handler. */
    private final SoundHandler sndHandler;

    /** Reference to the GameSettings object. */
    private final GameSettings options;

    /** A reference to the sound system. */
    private SoundManager.SoundSystemStarterThread sndSystem;

    /** Set to true when the SoundManager has been initialised. */
    private boolean loaded;

    /** A counter for how long the sound manager has been running */
    private int playTime = 0;
    private final Map<String, ISound> playingSounds = HashBiMap.create();
    private final Map<ISound, String> invPlayingSounds;
    private Map<ISound, SoundPoolEntry> playingSoundPoolEntries;
    private final Multimap<SoundCategory, String> categorySounds;
    private final List<ITickableSound> tickableSounds;
    private final Map<ISound, Integer> delayedSounds;
    private final Map<String, Integer> playingSoundsStopTime;

    public SoundManager(SoundHandler p_i45119_1_, GameSettings p_i45119_2_)
    {
        this.invPlayingSounds = ((BiMap)this.playingSounds).inverse();
        this.playingSoundPoolEntries = Maps.newHashMap();
        this.categorySounds = HashMultimap.create();
        this.tickableSounds = Lists.newArrayList();
        this.delayedSounds = Maps.newHashMap();
        this.playingSoundsStopTime = Maps.newHashMap();
        this.sndHandler = p_i45119_1_;
        this.options = p_i45119_2_;

        try
        {
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
        }
        catch (SoundSystemException var4)
        {
            logger.error(LOG_MARKER, "Error linking with the LibraryJavaSound plug-in", var4);
        }
    }

    public void reloadSoundSystem()
    {
        this.unloadSoundSystem();
        this.loadSoundSystem();
    }

    /**
     * Tries to add the paulscode library and the relevant codecs. If it fails, the master volume  will be set to zero.
     */
    private synchronized void loadSoundSystem()
    {
        if (!this.loaded)
        {
            try
            {
                (new Thread(new Runnable()
                {
                    public void run()
                    {
                        SoundSystemConfig.setLogger(new SoundSystemLogger()
                        {
                            public void message(String p_message_1_, int p_message_2_)
                            {
                                if (!p_message_1_.isEmpty())
                                {
                                    SoundManager.logger.info(p_message_1_);
                                }
                            }
                            public void importantMessage(String p_importantMessage_1_, int p_importantMessage_2_)
                            {
                                if (!p_importantMessage_1_.isEmpty())
                                {
                                    SoundManager.logger.warn(p_importantMessage_1_);
                                }
                            }
                            public void errorMessage(String p_errorMessage_1_, String p_errorMessage_2_, int p_errorMessage_3_)
                            {
                                if (!p_errorMessage_2_.isEmpty())
                                {
                                    SoundManager.logger.error("Error in class \'" + p_errorMessage_1_ + "\'");
                                    SoundManager.logger.error(p_errorMessage_2_);
                                }
                            }
                        });
                        SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread();
                        SoundManager.this.loaded = true;
                        SoundManager.this.sndSystem.setMasterVolume(SoundManager.this.options.getSoundLevel(SoundCategory.MASTER));
                        SoundManager.logger.info(SoundManager.LOG_MARKER, "Sound engine started");
                    }
                }, "Sound Library Loader")).start();
            }
            catch (RuntimeException var2)
            {
                logger.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", var2);
                this.options.setSoundLevel(SoundCategory.MASTER, 0.0F);
                this.options.saveOptions();
            }
        }
    }

    /**
     * Returns the sound level (between 0.0 and 1.0) for a category, but 1.0 for the master sound category
     */
    private float getSoundCategoryVolume(SoundCategory category)
    {
        return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
    }

    /**
     * Adjusts volume for currently playing sounds in this category
     */
    public void setSoundCategoryVolume(SoundCategory category, float volume)
    {
        if (this.loaded)
        {
            if (category == SoundCategory.MASTER)
            {
                this.sndSystem.setMasterVolume(volume);
            }
            else
            {
                for (String lvt_4_1_ : this.categorySounds.get(category))
                {
                    ISound lvt_5_1_ = (ISound)this.playingSounds.get(lvt_4_1_);
                    float lvt_6_1_ = this.getNormalizedVolume(lvt_5_1_, (SoundPoolEntry)this.playingSoundPoolEntries.get(lvt_5_1_), category);

                    if (lvt_6_1_ <= 0.0F)
                    {
                        this.stopSound(lvt_5_1_);
                    }
                    else
                    {
                        this.sndSystem.setVolume(lvt_4_1_, lvt_6_1_);
                    }
                }
            }
        }
    }

    /**
     * Cleans up the Sound System
     */
    public void unloadSoundSystem()
    {
        if (this.loaded)
        {
            this.stopAllSounds();
            this.sndSystem.cleanup();
            this.loaded = false;
        }
    }

    /**
     * Stops all currently playing sounds
     */
    public void stopAllSounds()
    {
        if (this.loaded)
        {
            for (String lvt_2_1_ : this.playingSounds.keySet())
            {
                this.sndSystem.stop(lvt_2_1_);
            }

            this.playingSounds.clear();
            this.delayedSounds.clear();
            this.tickableSounds.clear();
            this.categorySounds.clear();
            this.playingSoundPoolEntries.clear();
            this.playingSoundsStopTime.clear();
        }
    }

    public void updateAllSounds()
    {
        ++this.playTime;

        for (ITickableSound lvt_2_1_ : this.tickableSounds)
        {
            lvt_2_1_.update();

            if (lvt_2_1_.isDonePlaying())
            {
                this.stopSound(lvt_2_1_);
            }
            else
            {
                String lvt_3_1_ = (String)this.invPlayingSounds.get(lvt_2_1_);
                this.sndSystem.setVolume(lvt_3_1_, this.getNormalizedVolume(lvt_2_1_, (SoundPoolEntry)this.playingSoundPoolEntries.get(lvt_2_1_), this.sndHandler.getSound(lvt_2_1_.getSoundLocation()).getSoundCategory()));
                this.sndSystem.setPitch(lvt_3_1_, this.getNormalizedPitch(lvt_2_1_, (SoundPoolEntry)this.playingSoundPoolEntries.get(lvt_2_1_)));
                this.sndSystem.setPosition(lvt_3_1_, lvt_2_1_.getXPosF(), lvt_2_1_.getYPosF(), lvt_2_1_.getZPosF());
            }
        }

        Iterator<Entry<String, ISound>> lvt_1_2_ = this.playingSounds.entrySet().iterator();

        while (lvt_1_2_.hasNext())
        {
            Entry<String, ISound> lvt_2_2_ = (Entry)lvt_1_2_.next();
            String lvt_3_2_ = (String)lvt_2_2_.getKey();
            ISound lvt_4_1_ = (ISound)lvt_2_2_.getValue();

            if (!this.sndSystem.playing(lvt_3_2_))
            {
                int lvt_5_1_ = ((Integer)this.playingSoundsStopTime.get(lvt_3_2_)).intValue();

                if (lvt_5_1_ <= this.playTime)
                {
                    int lvt_6_1_ = lvt_4_1_.getRepeatDelay();

                    if (lvt_4_1_.canRepeat() && lvt_6_1_ > 0)
                    {
                        this.delayedSounds.put(lvt_4_1_, Integer.valueOf(this.playTime + lvt_6_1_));
                    }

                    lvt_1_2_.remove();
                    logger.debug(LOG_MARKER, "Removed channel {} because it\'s not playing anymore", new Object[] {lvt_3_2_});
                    this.sndSystem.removeSource(lvt_3_2_);
                    this.playingSoundsStopTime.remove(lvt_3_2_);
                    this.playingSoundPoolEntries.remove(lvt_4_1_);

                    try
                    {
                        this.categorySounds.remove(this.sndHandler.getSound(lvt_4_1_.getSoundLocation()).getSoundCategory(), lvt_3_2_);
                    }
                    catch (RuntimeException var8)
                    {
                        ;
                    }

                    if (lvt_4_1_ instanceof ITickableSound)
                    {
                        this.tickableSounds.remove(lvt_4_1_);
                    }
                }
            }
        }

        Iterator<Entry<ISound, Integer>> lvt_2_3_ = this.delayedSounds.entrySet().iterator();

        while (lvt_2_3_.hasNext())
        {
            Entry<ISound, Integer> lvt_3_3_ = (Entry)lvt_2_3_.next();

            if (this.playTime >= ((Integer)lvt_3_3_.getValue()).intValue())
            {
                ISound lvt_4_2_ = (ISound)lvt_3_3_.getKey();

                if (lvt_4_2_ instanceof ITickableSound)
                {
                    ((ITickableSound)lvt_4_2_).update();
                }

                this.playSound(lvt_4_2_);
                lvt_2_3_.remove();
            }
        }
    }

    /**
     * Returns true if the sound is playing or still within time
     */
    public boolean isSoundPlaying(ISound sound)
    {
        if (!this.loaded)
        {
            return false;
        }
        else
        {
            String lvt_2_1_ = (String)this.invPlayingSounds.get(sound);
            return lvt_2_1_ == null ? false : this.sndSystem.playing(lvt_2_1_) || this.playingSoundsStopTime.containsKey(lvt_2_1_) && ((Integer)this.playingSoundsStopTime.get(lvt_2_1_)).intValue() <= this.playTime;
        }
    }

    public void stopSound(ISound sound)
    {
        if (this.loaded)
        {
            String lvt_2_1_ = (String)this.invPlayingSounds.get(sound);

            if (lvt_2_1_ != null)
            {
                this.sndSystem.stop(lvt_2_1_);
            }
        }
    }

    public void playSound(ISound p_sound)
    {
        if (this.loaded)
        {
            if (this.sndSystem.getMasterVolume() <= 0.0F)
            {
                logger.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", new Object[] {p_sound.getSoundLocation()});
            }
            else
            {
                SoundEventAccessorComposite lvt_2_1_ = this.sndHandler.getSound(p_sound.getSoundLocation());

                if (lvt_2_1_ == null)
                {
                    logger.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", new Object[] {p_sound.getSoundLocation()});
                }
                else
                {
                    SoundPoolEntry lvt_3_1_ = lvt_2_1_.cloneEntry();

                    if (lvt_3_1_ == SoundHandler.missing_sound)
                    {
                        logger.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", new Object[] {lvt_2_1_.getSoundEventLocation()});
                    }
                    else
                    {
                        float lvt_4_1_ = p_sound.getVolume();
                        float lvt_5_1_ = 16.0F;

                        if (lvt_4_1_ > 1.0F)
                        {
                            lvt_5_1_ *= lvt_4_1_;
                        }

                        SoundCategory lvt_6_1_ = lvt_2_1_.getSoundCategory();
                        float lvt_7_1_ = this.getNormalizedVolume(p_sound, lvt_3_1_, lvt_6_1_);
                        double lvt_8_1_ = (double)this.getNormalizedPitch(p_sound, lvt_3_1_);
                        ResourceLocation lvt_10_1_ = lvt_3_1_.getSoundPoolEntryLocation();

                        if (lvt_7_1_ == 0.0F)
                        {
                            logger.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", new Object[] {lvt_10_1_});
                        }
                        else
                        {
                            boolean lvt_11_1_ = p_sound.canRepeat() && p_sound.getRepeatDelay() == 0;
                            String lvt_12_1_ = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();

                            if (lvt_3_1_.isStreamingSound())
                            {
                                this.sndSystem.newStreamingSource(false, lvt_12_1_, getURLForSoundResource(lvt_10_1_), lvt_10_1_.toString(), lvt_11_1_, p_sound.getXPosF(), p_sound.getYPosF(), p_sound.getZPosF(), p_sound.getAttenuationType().getTypeInt(), lvt_5_1_);
                            }
                            else
                            {
                                this.sndSystem.newSource(false, lvt_12_1_, getURLForSoundResource(lvt_10_1_), lvt_10_1_.toString(), lvt_11_1_, p_sound.getXPosF(), p_sound.getYPosF(), p_sound.getZPosF(), p_sound.getAttenuationType().getTypeInt(), lvt_5_1_);
                            }

                            logger.debug(LOG_MARKER, "Playing sound {} for event {} as channel {}", new Object[] {lvt_3_1_.getSoundPoolEntryLocation(), lvt_2_1_.getSoundEventLocation(), lvt_12_1_});
                            this.sndSystem.setPitch(lvt_12_1_, (float)lvt_8_1_);
                            this.sndSystem.setVolume(lvt_12_1_, lvt_7_1_);
                            this.sndSystem.play(lvt_12_1_);
                            this.playingSoundsStopTime.put(lvt_12_1_, Integer.valueOf(this.playTime + 20));
                            this.playingSounds.put(lvt_12_1_, p_sound);
                            this.playingSoundPoolEntries.put(p_sound, lvt_3_1_);

                            if (lvt_6_1_ != SoundCategory.MASTER)
                            {
                                this.categorySounds.put(lvt_6_1_, lvt_12_1_);
                            }

                            if (p_sound instanceof ITickableSound)
                            {
                                this.tickableSounds.add((ITickableSound)p_sound);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Normalizes pitch from parameters and clamps to [0.5, 2.0]
     */
    private float getNormalizedPitch(ISound sound, SoundPoolEntry entry)
    {
        return (float)MathHelper.clamp_double((double)sound.getPitch() * entry.getPitch(), 0.5D, 2.0D);
    }

    /**
     * Normalizes volume level from parameters.  Range [0.0, 1.0]
     */
    private float getNormalizedVolume(ISound sound, SoundPoolEntry entry, SoundCategory category)
    {
        return (float)MathHelper.clamp_double((double)sound.getVolume() * entry.getVolume(), 0.0D, 1.0D) * this.getSoundCategoryVolume(category);
    }

    /**
     * Pauses all currently playing sounds
     */
    public void pauseAllSounds()
    {
        for (String lvt_2_1_ : this.playingSounds.keySet())
        {
            logger.debug(LOG_MARKER, "Pausing channel {}", new Object[] {lvt_2_1_});
            this.sndSystem.pause(lvt_2_1_);
        }
    }

    /**
     * Resumes playing all currently playing sounds (after pauseAllSounds)
     */
    public void resumeAllSounds()
    {
        for (String lvt_2_1_ : this.playingSounds.keySet())
        {
            logger.debug(LOG_MARKER, "Resuming channel {}", new Object[] {lvt_2_1_});
            this.sndSystem.play(lvt_2_1_);
        }
    }

    /**
     * Adds a sound to play in n tick
     */
    public void playDelayedSound(ISound sound, int delay)
    {
        this.delayedSounds.put(sound, Integer.valueOf(this.playTime + delay));
    }

    private static URL getURLForSoundResource(final ResourceLocation p_148612_0_)
    {
        String lvt_1_1_ = String.format("%s:%s:%s", new Object[] {"mcsounddomain", p_148612_0_.getResourceDomain(), p_148612_0_.getResourcePath()});
        URLStreamHandler lvt_2_1_ = new URLStreamHandler()
        {
            protected URLConnection openConnection(final URL p_openConnection_1_)
            {
                return new URLConnection(p_openConnection_1_)
                {
                    public void connect() throws IOException
                    {
                    }
                    public InputStream getInputStream() throws IOException
                    {
                        return Minecraft.getMinecraft().getResourceManager().getResource(p_148612_0_).getInputStream();
                    }
                };
            }
        };

        try
        {
            return new URL((URL)null, lvt_1_1_, lvt_2_1_);
        }
        catch (MalformedURLException var4)
        {
            throw new Error("TODO: Sanely handle url exception! :D");
        }
    }

    /**
     * Sets the listener of sounds
     */
    public void setListener(EntityPlayer player, float p_148615_2_)
    {
        if (this.loaded && player != null)
        {
            float lvt_3_1_ = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * p_148615_2_;
            float lvt_4_1_ = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * p_148615_2_;
            double lvt_5_1_ = player.prevPosX + (player.posX - player.prevPosX) * (double)p_148615_2_;
            double lvt_7_1_ = player.prevPosY + (player.posY - player.prevPosY) * (double)p_148615_2_ + (double)player.getEyeHeight();
            double lvt_9_1_ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)p_148615_2_;
            float lvt_11_1_ = MathHelper.cos((lvt_4_1_ + 90.0F) * 0.017453292F);
            float lvt_12_1_ = MathHelper.sin((lvt_4_1_ + 90.0F) * 0.017453292F);
            float lvt_13_1_ = MathHelper.cos(-lvt_3_1_ * 0.017453292F);
            float lvt_14_1_ = MathHelper.sin(-lvt_3_1_ * 0.017453292F);
            float lvt_15_1_ = MathHelper.cos((-lvt_3_1_ + 90.0F) * 0.017453292F);
            float lvt_16_1_ = MathHelper.sin((-lvt_3_1_ + 90.0F) * 0.017453292F);
            float lvt_17_1_ = lvt_11_1_ * lvt_13_1_;
            float lvt_19_1_ = lvt_12_1_ * lvt_13_1_;
            float lvt_20_1_ = lvt_11_1_ * lvt_15_1_;
            float lvt_22_1_ = lvt_12_1_ * lvt_15_1_;
            this.sndSystem.setListenerPosition((float)lvt_5_1_, (float)lvt_7_1_, (float)lvt_9_1_);
            this.sndSystem.setListenerOrientation(lvt_17_1_, lvt_14_1_, lvt_19_1_, lvt_20_1_, lvt_16_1_, lvt_22_1_);
        }
    }

    class SoundSystemStarterThread extends SoundSystem
    {
        private SoundSystemStarterThread()
        {
        }

        public boolean playing(String p_playing_1_)
        {
            synchronized (SoundSystemConfig.THREAD_SYNC)
            {
                if (this.soundLibrary == null)
                {
                    return false;
                }
                else
                {
                    Source lvt_3_1_ = (Source)this.soundLibrary.getSources().get(p_playing_1_);
                    return lvt_3_1_ == null ? false : lvt_3_1_.playing() || lvt_3_1_.paused() || lvt_3_1_.preLoad;
                }
            }
        }
    }
}
