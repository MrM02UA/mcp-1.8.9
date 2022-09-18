package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad
{
    private final TextureAtlasSprite texture;

    public BreakingFour(BakedQuad quad, TextureAtlasSprite textureIn)
    {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()));
        this.texture = textureIn;
        this.remapQuad();
    }

    private void remapQuad()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < 4; ++lvt_1_1_)
        {
            this.remapVert(lvt_1_1_);
        }
    }

    private void remapVert(int vertex)
    {
        int lvt_2_1_ = 7 * vertex;
        float lvt_3_1_ = Float.intBitsToFloat(this.vertexData[lvt_2_1_]);
        float lvt_4_1_ = Float.intBitsToFloat(this.vertexData[lvt_2_1_ + 1]);
        float lvt_5_1_ = Float.intBitsToFloat(this.vertexData[lvt_2_1_ + 2]);
        float lvt_6_1_ = 0.0F;
        float lvt_7_1_ = 0.0F;

        switch (this.face)
        {
            case DOWN:
                lvt_6_1_ = lvt_3_1_ * 16.0F;
                lvt_7_1_ = (1.0F - lvt_5_1_) * 16.0F;
                break;

            case UP:
                lvt_6_1_ = lvt_3_1_ * 16.0F;
                lvt_7_1_ = lvt_5_1_ * 16.0F;
                break;

            case NORTH:
                lvt_6_1_ = (1.0F - lvt_3_1_) * 16.0F;
                lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
                break;

            case SOUTH:
                lvt_6_1_ = lvt_3_1_ * 16.0F;
                lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
                break;

            case WEST:
                lvt_6_1_ = lvt_5_1_ * 16.0F;
                lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
                break;

            case EAST:
                lvt_6_1_ = (1.0F - lvt_5_1_) * 16.0F;
                lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
        }

        this.vertexData[lvt_2_1_ + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)lvt_6_1_));
        this.vertexData[lvt_2_1_ + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)lvt_7_1_));
    }
}
