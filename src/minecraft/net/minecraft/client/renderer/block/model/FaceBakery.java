package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class FaceBakery
{
    private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
    private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((Math.PI / 4D)) - 1.0F;

    public BakedQuad makeBakedQuad(Vector3f posFrom, Vector3f posTo, BlockPartFace face, TextureAtlasSprite sprite, EnumFacing facing, ModelRotation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade)
    {
        int[] lvt_10_1_ = this.makeQuadVertexData(face, sprite, facing, this.getPositionsDiv16(posFrom, posTo), modelRotationIn, partRotation, uvLocked, shade);
        EnumFacing lvt_11_1_ = getFacingFromVertexData(lvt_10_1_);

        if (uvLocked)
        {
            this.lockUv(lvt_10_1_, lvt_11_1_, face.blockFaceUV, sprite);
        }

        if (partRotation == null)
        {
            this.applyFacing(lvt_10_1_, lvt_11_1_);
        }

        return new BakedQuad(lvt_10_1_, face.tintIndex, lvt_11_1_);
    }

    private int[] makeQuadVertexData(BlockPartFace partFace, TextureAtlasSprite sprite, EnumFacing facing, float[] p_178405_4_, ModelRotation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade)
    {
        int[] lvt_9_1_ = new int[28];

        for (int lvt_10_1_ = 0; lvt_10_1_ < 4; ++lvt_10_1_)
        {
            this.fillVertexData(lvt_9_1_, lvt_10_1_, facing, partFace, p_178405_4_, sprite, modelRotationIn, partRotation, uvLocked, shade);
        }

        return lvt_9_1_;
    }

    private int getFaceShadeColor(EnumFacing facing)
    {
        float lvt_2_1_ = this.getFaceBrightness(facing);
        int lvt_3_1_ = MathHelper.clamp_int((int)(lvt_2_1_ * 255.0F), 0, 255);
        return -16777216 | lvt_3_1_ << 16 | lvt_3_1_ << 8 | lvt_3_1_;
    }

    private float getFaceBrightness(EnumFacing facing)
    {
        switch (facing)
        {
            case DOWN:
                return 0.5F;

            case UP:
                return 1.0F;

            case NORTH:
            case SOUTH:
                return 0.8F;

            case WEST:
            case EAST:
                return 0.6F;

            default:
                return 1.0F;
        }
    }

    private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2)
    {
        float[] lvt_3_1_ = new float[EnumFacing.values().length];
        lvt_3_1_[EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0F;
        lvt_3_1_[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0F;
        lvt_3_1_[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0F;
        lvt_3_1_[EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0F;
        lvt_3_1_[EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0F;
        lvt_3_1_[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0F;
        return lvt_3_1_;
    }

    private void fillVertexData(int[] faceData, int vertexIndex, EnumFacing facing, BlockPartFace partFace, float[] p_178402_5_, TextureAtlasSprite sprite, ModelRotation modelRotationIn, BlockPartRotation partRotation, boolean uvLocked, boolean shade)
    {
        EnumFacing lvt_11_1_ = modelRotationIn.rotateFace(facing);
        int lvt_12_1_ = shade ? this.getFaceShadeColor(lvt_11_1_) : -1;
        EnumFaceDirection.VertexInformation lvt_13_1_ = EnumFaceDirection.getFacing(facing).getVertexInformation(vertexIndex);
        Vector3f lvt_14_1_ = new Vector3f(p_178402_5_[lvt_13_1_.xIndex], p_178402_5_[lvt_13_1_.yIndex], p_178402_5_[lvt_13_1_.zIndex]);
        this.rotatePart(lvt_14_1_, partRotation);
        int lvt_15_1_ = this.rotateVertex(lvt_14_1_, facing, vertexIndex, modelRotationIn, uvLocked);
        this.storeVertexData(faceData, lvt_15_1_, vertexIndex, lvt_14_1_, lvt_12_1_, sprite, partFace.blockFaceUV);
    }

    private void storeVertexData(int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV)
    {
        int lvt_8_1_ = storeIndex * 7;
        faceData[lvt_8_1_] = Float.floatToRawIntBits(position.x);
        faceData[lvt_8_1_ + 1] = Float.floatToRawIntBits(position.y);
        faceData[lvt_8_1_ + 2] = Float.floatToRawIntBits(position.z);
        faceData[lvt_8_1_ + 3] = shadeColor;
        faceData[lvt_8_1_ + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.func_178348_a(vertexIndex)));
        faceData[lvt_8_1_ + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.func_178346_b(vertexIndex)));
    }

    private void rotatePart(Vector3f p_178407_1_, BlockPartRotation partRotation)
    {
        if (partRotation != null)
        {
            Matrix4f lvt_3_1_ = this.getMatrixIdentity();
            Vector3f lvt_4_1_ = new Vector3f(0.0F, 0.0F, 0.0F);

            switch (partRotation.axis)
            {
                case X:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), lvt_3_1_, lvt_3_1_);
                    lvt_4_1_.set(0.0F, 1.0F, 1.0F);
                    break;

                case Y:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), lvt_3_1_, lvt_3_1_);
                    lvt_4_1_.set(1.0F, 0.0F, 1.0F);
                    break;

                case Z:
                    Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), lvt_3_1_, lvt_3_1_);
                    lvt_4_1_.set(1.0F, 1.0F, 0.0F);
            }

            if (partRotation.rescale)
            {
                if (Math.abs(partRotation.angle) == 22.5F)
                {
                    lvt_4_1_.scale(SCALE_ROTATION_22_5);
                }
                else
                {
                    lvt_4_1_.scale(SCALE_ROTATION_GENERAL);
                }

                Vector3f.add(lvt_4_1_, new Vector3f(1.0F, 1.0F, 1.0F), lvt_4_1_);
            }
            else
            {
                lvt_4_1_.set(1.0F, 1.0F, 1.0F);
            }

            this.rotateScale(p_178407_1_, new Vector3f(partRotation.origin), lvt_3_1_, lvt_4_1_);
        }
    }

    public int rotateVertex(Vector3f position, EnumFacing facing, int vertexIndex, ModelRotation modelRotationIn, boolean uvLocked)
    {
        if (modelRotationIn == ModelRotation.X0_Y0)
        {
            return vertexIndex;
        }
        else
        {
            this.rotateScale(position, new Vector3f(0.5F, 0.5F, 0.5F), modelRotationIn.getMatrix4d(), new Vector3f(1.0F, 1.0F, 1.0F));
            return modelRotationIn.rotateVertex(facing, vertexIndex);
        }
    }

    private void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale)
    {
        Vector4f lvt_5_1_ = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
        Matrix4f.transform(rotationMatrix, lvt_5_1_, lvt_5_1_);
        lvt_5_1_.x *= scale.x;
        lvt_5_1_.y *= scale.y;
        lvt_5_1_.z *= scale.z;
        position.set(lvt_5_1_.x + rotationOrigin.x, lvt_5_1_.y + rotationOrigin.y, lvt_5_1_.z + rotationOrigin.z);
    }

    private Matrix4f getMatrixIdentity()
    {
        Matrix4f lvt_1_1_ = new Matrix4f();
        lvt_1_1_.setIdentity();
        return lvt_1_1_;
    }

    public static EnumFacing getFacingFromVertexData(int[] faceData)
    {
        Vector3f lvt_1_1_ = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
        Vector3f lvt_2_1_ = new Vector3f(Float.intBitsToFloat(faceData[7]), Float.intBitsToFloat(faceData[8]), Float.intBitsToFloat(faceData[9]));
        Vector3f lvt_3_1_ = new Vector3f(Float.intBitsToFloat(faceData[14]), Float.intBitsToFloat(faceData[15]), Float.intBitsToFloat(faceData[16]));
        Vector3f lvt_4_1_ = new Vector3f();
        Vector3f lvt_5_1_ = new Vector3f();
        Vector3f lvt_6_1_ = new Vector3f();
        Vector3f.sub(lvt_1_1_, lvt_2_1_, lvt_4_1_);
        Vector3f.sub(lvt_3_1_, lvt_2_1_, lvt_5_1_);
        Vector3f.cross(lvt_5_1_, lvt_4_1_, lvt_6_1_);
        float lvt_7_1_ = (float)Math.sqrt((double)(lvt_6_1_.x * lvt_6_1_.x + lvt_6_1_.y * lvt_6_1_.y + lvt_6_1_.z * lvt_6_1_.z));
        lvt_6_1_.x /= lvt_7_1_;
        lvt_6_1_.y /= lvt_7_1_;
        lvt_6_1_.z /= lvt_7_1_;
        EnumFacing lvt_8_1_ = null;
        float lvt_9_1_ = 0.0F;

        for (EnumFacing lvt_13_1_ : EnumFacing.values())
        {
            Vec3i lvt_14_1_ = lvt_13_1_.getDirectionVec();
            Vector3f lvt_15_1_ = new Vector3f((float)lvt_14_1_.getX(), (float)lvt_14_1_.getY(), (float)lvt_14_1_.getZ());
            float lvt_16_1_ = Vector3f.dot(lvt_6_1_, lvt_15_1_);

            if (lvt_16_1_ >= 0.0F && lvt_16_1_ > lvt_9_1_)
            {
                lvt_9_1_ = lvt_16_1_;
                lvt_8_1_ = lvt_13_1_;
            }
        }

        if (lvt_8_1_ == null)
        {
            return EnumFacing.UP;
        }
        else
        {
            return lvt_8_1_;
        }
    }

    public void lockUv(int[] p_178409_1_, EnumFacing facing, BlockFaceUV p_178409_3_, TextureAtlasSprite p_178409_4_)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_)
        {
            this.lockVertexUv(lvt_5_1_, p_178409_1_, facing, p_178409_3_, p_178409_4_);
        }
    }

    private void applyFacing(int[] p_178408_1_, EnumFacing p_178408_2_)
    {
        int[] lvt_3_1_ = new int[p_178408_1_.length];
        System.arraycopy(p_178408_1_, 0, lvt_3_1_, 0, p_178408_1_.length);
        float[] lvt_4_1_ = new float[EnumFacing.values().length];
        lvt_4_1_[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
        lvt_4_1_[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
        lvt_4_1_[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
        lvt_4_1_[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
        lvt_4_1_[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
        lvt_4_1_[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;

        for (int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_)
        {
            int lvt_6_1_ = 7 * lvt_5_1_;
            float lvt_7_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_6_1_]);
            float lvt_8_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_6_1_ + 1]);
            float lvt_9_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_6_1_ + 2]);

            if (lvt_7_1_ < lvt_4_1_[EnumFaceDirection.Constants.WEST_INDEX])
            {
                lvt_4_1_[EnumFaceDirection.Constants.WEST_INDEX] = lvt_7_1_;
            }

            if (lvt_8_1_ < lvt_4_1_[EnumFaceDirection.Constants.DOWN_INDEX])
            {
                lvt_4_1_[EnumFaceDirection.Constants.DOWN_INDEX] = lvt_8_1_;
            }

            if (lvt_9_1_ < lvt_4_1_[EnumFaceDirection.Constants.NORTH_INDEX])
            {
                lvt_4_1_[EnumFaceDirection.Constants.NORTH_INDEX] = lvt_9_1_;
            }

            if (lvt_7_1_ > lvt_4_1_[EnumFaceDirection.Constants.EAST_INDEX])
            {
                lvt_4_1_[EnumFaceDirection.Constants.EAST_INDEX] = lvt_7_1_;
            }

            if (lvt_8_1_ > lvt_4_1_[EnumFaceDirection.Constants.UP_INDEX])
            {
                lvt_4_1_[EnumFaceDirection.Constants.UP_INDEX] = lvt_8_1_;
            }

            if (lvt_9_1_ > lvt_4_1_[EnumFaceDirection.Constants.SOUTH_INDEX])
            {
                lvt_4_1_[EnumFaceDirection.Constants.SOUTH_INDEX] = lvt_9_1_;
            }
        }

        EnumFaceDirection lvt_5_2_ = EnumFaceDirection.getFacing(p_178408_2_);

        for (int lvt_6_2_ = 0; lvt_6_2_ < 4; ++lvt_6_2_)
        {
            int lvt_7_2_ = 7 * lvt_6_2_;
            EnumFaceDirection.VertexInformation lvt_8_2_ = lvt_5_2_.getVertexInformation(lvt_6_2_);
            float lvt_9_2_ = lvt_4_1_[lvt_8_2_.xIndex];
            float lvt_10_1_ = lvt_4_1_[lvt_8_2_.yIndex];
            float lvt_11_1_ = lvt_4_1_[lvt_8_2_.zIndex];
            p_178408_1_[lvt_7_2_] = Float.floatToRawIntBits(lvt_9_2_);
            p_178408_1_[lvt_7_2_ + 1] = Float.floatToRawIntBits(lvt_10_1_);
            p_178408_1_[lvt_7_2_ + 2] = Float.floatToRawIntBits(lvt_11_1_);

            for (int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_)
            {
                int lvt_13_1_ = 7 * lvt_12_1_;
                float lvt_14_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_13_1_]);
                float lvt_15_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_13_1_ + 1]);
                float lvt_16_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_13_1_ + 2]);

                if (MathHelper.epsilonEquals(lvt_9_2_, lvt_14_1_) && MathHelper.epsilonEquals(lvt_10_1_, lvt_15_1_) && MathHelper.epsilonEquals(lvt_11_1_, lvt_16_1_))
                {
                    p_178408_1_[lvt_7_2_ + 4] = lvt_3_1_[lvt_13_1_ + 4];
                    p_178408_1_[lvt_7_2_ + 4 + 1] = lvt_3_1_[lvt_13_1_ + 4 + 1];
                }
            }
        }
    }

    private void lockVertexUv(int p_178401_1_, int[] p_178401_2_, EnumFacing facing, BlockFaceUV p_178401_4_, TextureAtlasSprite p_178401_5_)
    {
        int lvt_6_1_ = 7 * p_178401_1_;
        float lvt_7_1_ = Float.intBitsToFloat(p_178401_2_[lvt_6_1_]);
        float lvt_8_1_ = Float.intBitsToFloat(p_178401_2_[lvt_6_1_ + 1]);
        float lvt_9_1_ = Float.intBitsToFloat(p_178401_2_[lvt_6_1_ + 2]);

        if (lvt_7_1_ < -0.1F || lvt_7_1_ >= 1.1F)
        {
            lvt_7_1_ -= (float)MathHelper.floor_float(lvt_7_1_);
        }

        if (lvt_8_1_ < -0.1F || lvt_8_1_ >= 1.1F)
        {
            lvt_8_1_ -= (float)MathHelper.floor_float(lvt_8_1_);
        }

        if (lvt_9_1_ < -0.1F || lvt_9_1_ >= 1.1F)
        {
            lvt_9_1_ -= (float)MathHelper.floor_float(lvt_9_1_);
        }

        float lvt_10_1_ = 0.0F;
        float lvt_11_1_ = 0.0F;

        switch (facing)
        {
            case DOWN:
                lvt_10_1_ = lvt_7_1_ * 16.0F;
                lvt_11_1_ = (1.0F - lvt_9_1_) * 16.0F;
                break;

            case UP:
                lvt_10_1_ = lvt_7_1_ * 16.0F;
                lvt_11_1_ = lvt_9_1_ * 16.0F;
                break;

            case NORTH:
                lvt_10_1_ = (1.0F - lvt_7_1_) * 16.0F;
                lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
                break;

            case SOUTH:
                lvt_10_1_ = lvt_7_1_ * 16.0F;
                lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
                break;

            case WEST:
                lvt_10_1_ = lvt_9_1_ * 16.0F;
                lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
                break;

            case EAST:
                lvt_10_1_ = (1.0F - lvt_9_1_) * 16.0F;
                lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
        }

        int lvt_12_1_ = p_178401_4_.func_178345_c(p_178401_1_) * 7;
        p_178401_2_[lvt_12_1_ + 4] = Float.floatToRawIntBits(p_178401_5_.getInterpolatedU((double)lvt_10_1_));
        p_178401_2_[lvt_12_1_ + 4 + 1] = Float.floatToRawIntBits(p_178401_5_.getInterpolatedV((double)lvt_11_1_));
    }
}
