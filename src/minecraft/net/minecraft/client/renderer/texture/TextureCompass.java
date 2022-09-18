package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TextureCompass extends TextureAtlasSprite
{
    /** Current compass heading in radians */
    public double currentAngle;

    /** Speed and direction of compass rotation */
    public double angleDelta;
    public static String locationSprite;

    public TextureCompass(String iconName)
    {
        super(iconName);
        locationSprite = iconName;
    }

    public void updateAnimation()
    {
        Minecraft lvt_1_1_ = Minecraft.getMinecraft();

        if (lvt_1_1_.theWorld != null && lvt_1_1_.thePlayer != null)
        {
            this.updateCompass(lvt_1_1_.theWorld, lvt_1_1_.thePlayer.posX, lvt_1_1_.thePlayer.posZ, (double)lvt_1_1_.thePlayer.rotationYaw, false, false);
        }
        else
        {
            this.updateCompass((World)null, 0.0D, 0.0D, 0.0D, true, false);
        }
    }

    /**
     * Updates the compass based on the given x,z coords and camera direction
     */
    public void updateCompass(World worldIn, double p_94241_2_, double p_94241_4_, double p_94241_6_, boolean p_94241_8_, boolean p_94241_9_)
    {
        if (!this.framesTextureData.isEmpty())
        {
            double lvt_10_1_ = 0.0D;

            if (worldIn != null && !p_94241_8_)
            {
                BlockPos lvt_12_1_ = worldIn.getSpawnPoint();
                double lvt_13_1_ = (double)lvt_12_1_.getX() - p_94241_2_;
                double lvt_15_1_ = (double)lvt_12_1_.getZ() - p_94241_4_;
                p_94241_6_ = p_94241_6_ % 360.0D;
                lvt_10_1_ = -((p_94241_6_ - 90.0D) * Math.PI / 180.0D - Math.atan2(lvt_15_1_, lvt_13_1_));

                if (!worldIn.provider.isSurfaceWorld())
                {
                    lvt_10_1_ = Math.random() * Math.PI * 2.0D;
                }
            }

            if (p_94241_9_)
            {
                this.currentAngle = lvt_10_1_;
            }
            else
            {
                double lvt_12_2_;

                for (lvt_12_2_ = lvt_10_1_ - this.currentAngle; lvt_12_2_ < -Math.PI; lvt_12_2_ += (Math.PI * 2D))
                {
                    ;
                }

                while (lvt_12_2_ >= Math.PI)
                {
                    lvt_12_2_ -= (Math.PI * 2D);
                }

                lvt_12_2_ = MathHelper.clamp_double(lvt_12_2_, -1.0D, 1.0D);
                this.angleDelta += lvt_12_2_ * 0.1D;
                this.angleDelta *= 0.8D;
                this.currentAngle += this.angleDelta;
            }

            int lvt_12_3_;

            for (lvt_12_3_ = (int)((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double)this.framesTextureData.size()) % this.framesTextureData.size(); lvt_12_3_ < 0; lvt_12_3_ = (lvt_12_3_ + this.framesTextureData.size()) % this.framesTextureData.size())
            {
                ;
            }

            if (lvt_12_3_ != this.frameCounter)
            {
                this.frameCounter = lvt_12_3_;
                TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
    }
}
