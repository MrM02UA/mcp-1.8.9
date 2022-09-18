package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;

public class ItemCameraTransforms
{
    public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
    public static float field_181690_b = 0.0F;
    public static float field_181691_c = 0.0F;
    public static float field_181692_d = 0.0F;
    public static float field_181693_e = 0.0F;
    public static float field_181694_f = 0.0F;
    public static float field_181695_g = 0.0F;
    public static float field_181696_h = 0.0F;
    public static float field_181697_i = 0.0F;
    public static float field_181698_j = 0.0F;
    public final ItemTransformVec3f thirdPerson;
    public final ItemTransformVec3f firstPerson;
    public final ItemTransformVec3f head;
    public final ItemTransformVec3f gui;
    public final ItemTransformVec3f ground;
    public final ItemTransformVec3f fixed;

    private ItemCameraTransforms()
    {
        this(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    public ItemCameraTransforms(ItemCameraTransforms transforms)
    {
        this.thirdPerson = transforms.thirdPerson;
        this.firstPerson = transforms.firstPerson;
        this.head = transforms.head;
        this.gui = transforms.gui;
        this.ground = transforms.ground;
        this.fixed = transforms.fixed;
    }

    public ItemCameraTransforms(ItemTransformVec3f thirdPersonIn, ItemTransformVec3f firstPersonIn, ItemTransformVec3f headIn, ItemTransformVec3f guiIn, ItemTransformVec3f groundIn, ItemTransformVec3f fixedIn)
    {
        this.thirdPerson = thirdPersonIn;
        this.firstPerson = firstPersonIn;
        this.head = headIn;
        this.gui = guiIn;
        this.ground = groundIn;
        this.fixed = fixedIn;
    }

    public void applyTransform(ItemCameraTransforms.TransformType type)
    {
        ItemTransformVec3f lvt_2_1_ = this.getTransform(type);

        if (lvt_2_1_ != ItemTransformVec3f.DEFAULT)
        {
            GlStateManager.translate(lvt_2_1_.translation.x + field_181690_b, lvt_2_1_.translation.y + field_181691_c, lvt_2_1_.translation.z + field_181692_d);
            GlStateManager.rotate(lvt_2_1_.rotation.y + field_181694_f, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(lvt_2_1_.rotation.x + field_181693_e, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(lvt_2_1_.rotation.z + field_181695_g, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(lvt_2_1_.scale.x + field_181696_h, lvt_2_1_.scale.y + field_181697_i, lvt_2_1_.scale.z + field_181698_j);
        }
    }

    public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
    {
        switch (type)
        {
            case THIRD_PERSON:
                return this.thirdPerson;

            case FIRST_PERSON:
                return this.firstPerson;

            case HEAD:
                return this.head;

            case GUI:
                return this.gui;

            case GROUND:
                return this.ground;

            case FIXED:
                return this.fixed;

            default:
                return ItemTransformVec3f.DEFAULT;
        }
    }

    public boolean func_181687_c(ItemCameraTransforms.TransformType type)
    {
        return !this.getTransform(type).equals(ItemTransformVec3f.DEFAULT);
    }

    static class Deserializer implements JsonDeserializer<ItemCameraTransforms>
    {
        public ItemCameraTransforms deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            ItemTransformVec3f lvt_5_1_ = this.func_181683_a(p_deserialize_3_, lvt_4_1_, "thirdperson");
            ItemTransformVec3f lvt_6_1_ = this.func_181683_a(p_deserialize_3_, lvt_4_1_, "firstperson");
            ItemTransformVec3f lvt_7_1_ = this.func_181683_a(p_deserialize_3_, lvt_4_1_, "head");
            ItemTransformVec3f lvt_8_1_ = this.func_181683_a(p_deserialize_3_, lvt_4_1_, "gui");
            ItemTransformVec3f lvt_9_1_ = this.func_181683_a(p_deserialize_3_, lvt_4_1_, "ground");
            ItemTransformVec3f lvt_10_1_ = this.func_181683_a(p_deserialize_3_, lvt_4_1_, "fixed");
            return new ItemCameraTransforms(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_, lvt_10_1_);
        }

        private ItemTransformVec3f func_181683_a(JsonDeserializationContext p_181683_1_, JsonObject p_181683_2_, String p_181683_3_)
        {
            return p_181683_2_.has(p_181683_3_) ? (ItemTransformVec3f)p_181683_1_.deserialize(p_181683_2_.get(p_181683_3_), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }

    public static enum TransformType
    {
        NONE,
        THIRD_PERSON,
        FIRST_PERSON,
        HEAD,
        GUI,
        GROUND,
        FIXED;
    }
}
