package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;

public class TexturedQuad
{
    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    private boolean invertNormal;

    public TexturedQuad(PositionTextureVertex[] vertices)
    {
        this.vertexPositions = vertices;
        this.nVertices = vertices.length;
    }

    public TexturedQuad(PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight)
    {
        this(vertices);
        float lvt_8_1_ = 0.0F / textureWidth;
        float lvt_9_1_ = 0.0F / textureHeight;
        vertices[0] = vertices[0].setTexturePosition((float)texcoordU2 / textureWidth - lvt_8_1_, (float)texcoordV1 / textureHeight + lvt_9_1_);
        vertices[1] = vertices[1].setTexturePosition((float)texcoordU1 / textureWidth + lvt_8_1_, (float)texcoordV1 / textureHeight + lvt_9_1_);
        vertices[2] = vertices[2].setTexturePosition((float)texcoordU1 / textureWidth + lvt_8_1_, (float)texcoordV2 / textureHeight - lvt_9_1_);
        vertices[3] = vertices[3].setTexturePosition((float)texcoordU2 / textureWidth - lvt_8_1_, (float)texcoordV2 / textureHeight - lvt_9_1_);
    }

    public void flipFace()
    {
        PositionTextureVertex[] lvt_1_1_ = new PositionTextureVertex[this.vertexPositions.length];

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.vertexPositions.length; ++lvt_2_1_)
        {
            lvt_1_1_[lvt_2_1_] = this.vertexPositions[this.vertexPositions.length - lvt_2_1_ - 1];
        }

        this.vertexPositions = lvt_1_1_;
    }

    /**
     * Draw this primitve. This is typically called only once as the generated drawing instructions are saved by the
     * renderer and reused later.
     */
    public void draw(WorldRenderer renderer, float scale)
    {
        Vec3 lvt_3_1_ = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
        Vec3 lvt_4_1_ = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
        Vec3 lvt_5_1_ = lvt_4_1_.crossProduct(lvt_3_1_).normalize();
        float lvt_6_1_ = (float)lvt_5_1_.xCoord;
        float lvt_7_1_ = (float)lvt_5_1_.yCoord;
        float lvt_8_1_ = (float)lvt_5_1_.zCoord;

        if (this.invertNormal)
        {
            lvt_6_1_ = -lvt_6_1_;
            lvt_7_1_ = -lvt_7_1_;
            lvt_8_1_ = -lvt_8_1_;
        }

        renderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

        for (int lvt_9_1_ = 0; lvt_9_1_ < 4; ++lvt_9_1_)
        {
            PositionTextureVertex lvt_10_1_ = this.vertexPositions[lvt_9_1_];
            renderer.pos(lvt_10_1_.vector3D.xCoord * (double)scale, lvt_10_1_.vector3D.yCoord * (double)scale, lvt_10_1_.vector3D.zCoord * (double)scale).tex((double)lvt_10_1_.texturePositionX, (double)lvt_10_1_.texturePositionY).normal(lvt_6_1_, lvt_7_1_, lvt_8_1_).endVertex();
        }

        Tessellator.getInstance().draw();
    }
}
