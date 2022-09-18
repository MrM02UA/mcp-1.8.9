package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ActiveRenderInfo
{
    /** The current GL viewport */
    private static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);

    /** The current GL modelview matrix */
    private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);

    /** The current GL projection matrix */
    private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

    /** The computed view object coordinates */
    private static final FloatBuffer OBJECTCOORDS = GLAllocation.createDirectFloatBuffer(3);
    private static Vec3 position = new Vec3(0.0D, 0.0D, 0.0D);

    /** The X component of the entity's yaw rotation */
    private static float rotationX;

    /** The combined X and Z components of the entity's pitch rotation */
    private static float rotationXZ;

    /** The Z component of the entity's yaw rotation */
    private static float rotationZ;

    /**
     * The Y component (scaled along the Z axis) of the entity's pitch rotation
     */
    private static float rotationYZ;

    /**
     * The Y component (scaled along the X axis) of the entity's pitch rotation
     */
    private static float rotationXY;

    /**
     * Updates the current render info and camera location based on entity look angles and 1st/3rd person view mode
     */
    public static void updateRenderInfo(EntityPlayer entityplayerIn, boolean p_74583_1_)
    {
        GlStateManager.getFloat(2982, MODELVIEW);
        GlStateManager.getFloat(2983, PROJECTION);
        GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT);
        float lvt_2_1_ = (float)((VIEWPORT.get(0) + VIEWPORT.get(2)) / 2);
        float lvt_3_1_ = (float)((VIEWPORT.get(1) + VIEWPORT.get(3)) / 2);
        GLU.gluUnProject(lvt_2_1_, lvt_3_1_, 0.0F, MODELVIEW, PROJECTION, VIEWPORT, OBJECTCOORDS);
        position = new Vec3((double)OBJECTCOORDS.get(0), (double)OBJECTCOORDS.get(1), (double)OBJECTCOORDS.get(2));
        int lvt_4_1_ = p_74583_1_ ? 1 : 0;
        float lvt_5_1_ = entityplayerIn.rotationPitch;
        float lvt_6_1_ = entityplayerIn.rotationYaw;
        rotationX = MathHelper.cos(lvt_6_1_ * (float)Math.PI / 180.0F) * (float)(1 - lvt_4_1_ * 2);
        rotationZ = MathHelper.sin(lvt_6_1_ * (float)Math.PI / 180.0F) * (float)(1 - lvt_4_1_ * 2);
        rotationYZ = -rotationZ * MathHelper.sin(lvt_5_1_ * (float)Math.PI / 180.0F) * (float)(1 - lvt_4_1_ * 2);
        rotationXY = rotationX * MathHelper.sin(lvt_5_1_ * (float)Math.PI / 180.0F) * (float)(1 - lvt_4_1_ * 2);
        rotationXZ = MathHelper.cos(lvt_5_1_ * (float)Math.PI / 180.0F);
    }

    public static Vec3 projectViewFromEntity(Entity p_178806_0_, double p_178806_1_)
    {
        double lvt_3_1_ = p_178806_0_.prevPosX + (p_178806_0_.posX - p_178806_0_.prevPosX) * p_178806_1_;
        double lvt_5_1_ = p_178806_0_.prevPosY + (p_178806_0_.posY - p_178806_0_.prevPosY) * p_178806_1_;
        double lvt_7_1_ = p_178806_0_.prevPosZ + (p_178806_0_.posZ - p_178806_0_.prevPosZ) * p_178806_1_;
        double lvt_9_1_ = lvt_3_1_ + position.xCoord;
        double lvt_11_1_ = lvt_5_1_ + position.yCoord;
        double lvt_13_1_ = lvt_7_1_ + position.zCoord;
        return new Vec3(lvt_9_1_, lvt_11_1_, lvt_13_1_);
    }

    public static Block getBlockAtEntityViewpoint(World worldIn, Entity p_180786_1_, float p_180786_2_)
    {
        Vec3 lvt_3_1_ = projectViewFromEntity(p_180786_1_, (double)p_180786_2_);
        BlockPos lvt_4_1_ = new BlockPos(lvt_3_1_);
        IBlockState lvt_5_1_ = worldIn.getBlockState(lvt_4_1_);
        Block lvt_6_1_ = lvt_5_1_.getBlock();

        if (lvt_6_1_.getMaterial().isLiquid())
        {
            float lvt_7_1_ = 0.0F;

            if (lvt_5_1_.getBlock() instanceof BlockLiquid)
            {
                lvt_7_1_ = BlockLiquid.getLiquidHeightPercent(((Integer)lvt_5_1_.getValue(BlockLiquid.LEVEL)).intValue()) - 0.11111111F;
            }

            float lvt_8_1_ = (float)(lvt_4_1_.getY() + 1) - lvt_7_1_;

            if (lvt_3_1_.yCoord >= (double)lvt_8_1_)
            {
                lvt_6_1_ = worldIn.getBlockState(lvt_4_1_.up()).getBlock();
            }
        }

        return lvt_6_1_;
    }

    public static Vec3 getPosition()
    {
        return position;
    }

    public static float getRotationX()
    {
        return rotationX;
    }

    public static float getRotationXZ()
    {
        return rotationXZ;
    }

    public static float getRotationZ()
    {
        return rotationZ;
    }

    public static float getRotationYZ()
    {
        return rotationYZ;
    }

    public static float getRotationXY()
    {
        return rotationXY;
    }
}
