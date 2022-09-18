package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity
{
    private int skullType;
    private int skullRotation;
    private GameProfile playerProfile = null;

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setByte("SkullType", (byte)(this.skullType & 255));
        compound.setByte("Rot", (byte)(this.skullRotation & 255));

        if (this.playerProfile != null)
        {
            NBTTagCompound lvt_2_1_ = new NBTTagCompound();
            NBTUtil.writeGameProfile(lvt_2_1_, this.playerProfile);
            compound.setTag("Owner", lvt_2_1_);
        }
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.skullType = compound.getByte("SkullType");
        this.skullRotation = compound.getByte("Rot");

        if (this.skullType == 3)
        {
            if (compound.hasKey("Owner", 10))
            {
                this.playerProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner"));
            }
            else if (compound.hasKey("ExtraType", 8))
            {
                String lvt_2_1_ = compound.getString("ExtraType");

                if (!StringUtils.isNullOrEmpty(lvt_2_1_))
                {
                    this.playerProfile = new GameProfile((UUID)null, lvt_2_1_);
                    this.updatePlayerProfile();
                }
            }
        }
    }

    public GameProfile getPlayerProfile()
    {
        return this.playerProfile;
    }

    /**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();
        this.writeToNBT(lvt_1_1_);
        return new S35PacketUpdateTileEntity(this.pos, 4, lvt_1_1_);
    }

    public void setType(int type)
    {
        this.skullType = type;
        this.playerProfile = null;
    }

    public void setPlayerProfile(GameProfile playerProfile)
    {
        this.skullType = 3;
        this.playerProfile = playerProfile;
        this.updatePlayerProfile();
    }

    private void updatePlayerProfile()
    {
        this.playerProfile = updateGameprofile(this.playerProfile);
        this.markDirty();
    }

    public static GameProfile updateGameprofile(GameProfile input)
    {
        if (input != null && !StringUtils.isNullOrEmpty(input.getName()))
        {
            if (input.isComplete() && input.getProperties().containsKey("textures"))
            {
                return input;
            }
            else if (MinecraftServer.getServer() == null)
            {
                return input;
            }
            else
            {
                GameProfile lvt_1_1_ = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(input.getName());

                if (lvt_1_1_ == null)
                {
                    return input;
                }
                else
                {
                    Property lvt_2_1_ = (Property)Iterables.getFirst(lvt_1_1_.getProperties().get("textures"), (Object)null);

                    if (lvt_2_1_ == null)
                    {
                        lvt_1_1_ = MinecraftServer.getServer().getMinecraftSessionService().fillProfileProperties(lvt_1_1_, true);
                    }

                    return lvt_1_1_;
                }
            }
        }
        else
        {
            return input;
        }
    }

    public int getSkullType()
    {
        return this.skullType;
    }

    public int getSkullRotation()
    {
        return this.skullRotation;
    }

    public void setSkullRotation(int rotation)
    {
        this.skullRotation = rotation;
    }
}
