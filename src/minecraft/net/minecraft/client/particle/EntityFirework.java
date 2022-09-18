package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFirework
{
    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            EntityFirework.SparkFX lvt_16_1_ = new EntityFirework.SparkFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Minecraft.getMinecraft().effectRenderer);
            lvt_16_1_.setAlphaF(0.99F);
            return lvt_16_1_;
        }
    }

    public static class OverlayFX extends EntityFX
    {
        protected OverlayFX(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_)
        {
            super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
            this.particleMaxAge = 4;
        }

        public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
        {
            float lvt_9_1_ = 0.25F;
            float lvt_10_1_ = 0.5F;
            float lvt_11_1_ = 0.125F;
            float lvt_12_1_ = 0.375F;
            float lvt_13_1_ = 7.1F * MathHelper.sin(((float)this.particleAge + partialTicks - 1.0F) * 0.25F * (float)Math.PI);
            this.particleAlpha = 0.6F - ((float)this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F;
            float lvt_14_1_ = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
            float lvt_15_1_ = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
            float lvt_16_1_ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
            int lvt_17_1_ = this.getBrightnessForRender(partialTicks);
            int lvt_18_1_ = lvt_17_1_ >> 16 & 65535;
            int lvt_19_1_ = lvt_17_1_ & 65535;
            worldRendererIn.pos((double)(lvt_14_1_ - rotationX * lvt_13_1_ - rotationXY * lvt_13_1_), (double)(lvt_15_1_ - rotationZ * lvt_13_1_), (double)(lvt_16_1_ - rotationYZ * lvt_13_1_ - rotationXZ * lvt_13_1_)).tex(0.5D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
            worldRendererIn.pos((double)(lvt_14_1_ - rotationX * lvt_13_1_ + rotationXY * lvt_13_1_), (double)(lvt_15_1_ + rotationZ * lvt_13_1_), (double)(lvt_16_1_ - rotationYZ * lvt_13_1_ + rotationXZ * lvt_13_1_)).tex(0.5D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
            worldRendererIn.pos((double)(lvt_14_1_ + rotationX * lvt_13_1_ + rotationXY * lvt_13_1_), (double)(lvt_15_1_ + rotationZ * lvt_13_1_), (double)(lvt_16_1_ + rotationYZ * lvt_13_1_ + rotationXZ * lvt_13_1_)).tex(0.25D, 0.125D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
            worldRendererIn.pos((double)(lvt_14_1_ + rotationX * lvt_13_1_ - rotationXY * lvt_13_1_), (double)(lvt_15_1_ - rotationZ * lvt_13_1_), (double)(lvt_16_1_ + rotationYZ * lvt_13_1_ - rotationXZ * lvt_13_1_)).tex(0.25D, 0.375D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        }
    }

    public static class SparkFX extends EntityFX
    {
        private int baseTextureIndex = 160;
        private boolean trail;
        private boolean twinkle;
        private final EffectRenderer field_92047_az;
        private float fadeColourRed;
        private float fadeColourGreen;
        private float fadeColourBlue;
        private boolean hasFadeColour;

        public SparkFX(World p_i46465_1_, double p_i46465_2_, double p_i46465_4_, double p_i46465_6_, double p_i46465_8_, double p_i46465_10_, double p_i46465_12_, EffectRenderer p_i46465_14_)
        {
            super(p_i46465_1_, p_i46465_2_, p_i46465_4_, p_i46465_6_);
            this.motionX = p_i46465_8_;
            this.motionY = p_i46465_10_;
            this.motionZ = p_i46465_12_;
            this.field_92047_az = p_i46465_14_;
            this.particleScale *= 0.75F;
            this.particleMaxAge = 48 + this.rand.nextInt(12);
            this.noClip = false;
        }

        public void setTrail(boolean trailIn)
        {
            this.trail = trailIn;
        }

        public void setTwinkle(boolean twinkleIn)
        {
            this.twinkle = twinkleIn;
        }

        public void setColour(int colour)
        {
            float lvt_2_1_ = (float)((colour & 16711680) >> 16) / 255.0F;
            float lvt_3_1_ = (float)((colour & 65280) >> 8) / 255.0F;
            float lvt_4_1_ = (float)((colour & 255) >> 0) / 255.0F;
            float lvt_5_1_ = 1.0F;
            this.setRBGColorF(lvt_2_1_ * lvt_5_1_, lvt_3_1_ * lvt_5_1_, lvt_4_1_ * lvt_5_1_);
        }

        public void setFadeColour(int faceColour)
        {
            this.fadeColourRed = (float)((faceColour & 16711680) >> 16) / 255.0F;
            this.fadeColourGreen = (float)((faceColour & 65280) >> 8) / 255.0F;
            this.fadeColourBlue = (float)((faceColour & 255) >> 0) / 255.0F;
            this.hasFadeColour = true;
        }

        public AxisAlignedBB getCollisionBoundingBox()
        {
            return null;
        }

        public boolean canBePushed()
        {
            return false;
        }

        public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
        {
            if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0)
            {
                super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
            }
        }

        public void onUpdate()
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.particleAge++ >= this.particleMaxAge)
            {
                this.setDead();
            }

            if (this.particleAge > this.particleMaxAge / 2)
            {
                this.setAlphaF(1.0F - ((float)this.particleAge - (float)(this.particleMaxAge / 2)) / (float)this.particleMaxAge);

                if (this.hasFadeColour)
                {
                    this.particleRed += (this.fadeColourRed - this.particleRed) * 0.2F;
                    this.particleGreen += (this.fadeColourGreen - this.particleGreen) * 0.2F;
                    this.particleBlue += (this.fadeColourBlue - this.particleBlue) * 0.2F;
                }
            }

            this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
            this.motionY -= 0.004D;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9100000262260437D;
            this.motionY *= 0.9100000262260437D;
            this.motionZ *= 0.9100000262260437D;

            if (this.onGround)
            {
                this.motionX *= 0.699999988079071D;
                this.motionZ *= 0.699999988079071D;
            }

            if (this.trail && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0)
            {
                EntityFirework.SparkFX lvt_1_1_ = new EntityFirework.SparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.field_92047_az);
                lvt_1_1_.setAlphaF(0.99F);
                lvt_1_1_.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
                lvt_1_1_.particleAge = lvt_1_1_.particleMaxAge / 2;

                if (this.hasFadeColour)
                {
                    lvt_1_1_.hasFadeColour = true;
                    lvt_1_1_.fadeColourRed = this.fadeColourRed;
                    lvt_1_1_.fadeColourGreen = this.fadeColourGreen;
                    lvt_1_1_.fadeColourBlue = this.fadeColourBlue;
                }

                lvt_1_1_.twinkle = this.twinkle;
                this.field_92047_az.addEffect(lvt_1_1_);
            }
        }

        public int getBrightnessForRender(float partialTicks)
        {
            return 15728880;
        }

        public float getBrightness(float partialTicks)
        {
            return 1.0F;
        }
    }

    public static class StarterFX extends EntityFX
    {
        private int fireworkAge;
        private final EffectRenderer theEffectRenderer;
        private NBTTagList fireworkExplosions;
        boolean twinkle;

        public StarterFX(World p_i46464_1_, double p_i46464_2_, double p_i46464_4_, double p_i46464_6_, double p_i46464_8_, double p_i46464_10_, double p_i46464_12_, EffectRenderer p_i46464_14_, NBTTagCompound p_i46464_15_)
        {
            super(p_i46464_1_, p_i46464_2_, p_i46464_4_, p_i46464_6_, 0.0D, 0.0D, 0.0D);
            this.motionX = p_i46464_8_;
            this.motionY = p_i46464_10_;
            this.motionZ = p_i46464_12_;
            this.theEffectRenderer = p_i46464_14_;
            this.particleMaxAge = 8;

            if (p_i46464_15_ != null)
            {
                this.fireworkExplosions = p_i46464_15_.getTagList("Explosions", 10);

                if (this.fireworkExplosions.tagCount() == 0)
                {
                    this.fireworkExplosions = null;
                }
                else
                {
                    this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

                    for (int lvt_16_1_ = 0; lvt_16_1_ < this.fireworkExplosions.tagCount(); ++lvt_16_1_)
                    {
                        NBTTagCompound lvt_17_1_ = this.fireworkExplosions.getCompoundTagAt(lvt_16_1_);

                        if (lvt_17_1_.getBoolean("Flicker"))
                        {
                            this.twinkle = true;
                            this.particleMaxAge += 15;
                            break;
                        }
                    }
                }
            }
        }

        public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
        {
        }

        public void onUpdate()
        {
            if (this.fireworkAge == 0 && this.fireworkExplosions != null)
            {
                boolean lvt_1_1_ = this.func_92037_i();
                boolean lvt_2_1_ = false;

                if (this.fireworkExplosions.tagCount() >= 3)
                {
                    lvt_2_1_ = true;
                }
                else
                {
                    for (int lvt_3_1_ = 0; lvt_3_1_ < this.fireworkExplosions.tagCount(); ++lvt_3_1_)
                    {
                        NBTTagCompound lvt_4_1_ = this.fireworkExplosions.getCompoundTagAt(lvt_3_1_);

                        if (lvt_4_1_.getByte("Type") == 1)
                        {
                            lvt_2_1_ = true;
                            break;
                        }
                    }
                }

                String lvt_3_2_ = "fireworks." + (lvt_2_1_ ? "largeBlast" : "blast") + (lvt_1_1_ ? "_far" : "");
                this.worldObj.playSound(this.posX, this.posY, this.posZ, lvt_3_2_, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
            }

            if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount())
            {
                int lvt_1_2_ = this.fireworkAge / 2;
                NBTTagCompound lvt_2_2_ = this.fireworkExplosions.getCompoundTagAt(lvt_1_2_);
                int lvt_3_3_ = lvt_2_2_.getByte("Type");
                boolean lvt_4_2_ = lvt_2_2_.getBoolean("Trail");
                boolean lvt_5_1_ = lvt_2_2_.getBoolean("Flicker");
                int[] lvt_6_1_ = lvt_2_2_.getIntArray("Colors");
                int[] lvt_7_1_ = lvt_2_2_.getIntArray("FadeColors");

                if (lvt_6_1_.length == 0)
                {
                    lvt_6_1_ = new int[] {ItemDye.dyeColors[0]};
                }

                if (lvt_3_3_ == 1)
                {
                    this.createBall(0.5D, 4, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_);
                }
                else if (lvt_3_3_ == 2)
                {
                    this.createShaped(0.5D, new double[][] {{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_, false);
                }
                else if (lvt_3_3_ == 3)
                {
                    this.createShaped(0.5D, new double[][] {{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_, true);
                }
                else if (lvt_3_3_ == 4)
                {
                    this.createBurst(lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_);
                }
                else
                {
                    this.createBall(0.25D, 2, lvt_6_1_, lvt_7_1_, lvt_4_2_, lvt_5_1_);
                }

                int lvt_8_1_ = lvt_6_1_[0];
                float lvt_9_1_ = (float)((lvt_8_1_ & 16711680) >> 16) / 255.0F;
                float lvt_10_1_ = (float)((lvt_8_1_ & 65280) >> 8) / 255.0F;
                float lvt_11_1_ = (float)((lvt_8_1_ & 255) >> 0) / 255.0F;
                EntityFirework.OverlayFX lvt_12_1_ = new EntityFirework.OverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
                lvt_12_1_.setRBGColorF(lvt_9_1_, lvt_10_1_, lvt_11_1_);
                this.theEffectRenderer.addEffect(lvt_12_1_);
            }

            ++this.fireworkAge;

            if (this.fireworkAge > this.particleMaxAge)
            {
                if (this.twinkle)
                {
                    boolean lvt_1_3_ = this.func_92037_i();
                    String lvt_2_3_ = "fireworks." + (lvt_1_3_ ? "twinkle_far" : "twinkle");
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, lvt_2_3_, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
                }

                this.setDead();
            }
        }

        private boolean func_92037_i()
        {
            Minecraft lvt_1_1_ = Minecraft.getMinecraft();
            return lvt_1_1_ == null || lvt_1_1_.getRenderViewEntity() == null || lvt_1_1_.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0D;
        }

        private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_)
        {
            EntityFirework.SparkFX lvt_17_1_ = new EntityFirework.SparkFX(this.worldObj, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_, this.theEffectRenderer);
            lvt_17_1_.setAlphaF(0.99F);
            lvt_17_1_.setTrail(p_92034_15_);
            lvt_17_1_.setTwinkle(p_92034_16_);
            int lvt_18_1_ = this.rand.nextInt(p_92034_13_.length);
            lvt_17_1_.setColour(p_92034_13_[lvt_18_1_]);

            if (p_92034_14_ != null && p_92034_14_.length > 0)
            {
                lvt_17_1_.setFadeColour(p_92034_14_[this.rand.nextInt(p_92034_14_.length)]);
            }

            this.theEffectRenderer.addEffect(lvt_17_1_);
        }

        private void createBall(double speed, int size, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn)
        {
            double lvt_8_1_ = this.posX;
            double lvt_10_1_ = this.posY;
            double lvt_12_1_ = this.posZ;

            for (int lvt_14_1_ = -size; lvt_14_1_ <= size; ++lvt_14_1_)
            {
                for (int lvt_15_1_ = -size; lvt_15_1_ <= size; ++lvt_15_1_)
                {
                    for (int lvt_16_1_ = -size; lvt_16_1_ <= size; ++lvt_16_1_)
                    {
                        double lvt_17_1_ = (double)lvt_15_1_ + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                        double lvt_19_1_ = (double)lvt_14_1_ + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                        double lvt_21_1_ = (double)lvt_16_1_ + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                        double lvt_23_1_ = (double)MathHelper.sqrt_double(lvt_17_1_ * lvt_17_1_ + lvt_19_1_ * lvt_19_1_ + lvt_21_1_ * lvt_21_1_) / speed + this.rand.nextGaussian() * 0.05D;
                        this.createParticle(lvt_8_1_, lvt_10_1_, lvt_12_1_, lvt_17_1_ / lvt_23_1_, lvt_19_1_ / lvt_23_1_, lvt_21_1_ / lvt_23_1_, colours, fadeColours, trail, twinkleIn);

                        if (lvt_14_1_ != -size && lvt_14_1_ != size && lvt_15_1_ != -size && lvt_15_1_ != size)
                        {
                            lvt_16_1_ += size * 2 - 1;
                        }
                    }
                }
            }
        }

        private void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean p_92038_8_)
        {
            double lvt_9_1_ = shape[0][0];
            double lvt_11_1_ = shape[0][1];
            this.createParticle(this.posX, this.posY, this.posZ, lvt_9_1_ * speed, lvt_11_1_ * speed, 0.0D, colours, fadeColours, trail, twinkleIn);
            float lvt_13_1_ = this.rand.nextFloat() * (float)Math.PI;
            double lvt_14_1_ = p_92038_8_ ? 0.034D : 0.34D;

            for (int lvt_16_1_ = 0; lvt_16_1_ < 3; ++lvt_16_1_)
            {
                double lvt_17_1_ = (double)lvt_13_1_ + (double)((float)lvt_16_1_ * (float)Math.PI) * lvt_14_1_;
                double lvt_19_1_ = lvt_9_1_;
                double lvt_21_1_ = lvt_11_1_;

                for (int lvt_23_1_ = 1; lvt_23_1_ < shape.length; ++lvt_23_1_)
                {
                    double lvt_24_1_ = shape[lvt_23_1_][0];
                    double lvt_26_1_ = shape[lvt_23_1_][1];

                    for (double lvt_28_1_ = 0.25D; lvt_28_1_ <= 1.0D; lvt_28_1_ += 0.25D)
                    {
                        double lvt_30_1_ = (lvt_19_1_ + (lvt_24_1_ - lvt_19_1_) * lvt_28_1_) * speed;
                        double lvt_32_1_ = (lvt_21_1_ + (lvt_26_1_ - lvt_21_1_) * lvt_28_1_) * speed;
                        double lvt_34_1_ = lvt_30_1_ * Math.sin(lvt_17_1_);
                        lvt_30_1_ = lvt_30_1_ * Math.cos(lvt_17_1_);

                        for (double lvt_36_1_ = -1.0D; lvt_36_1_ <= 1.0D; lvt_36_1_ += 2.0D)
                        {
                            this.createParticle(this.posX, this.posY, this.posZ, lvt_30_1_ * lvt_36_1_, lvt_32_1_, lvt_34_1_ * lvt_36_1_, colours, fadeColours, trail, twinkleIn);
                        }
                    }

                    lvt_19_1_ = lvt_24_1_;
                    lvt_21_1_ = lvt_26_1_;
                }
            }
        }

        private void createBurst(int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn)
        {
            double lvt_5_1_ = this.rand.nextGaussian() * 0.05D;
            double lvt_7_1_ = this.rand.nextGaussian() * 0.05D;

            for (int lvt_9_1_ = 0; lvt_9_1_ < 70; ++lvt_9_1_)
            {
                double lvt_10_1_ = this.motionX * 0.5D + this.rand.nextGaussian() * 0.15D + lvt_5_1_;
                double lvt_12_1_ = this.motionZ * 0.5D + this.rand.nextGaussian() * 0.15D + lvt_7_1_;
                double lvt_14_1_ = this.motionY * 0.5D + this.rand.nextDouble() * 0.5D;
                this.createParticle(this.posX, this.posY, this.posZ, lvt_10_1_, lvt_14_1_, lvt_12_1_, colours, fadeColours, trail, twinkleIn);
            }
        }

        public int getFXLayer()
        {
            return 0;
        }
    }
}
