package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class TileEntityItemStackRenderer
{
    public static TileEntityItemStackRenderer instance = new TileEntityItemStackRenderer();
    private TileEntityChest field_147717_b = new TileEntityChest(0);
    private TileEntityChest field_147718_c = new TileEntityChest(1);
    private TileEntityEnderChest enderChest = new TileEntityEnderChest();
    private TileEntityBanner banner = new TileEntityBanner();
    private TileEntitySkull skull = new TileEntitySkull();

    public void renderByItem(ItemStack itemStackIn)
    {
        if (itemStackIn.getItem() == Items.banner)
        {
            this.banner.setItemValues(itemStackIn);
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.banner, 0.0D, 0.0D, 0.0D, 0.0F);
        }
        else if (itemStackIn.getItem() == Items.skull)
        {
            GameProfile lvt_2_1_ = null;

            if (itemStackIn.hasTagCompound())
            {
                NBTTagCompound lvt_3_1_ = itemStackIn.getTagCompound();

                if (lvt_3_1_.hasKey("SkullOwner", 10))
                {
                    lvt_2_1_ = NBTUtil.readGameProfileFromNBT(lvt_3_1_.getCompoundTag("SkullOwner"));
                }
                else if (lvt_3_1_.hasKey("SkullOwner", 8) && lvt_3_1_.getString("SkullOwner").length() > 0)
                {
                    lvt_2_1_ = new GameProfile((UUID)null, lvt_3_1_.getString("SkullOwner"));
                    lvt_2_1_ = TileEntitySkull.updateGameprofile(lvt_2_1_);
                    lvt_3_1_.removeTag("SkullOwner");
                    lvt_3_1_.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), lvt_2_1_));
                }
            }

            if (TileEntitySkullRenderer.instance != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(-0.5F, 0.0F, -0.5F);
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                GlStateManager.disableCull();
                TileEntitySkullRenderer.instance.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, itemStackIn.getMetadata(), lvt_2_1_, -1);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        }
        else
        {
            Block lvt_2_2_ = Block.getBlockFromItem(itemStackIn.getItem());

            if (lvt_2_2_ == Blocks.ender_chest)
            {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(this.enderChest, 0.0D, 0.0D, 0.0D, 0.0F);
            }
            else if (lvt_2_2_ == Blocks.trapped_chest)
            {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147718_c, 0.0D, 0.0D, 0.0D, 0.0F);
            }
            else
            {
                TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147717_b, 0.0D, 0.0D, 0.0D, 0.0F);
            }
        }
    }
}
