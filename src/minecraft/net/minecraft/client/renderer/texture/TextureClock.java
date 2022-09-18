package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class TextureClock extends TextureAtlasSprite
{
    private double currentAngle;
    private double angleDelta;

    public TextureClock(String iconName)
    {
        super(iconName);
    }

    public void updateAnimation()
    {
        if (!this.framesTextureData.isEmpty())
        {
            Minecraft lvt_1_1_ = Minecraft.getMinecraft();
            double lvt_2_1_ = 0.0D;

            if (lvt_1_1_.theWorld != null && lvt_1_1_.thePlayer != null)
            {
                lvt_2_1_ = (double)lvt_1_1_.theWorld.getCelestialAngle(1.0F);

                if (!lvt_1_1_.theWorld.provider.isSurfaceWorld())
                {
                    lvt_2_1_ = Math.random();
                }
            }

            double lvt_4_1_;

            for (lvt_4_1_ = lvt_2_1_ - this.currentAngle; lvt_4_1_ < -0.5D; ++lvt_4_1_)
            {
                ;
            }

            while (lvt_4_1_ >= 0.5D)
            {
                --lvt_4_1_;
            }

            lvt_4_1_ = MathHelper.clamp_double(lvt_4_1_, -1.0D, 1.0D);
            this.angleDelta += lvt_4_1_ * 0.1D;
            this.angleDelta *= 0.8D;
            this.currentAngle += this.angleDelta;
            int lvt_6_1_;

            for (lvt_6_1_ = (int)((this.currentAngle + 1.0D) * (double)this.framesTextureData.size()) % this.framesTextureData.size(); lvt_6_1_ < 0; lvt_6_1_ = (lvt_6_1_ + this.framesTextureData.size()) % this.framesTextureData.size())
            {
                ;
            }

            if (lvt_6_1_ != this.frameCounter)
            {
                this.frameCounter = lvt_6_1_;
                TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
    }
}
