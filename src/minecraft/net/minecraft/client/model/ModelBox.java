package net.minecraft.client.model;

import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox
{
    /**
     * The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube
     */
    private PositionTextureVertex[] vertexPositions;

    /** An array of 6 TexturedQuads, one for each face of a cube */
    private TexturedQuad[] quadList;

    /** X vertex coordinate of lower box corner */
    public final float posX1;

    /** Y vertex coordinate of lower box corner */
    public final float posY1;

    /** Z vertex coordinate of lower box corner */
    public final float posZ1;

    /** X vertex coordinate of upper box corner */
    public final float posX2;

    /** Y vertex coordinate of upper box corner */
    public final float posY2;

    /** Z vertex coordinate of upper box corner */
    public final float posZ2;
    public String boxName;

    public ModelBox(ModelRenderer renderer, int p_i46359_2_, int p_i46359_3_, float p_i46359_4_, float p_i46359_5_, float p_i46359_6_, int p_i46359_7_, int p_i46359_8_, int p_i46359_9_, float p_i46359_10_)
    {
        this(renderer, p_i46359_2_, p_i46359_3_, p_i46359_4_, p_i46359_5_, p_i46359_6_, p_i46359_7_, p_i46359_8_, p_i46359_9_, p_i46359_10_, renderer.mirror);
    }

    public ModelBox(ModelRenderer renderer, int textureX, int textureY, float p_i46301_4_, float p_i46301_5_, float p_i46301_6_, int p_i46301_7_, int p_i46301_8_, int p_i46301_9_, float p_i46301_10_, boolean p_i46301_11_)
    {
        this.posX1 = p_i46301_4_;
        this.posY1 = p_i46301_5_;
        this.posZ1 = p_i46301_6_;
        this.posX2 = p_i46301_4_ + (float)p_i46301_7_;
        this.posY2 = p_i46301_5_ + (float)p_i46301_8_;
        this.posZ2 = p_i46301_6_ + (float)p_i46301_9_;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float lvt_12_1_ = p_i46301_4_ + (float)p_i46301_7_;
        float lvt_13_1_ = p_i46301_5_ + (float)p_i46301_8_;
        float lvt_14_1_ = p_i46301_6_ + (float)p_i46301_9_;
        p_i46301_4_ = p_i46301_4_ - p_i46301_10_;
        p_i46301_5_ = p_i46301_5_ - p_i46301_10_;
        p_i46301_6_ = p_i46301_6_ - p_i46301_10_;
        lvt_12_1_ = lvt_12_1_ + p_i46301_10_;
        lvt_13_1_ = lvt_13_1_ + p_i46301_10_;
        lvt_14_1_ = lvt_14_1_ + p_i46301_10_;

        if (p_i46301_11_)
        {
            float lvt_15_1_ = lvt_12_1_;
            lvt_12_1_ = p_i46301_4_;
            p_i46301_4_ = lvt_15_1_;
        }

        PositionTextureVertex lvt_15_2_ = new PositionTextureVertex(p_i46301_4_, p_i46301_5_, p_i46301_6_, 0.0F, 0.0F);
        PositionTextureVertex lvt_16_1_ = new PositionTextureVertex(lvt_12_1_, p_i46301_5_, p_i46301_6_, 0.0F, 8.0F);
        PositionTextureVertex lvt_17_1_ = new PositionTextureVertex(lvt_12_1_, lvt_13_1_, p_i46301_6_, 8.0F, 8.0F);
        PositionTextureVertex lvt_18_1_ = new PositionTextureVertex(p_i46301_4_, lvt_13_1_, p_i46301_6_, 8.0F, 0.0F);
        PositionTextureVertex lvt_19_1_ = new PositionTextureVertex(p_i46301_4_, p_i46301_5_, lvt_14_1_, 0.0F, 0.0F);
        PositionTextureVertex lvt_20_1_ = new PositionTextureVertex(lvt_12_1_, p_i46301_5_, lvt_14_1_, 0.0F, 8.0F);
        PositionTextureVertex lvt_21_1_ = new PositionTextureVertex(lvt_12_1_, lvt_13_1_, lvt_14_1_, 8.0F, 8.0F);
        PositionTextureVertex lvt_22_1_ = new PositionTextureVertex(p_i46301_4_, lvt_13_1_, lvt_14_1_, 8.0F, 0.0F);
        this.vertexPositions[0] = lvt_15_2_;
        this.vertexPositions[1] = lvt_16_1_;
        this.vertexPositions[2] = lvt_17_1_;
        this.vertexPositions[3] = lvt_18_1_;
        this.vertexPositions[4] = lvt_19_1_;
        this.vertexPositions[5] = lvt_20_1_;
        this.vertexPositions[6] = lvt_21_1_;
        this.vertexPositions[7] = lvt_22_1_;
        this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] {lvt_20_1_, lvt_16_1_, lvt_17_1_, lvt_21_1_}, textureX + p_i46301_9_ + p_i46301_7_, textureY + p_i46301_9_, textureX + p_i46301_9_ + p_i46301_7_ + p_i46301_9_, textureY + p_i46301_9_ + p_i46301_8_, renderer.textureWidth, renderer.textureHeight);
        this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] {lvt_15_2_, lvt_19_1_, lvt_22_1_, lvt_18_1_}, textureX, textureY + p_i46301_9_, textureX + p_i46301_9_, textureY + p_i46301_9_ + p_i46301_8_, renderer.textureWidth, renderer.textureHeight);
        this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] {lvt_20_1_, lvt_19_1_, lvt_15_2_, lvt_16_1_}, textureX + p_i46301_9_, textureY, textureX + p_i46301_9_ + p_i46301_7_, textureY + p_i46301_9_, renderer.textureWidth, renderer.textureHeight);
        this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] {lvt_17_1_, lvt_18_1_, lvt_22_1_, lvt_21_1_}, textureX + p_i46301_9_ + p_i46301_7_, textureY + p_i46301_9_, textureX + p_i46301_9_ + p_i46301_7_ + p_i46301_7_, textureY, renderer.textureWidth, renderer.textureHeight);
        this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] {lvt_16_1_, lvt_15_2_, lvt_18_1_, lvt_17_1_}, textureX + p_i46301_9_, textureY + p_i46301_9_, textureX + p_i46301_9_ + p_i46301_7_, textureY + p_i46301_9_ + p_i46301_8_, renderer.textureWidth, renderer.textureHeight);
        this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] {lvt_19_1_, lvt_20_1_, lvt_21_1_, lvt_22_1_}, textureX + p_i46301_9_ + p_i46301_7_ + p_i46301_9_, textureY + p_i46301_9_, textureX + p_i46301_9_ + p_i46301_7_ + p_i46301_9_ + p_i46301_7_, textureY + p_i46301_9_ + p_i46301_8_, renderer.textureWidth, renderer.textureHeight);

        if (p_i46301_11_)
        {
            for (int lvt_23_1_ = 0; lvt_23_1_ < this.quadList.length; ++lvt_23_1_)
            {
                this.quadList[lvt_23_1_].flipFace();
            }
        }
    }

    public void render(WorldRenderer renderer, float scale)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < this.quadList.length; ++lvt_3_1_)
        {
            this.quadList[lvt_3_1_].draw(renderer, scale);
        }
    }

    public ModelBox setBoxName(String name)
    {
        this.boxName = name;
        return this;
    }
}
