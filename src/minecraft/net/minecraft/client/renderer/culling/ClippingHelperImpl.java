package net.minecraft.client.renderer.culling;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ClippingHelperImpl extends ClippingHelper
{
    private static ClippingHelperImpl instance = new ClippingHelperImpl();
    private FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    private FloatBuffer field_78564_h = GLAllocation.createDirectFloatBuffer(16);

    /**
     * Initialises the ClippingHelper object then returns an instance of it.
     */
    public static ClippingHelper getInstance()
    {
        instance.init();
        return instance;
    }

    private void normalize(float[] p_180547_1_)
    {
        float lvt_2_1_ = MathHelper.sqrt_float(p_180547_1_[0] * p_180547_1_[0] + p_180547_1_[1] * p_180547_1_[1] + p_180547_1_[2] * p_180547_1_[2]);
        p_180547_1_[0] /= lvt_2_1_;
        p_180547_1_[1] /= lvt_2_1_;
        p_180547_1_[2] /= lvt_2_1_;
        p_180547_1_[3] /= lvt_2_1_;
    }

    public void init()
    {
        this.projectionMatrixBuffer.clear();
        this.modelviewMatrixBuffer.clear();
        this.field_78564_h.clear();
        GlStateManager.getFloat(2983, this.projectionMatrixBuffer);
        GlStateManager.getFloat(2982, this.modelviewMatrixBuffer);
        float[] lvt_1_1_ = this.projectionMatrix;
        float[] lvt_2_1_ = this.modelviewMatrix;
        this.projectionMatrixBuffer.flip().limit(16);
        this.projectionMatrixBuffer.get(lvt_1_1_);
        this.modelviewMatrixBuffer.flip().limit(16);
        this.modelviewMatrixBuffer.get(lvt_2_1_);
        this.clippingMatrix[0] = lvt_2_1_[0] * lvt_1_1_[0] + lvt_2_1_[1] * lvt_1_1_[4] + lvt_2_1_[2] * lvt_1_1_[8] + lvt_2_1_[3] * lvt_1_1_[12];
        this.clippingMatrix[1] = lvt_2_1_[0] * lvt_1_1_[1] + lvt_2_1_[1] * lvt_1_1_[5] + lvt_2_1_[2] * lvt_1_1_[9] + lvt_2_1_[3] * lvt_1_1_[13];
        this.clippingMatrix[2] = lvt_2_1_[0] * lvt_1_1_[2] + lvt_2_1_[1] * lvt_1_1_[6] + lvt_2_1_[2] * lvt_1_1_[10] + lvt_2_1_[3] * lvt_1_1_[14];
        this.clippingMatrix[3] = lvt_2_1_[0] * lvt_1_1_[3] + lvt_2_1_[1] * lvt_1_1_[7] + lvt_2_1_[2] * lvt_1_1_[11] + lvt_2_1_[3] * lvt_1_1_[15];
        this.clippingMatrix[4] = lvt_2_1_[4] * lvt_1_1_[0] + lvt_2_1_[5] * lvt_1_1_[4] + lvt_2_1_[6] * lvt_1_1_[8] + lvt_2_1_[7] * lvt_1_1_[12];
        this.clippingMatrix[5] = lvt_2_1_[4] * lvt_1_1_[1] + lvt_2_1_[5] * lvt_1_1_[5] + lvt_2_1_[6] * lvt_1_1_[9] + lvt_2_1_[7] * lvt_1_1_[13];
        this.clippingMatrix[6] = lvt_2_1_[4] * lvt_1_1_[2] + lvt_2_1_[5] * lvt_1_1_[6] + lvt_2_1_[6] * lvt_1_1_[10] + lvt_2_1_[7] * lvt_1_1_[14];
        this.clippingMatrix[7] = lvt_2_1_[4] * lvt_1_1_[3] + lvt_2_1_[5] * lvt_1_1_[7] + lvt_2_1_[6] * lvt_1_1_[11] + lvt_2_1_[7] * lvt_1_1_[15];
        this.clippingMatrix[8] = lvt_2_1_[8] * lvt_1_1_[0] + lvt_2_1_[9] * lvt_1_1_[4] + lvt_2_1_[10] * lvt_1_1_[8] + lvt_2_1_[11] * lvt_1_1_[12];
        this.clippingMatrix[9] = lvt_2_1_[8] * lvt_1_1_[1] + lvt_2_1_[9] * lvt_1_1_[5] + lvt_2_1_[10] * lvt_1_1_[9] + lvt_2_1_[11] * lvt_1_1_[13];
        this.clippingMatrix[10] = lvt_2_1_[8] * lvt_1_1_[2] + lvt_2_1_[9] * lvt_1_1_[6] + lvt_2_1_[10] * lvt_1_1_[10] + lvt_2_1_[11] * lvt_1_1_[14];
        this.clippingMatrix[11] = lvt_2_1_[8] * lvt_1_1_[3] + lvt_2_1_[9] * lvt_1_1_[7] + lvt_2_1_[10] * lvt_1_1_[11] + lvt_2_1_[11] * lvt_1_1_[15];
        this.clippingMatrix[12] = lvt_2_1_[12] * lvt_1_1_[0] + lvt_2_1_[13] * lvt_1_1_[4] + lvt_2_1_[14] * lvt_1_1_[8] + lvt_2_1_[15] * lvt_1_1_[12];
        this.clippingMatrix[13] = lvt_2_1_[12] * lvt_1_1_[1] + lvt_2_1_[13] * lvt_1_1_[5] + lvt_2_1_[14] * lvt_1_1_[9] + lvt_2_1_[15] * lvt_1_1_[13];
        this.clippingMatrix[14] = lvt_2_1_[12] * lvt_1_1_[2] + lvt_2_1_[13] * lvt_1_1_[6] + lvt_2_1_[14] * lvt_1_1_[10] + lvt_2_1_[15] * lvt_1_1_[14];
        this.clippingMatrix[15] = lvt_2_1_[12] * lvt_1_1_[3] + lvt_2_1_[13] * lvt_1_1_[7] + lvt_2_1_[14] * lvt_1_1_[11] + lvt_2_1_[15] * lvt_1_1_[15];
        float[] lvt_3_1_ = this.frustum[0];
        lvt_3_1_[0] = this.clippingMatrix[3] - this.clippingMatrix[0];
        lvt_3_1_[1] = this.clippingMatrix[7] - this.clippingMatrix[4];
        lvt_3_1_[2] = this.clippingMatrix[11] - this.clippingMatrix[8];
        lvt_3_1_[3] = this.clippingMatrix[15] - this.clippingMatrix[12];
        this.normalize(lvt_3_1_);
        float[] lvt_4_1_ = this.frustum[1];
        lvt_4_1_[0] = this.clippingMatrix[3] + this.clippingMatrix[0];
        lvt_4_1_[1] = this.clippingMatrix[7] + this.clippingMatrix[4];
        lvt_4_1_[2] = this.clippingMatrix[11] + this.clippingMatrix[8];
        lvt_4_1_[3] = this.clippingMatrix[15] + this.clippingMatrix[12];
        this.normalize(lvt_4_1_);
        float[] lvt_5_1_ = this.frustum[2];
        lvt_5_1_[0] = this.clippingMatrix[3] + this.clippingMatrix[1];
        lvt_5_1_[1] = this.clippingMatrix[7] + this.clippingMatrix[5];
        lvt_5_1_[2] = this.clippingMatrix[11] + this.clippingMatrix[9];
        lvt_5_1_[3] = this.clippingMatrix[15] + this.clippingMatrix[13];
        this.normalize(lvt_5_1_);
        float[] lvt_6_1_ = this.frustum[3];
        lvt_6_1_[0] = this.clippingMatrix[3] - this.clippingMatrix[1];
        lvt_6_1_[1] = this.clippingMatrix[7] - this.clippingMatrix[5];
        lvt_6_1_[2] = this.clippingMatrix[11] - this.clippingMatrix[9];
        lvt_6_1_[3] = this.clippingMatrix[15] - this.clippingMatrix[13];
        this.normalize(lvt_6_1_);
        float[] lvt_7_1_ = this.frustum[4];
        lvt_7_1_[0] = this.clippingMatrix[3] - this.clippingMatrix[2];
        lvt_7_1_[1] = this.clippingMatrix[7] - this.clippingMatrix[6];
        lvt_7_1_[2] = this.clippingMatrix[11] - this.clippingMatrix[10];
        lvt_7_1_[3] = this.clippingMatrix[15] - this.clippingMatrix[14];
        this.normalize(lvt_7_1_);
        float[] lvt_8_1_ = this.frustum[5];
        lvt_8_1_[0] = this.clippingMatrix[3] + this.clippingMatrix[2];
        lvt_8_1_[1] = this.clippingMatrix[7] + this.clippingMatrix[6];
        lvt_8_1_[2] = this.clippingMatrix[11] + this.clippingMatrix[10];
        lvt_8_1_[3] = this.clippingMatrix[15] + this.clippingMatrix[14];
        this.normalize(lvt_8_1_);
    }
}
