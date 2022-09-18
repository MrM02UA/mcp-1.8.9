package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public abstract class AbstractClientPlayer extends EntityPlayer
{
    private NetworkPlayerInfo playerInfo;

    public AbstractClientPlayer(World worldIn, GameProfile playerProfile)
    {
        super(worldIn, playerProfile);
    }

    /**
     * Returns true if the player is in spectator mode.
     */
    public boolean isSpectator()
    {
        NetworkPlayerInfo lvt_1_1_ = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
        return lvt_1_1_ != null && lvt_1_1_.getGameType() == WorldSettings.GameType.SPECTATOR;
    }

    /**
     * Checks if this instance of AbstractClientPlayer has any associated player data.
     */
    public boolean hasPlayerInfo()
    {
        return this.getPlayerInfo() != null;
    }

    protected NetworkPlayerInfo getPlayerInfo()
    {
        if (this.playerInfo == null)
        {
            this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
        }

        return this.playerInfo;
    }

    /**
     * Returns true if the player has an associated skin.
     */
    public boolean hasSkin()
    {
        NetworkPlayerInfo lvt_1_1_ = this.getPlayerInfo();
        return lvt_1_1_ != null && lvt_1_1_.hasLocationSkin();
    }

    /**
     * Returns true if the player instance has an associated skin.
     */
    public ResourceLocation getLocationSkin()
    {
        NetworkPlayerInfo lvt_1_1_ = this.getPlayerInfo();
        return lvt_1_1_ == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : lvt_1_1_.getLocationSkin();
    }

    public ResourceLocation getLocationCape()
    {
        NetworkPlayerInfo lvt_1_1_ = this.getPlayerInfo();
        return lvt_1_1_ == null ? null : lvt_1_1_.getLocationCape();
    }

    public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username)
    {
        TextureManager lvt_2_1_ = Minecraft.getMinecraft().getTextureManager();
        ITextureObject lvt_3_1_ = lvt_2_1_.getTexture(resourceLocationIn);

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = new ThreadDownloadImageData((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] {StringUtils.stripControlCodes(username)}), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
            lvt_2_1_.loadTexture(resourceLocationIn, lvt_3_1_);
        }

        return (ThreadDownloadImageData)lvt_3_1_;
    }

    /**
     * Returns true if the username has an associated skin.
     */
    public static ResourceLocation getLocationSkin(String username)
    {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
    }

    public String getSkinType()
    {
        NetworkPlayerInfo lvt_1_1_ = this.getPlayerInfo();
        return lvt_1_1_ == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : lvt_1_1_.getSkinType();
    }

    public float getFovModifier()
    {
        float lvt_1_1_ = 1.0F;

        if (this.capabilities.isFlying)
        {
            lvt_1_1_ *= 1.1F;
        }

        IAttributeInstance lvt_2_1_ = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        lvt_1_1_ = (float)((double)lvt_1_1_ * ((lvt_2_1_.getAttributeValue() / (double)this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(lvt_1_1_) || Float.isInfinite(lvt_1_1_))
        {
            lvt_1_1_ = 1.0F;
        }

        if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow)
        {
            int lvt_3_1_ = this.getItemInUseDuration();
            float lvt_4_1_ = (float)lvt_3_1_ / 20.0F;

            if (lvt_4_1_ > 1.0F)
            {
                lvt_4_1_ = 1.0F;
            }
            else
            {
                lvt_4_1_ = lvt_4_1_ * lvt_4_1_;
            }

            lvt_1_1_ *= 1.0F - lvt_4_1_ * 0.15F;
        }

        return lvt_1_1_;
    }
}
