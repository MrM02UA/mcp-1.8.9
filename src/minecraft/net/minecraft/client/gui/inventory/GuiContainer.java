package net.minecraft.client.gui.inventory;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public abstract class GuiContainer extends GuiScreen
{
    /** The location of the inventory background texture */
    protected static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");

    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;

    /** The Y size of the inventory window in pixels. */
    protected int ySize = 166;

    /** A list of the players inventory slots */
    public Container inventorySlots;

    /**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;

    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiTop;

    /** holds the slot currently hovered */
    private Slot theSlot;

    /** Used when touchscreen is enabled. */
    private Slot clickedSlot;

    /** Used when touchscreen is enabled. */
    private boolean isRightMouseClick;

    /** Used when touchscreen is enabled */
    private ItemStack draggedStack;
    private int touchUpX;
    private int touchUpY;
    private Slot returningStackDestSlot;
    private long returningStackTime;

    /** Used when touchscreen is enabled */
    private ItemStack returningStack;
    private Slot currentDragTargetSlot;
    private long dragItemDropDelay;
    protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
    protected boolean dragSplitting;
    private int dragSplittingLimit;
    private int dragSplittingButton;
    private boolean ignoreMouseUp;
    private int dragSplittingRemnant;
    private long lastClickTime;
    private Slot lastClickSlot;
    private int lastClickButton;
    private boolean doubleClick;
    private ItemStack shiftClickedSlot;

    public GuiContainer(Container inventorySlotsIn)
    {
        this.inventorySlots = inventorySlotsIn;
        this.ignoreMouseUp = true;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.mc.thePlayer.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)lvt_4_1_, (float)lvt_5_1_, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.theSlot = null;
        int lvt_6_1_ = 240;
        int lvt_7_1_ = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lvt_6_1_ / 1.0F, (float)lvt_7_1_ / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (int lvt_8_1_ = 0; lvt_8_1_ < this.inventorySlots.inventorySlots.size(); ++lvt_8_1_)
        {
            Slot lvt_9_1_ = (Slot)this.inventorySlots.inventorySlots.get(lvt_8_1_);
            this.drawSlot(lvt_9_1_);

            if (this.isMouseOverSlot(lvt_9_1_, mouseX, mouseY) && lvt_9_1_.canBeHovered())
            {
                this.theSlot = lvt_9_1_;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                int lvt_10_1_ = lvt_9_1_.xDisplayPosition;
                int lvt_11_1_ = lvt_9_1_.yDisplayPosition;
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(lvt_10_1_, lvt_11_1_, lvt_10_1_ + 16, lvt_11_1_ + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }

        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        InventoryPlayer lvt_8_2_ = this.mc.thePlayer.inventory;
        ItemStack lvt_9_2_ = this.draggedStack == null ? lvt_8_2_.getItemStack() : this.draggedStack;

        if (lvt_9_2_ != null)
        {
            int lvt_10_2_ = 8;
            int lvt_11_2_ = this.draggedStack == null ? 8 : 16;
            String lvt_12_1_ = null;

            if (this.draggedStack != null && this.isRightMouseClick)
            {
                lvt_9_2_ = lvt_9_2_.copy();
                lvt_9_2_.stackSize = MathHelper.ceiling_float_int((float)lvt_9_2_.stackSize / 2.0F);
            }
            else if (this.dragSplitting && this.dragSplittingSlots.size() > 1)
            {
                lvt_9_2_ = lvt_9_2_.copy();
                lvt_9_2_.stackSize = this.dragSplittingRemnant;

                if (lvt_9_2_.stackSize == 0)
                {
                    lvt_12_1_ = "" + EnumChatFormatting.YELLOW + "0";
                }
            }

            this.drawItemStack(lvt_9_2_, mouseX - lvt_4_1_ - lvt_10_2_, mouseY - lvt_5_1_ - lvt_11_2_, lvt_12_1_);
        }

        if (this.returningStack != null)
        {
            float lvt_10_3_ = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

            if (lvt_10_3_ >= 1.0F)
            {
                lvt_10_3_ = 1.0F;
                this.returningStack = null;
            }

            int lvt_11_3_ = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
            int lvt_12_2_ = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
            int lvt_13_1_ = this.touchUpX + (int)((float)lvt_11_3_ * lvt_10_3_);
            int lvt_14_1_ = this.touchUpY + (int)((float)lvt_12_2_ * lvt_10_3_);
            this.drawItemStack(this.returningStack, lvt_13_1_, lvt_14_1_, (String)null);
        }

        GlStateManager.popMatrix();

        if (lvt_8_2_.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack())
        {
            ItemStack lvt_10_4_ = this.theSlot.getStack();
            this.renderToolTip(lvt_10_4_, mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    /**
     * Render an ItemStack. Args : stack, x, y, format
     */
    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

    private void drawSlot(Slot slotIn)
    {
        int lvt_2_1_ = slotIn.xDisplayPosition;
        int lvt_3_1_ = slotIn.yDisplayPosition;
        ItemStack lvt_4_1_ = slotIn.getStack();
        boolean lvt_5_1_ = false;
        boolean lvt_6_1_ = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
        ItemStack lvt_7_1_ = this.mc.thePlayer.inventory.getItemStack();
        String lvt_8_1_ = null;

        if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && lvt_4_1_ != null)
        {
            lvt_4_1_ = lvt_4_1_.copy();
            lvt_4_1_.stackSize /= 2;
        }
        else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && lvt_7_1_ != null)
        {
            if (this.dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slotIn, lvt_7_1_, true) && this.inventorySlots.canDragIntoSlot(slotIn))
            {
                lvt_4_1_ = lvt_7_1_.copy();
                lvt_5_1_ = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, lvt_4_1_, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);

                if (lvt_4_1_.stackSize > lvt_4_1_.getMaxStackSize())
                {
                    lvt_8_1_ = EnumChatFormatting.YELLOW + "" + lvt_4_1_.getMaxStackSize();
                    lvt_4_1_.stackSize = lvt_4_1_.getMaxStackSize();
                }

                if (lvt_4_1_.stackSize > slotIn.getItemStackLimit(lvt_4_1_))
                {
                    lvt_8_1_ = EnumChatFormatting.YELLOW + "" + slotIn.getItemStackLimit(lvt_4_1_);
                    lvt_4_1_.stackSize = slotIn.getItemStackLimit(lvt_4_1_);
                }
            }
            else
            {
                this.dragSplittingSlots.remove(slotIn);
                this.updateDragSplitting();
            }
        }

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;

        if (lvt_4_1_ == null)
        {
            String lvt_9_1_ = slotIn.getSlotTexture();

            if (lvt_9_1_ != null)
            {
                TextureAtlasSprite lvt_10_1_ = this.mc.getTextureMapBlocks().getAtlasSprite(lvt_9_1_);
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModalRect(lvt_2_1_, lvt_3_1_, lvt_10_1_, 16, 16);
                GlStateManager.enableLighting();
                lvt_6_1_ = true;
            }
        }

        if (!lvt_6_1_)
        {
            if (lvt_5_1_)
            {
                drawRect(lvt_2_1_, lvt_3_1_, lvt_2_1_ + 16, lvt_3_1_ + 16, -2130706433);
            }

            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(lvt_4_1_, lvt_2_1_, lvt_3_1_);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, lvt_4_1_, lvt_2_1_, lvt_3_1_, lvt_8_1_);
        }

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private void updateDragSplitting()
    {
        ItemStack lvt_1_1_ = this.mc.thePlayer.inventory.getItemStack();

        if (lvt_1_1_ != null && this.dragSplitting)
        {
            this.dragSplittingRemnant = lvt_1_1_.stackSize;

            for (Slot lvt_3_1_ : this.dragSplittingSlots)
            {
                ItemStack lvt_4_1_ = lvt_1_1_.copy();
                int lvt_5_1_ = lvt_3_1_.getStack() == null ? 0 : lvt_3_1_.getStack().stackSize;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, lvt_4_1_, lvt_5_1_);

                if (lvt_4_1_.stackSize > lvt_4_1_.getMaxStackSize())
                {
                    lvt_4_1_.stackSize = lvt_4_1_.getMaxStackSize();
                }

                if (lvt_4_1_.stackSize > lvt_3_1_.getItemStackLimit(lvt_4_1_))
                {
                    lvt_4_1_.stackSize = lvt_3_1_.getItemStackLimit(lvt_4_1_);
                }

                this.dragSplittingRemnant -= lvt_4_1_.stackSize - lvt_5_1_;
            }
        }
    }

    /**
     * Returns the slot at the given coordinates or null if there is none.
     */
    private Slot getSlotAtPosition(int x, int y)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < this.inventorySlots.inventorySlots.size(); ++lvt_3_1_)
        {
            Slot lvt_4_1_ = (Slot)this.inventorySlots.inventorySlots.get(lvt_3_1_);

            if (this.isMouseOverSlot(lvt_4_1_, x, y))
            {
                return lvt_4_1_;
            }
        }

        return null;
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean lvt_4_1_ = mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
        Slot lvt_5_1_ = this.getSlotAtPosition(mouseX, mouseY);
        long lvt_6_1_ = Minecraft.getSystemTime();
        this.doubleClick = this.lastClickSlot == lvt_5_1_ && lvt_6_1_ - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
        this.ignoreMouseUp = false;

        if (mouseButton == 0 || mouseButton == 1 || lvt_4_1_)
        {
            int lvt_8_1_ = this.guiLeft;
            int lvt_9_1_ = this.guiTop;
            boolean lvt_10_1_ = mouseX < lvt_8_1_ || mouseY < lvt_9_1_ || mouseX >= lvt_8_1_ + this.xSize || mouseY >= lvt_9_1_ + this.ySize;
            int lvt_11_1_ = -1;

            if (lvt_5_1_ != null)
            {
                lvt_11_1_ = lvt_5_1_.slotNumber;
            }

            if (lvt_10_1_)
            {
                lvt_11_1_ = -999;
            }

            if (this.mc.gameSettings.touchscreen && lvt_10_1_ && this.mc.thePlayer.inventory.getItemStack() == null)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                return;
            }

            if (lvt_11_1_ != -1)
            {
                if (this.mc.gameSettings.touchscreen)
                {
                    if (lvt_5_1_ != null && lvt_5_1_.getHasStack())
                    {
                        this.clickedSlot = lvt_5_1_;
                        this.draggedStack = null;
                        this.isRightMouseClick = mouseButton == 1;
                    }
                    else
                    {
                        this.clickedSlot = null;
                    }
                }
                else if (!this.dragSplitting)
                {
                    if (this.mc.thePlayer.inventory.getItemStack() == null)
                    {
                        if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                        {
                            this.handleMouseClick(lvt_5_1_, lvt_11_1_, mouseButton, 3);
                        }
                        else
                        {
                            boolean lvt_12_1_ = lvt_11_1_ != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            int lvt_13_1_ = 0;

                            if (lvt_12_1_)
                            {
                                this.shiftClickedSlot = lvt_5_1_ != null && lvt_5_1_.getHasStack() ? lvt_5_1_.getStack() : null;
                                lvt_13_1_ = 1;
                            }
                            else if (lvt_11_1_ == -999)
                            {
                                lvt_13_1_ = 4;
                            }

                            this.handleMouseClick(lvt_5_1_, lvt_11_1_, mouseButton, lvt_13_1_);
                        }

                        this.ignoreMouseUp = true;
                    }
                    else
                    {
                        this.dragSplitting = true;
                        this.dragSplittingButton = mouseButton;
                        this.dragSplittingSlots.clear();

                        if (mouseButton == 0)
                        {
                            this.dragSplittingLimit = 0;
                        }
                        else if (mouseButton == 1)
                        {
                            this.dragSplittingLimit = 1;
                        }
                        else if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                        {
                            this.dragSplittingLimit = 2;
                        }
                    }
                }
            }
        }

        this.lastClickSlot = lvt_5_1_;
        this.lastClickTime = lvt_6_1_;
        this.lastClickButton = mouseButton;
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        Slot lvt_6_1_ = this.getSlotAtPosition(mouseX, mouseY);
        ItemStack lvt_7_1_ = this.mc.thePlayer.inventory.getItemStack();

        if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
        {
            if (clickedMouseButton == 0 || clickedMouseButton == 1)
            {
                if (this.draggedStack == null)
                {
                    if (lvt_6_1_ != this.clickedSlot && this.clickedSlot.getStack() != null)
                    {
                        this.draggedStack = this.clickedSlot.getStack().copy();
                    }
                }
                else if (this.draggedStack.stackSize > 1 && lvt_6_1_ != null && Container.canAddItemToSlot(lvt_6_1_, this.draggedStack, false))
                {
                    long lvt_8_1_ = Minecraft.getSystemTime();

                    if (this.currentDragTargetSlot == lvt_6_1_)
                    {
                        if (lvt_8_1_ - this.dragItemDropDelay > 500L)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                            this.handleMouseClick(lvt_6_1_, lvt_6_1_.slotNumber, 1, 0);
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                            this.dragItemDropDelay = lvt_8_1_ + 750L;
                            --this.draggedStack.stackSize;
                        }
                    }
                    else
                    {
                        this.currentDragTargetSlot = lvt_6_1_;
                        this.dragItemDropDelay = lvt_8_1_;
                    }
                }
            }
        }
        else if (this.dragSplitting && lvt_6_1_ != null && lvt_7_1_ != null && lvt_7_1_.stackSize > this.dragSplittingSlots.size() && Container.canAddItemToSlot(lvt_6_1_, lvt_7_1_, true) && lvt_6_1_.isItemValid(lvt_7_1_) && this.inventorySlots.canDragIntoSlot(lvt_6_1_))
        {
            this.dragSplittingSlots.add(lvt_6_1_);
            this.updateDragSplitting();
        }
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        Slot lvt_4_1_ = this.getSlotAtPosition(mouseX, mouseY);
        int lvt_5_1_ = this.guiLeft;
        int lvt_6_1_ = this.guiTop;
        boolean lvt_7_1_ = mouseX < lvt_5_1_ || mouseY < lvt_6_1_ || mouseX >= lvt_5_1_ + this.xSize || mouseY >= lvt_6_1_ + this.ySize;
        int lvt_8_1_ = -1;

        if (lvt_4_1_ != null)
        {
            lvt_8_1_ = lvt_4_1_.slotNumber;
        }

        if (lvt_7_1_)
        {
            lvt_8_1_ = -999;
        }

        if (this.doubleClick && lvt_4_1_ != null && state == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, lvt_4_1_))
        {
            if (isShiftKeyDown())
            {
                if (lvt_4_1_ != null && lvt_4_1_.inventory != null && this.shiftClickedSlot != null)
                {
                    for (Slot lvt_10_1_ : this.inventorySlots.inventorySlots)
                    {
                        if (lvt_10_1_ != null && lvt_10_1_.canTakeStack(this.mc.thePlayer) && lvt_10_1_.getHasStack() && lvt_10_1_.inventory == lvt_4_1_.inventory && Container.canAddItemToSlot(lvt_10_1_, this.shiftClickedSlot, true))
                        {
                            this.handleMouseClick(lvt_10_1_, lvt_10_1_.slotNumber, state, 1);
                        }
                    }
                }
            }
            else
            {
                this.handleMouseClick(lvt_4_1_, lvt_8_1_, state, 6);
            }

            this.doubleClick = false;
            this.lastClickTime = 0L;
        }
        else
        {
            if (this.dragSplitting && this.dragSplittingButton != state)
            {
                this.dragSplitting = false;
                this.dragSplittingSlots.clear();
                this.ignoreMouseUp = true;
                return;
            }

            if (this.ignoreMouseUp)
            {
                this.ignoreMouseUp = false;
                return;
            }

            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
            {
                if (state == 0 || state == 1)
                {
                    if (this.draggedStack == null && lvt_4_1_ != this.clickedSlot)
                    {
                        this.draggedStack = this.clickedSlot.getStack();
                    }

                    boolean lvt_9_2_ = Container.canAddItemToSlot(lvt_4_1_, this.draggedStack, false);

                    if (lvt_8_1_ != -1 && this.draggedStack != null && lvt_9_2_)
                    {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                        this.handleMouseClick(lvt_4_1_, lvt_8_1_, 0, 0);

                        if (this.mc.thePlayer.inventory.getItemStack() != null)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                            this.touchUpX = mouseX - lvt_5_1_;
                            this.touchUpY = mouseY - lvt_6_1_;
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        }
                        else
                        {
                            this.returningStack = null;
                        }
                    }
                    else if (this.draggedStack != null)
                    {
                        this.touchUpX = mouseX - lvt_5_1_;
                        this.touchUpY = mouseY - lvt_6_1_;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                    }

                    this.draggedStack = null;
                    this.clickedSlot = null;
                }
            }
            else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty())
            {
                this.handleMouseClick((Slot)null, -999, Container.func_94534_d(0, this.dragSplittingLimit), 5);

                for (Slot lvt_10_2_ : this.dragSplittingSlots)
                {
                    this.handleMouseClick(lvt_10_2_, lvt_10_2_.slotNumber, Container.func_94534_d(1, this.dragSplittingLimit), 5);
                }

                this.handleMouseClick((Slot)null, -999, Container.func_94534_d(2, this.dragSplittingLimit), 5);
            }
            else if (this.mc.thePlayer.inventory.getItemStack() != null)
            {
                if (state == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
                {
                    this.handleMouseClick(lvt_4_1_, lvt_8_1_, state, 3);
                }
                else
                {
                    boolean lvt_9_4_ = lvt_8_1_ != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                    if (lvt_9_4_)
                    {
                        this.shiftClickedSlot = lvt_4_1_ != null && lvt_4_1_.getHasStack() ? lvt_4_1_.getStack() : null;
                    }

                    this.handleMouseClick(lvt_4_1_, lvt_8_1_, state, lvt_9_4_ ? 1 : 0);
                }
            }
        }

        if (this.mc.thePlayer.inventory.getItemStack() == null)
        {
            this.lastClickTime = 0L;
        }

        this.dragSplitting = false;
    }

    /**
     * Returns if the passed mouse position is over the specified slot. Args : slot, mouseX, mouseY
     */
    private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
    }

    /**
     * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
     * pointY
     */
    protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY)
    {
        int lvt_7_1_ = this.guiLeft;
        int lvt_8_1_ = this.guiTop;
        pointX = pointX - lvt_7_1_;
        pointY = pointY - lvt_8_1_;
        return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType)
    {
        if (slotIn != null)
        {
            slotId = slotIn.slotNumber;
        }

        this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, clickType, this.mc.thePlayer);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.thePlayer.closeScreen();
        }

        this.checkHotbarKeys(keyCode);

        if (this.theSlot != null && this.theSlot.getHasStack())
        {
            if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode())
            {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
            }
            else if (keyCode == this.mc.gameSettings.keyBindDrop.getKeyCode())
            {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
            }
        }
    }

    /**
     * This function is what controls the hotbar shortcut check when you press a number key when hovering a stack. Args
     * : keyCode, Returns true if a Hotbar key is pressed, else false
     */
    protected boolean checkHotbarKeys(int keyCode)
    {
        if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < 9; ++lvt_2_1_)
            {
                if (keyCode == this.mc.gameSettings.keyBindsHotbar[lvt_2_1_].getKeyCode())
                {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, lvt_2_1_, 2);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        if (this.mc.thePlayer != null)
        {
            this.inventorySlots.onContainerClosed(this.mc.thePlayer);
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }
}
