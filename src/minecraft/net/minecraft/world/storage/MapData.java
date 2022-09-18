package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec4b;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData
{
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;

    /** colours */
    public byte[] colors = new byte[16384];
    public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
    private Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
    public Map<String, Vec4b> mapDecorations = Maps.newLinkedHashMap();

    public MapData(String mapname)
    {
        super(mapname);
    }

    public void calculateMapCenter(double x, double z, int mapScale)
    {
        int lvt_6_1_ = 128 * (1 << mapScale);
        int lvt_7_1_ = MathHelper.floor_double((x + 64.0D) / (double)lvt_6_1_);
        int lvt_8_1_ = MathHelper.floor_double((z + 64.0D) / (double)lvt_6_1_);
        this.xCenter = lvt_7_1_ * lvt_6_1_ + lvt_6_1_ / 2 - 64;
        this.zCenter = lvt_8_1_ * lvt_6_1_ + lvt_6_1_ / 2 - 64;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.dimension = nbt.getByte("dimension");
        this.xCenter = nbt.getInteger("xCenter");
        this.zCenter = nbt.getInteger("zCenter");
        this.scale = nbt.getByte("scale");
        this.scale = (byte)MathHelper.clamp_int(this.scale, 0, 4);
        int lvt_2_1_ = nbt.getShort("width");
        int lvt_3_1_ = nbt.getShort("height");

        if (lvt_2_1_ == 128 && lvt_3_1_ == 128)
        {
            this.colors = nbt.getByteArray("colors");
        }
        else
        {
            byte[] lvt_4_1_ = nbt.getByteArray("colors");
            this.colors = new byte[16384];
            int lvt_5_1_ = (128 - lvt_2_1_) / 2;
            int lvt_6_1_ = (128 - lvt_3_1_) / 2;

            for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_3_1_; ++lvt_7_1_)
            {
                int lvt_8_1_ = lvt_7_1_ + lvt_6_1_;

                if (lvt_8_1_ >= 0 || lvt_8_1_ < 128)
                {
                    for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_2_1_; ++lvt_9_1_)
                    {
                        int lvt_10_1_ = lvt_9_1_ + lvt_5_1_;

                        if (lvt_10_1_ >= 0 || lvt_10_1_ < 128)
                        {
                            this.colors[lvt_10_1_ + lvt_8_1_ * 128] = lvt_4_1_[lvt_9_1_ + lvt_7_1_ * lvt_2_1_];
                        }
                    }
                }
            }
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setByte("dimension", this.dimension);
        nbt.setInteger("xCenter", this.xCenter);
        nbt.setInteger("zCenter", this.zCenter);
        nbt.setByte("scale", this.scale);
        nbt.setShort("width", (short)128);
        nbt.setShort("height", (short)128);
        nbt.setByteArray("colors", this.colors);
    }

    /**
     * Adds the player passed to the list of visible players and checks to see which players are visible
     */
    public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack)
    {
        if (!this.playersHashMap.containsKey(player))
        {
            MapData.MapInfo lvt_3_1_ = new MapData.MapInfo(player);
            this.playersHashMap.put(player, lvt_3_1_);
            this.playersArrayList.add(lvt_3_1_);
        }

        if (!player.inventory.hasItemStack(mapStack))
        {
            this.mapDecorations.remove(player.getName());
        }

        for (int lvt_3_2_ = 0; lvt_3_2_ < this.playersArrayList.size(); ++lvt_3_2_)
        {
            MapData.MapInfo lvt_4_1_ = (MapData.MapInfo)this.playersArrayList.get(lvt_3_2_);

            if (!lvt_4_1_.entityplayerObj.isDead && (lvt_4_1_.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame()))
            {
                if (!mapStack.isOnItemFrame() && lvt_4_1_.entityplayerObj.dimension == this.dimension)
                {
                    this.updateDecorations(0, lvt_4_1_.entityplayerObj.worldObj, lvt_4_1_.entityplayerObj.getName(), lvt_4_1_.entityplayerObj.posX, lvt_4_1_.entityplayerObj.posZ, (double)lvt_4_1_.entityplayerObj.rotationYaw);
                }
            }
            else
            {
                this.playersHashMap.remove(lvt_4_1_.entityplayerObj);
                this.playersArrayList.remove(lvt_4_1_);
            }
        }

        if (mapStack.isOnItemFrame())
        {
            EntityItemFrame lvt_3_3_ = mapStack.getItemFrame();
            BlockPos lvt_4_2_ = lvt_3_3_.getHangingPosition();
            this.updateDecorations(1, player.worldObj, "frame-" + lvt_3_3_.getEntityId(), (double)lvt_4_2_.getX(), (double)lvt_4_2_.getZ(), (double)(lvt_3_3_.facingDirection.getHorizontalIndex() * 90));
        }

        if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9))
        {
            NBTTagList lvt_3_4_ = mapStack.getTagCompound().getTagList("Decorations", 10);

            for (int lvt_4_3_ = 0; lvt_4_3_ < lvt_3_4_.tagCount(); ++lvt_4_3_)
            {
                NBTTagCompound lvt_5_1_ = lvt_3_4_.getCompoundTagAt(lvt_4_3_);

                if (!this.mapDecorations.containsKey(lvt_5_1_.getString("id")))
                {
                    this.updateDecorations(lvt_5_1_.getByte("type"), player.worldObj, lvt_5_1_.getString("id"), lvt_5_1_.getDouble("x"), lvt_5_1_.getDouble("z"), lvt_5_1_.getDouble("rot"));
                }
            }
        }
    }

    private void updateDecorations(int type, World worldIn, String entityIdentifier, double worldX, double worldZ, double rotation)
    {
        int lvt_10_1_ = 1 << this.scale;
        float lvt_11_1_ = (float)(worldX - (double)this.xCenter) / (float)lvt_10_1_;
        float lvt_12_1_ = (float)(worldZ - (double)this.zCenter) / (float)lvt_10_1_;
        byte lvt_13_1_ = (byte)((int)((double)(lvt_11_1_ * 2.0F) + 0.5D));
        byte lvt_14_1_ = (byte)((int)((double)(lvt_12_1_ * 2.0F) + 0.5D));
        int lvt_16_1_ = 63;
        byte lvt_15_1_;

        if (lvt_11_1_ >= (float)(-lvt_16_1_) && lvt_12_1_ >= (float)(-lvt_16_1_) && lvt_11_1_ <= (float)lvt_16_1_ && lvt_12_1_ <= (float)lvt_16_1_)
        {
            rotation = rotation + (rotation < 0.0D ? -8.0D : 8.0D);
            lvt_15_1_ = (byte)((int)(rotation * 16.0D / 360.0D));

            if (this.dimension < 0)
            {
                int lvt_17_1_ = (int)(worldIn.getWorldInfo().getWorldTime() / 10L);
                lvt_15_1_ = (byte)(lvt_17_1_ * lvt_17_1_ * 34187121 + lvt_17_1_ * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(lvt_11_1_) >= 320.0F || Math.abs(lvt_12_1_) >= 320.0F)
            {
                this.mapDecorations.remove(entityIdentifier);
                return;
            }

            type = 6;
            lvt_15_1_ = 0;

            if (lvt_11_1_ <= (float)(-lvt_16_1_))
            {
                lvt_13_1_ = (byte)((int)((double)(lvt_16_1_ * 2) + 2.5D));
            }

            if (lvt_12_1_ <= (float)(-lvt_16_1_))
            {
                lvt_14_1_ = (byte)((int)((double)(lvt_16_1_ * 2) + 2.5D));
            }

            if (lvt_11_1_ >= (float)lvt_16_1_)
            {
                lvt_13_1_ = (byte)(lvt_16_1_ * 2 + 1);
            }

            if (lvt_12_1_ >= (float)lvt_16_1_)
            {
                lvt_14_1_ = (byte)(lvt_16_1_ * 2 + 1);
            }
        }

        this.mapDecorations.put(entityIdentifier, new Vec4b((byte)type, lvt_13_1_, lvt_14_1_, lvt_15_1_));
    }

    public Packet getMapPacket(ItemStack mapStack, World worldIn, EntityPlayer player)
    {
        MapData.MapInfo lvt_4_1_ = (MapData.MapInfo)this.playersHashMap.get(player);
        return lvt_4_1_ == null ? null : lvt_4_1_.getPacket(mapStack);
    }

    public void updateMapData(int x, int y)
    {
        super.markDirty();

        for (MapData.MapInfo lvt_4_1_ : this.playersArrayList)
        {
            lvt_4_1_.update(x, y);
        }
    }

    public MapData.MapInfo getMapInfo(EntityPlayer player)
    {
        MapData.MapInfo lvt_2_1_ = (MapData.MapInfo)this.playersHashMap.get(player);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = new MapData.MapInfo(player);
            this.playersHashMap.put(player, lvt_2_1_);
            this.playersArrayList.add(lvt_2_1_);
        }

        return lvt_2_1_;
    }

    public class MapInfo
    {
        public final EntityPlayer entityplayerObj;
        private boolean field_176105_d = true;
        private int minX = 0;
        private int minY = 0;
        private int maxX = 127;
        private int maxY = 127;
        private int field_176109_i;
        public int field_82569_d;

        public MapInfo(EntityPlayer player)
        {
            this.entityplayerObj = player;
        }

        public Packet getPacket(ItemStack stack)
        {
            if (this.field_176105_d)
            {
                this.field_176105_d = false;
                return new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.mapDecorations.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
            }
            else
            {
                return this.field_176109_i++ % 5 == 0 ? new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0) : null;
            }
        }

        public void update(int x, int y)
        {
            if (this.field_176105_d)
            {
                this.minX = Math.min(this.minX, x);
                this.minY = Math.min(this.minY, y);
                this.maxX = Math.max(this.maxX, x);
                this.maxY = Math.max(this.maxY, y);
            }
            else
            {
                this.field_176105_d = true;
                this.minX = x;
                this.minY = y;
                this.maxX = x;
                this.maxY = y;
            }
        }
    }
}
