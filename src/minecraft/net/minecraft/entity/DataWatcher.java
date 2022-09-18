package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Rotations;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher
{
    private final Entity owner;

    /** When isBlank is true the DataWatcher is not watching any objects */
    private boolean isBlank = true;
    private static final Map < Class<?>, Integer > dataTypes = Maps.newHashMap();
    private final Map<Integer, DataWatcher.WatchableObject> watchedObjects = Maps.newHashMap();

    /** true if one or more object was changed */
    private boolean objectChanged;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public DataWatcher(Entity owner)
    {
        this.owner = owner;
    }

    public <T> void addObject(int id, T object)
    {
        Integer lvt_3_1_ = (Integer)dataTypes.get(object.getClass());

        if (lvt_3_1_ == null)
        {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        }
        else if (id > 31)
        {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        }
        else if (this.watchedObjects.containsKey(Integer.valueOf(id)))
        {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }
        else
        {
            DataWatcher.WatchableObject lvt_4_1_ = new DataWatcher.WatchableObject(lvt_3_1_.intValue(), id, object);
            this.lock.writeLock().lock();
            this.watchedObjects.put(Integer.valueOf(id), lvt_4_1_);
            this.lock.writeLock().unlock();
            this.isBlank = false;
        }
    }

    /**
     * Add a new object for the DataWatcher to watch, using the specified data type.
     */
    public void addObjectByDataType(int id, int type)
    {
        DataWatcher.WatchableObject lvt_3_1_ = new DataWatcher.WatchableObject(type, id, (Object)null);
        this.lock.writeLock().lock();
        this.watchedObjects.put(Integer.valueOf(id), lvt_3_1_);
        this.lock.writeLock().unlock();
        this.isBlank = false;
    }

    /**
     * gets the bytevalue of a watchable object
     */
    public byte getWatchableObjectByte(int id)
    {
        return ((Byte)this.getWatchedObject(id).getObject()).byteValue();
    }

    public short getWatchableObjectShort(int id)
    {
        return ((Short)this.getWatchedObject(id).getObject()).shortValue();
    }

    /**
     * gets a watchable object and returns it as a Integer
     */
    public int getWatchableObjectInt(int id)
    {
        return ((Integer)this.getWatchedObject(id).getObject()).intValue();
    }

    public float getWatchableObjectFloat(int id)
    {
        return ((Float)this.getWatchedObject(id).getObject()).floatValue();
    }

    /**
     * gets a watchable object and returns it as a String
     */
    public String getWatchableObjectString(int id)
    {
        return (String)this.getWatchedObject(id).getObject();
    }

    /**
     * Get a watchable object as an ItemStack.
     */
    public ItemStack getWatchableObjectItemStack(int id)
    {
        return (ItemStack)this.getWatchedObject(id).getObject();
    }

    /**
     * is threadsafe, unless it throws an exception, then
     */
    private DataWatcher.WatchableObject getWatchedObject(int id)
    {
        this.lock.readLock().lock();
        DataWatcher.WatchableObject lvt_2_1_;

        try
        {
            lvt_2_1_ = (DataWatcher.WatchableObject)this.watchedObjects.get(Integer.valueOf(id));
        }
        catch (Throwable var6)
        {
            CrashReport lvt_4_1_ = CrashReport.makeCrashReport(var6, "Getting synched entity data");
            CrashReportCategory lvt_5_1_ = lvt_4_1_.makeCategory("Synched entity data");
            lvt_5_1_.addCrashSection("Data ID", Integer.valueOf(id));
            throw new ReportedException(lvt_4_1_);
        }

        this.lock.readLock().unlock();
        return lvt_2_1_;
    }

    public Rotations getWatchableObjectRotations(int id)
    {
        return (Rotations)this.getWatchedObject(id).getObject();
    }

    public <T> void updateObject(int id, T newData)
    {
        DataWatcher.WatchableObject lvt_3_1_ = this.getWatchedObject(id);

        if (ObjectUtils.notEqual(newData, lvt_3_1_.getObject()))
        {
            lvt_3_1_.setObject(newData);
            this.owner.onDataWatcherUpdate(id);
            lvt_3_1_.setWatched(true);
            this.objectChanged = true;
        }
    }

    public void setObjectWatched(int id)
    {
        this.getWatchedObject(id).watched = true;
        this.objectChanged = true;
    }

    /**
     * true if one or more object was changed
     */
    public boolean hasObjectChanged()
    {
        return this.objectChanged;
    }

    /**
     * Writes the list of watched objects (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) to the specified PacketBuffer
     */
    public static void writeWatchedListToPacketBuffer(List<DataWatcher.WatchableObject> objectsList, PacketBuffer buffer) throws IOException
    {
        if (objectsList != null)
        {
            for (DataWatcher.WatchableObject lvt_3_1_ : objectsList)
            {
                writeWatchableObjectToPacketBuffer(buffer, lvt_3_1_);
            }
        }

        buffer.writeByte(127);
    }

    public List<DataWatcher.WatchableObject> getChanged()
    {
        List<DataWatcher.WatchableObject> lvt_1_1_ = null;

        if (this.objectChanged)
        {
            this.lock.readLock().lock();

            for (DataWatcher.WatchableObject lvt_3_1_ : this.watchedObjects.values())
            {
                if (lvt_3_1_.isWatched())
                {
                    lvt_3_1_.setWatched(false);

                    if (lvt_1_1_ == null)
                    {
                        lvt_1_1_ = Lists.newArrayList();
                    }

                    lvt_1_1_.add(lvt_3_1_);
                }
            }

            this.lock.readLock().unlock();
        }

        this.objectChanged = false;
        return lvt_1_1_;
    }

    public void writeTo(PacketBuffer buffer) throws IOException
    {
        this.lock.readLock().lock();

        for (DataWatcher.WatchableObject lvt_3_1_ : this.watchedObjects.values())
        {
            writeWatchableObjectToPacketBuffer(buffer, lvt_3_1_);
        }

        this.lock.readLock().unlock();
        buffer.writeByte(127);
    }

    public List<DataWatcher.WatchableObject> getAllWatched()
    {
        List<DataWatcher.WatchableObject> lvt_1_1_ = null;
        this.lock.readLock().lock();

        for (DataWatcher.WatchableObject lvt_3_1_ : this.watchedObjects.values())
        {
            if (lvt_1_1_ == null)
            {
                lvt_1_1_ = Lists.newArrayList();
            }

            lvt_1_1_.add(lvt_3_1_);
        }

        this.lock.readLock().unlock();
        return lvt_1_1_;
    }

    /**
     * Writes a watchable object (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) to the specified PacketBuffer
     */
    private static void writeWatchableObjectToPacketBuffer(PacketBuffer buffer, DataWatcher.WatchableObject object) throws IOException
    {
        int lvt_2_1_ = (object.getObjectType() << 5 | object.getDataValueId() & 31) & 255;
        buffer.writeByte(lvt_2_1_);

        switch (object.getObjectType())
        {
            case 0:
                buffer.writeByte(((Byte)object.getObject()).byteValue());
                break;

            case 1:
                buffer.writeShort(((Short)object.getObject()).shortValue());
                break;

            case 2:
                buffer.writeInt(((Integer)object.getObject()).intValue());
                break;

            case 3:
                buffer.writeFloat(((Float)object.getObject()).floatValue());
                break;

            case 4:
                buffer.writeString((String)object.getObject());
                break;

            case 5:
                ItemStack lvt_3_1_ = (ItemStack)object.getObject();
                buffer.writeItemStackToBuffer(lvt_3_1_);
                break;

            case 6:
                BlockPos lvt_4_1_ = (BlockPos)object.getObject();
                buffer.writeInt(lvt_4_1_.getX());
                buffer.writeInt(lvt_4_1_.getY());
                buffer.writeInt(lvt_4_1_.getZ());
                break;

            case 7:
                Rotations lvt_5_1_ = (Rotations)object.getObject();
                buffer.writeFloat(lvt_5_1_.getX());
                buffer.writeFloat(lvt_5_1_.getY());
                buffer.writeFloat(lvt_5_1_.getZ());
        }
    }

    public static List<DataWatcher.WatchableObject> readWatchedListFromPacketBuffer(PacketBuffer buffer) throws IOException
    {
        List<DataWatcher.WatchableObject> lvt_1_1_ = null;

        for (int lvt_2_1_ = buffer.readByte(); lvt_2_1_ != 127; lvt_2_1_ = buffer.readByte())
        {
            if (lvt_1_1_ == null)
            {
                lvt_1_1_ = Lists.newArrayList();
            }

            int lvt_3_1_ = (lvt_2_1_ & 224) >> 5;
            int lvt_4_1_ = lvt_2_1_ & 31;
            DataWatcher.WatchableObject lvt_5_1_ = null;

            switch (lvt_3_1_)
            {
                case 0:
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, Byte.valueOf(buffer.readByte()));
                    break;

                case 1:
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, Short.valueOf(buffer.readShort()));
                    break;

                case 2:
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, Integer.valueOf(buffer.readInt()));
                    break;

                case 3:
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, Float.valueOf(buffer.readFloat()));
                    break;

                case 4:
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, buffer.readStringFromBuffer(32767));
                    break;

                case 5:
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, buffer.readItemStackFromBuffer());
                    break;

                case 6:
                    int lvt_6_1_ = buffer.readInt();
                    int lvt_7_1_ = buffer.readInt();
                    int lvt_8_1_ = buffer.readInt();
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, new BlockPos(lvt_6_1_, lvt_7_1_, lvt_8_1_));
                    break;

                case 7:
                    float lvt_9_1_ = buffer.readFloat();
                    float lvt_10_1_ = buffer.readFloat();
                    float lvt_11_1_ = buffer.readFloat();
                    lvt_5_1_ = new DataWatcher.WatchableObject(lvt_3_1_, lvt_4_1_, new Rotations(lvt_9_1_, lvt_10_1_, lvt_11_1_));
            }

            lvt_1_1_.add(lvt_5_1_);
        }

        return lvt_1_1_;
    }

    public void updateWatchedObjectsFromList(List<DataWatcher.WatchableObject> p_75687_1_)
    {
        this.lock.writeLock().lock();

        for (DataWatcher.WatchableObject lvt_3_1_ : p_75687_1_)
        {
            DataWatcher.WatchableObject lvt_4_1_ = (DataWatcher.WatchableObject)this.watchedObjects.get(Integer.valueOf(lvt_3_1_.getDataValueId()));

            if (lvt_4_1_ != null)
            {
                lvt_4_1_.setObject(lvt_3_1_.getObject());
                this.owner.onDataWatcherUpdate(lvt_3_1_.getDataValueId());
            }
        }

        this.lock.writeLock().unlock();
        this.objectChanged = true;
    }

    public boolean getIsBlank()
    {
        return this.isBlank;
    }

    public void func_111144_e()
    {
        this.objectChanged = false;
    }

    static
    {
        dataTypes.put(Byte.class, Integer.valueOf(0));
        dataTypes.put(Short.class, Integer.valueOf(1));
        dataTypes.put(Integer.class, Integer.valueOf(2));
        dataTypes.put(Float.class, Integer.valueOf(3));
        dataTypes.put(String.class, Integer.valueOf(4));
        dataTypes.put(ItemStack.class, Integer.valueOf(5));
        dataTypes.put(BlockPos.class, Integer.valueOf(6));
        dataTypes.put(Rotations.class, Integer.valueOf(7));
    }

    public static class WatchableObject
    {
        private final int objectType;
        private final int dataValueId;
        private Object watchedObject;
        private boolean watched;

        public WatchableObject(int type, int id, Object object)
        {
            this.dataValueId = id;
            this.watchedObject = object;
            this.objectType = type;
            this.watched = true;
        }

        public int getDataValueId()
        {
            return this.dataValueId;
        }

        public void setObject(Object object)
        {
            this.watchedObject = object;
        }

        public Object getObject()
        {
            return this.watchedObject;
        }

        public int getObjectType()
        {
            return this.objectType;
        }

        public boolean isWatched()
        {
            return this.watched;
        }

        public void setWatched(boolean watched)
        {
            this.watched = watched;
        }
    }
}
