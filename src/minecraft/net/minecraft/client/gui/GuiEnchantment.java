package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer
{
    /** The ResourceLocation containing the Enchantment GUI texture location */
    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");

    /**
     * The ResourceLocation containing the texture for the Book rendered above the enchantment table
     */
    private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");

    /**
     * The ModelBook instance used for rendering the book on the Enchantment table
     */
    private static final ModelBook MODEL_BOOK = new ModelBook();

    /** The player inventory currently bound to this GuiEnchantment instance. */
    private final InventoryPlayer playerInventory;

    /** A Random instance for use with the enchantment gui */
    private Random random = new Random();
    private ContainerEnchantment container;
    public int field_147073_u;
    public float field_147071_v;
    public float field_147069_w;
    public float field_147082_x;
    public float field_147081_y;
    public float field_147080_z;
    public float field_147076_A;
    ItemStack field_147077_B;
    private final IWorldNameable field_175380_I;

    public GuiEnchantment(InventoryPlayer inventory, World worldIn, IWorldNameable p_i45502_3_)
    {
        super(new ContainerEnchantment(inventory, worldIn));
        this.playerInventory = inventory;
        this.container = (ContainerEnchantment)this.inventorySlots;
        this.field_175380_I = p_i45502_3_;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.field_175380_I.getDisplayName().getUnformattedText(), 12, 5, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        this.func_147068_g();
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;

        for (int lvt_6_1_ = 0; lvt_6_1_ < 3; ++lvt_6_1_)
        {
            int lvt_7_1_ = mouseX - (lvt_4_1_ + 60);
            int lvt_8_1_ = mouseY - (lvt_5_1_ + 14 + 19 * lvt_6_1_);

            if (lvt_7_1_ >= 0 && lvt_8_1_ >= 0 && lvt_7_1_ < 108 && lvt_8_1_ < 19 && this.container.enchantItem(this.mc.thePlayer, lvt_6_1_))
            {
                this.mc.playerController.sendEnchantPacket(this.container.windowId, lvt_6_1_);
            }
        }
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
        int lvt_4_1_ = (this.width - this.xSize) / 2;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        ScaledResolution lvt_6_1_ = new ScaledResolution(this.mc);
        GlStateManager.viewport((lvt_6_1_.getScaledWidth() - 320) / 2 * lvt_6_1_.getScaleFactor(), (lvt_6_1_.getScaledHeight() - 240) / 2 * lvt_6_1_.getScaleFactor(), 320 * lvt_6_1_.getScaleFactor(), 240 * lvt_6_1_.getScaleFactor());
        GlStateManager.translate(-0.34F, 0.23F, 0.0F);
        Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
        float lvt_7_1_ = 1.0F;
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(0.0F, 3.3F, -16.0F);
        GlStateManager.scale(lvt_7_1_, lvt_7_1_, lvt_7_1_);
        float lvt_8_1_ = 5.0F;
        GlStateManager.scale(lvt_8_1_, lvt_8_1_, lvt_8_1_);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
        GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
        float lvt_9_1_ = this.field_147076_A + (this.field_147080_z - this.field_147076_A) * partialTicks;
        GlStateManager.translate((1.0F - lvt_9_1_) * 0.2F, (1.0F - lvt_9_1_) * 0.1F, (1.0F - lvt_9_1_) * 0.25F);
        GlStateManager.rotate(-(1.0F - lvt_9_1_) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        float lvt_10_1_ = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * partialTicks + 0.25F;
        float lvt_11_1_ = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * partialTicks + 0.75F;
        lvt_10_1_ = (lvt_10_1_ - (float)MathHelper.truncateDoubleToInt((double)lvt_10_1_)) * 1.6F - 0.3F;
        lvt_11_1_ = (lvt_11_1_ - (float)MathHelper.truncateDoubleToInt((double)lvt_11_1_)) * 1.6F - 0.3F;

        if (lvt_10_1_ < 0.0F)
        {
            lvt_10_1_ = 0.0F;
        }

        if (lvt_11_1_ < 0.0F)
        {
            lvt_11_1_ = 0.0F;
        }

        if (lvt_10_1_ > 1.0F)
        {
            lvt_10_1_ = 1.0F;
        }

        if (lvt_11_1_ > 1.0F)
        {
            lvt_11_1_ = 1.0F;
        }

        GlStateManager.enableRescaleNormal();
        MODEL_BOOK.render((Entity)null, 0.0F, lvt_10_1_, lvt_11_1_, lvt_9_1_, 0.0F, 0.0625F);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.getInstance().reseedRandomGenerator((long)this.container.xpSeed);
        int lvt_12_1_ = this.container.getLapisAmount();

        for (int lvt_13_1_ = 0; lvt_13_1_ < 3; ++lvt_13_1_)
        {
            int lvt_14_1_ = lvt_4_1_ + 60;
            int lvt_15_1_ = lvt_14_1_ + 20;
            int lvt_16_1_ = 86;
            String lvt_17_1_ = EnchantmentNameParts.getInstance().generateNewRandomName();
            this.zLevel = 0.0F;
            this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
            int lvt_18_1_ = this.container.enchantLevels[lvt_13_1_];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (lvt_18_1_ == 0)
            {
                this.drawTexturedModalRect(lvt_14_1_, lvt_5_1_ + 14 + 19 * lvt_13_1_, 0, 185, 108, 19);
            }
            else
            {
                String lvt_19_1_ = "" + lvt_18_1_;
                FontRenderer lvt_20_1_ = this.mc.standardGalacticFontRenderer;
                int lvt_21_1_ = 6839882;

                if ((lvt_12_1_ < lvt_13_1_ + 1 || this.mc.thePlayer.experienceLevel < lvt_18_1_) && !this.mc.thePlayer.capabilities.isCreativeMode)
                {
                    this.drawTexturedModalRect(lvt_14_1_, lvt_5_1_ + 14 + 19 * lvt_13_1_, 0, 185, 108, 19);
                    this.drawTexturedModalRect(lvt_14_1_ + 1, lvt_5_1_ + 15 + 19 * lvt_13_1_, 16 * lvt_13_1_, 239, 16, 16);
                    lvt_20_1_.drawSplitString(lvt_17_1_, lvt_15_1_, lvt_5_1_ + 16 + 19 * lvt_13_1_, lvt_16_1_, (lvt_21_1_ & 16711422) >> 1);
                    lvt_21_1_ = 4226832;
                }
                else
                {
                    int lvt_22_1_ = mouseX - (lvt_4_1_ + 60);
                    int lvt_23_1_ = mouseY - (lvt_5_1_ + 14 + 19 * lvt_13_1_);

                    if (lvt_22_1_ >= 0 && lvt_23_1_ >= 0 && lvt_22_1_ < 108 && lvt_23_1_ < 19)
                    {
                        this.drawTexturedModalRect(lvt_14_1_, lvt_5_1_ + 14 + 19 * lvt_13_1_, 0, 204, 108, 19);
                        lvt_21_1_ = 16777088;
                    }
                    else
                    {
                        this.drawTexturedModalRect(lvt_14_1_, lvt_5_1_ + 14 + 19 * lvt_13_1_, 0, 166, 108, 19);
                    }

                    this.drawTexturedModalRect(lvt_14_1_ + 1, lvt_5_1_ + 15 + 19 * lvt_13_1_, 16 * lvt_13_1_, 223, 16, 16);
                    lvt_20_1_.drawSplitString(lvt_17_1_, lvt_15_1_, lvt_5_1_ + 16 + 19 * lvt_13_1_, lvt_16_1_, lvt_21_1_);
                    lvt_21_1_ = 8453920;
                }

                lvt_20_1_ = this.mc.fontRendererObj;
                lvt_20_1_.drawStringWithShadow(lvt_19_1_, (float)(lvt_15_1_ + 86 - lvt_20_1_.getStringWidth(lvt_19_1_)), (float)(lvt_5_1_ + 16 + 19 * lvt_13_1_ + 7), lvt_21_1_);
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean lvt_4_1_ = this.mc.thePlayer.capabilities.isCreativeMode;
        int lvt_5_1_ = this.container.getLapisAmount();

        for (int lvt_6_1_ = 0; lvt_6_1_ < 3; ++lvt_6_1_)
        {
            int lvt_7_1_ = this.container.enchantLevels[lvt_6_1_];
            int lvt_8_1_ = this.container.enchantmentIds[lvt_6_1_];
            int lvt_9_1_ = lvt_6_1_ + 1;

            if (this.isPointInRegion(60, 14 + 19 * lvt_6_1_, 108, 17, mouseX, mouseY) && lvt_7_1_ > 0 && lvt_8_1_ >= 0)
            {
                List<String> lvt_10_1_ = Lists.newArrayList();

                if (lvt_8_1_ >= 0 && Enchantment.getEnchantmentById(lvt_8_1_ & 255) != null)
                {
                    String lvt_11_1_ = Enchantment.getEnchantmentById(lvt_8_1_ & 255).getTranslatedName((lvt_8_1_ & 65280) >> 8);
                    lvt_10_1_.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC.toString() + I18n.format("container.enchant.clue", new Object[] {lvt_11_1_}));
                }

                if (!lvt_4_1_)
                {
                    if (lvt_8_1_ >= 0)
                    {
                        lvt_10_1_.add("");
                    }

                    if (this.mc.thePlayer.experienceLevel < lvt_7_1_)
                    {
                        lvt_10_1_.add(EnumChatFormatting.RED.toString() + "Level Requirement: " + this.container.enchantLevels[lvt_6_1_]);
                    }
                    else
                    {
                        String lvt_11_2_ = "";

                        if (lvt_9_1_ == 1)
                        {
                            lvt_11_2_ = I18n.format("container.enchant.lapis.one", new Object[0]);
                        }
                        else
                        {
                            lvt_11_2_ = I18n.format("container.enchant.lapis.many", new Object[] {Integer.valueOf(lvt_9_1_)});
                        }

                        if (lvt_5_1_ >= lvt_9_1_)
                        {
                            lvt_10_1_.add(EnumChatFormatting.GRAY.toString() + "" + lvt_11_2_);
                        }
                        else
                        {
                            lvt_10_1_.add(EnumChatFormatting.RED.toString() + "" + lvt_11_2_);
                        }

                        if (lvt_9_1_ == 1)
                        {
                            lvt_11_2_ = I18n.format("container.enchant.level.one", new Object[0]);
                        }
                        else
                        {
                            lvt_11_2_ = I18n.format("container.enchant.level.many", new Object[] {Integer.valueOf(lvt_9_1_)});
                        }

                        lvt_10_1_.add(EnumChatFormatting.GRAY.toString() + "" + lvt_11_2_);
                    }
                }

                this.drawHoveringText(lvt_10_1_, mouseX, mouseY);
                break;
            }
        }
    }

    public void func_147068_g()
    {
        ItemStack lvt_1_1_ = this.inventorySlots.getSlot(0).getStack();

        if (!ItemStack.areItemStacksEqual(lvt_1_1_, this.field_147077_B))
        {
            this.field_147077_B = lvt_1_1_;

            while (true)
            {
                this.field_147082_x += (float)(this.random.nextInt(4) - this.random.nextInt(4));

                if (this.field_147071_v > this.field_147082_x + 1.0F || this.field_147071_v < this.field_147082_x - 1.0F)
                {
                    break;
                }
            }
        }

        ++this.field_147073_u;
        this.field_147069_w = this.field_147071_v;
        this.field_147076_A = this.field_147080_z;
        boolean lvt_2_1_ = false;

        for (int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_)
        {
            if (this.container.enchantLevels[lvt_3_1_] != 0)
            {
                lvt_2_1_ = true;
            }
        }

        if (lvt_2_1_)
        {
            this.field_147080_z += 0.2F;
        }
        else
        {
            this.field_147080_z -= 0.2F;
        }

        this.field_147080_z = MathHelper.clamp_float(this.field_147080_z, 0.0F, 1.0F);
        float lvt_3_2_ = (this.field_147082_x - this.field_147071_v) * 0.4F;
        float lvt_4_1_ = 0.2F;
        lvt_3_2_ = MathHelper.clamp_float(lvt_3_2_, -lvt_4_1_, lvt_4_1_);
        this.field_147081_y += (lvt_3_2_ - this.field_147081_y) * 0.9F;
        this.field_147071_v += this.field_147081_y;
    }
}
