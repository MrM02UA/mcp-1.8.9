package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;

public class WorldRenderer
{
    private ByteBuffer byteBuffer;
    private IntBuffer rawIntBuffer;
    private ShortBuffer rawShortBuffer;
    private FloatBuffer rawFloatBuffer;
    private int vertexCount;
    private VertexFormatElement vertexFormatElement;
    private int vertexFormatIndex;

    /** None */
    private boolean noColor;
    private int drawMode;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private VertexFormat vertexFormat;
    private boolean isDrawing;

    public WorldRenderer(int bufferSizeIn)
    {
        this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
        this.rawIntBuffer = this.byteBuffer.asIntBuffer();
        this.rawShortBuffer = this.byteBuffer.asShortBuffer();
        this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
    }

    private void growBuffer(int p_181670_1_)
    {
        if (p_181670_1_ > this.rawIntBuffer.remaining())
        {
            int lvt_2_1_ = this.byteBuffer.capacity();
            int lvt_3_1_ = lvt_2_1_ % 2097152;
            int lvt_4_1_ = lvt_3_1_ + (((this.rawIntBuffer.position() + p_181670_1_) * 4 - lvt_3_1_) / 2097152 + 1) * 2097152;
            LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + lvt_2_1_ + " bytes, new size " + lvt_4_1_ + " bytes.");
            int lvt_5_1_ = this.rawIntBuffer.position();
            ByteBuffer lvt_6_1_ = GLAllocation.createDirectByteBuffer(lvt_4_1_);
            this.byteBuffer.position(0);
            lvt_6_1_.put(this.byteBuffer);
            lvt_6_1_.rewind();
            this.byteBuffer = lvt_6_1_;
            this.rawFloatBuffer = this.byteBuffer.asFloatBuffer().asReadOnlyBuffer();
            this.rawIntBuffer = this.byteBuffer.asIntBuffer();
            this.rawIntBuffer.position(lvt_5_1_);
            this.rawShortBuffer = this.byteBuffer.asShortBuffer();
            this.rawShortBuffer.position(lvt_5_1_ << 1);
        }
    }

    public void sortVertexData(float p_181674_1_, float p_181674_2_, float p_181674_3_)
    {
        int lvt_4_1_ = this.vertexCount / 4;
        final float[] lvt_5_1_ = new float[lvt_4_1_];

        for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_4_1_; ++lvt_6_1_)
        {
            lvt_5_1_[lvt_6_1_] = getDistanceSq(this.rawFloatBuffer, (float)((double)p_181674_1_ + this.xOffset), (float)((double)p_181674_2_ + this.yOffset), (float)((double)p_181674_3_ + this.zOffset), this.vertexFormat.getIntegerSize(), lvt_6_1_ * this.vertexFormat.getNextOffset());
        }

        Integer[] lvt_6_2_ = new Integer[lvt_4_1_];

        for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_2_.length; ++lvt_7_1_)
        {
            lvt_6_2_[lvt_7_1_] = Integer.valueOf(lvt_7_1_);
        }

        Arrays.sort(lvt_6_2_, new Comparator<Integer>()
        {
            public int compare(Integer p_compare_1_, Integer p_compare_2_)
            {
                return Floats.compare(lvt_5_1_[p_compare_2_.intValue()], lvt_5_1_[p_compare_1_.intValue()]);
            }
            public int compare(Object p_compare_1_, Object p_compare_2_)
            {
                return this.compare((Integer)p_compare_1_, (Integer)p_compare_2_);
            }
        });
        BitSet lvt_7_2_ = new BitSet();
        int lvt_8_1_ = this.vertexFormat.getNextOffset();
        int[] lvt_9_1_ = new int[lvt_8_1_];

        for (int lvt_10_1_ = 0; (lvt_10_1_ = lvt_7_2_.nextClearBit(lvt_10_1_)) < lvt_6_2_.length; ++lvt_10_1_)
        {
            int lvt_11_1_ = lvt_6_2_[lvt_10_1_].intValue();

            if (lvt_11_1_ != lvt_10_1_)
            {
                this.rawIntBuffer.limit(lvt_11_1_ * lvt_8_1_ + lvt_8_1_);
                this.rawIntBuffer.position(lvt_11_1_ * lvt_8_1_);
                this.rawIntBuffer.get(lvt_9_1_);
                int lvt_12_1_ = lvt_11_1_;

                for (int lvt_13_1_ = lvt_6_2_[lvt_11_1_].intValue(); lvt_12_1_ != lvt_10_1_; lvt_13_1_ = lvt_6_2_[lvt_13_1_].intValue())
                {
                    this.rawIntBuffer.limit(lvt_13_1_ * lvt_8_1_ + lvt_8_1_);
                    this.rawIntBuffer.position(lvt_13_1_ * lvt_8_1_);
                    IntBuffer lvt_14_1_ = this.rawIntBuffer.slice();
                    this.rawIntBuffer.limit(lvt_12_1_ * lvt_8_1_ + lvt_8_1_);
                    this.rawIntBuffer.position(lvt_12_1_ * lvt_8_1_);
                    this.rawIntBuffer.put(lvt_14_1_);
                    lvt_7_2_.set(lvt_12_1_);
                    lvt_12_1_ = lvt_13_1_;
                }

                this.rawIntBuffer.limit(lvt_10_1_ * lvt_8_1_ + lvt_8_1_);
                this.rawIntBuffer.position(lvt_10_1_ * lvt_8_1_);
                this.rawIntBuffer.put(lvt_9_1_);
            }

            lvt_7_2_.set(lvt_10_1_);
        }
    }

    public WorldRenderer.State getVertexState()
    {
        this.rawIntBuffer.rewind();
        int lvt_1_1_ = this.getBufferSize();
        this.rawIntBuffer.limit(lvt_1_1_);
        int[] lvt_2_1_ = new int[lvt_1_1_];
        this.rawIntBuffer.get(lvt_2_1_);
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(lvt_1_1_);
        return new WorldRenderer.State(lvt_2_1_, new VertexFormat(this.vertexFormat));
    }

    private int getBufferSize()
    {
        return this.vertexCount * this.vertexFormat.getIntegerSize();
    }

    private static float getDistanceSq(FloatBuffer p_181665_0_, float p_181665_1_, float p_181665_2_, float p_181665_3_, int p_181665_4_, int p_181665_5_)
    {
        float lvt_6_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 0);
        float lvt_7_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 1);
        float lvt_8_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 2);
        float lvt_9_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 0);
        float lvt_10_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 1);
        float lvt_11_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 2);
        float lvt_12_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 0);
        float lvt_13_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 1);
        float lvt_14_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 2);
        float lvt_15_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 0);
        float lvt_16_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 1);
        float lvt_17_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 2);
        float lvt_18_1_ = (lvt_6_1_ + lvt_9_1_ + lvt_12_1_ + lvt_15_1_) * 0.25F - p_181665_1_;
        float lvt_19_1_ = (lvt_7_1_ + lvt_10_1_ + lvt_13_1_ + lvt_16_1_) * 0.25F - p_181665_2_;
        float lvt_20_1_ = (lvt_8_1_ + lvt_11_1_ + lvt_14_1_ + lvt_17_1_) * 0.25F - p_181665_3_;
        return lvt_18_1_ * lvt_18_1_ + lvt_19_1_ * lvt_19_1_ + lvt_20_1_ * lvt_20_1_;
    }

    public void setVertexState(WorldRenderer.State state)
    {
        this.rawIntBuffer.clear();
        this.growBuffer(state.getRawBuffer().length);
        this.rawIntBuffer.put(state.getRawBuffer());
        this.vertexCount = state.getVertexCount();
        this.vertexFormat = new VertexFormat(state.getVertexFormat());
    }

    public void reset()
    {
        this.vertexCount = 0;
        this.vertexFormatElement = null;
        this.vertexFormatIndex = 0;
    }

    public void begin(int glMode, VertexFormat format)
    {
        if (this.isDrawing)
        {
            throw new IllegalStateException("Already building!");
        }
        else
        {
            this.isDrawing = true;
            this.reset();
            this.drawMode = glMode;
            this.vertexFormat = format;
            this.vertexFormatElement = format.getElement(this.vertexFormatIndex);
            this.noColor = false;
            this.byteBuffer.limit(this.byteBuffer.capacity());
        }
    }

    public WorldRenderer tex(double u, double v)
    {
        int lvt_5_1_ = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);

        switch (this.vertexFormatElement.getType())
        {
            case FLOAT:
                this.byteBuffer.putFloat(lvt_5_1_, (float)u);
                this.byteBuffer.putFloat(lvt_5_1_ + 4, (float)v);
                break;

            case UINT:
            case INT:
                this.byteBuffer.putInt(lvt_5_1_, (int)u);
                this.byteBuffer.putInt(lvt_5_1_ + 4, (int)v);
                break;

            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(lvt_5_1_, (short)((int)v));
                this.byteBuffer.putShort(lvt_5_1_ + 2, (short)((int)u));
                break;

            case UBYTE:
            case BYTE:
                this.byteBuffer.put(lvt_5_1_, (byte)((int)v));
                this.byteBuffer.put(lvt_5_1_ + 1, (byte)((int)u));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public WorldRenderer lightmap(int p_181671_1_, int p_181671_2_)
    {
        int lvt_3_1_ = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);

        switch (this.vertexFormatElement.getType())
        {
            case FLOAT:
                this.byteBuffer.putFloat(lvt_3_1_, (float)p_181671_1_);
                this.byteBuffer.putFloat(lvt_3_1_ + 4, (float)p_181671_2_);
                break;

            case UINT:
            case INT:
                this.byteBuffer.putInt(lvt_3_1_, p_181671_1_);
                this.byteBuffer.putInt(lvt_3_1_ + 4, p_181671_2_);
                break;

            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(lvt_3_1_, (short)p_181671_2_);
                this.byteBuffer.putShort(lvt_3_1_ + 2, (short)p_181671_1_);
                break;

            case UBYTE:
            case BYTE:
                this.byteBuffer.put(lvt_3_1_, (byte)p_181671_2_);
                this.byteBuffer.put(lvt_3_1_ + 1, (byte)p_181671_1_);
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void putBrightness4(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_)
    {
        int lvt_5_1_ = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
        int lvt_6_1_ = this.vertexFormat.getNextOffset() >> 2;
        this.rawIntBuffer.put(lvt_5_1_, p_178962_1_);
        this.rawIntBuffer.put(lvt_5_1_ + lvt_6_1_, p_178962_2_);
        this.rawIntBuffer.put(lvt_5_1_ + lvt_6_1_ * 2, p_178962_3_);
        this.rawIntBuffer.put(lvt_5_1_ + lvt_6_1_ * 3, p_178962_4_);
    }

    public void putPosition(double x, double y, double z)
    {
        int lvt_7_1_ = this.vertexFormat.getIntegerSize();
        int lvt_8_1_ = (this.vertexCount - 4) * lvt_7_1_;

        for (int lvt_9_1_ = 0; lvt_9_1_ < 4; ++lvt_9_1_)
        {
            int lvt_10_1_ = lvt_8_1_ + lvt_9_1_ * lvt_7_1_;
            int lvt_11_1_ = lvt_10_1_ + 1;
            int lvt_12_1_ = lvt_11_1_ + 1;
            this.rawIntBuffer.put(lvt_10_1_, Float.floatToRawIntBits((float)(x + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(lvt_10_1_))));
            this.rawIntBuffer.put(lvt_11_1_, Float.floatToRawIntBits((float)(y + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(lvt_11_1_))));
            this.rawIntBuffer.put(lvt_12_1_, Float.floatToRawIntBits((float)(z + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(lvt_12_1_))));
        }
    }

    /**
     * Takes in the pass the call list is being requested for. Args: renderPass
     */
    private int getColorIndex(int p_78909_1_)
    {
        return ((this.vertexCount - p_78909_1_) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
    }

    public void putColorMultiplier(float red, float green, float blue, int p_178978_4_)
    {
        int lvt_5_1_ = this.getColorIndex(p_178978_4_);
        int lvt_6_1_ = -1;

        if (!this.noColor)
        {
            lvt_6_1_ = this.rawIntBuffer.get(lvt_5_1_);

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            {
                int lvt_7_1_ = (int)((float)(lvt_6_1_ & 255) * red);
                int lvt_8_1_ = (int)((float)(lvt_6_1_ >> 8 & 255) * green);
                int lvt_9_1_ = (int)((float)(lvt_6_1_ >> 16 & 255) * blue);
                lvt_6_1_ = lvt_6_1_ & -16777216;
                lvt_6_1_ = lvt_6_1_ | lvt_9_1_ << 16 | lvt_8_1_ << 8 | lvt_7_1_;
            }
            else
            {
                int lvt_7_2_ = (int)((float)(lvt_6_1_ >> 24 & 255) * red);
                int lvt_8_2_ = (int)((float)(lvt_6_1_ >> 16 & 255) * green);
                int lvt_9_2_ = (int)((float)(lvt_6_1_ >> 8 & 255) * blue);
                lvt_6_1_ = lvt_6_1_ & 255;
                lvt_6_1_ = lvt_6_1_ | lvt_7_2_ << 24 | lvt_8_2_ << 16 | lvt_9_2_ << 8;
            }
        }

        this.rawIntBuffer.put(lvt_5_1_, lvt_6_1_);
    }

    private void putColor(int argb, int p_178988_2_)
    {
        int lvt_3_1_ = this.getColorIndex(p_178988_2_);
        int lvt_4_1_ = argb >> 16 & 255;
        int lvt_5_1_ = argb >> 8 & 255;
        int lvt_6_1_ = argb & 255;
        int lvt_7_1_ = argb >> 24 & 255;
        this.putColorRGBA(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
    }

    public void putColorRGB_F(float red, float green, float blue, int p_178994_4_)
    {
        int lvt_5_1_ = this.getColorIndex(p_178994_4_);
        int lvt_6_1_ = MathHelper.clamp_int((int)(red * 255.0F), 0, 255);
        int lvt_7_1_ = MathHelper.clamp_int((int)(green * 255.0F), 0, 255);
        int lvt_8_1_ = MathHelper.clamp_int((int)(blue * 255.0F), 0, 255);
        this.putColorRGBA(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_, 255);
    }

    private void putColorRGBA(int index, int red, int p_178972_3_, int p_178972_4_, int p_178972_5_)
    {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
        {
            this.rawIntBuffer.put(index, p_178972_5_ << 24 | p_178972_4_ << 16 | p_178972_3_ << 8 | red);
        }
        else
        {
            this.rawIntBuffer.put(index, red << 24 | p_178972_3_ << 16 | p_178972_4_ << 8 | p_178972_5_);
        }
    }

    /**
     * Disabels color processing.
     */
    public void noColor()
    {
        this.noColor = true;
    }

    public WorldRenderer color(float red, float green, float blue, float alpha)
    {
        return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
    }

    public WorldRenderer color(int red, int green, int blue, int alpha)
    {
        if (this.noColor)
        {
            return this;
        }
        else
        {
            int lvt_5_1_ = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);

            switch (this.vertexFormatElement.getType())
            {
                case FLOAT:
                    this.byteBuffer.putFloat(lvt_5_1_, (float)red / 255.0F);
                    this.byteBuffer.putFloat(lvt_5_1_ + 4, (float)green / 255.0F);
                    this.byteBuffer.putFloat(lvt_5_1_ + 8, (float)blue / 255.0F);
                    this.byteBuffer.putFloat(lvt_5_1_ + 12, (float)alpha / 255.0F);
                    break;

                case UINT:
                case INT:
                    this.byteBuffer.putFloat(lvt_5_1_, (float)red);
                    this.byteBuffer.putFloat(lvt_5_1_ + 4, (float)green);
                    this.byteBuffer.putFloat(lvt_5_1_ + 8, (float)blue);
                    this.byteBuffer.putFloat(lvt_5_1_ + 12, (float)alpha);
                    break;

                case USHORT:
                case SHORT:
                    this.byteBuffer.putShort(lvt_5_1_, (short)red);
                    this.byteBuffer.putShort(lvt_5_1_ + 2, (short)green);
                    this.byteBuffer.putShort(lvt_5_1_ + 4, (short)blue);
                    this.byteBuffer.putShort(lvt_5_1_ + 6, (short)alpha);
                    break;

                case UBYTE:
                case BYTE:
                    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
                    {
                        this.byteBuffer.put(lvt_5_1_, (byte)red);
                        this.byteBuffer.put(lvt_5_1_ + 1, (byte)green);
                        this.byteBuffer.put(lvt_5_1_ + 2, (byte)blue);
                        this.byteBuffer.put(lvt_5_1_ + 3, (byte)alpha);
                    }
                    else
                    {
                        this.byteBuffer.put(lvt_5_1_, (byte)alpha);
                        this.byteBuffer.put(lvt_5_1_ + 1, (byte)blue);
                        this.byteBuffer.put(lvt_5_1_ + 2, (byte)green);
                        this.byteBuffer.put(lvt_5_1_ + 3, (byte)red);
                    }
            }

            this.nextVertexFormatIndex();
            return this;
        }
    }

    public void addVertexData(int[] vertexData)
    {
        this.growBuffer(vertexData.length);
        this.rawIntBuffer.position(this.getBufferSize());
        this.rawIntBuffer.put(vertexData);
        this.vertexCount += vertexData.length / this.vertexFormat.getIntegerSize();
    }

    public void endVertex()
    {
        ++this.vertexCount;
        this.growBuffer(this.vertexFormat.getIntegerSize());
    }

    public WorldRenderer pos(double x, double y, double z)
    {
        int lvt_7_1_ = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);

        switch (this.vertexFormatElement.getType())
        {
            case FLOAT:
                this.byteBuffer.putFloat(lvt_7_1_, (float)(x + this.xOffset));
                this.byteBuffer.putFloat(lvt_7_1_ + 4, (float)(y + this.yOffset));
                this.byteBuffer.putFloat(lvt_7_1_ + 8, (float)(z + this.zOffset));
                break;

            case UINT:
            case INT:
                this.byteBuffer.putInt(lvt_7_1_, Float.floatToRawIntBits((float)(x + this.xOffset)));
                this.byteBuffer.putInt(lvt_7_1_ + 4, Float.floatToRawIntBits((float)(y + this.yOffset)));
                this.byteBuffer.putInt(lvt_7_1_ + 8, Float.floatToRawIntBits((float)(z + this.zOffset)));
                break;

            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(lvt_7_1_, (short)((int)(x + this.xOffset)));
                this.byteBuffer.putShort(lvt_7_1_ + 2, (short)((int)(y + this.yOffset)));
                this.byteBuffer.putShort(lvt_7_1_ + 4, (short)((int)(z + this.zOffset)));
                break;

            case UBYTE:
            case BYTE:
                this.byteBuffer.put(lvt_7_1_, (byte)((int)(x + this.xOffset)));
                this.byteBuffer.put(lvt_7_1_ + 1, (byte)((int)(y + this.yOffset)));
                this.byteBuffer.put(lvt_7_1_ + 2, (byte)((int)(z + this.zOffset)));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void putNormal(float x, float y, float z)
    {
        int lvt_4_1_ = (byte)((int)(x * 127.0F)) & 255;
        int lvt_5_1_ = (byte)((int)(y * 127.0F)) & 255;
        int lvt_6_1_ = (byte)((int)(z * 127.0F)) & 255;
        int lvt_7_1_ = lvt_4_1_ | lvt_5_1_ << 8 | lvt_6_1_ << 16;
        int lvt_8_1_ = this.vertexFormat.getNextOffset() >> 2;
        int lvt_9_1_ = (this.vertexCount - 4) * lvt_8_1_ + this.vertexFormat.getNormalOffset() / 4;
        this.rawIntBuffer.put(lvt_9_1_, lvt_7_1_);
        this.rawIntBuffer.put(lvt_9_1_ + lvt_8_1_, lvt_7_1_);
        this.rawIntBuffer.put(lvt_9_1_ + lvt_8_1_ * 2, lvt_7_1_);
        this.rawIntBuffer.put(lvt_9_1_ + lvt_8_1_ * 3, lvt_7_1_);
    }

    private void nextVertexFormatIndex()
    {
        ++this.vertexFormatIndex;
        this.vertexFormatIndex %= this.vertexFormat.getElementCount();
        this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);

        if (this.vertexFormatElement.getUsage() == VertexFormatElement.EnumUsage.PADDING)
        {
            this.nextVertexFormatIndex();
        }
    }

    public WorldRenderer normal(float p_181663_1_, float p_181663_2_, float p_181663_3_)
    {
        int lvt_4_1_ = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);

        switch (this.vertexFormatElement.getType())
        {
            case FLOAT:
                this.byteBuffer.putFloat(lvt_4_1_, p_181663_1_);
                this.byteBuffer.putFloat(lvt_4_1_ + 4, p_181663_2_);
                this.byteBuffer.putFloat(lvt_4_1_ + 8, p_181663_3_);
                break;

            case UINT:
            case INT:
                this.byteBuffer.putInt(lvt_4_1_, (int)p_181663_1_);
                this.byteBuffer.putInt(lvt_4_1_ + 4, (int)p_181663_2_);
                this.byteBuffer.putInt(lvt_4_1_ + 8, (int)p_181663_3_);
                break;

            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(lvt_4_1_, (short)((int)p_181663_1_ * 32767 & 65535));
                this.byteBuffer.putShort(lvt_4_1_ + 2, (short)((int)p_181663_2_ * 32767 & 65535));
                this.byteBuffer.putShort(lvt_4_1_ + 4, (short)((int)p_181663_3_ * 32767 & 65535));
                break;

            case UBYTE:
            case BYTE:
                this.byteBuffer.put(lvt_4_1_, (byte)((int)p_181663_1_ * 127 & 255));
                this.byteBuffer.put(lvt_4_1_ + 1, (byte)((int)p_181663_2_ * 127 & 255));
                this.byteBuffer.put(lvt_4_1_ + 2, (byte)((int)p_181663_3_ * 127 & 255));
        }

        this.nextVertexFormatIndex();
        return this;
    }

    public void setTranslation(double x, double y, double z)
    {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
    }

    public void finishDrawing()
    {
        if (!this.isDrawing)
        {
            throw new IllegalStateException("Not building!");
        }
        else
        {
            this.isDrawing = false;
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.getBufferSize() * 4);
        }
    }

    public ByteBuffer getByteBuffer()
    {
        return this.byteBuffer;
    }

    public VertexFormat getVertexFormat()
    {
        return this.vertexFormat;
    }

    public int getVertexCount()
    {
        return this.vertexCount;
    }

    public int getDrawMode()
    {
        return this.drawMode;
    }

    public void putColor4(int argb)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_)
        {
            this.putColor(argb, lvt_2_1_ + 1);
        }
    }

    public void putColorRGB_F4(float red, float green, float blue)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_)
        {
            this.putColorRGB_F(red, green, blue, lvt_4_1_ + 1);
        }
    }

    public class State
    {
        private final int[] stateRawBuffer;
        private final VertexFormat stateVertexFormat;

        public State(int[] buffer, VertexFormat format)
        {
            this.stateRawBuffer = buffer;
            this.stateVertexFormat = format;
        }

        public int[] getRawBuffer()
        {
            return this.stateRawBuffer;
        }

        public int getVertexCount()
        {
            return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
        }

        public VertexFormat getVertexFormat()
        {
            return this.stateVertexFormat;
        }
    }
}
