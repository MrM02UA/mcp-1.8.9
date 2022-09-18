package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;

public class LayerCustomHead implements LayerRenderer<EntityLivingBase>
{
    private final ModelRenderer field_177209_a;

    public LayerCustomHead(ModelRenderer p_i46120_1_)
    {
        this.field_177209_a = p_i46120_1_;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        ItemStack lvt_9_1_ = entitylivingbaseIn.getCurrentArmor(3);

        if (lvt_9_1_ != null && lvt_9_1_.getItem() != null)
        {
            Item lvt_10_1_ = lvt_9_1_.getItem();
            Minecraft lvt_11_1_ = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();

            if (entitylivingbaseIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            boolean lvt_12_1_ = entitylivingbaseIn instanceof EntityVillager || entitylivingbaseIn instanceof EntityZombie && ((EntityZombie)entitylivingbaseIn).isVillager();

            if (!lvt_12_1_ && entitylivingbaseIn.isChild())
            {
                float lvt_13_1_ = 2.0F;
                float lvt_14_1_ = 1.4F;
                GlStateManager.scale(lvt_14_1_ / lvt_13_1_, lvt_14_1_ / lvt_13_1_, lvt_14_1_ / lvt_13_1_);
                GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            this.field_177209_a.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (lvt_10_1_ instanceof ItemBlock)
            {
                float lvt_13_2_ = 0.625F;
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(lvt_13_2_, -lvt_13_2_, -lvt_13_2_);

                if (lvt_12_1_)
                {
                    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                }

                lvt_11_1_.getItemRenderer().renderItem(entitylivingbaseIn, lvt_9_1_, ItemCameraTransforms.TransformType.HEAD);
            }
            else if (lvt_10_1_ == Items.skull)
            {
                float lvt_13_3_ = 1.1875F;
                GlStateManager.scale(lvt_13_3_, -lvt_13_3_, -lvt_13_3_);

                if (lvt_12_1_)
                {
                    GlStateManager.translate(0.0F, 0.0625F, 0.0F);
                }

                GameProfile lvt_14_2_ = null;

                if (lvt_9_1_.hasTagCompound())
                {
                    NBTTagCompound lvt_15_1_ = lvt_9_1_.getTagCompound();

                    if (lvt_15_1_.hasKey("SkullOwner", 10))
                    {
                        lvt_14_2_ = NBTUtil.readGameProfileFromNBT(lvt_15_1_.getCompoundTag("SkullOwner"));
                    }
                    else if (lvt_15_1_.hasKey("SkullOwner", 8))
                    {
                        String lvt_16_1_ = lvt_15_1_.getString("SkullOwner");

                        if (!StringUtils.isNullOrEmpty(lvt_16_1_))
                        {
                            lvt_14_2_ = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, lvt_16_1_));
                            lvt_15_1_.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), lvt_14_2_));
                        }
                    }
                }

                TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, lvt_9_1_.getMetadata(), lvt_14_2_, -1);
            }

            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}
