package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiContainerCreative extends InventoryEffectRenderer
{
    /** The location of the creative inventory tabs texture */
    private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static InventoryBasic field_147060_v = new InventoryBasic("tmp", true, 45);

    /** Currently selected creative inventory tab index. */
    private static int selectedTabIndex = CreativeTabs.tabBlock.getTabIndex();

    /** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
    private float currentScroll;

    /** True if the scrollbar is being dragged */
    private boolean isScrolling;

    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking;
    private GuiTextField searchField;
    private List<Slot> field_147063_B;
    private Slot field_147064_C;
    private boolean field_147057_D;
    private CreativeCrafting field_147059_E;

    public GuiContainerCreative(EntityPlayer p_i1088_1_)
    {
        super(new GuiContainerCreative.ContainerCreative(p_i1088_1_));
        p_i1088_1_.openContainer = this.inventorySlots;
        this.allowUserInput = true;
        this.ySize = 136;
        this.xSize = 195;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (!this.mc.playerController.isInCreativeMode())
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }

        this.updateActivePotionEffects();
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType)
    {
        this.field_147057_D = true;
        boolean lvt_5_1_ = clickType == 1;
        clickType = slotId == -999 && clickType == 0 ? 4 : clickType;

        if (slotIn == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && clickType != 5)
        {
            InventoryPlayer lvt_6_6_ = this.mc.thePlayer.inventory;

            if (lvt_6_6_.getItemStack() != null)
            {
                if (clickedButton == 0)
                {
                    this.mc.thePlayer.dropPlayerItemWithRandomChoice(lvt_6_6_.getItemStack(), true);
                    this.mc.playerController.sendPacketDropItem(lvt_6_6_.getItemStack());
                    lvt_6_6_.setItemStack((ItemStack)null);
                }

                if (clickedButton == 1)
                {
                    ItemStack lvt_7_2_ = lvt_6_6_.getItemStack().splitStack(1);
                    this.mc.thePlayer.dropPlayerItemWithRandomChoice(lvt_7_2_, true);
                    this.mc.playerController.sendPacketDropItem(lvt_7_2_);

                    if (lvt_6_6_.getItemStack().stackSize == 0)
                    {
                        lvt_6_6_.setItemStack((ItemStack)null);
                    }
                }
            }
        }
        else if (slotIn == this.field_147064_C && lvt_5_1_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < this.mc.thePlayer.inventoryContainer.getInventory().size(); ++lvt_6_1_)
            {
                this.mc.playerController.sendSlotPacket((ItemStack)null, lvt_6_1_);
            }
        }
        else if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex())
        {
            if (slotIn == this.field_147064_C)
            {
                this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
            }
            else if (clickType == 4 && slotIn != null && slotIn.getHasStack())
            {
                ItemStack lvt_6_2_ = slotIn.decrStackSize(clickedButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
                this.mc.thePlayer.dropPlayerItemWithRandomChoice(lvt_6_2_, true);
                this.mc.playerController.sendPacketDropItem(lvt_6_2_);
            }
            else if (clickType == 4 && this.mc.thePlayer.inventory.getItemStack() != null)
            {
                this.mc.thePlayer.dropPlayerItemWithRandomChoice(this.mc.thePlayer.inventory.getItemStack(), true);
                this.mc.playerController.sendPacketDropItem(this.mc.thePlayer.inventory.getItemStack());
                this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
            }
            else
            {
                this.mc.thePlayer.inventoryContainer.slotClick(slotIn == null ? slotId : ((GuiContainerCreative.CreativeSlot)slotIn).slot.slotNumber, clickedButton, clickType, this.mc.thePlayer);
                this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
            }
        }
        else if (clickType != 5 && slotIn.inventory == field_147060_v)
        {
            InventoryPlayer lvt_6_3_ = this.mc.thePlayer.inventory;
            ItemStack lvt_7_1_ = lvt_6_3_.getItemStack();
            ItemStack lvt_8_1_ = slotIn.getStack();

            if (clickType == 2)
            {
                if (lvt_8_1_ != null && clickedButton >= 0 && clickedButton < 9)
                {
                    ItemStack lvt_9_1_ = lvt_8_1_.copy();
                    lvt_9_1_.stackSize = lvt_9_1_.getMaxStackSize();
                    this.mc.thePlayer.inventory.setInventorySlotContents(clickedButton, lvt_9_1_);
                    this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
                }

                return;
            }

            if (clickType == 3)
            {
                if (lvt_6_3_.getItemStack() == null && slotIn.getHasStack())
                {
                    ItemStack lvt_9_2_ = slotIn.getStack().copy();
                    lvt_9_2_.stackSize = lvt_9_2_.getMaxStackSize();
                    lvt_6_3_.setItemStack(lvt_9_2_);
                }

                return;
            }

            if (clickType == 4)
            {
                if (lvt_8_1_ != null)
                {
                    ItemStack lvt_9_3_ = lvt_8_1_.copy();
                    lvt_9_3_.stackSize = clickedButton == 0 ? 1 : lvt_9_3_.getMaxStackSize();
                    this.mc.thePlayer.dropPlayerItemWithRandomChoice(lvt_9_3_, true);
                    this.mc.playerController.sendPacketDropItem(lvt_9_3_);
                }

                return;
            }

            if (lvt_7_1_ != null && lvt_8_1_ != null && lvt_7_1_.isItemEqual(lvt_8_1_))
            {
                if (clickedButton == 0)
                {
                    if (lvt_5_1_)
                    {
                        lvt_7_1_.stackSize = lvt_7_1_.getMaxStackSize();
                    }
                    else if (lvt_7_1_.stackSize < lvt_7_1_.getMaxStackSize())
                    {
                        ++lvt_7_1_.stackSize;
                    }
                }
                else if (lvt_7_1_.stackSize <= 1)
                {
                    lvt_6_3_.setItemStack((ItemStack)null);
                }
                else
                {
                    --lvt_7_1_.stackSize;
                }
            }
            else if (lvt_8_1_ != null && lvt_7_1_ == null)
            {
                lvt_6_3_.setItemStack(ItemStack.copyItemStack(lvt_8_1_));
                lvt_7_1_ = lvt_6_3_.getItemStack();

                if (lvt_5_1_)
                {
                    lvt_7_1_.stackSize = lvt_7_1_.getMaxStackSize();
                }
            }
            else
            {
                lvt_6_3_.setItemStack((ItemStack)null);
            }
        }
        else
        {
            this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, clickedButton, clickType, this.mc.thePlayer);

            if (Container.getDragEvent(clickedButton) == 2)
            {
                for (int lvt_6_4_ = 0; lvt_6_4_ < 9; ++lvt_6_4_)
                {
                    this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + lvt_6_4_).getStack(), 36 + lvt_6_4_);
                }
            }
            else if (slotIn != null)
            {
                ItemStack lvt_6_5_ = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
                this.mc.playerController.sendSlotPacket(lvt_6_5_, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
            }
        }
    }

    protected void updateActivePotionEffects()
    {
        int lvt_1_1_ = this.guiLeft;
        super.updateActivePotionEffects();

        if (this.searchField != null && this.guiLeft != lvt_1_1_)
        {
            this.searchField.xPosition = this.guiLeft + 82;
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        if (this.mc.playerController.isInCreativeMode())
        {
            super.initGui();
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
            this.searchField.setMaxStringLength(15);
            this.searchField.setEnableBackgroundDrawing(false);
            this.searchField.setVisible(false);
            this.searchField.setTextColor(16777215);
            int lvt_1_1_ = selectedTabIndex;
            selectedTabIndex = -1;
            this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[lvt_1_1_]);
            this.field_147059_E = new CreativeCrafting(this.mc);
            this.mc.thePlayer.inventoryContainer.onCraftGuiOpened(this.field_147059_E);
        }
        else
        {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null)
        {
            this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.field_147059_E);
        }

        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (selectedTabIndex != CreativeTabs.tabAllSearch.getTabIndex())
        {
            if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat))
            {
                this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
            }
            else
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
        else
        {
            if (this.field_147057_D)
            {
                this.field_147057_D = false;
                this.searchField.setText("");
            }

            if (!this.checkHotbarKeys(keyCode))
            {
                if (this.searchField.textboxKeyTyped(typedChar, keyCode))
                {
                    this.updateCreativeSearch();
                }
                else
                {
                    super.keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    private void updateCreativeSearch()
    {
        GuiContainerCreative.ContainerCreative lvt_1_1_ = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
        lvt_1_1_.itemList.clear();

        for (Item lvt_3_1_ : Item.itemRegistry)
        {
            if (lvt_3_1_ != null && lvt_3_1_.getCreativeTab() != null)
            {
                lvt_3_1_.getSubItems(lvt_3_1_, (CreativeTabs)null, lvt_1_1_.itemList);
            }
        }

        for (Enchantment lvt_5_1_ : Enchantment.enchantmentsBookList)
        {
            if (lvt_5_1_ != null && lvt_5_1_.type != null)
            {
                Items.enchanted_book.getAll(lvt_5_1_, lvt_1_1_.itemList);
            }
        }

        Iterator<ItemStack> lvt_2_3_ = lvt_1_1_.itemList.iterator();
        String lvt_3_3_ = this.searchField.getText().toLowerCase();

        while (lvt_2_3_.hasNext())
        {
            ItemStack lvt_4_2_ = (ItemStack)lvt_2_3_.next();
            boolean lvt_5_2_ = false;

            for (String lvt_7_1_ : lvt_4_2_.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips))
            {
                if (EnumChatFormatting.getTextWithoutFormattingCodes(lvt_7_1_).toLowerCase().contains(lvt_3_3_))
                {
                    lvt_5_2_ = true;
                    break;
                }
            }

            if (!lvt_5_2_)
            {
                lvt_2_3_.remove();
            }
        }

        this.currentScroll = 0.0F;
        lvt_1_1_.scrollTo(0.0F);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        CreativeTabs lvt_3_1_ = CreativeTabs.creativeTabArray[selectedTabIndex];

        if (lvt_3_1_.drawInForegroundOfTab())
        {
            GlStateManager.disableBlend();
            this.fontRendererObj.drawString(I18n.format(lvt_3_1_.getTranslatedTabLabel(), new Object[0]), 8, 6, 4210752);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            int lvt_4_1_ = mouseX - this.guiLeft;
            int lvt_5_1_ = mouseY - this.guiTop;

            for (CreativeTabs lvt_9_1_ : CreativeTabs.creativeTabArray)
            {
                if (this.func_147049_a(lvt_9_1_, lvt_4_1_, lvt_5_1_))
                {
                    return;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state == 0)
        {
            int lvt_4_1_ = mouseX - this.guiLeft;
            int lvt_5_1_ = mouseY - this.guiTop;

            for (CreativeTabs lvt_9_1_ : CreativeTabs.creativeTabArray)
            {
                if (this.func_147049_a(lvt_9_1_, lvt_4_1_, lvt_5_1_))
                {
                    this.setCurrentCreativeTab(lvt_9_1_);
                    return;
                }
            }
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * returns (if you are not on the inventoryTab) and (the flag isn't set) and (you have more than 1 page of items)
     */
    private boolean needsScrollBars()
    {
        return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory() && ((GuiContainerCreative.ContainerCreative)this.inventorySlots).func_148328_e();
    }

    private void setCurrentCreativeTab(CreativeTabs p_147050_1_)
    {
        int lvt_2_1_ = selectedTabIndex;
        selectedTabIndex = p_147050_1_.getTabIndex();
        GuiContainerCreative.ContainerCreative lvt_3_1_ = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
        this.dragSplittingSlots.clear();
        lvt_3_1_.itemList.clear();
        p_147050_1_.displayAllReleventItems(lvt_3_1_.itemList);

        if (p_147050_1_ == CreativeTabs.tabInventory)
        {
            Container lvt_4_1_ = this.mc.thePlayer.inventoryContainer;

            if (this.field_147063_B == null)
            {
                this.field_147063_B = lvt_3_1_.inventorySlots;
            }

            lvt_3_1_.inventorySlots = Lists.newArrayList();

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.inventorySlots.size(); ++lvt_5_1_)
            {
                Slot lvt_6_1_ = new GuiContainerCreative.CreativeSlot((Slot)lvt_4_1_.inventorySlots.get(lvt_5_1_), lvt_5_1_);
                lvt_3_1_.inventorySlots.add(lvt_6_1_);

                if (lvt_5_1_ >= 5 && lvt_5_1_ < 9)
                {
                    int lvt_7_1_ = lvt_5_1_ - 5;
                    int lvt_8_1_ = lvt_7_1_ / 2;
                    int lvt_9_1_ = lvt_7_1_ % 2;
                    lvt_6_1_.xDisplayPosition = 9 + lvt_8_1_ * 54;
                    lvt_6_1_.yDisplayPosition = 6 + lvt_9_1_ * 27;
                }
                else if (lvt_5_1_ >= 0 && lvt_5_1_ < 5)
                {
                    lvt_6_1_.yDisplayPosition = -2000;
                    lvt_6_1_.xDisplayPosition = -2000;
                }
                else if (lvt_5_1_ < lvt_4_1_.inventorySlots.size())
                {
                    int lvt_7_2_ = lvt_5_1_ - 9;
                    int lvt_8_2_ = lvt_7_2_ % 9;
                    int lvt_9_2_ = lvt_7_2_ / 9;
                    lvt_6_1_.xDisplayPosition = 9 + lvt_8_2_ * 18;

                    if (lvt_5_1_ >= 36)
                    {
                        lvt_6_1_.yDisplayPosition = 112;
                    }
                    else
                    {
                        lvt_6_1_.yDisplayPosition = 54 + lvt_9_2_ * 18;
                    }
                }
            }

            this.field_147064_C = new Slot(field_147060_v, 0, 173, 112);
            lvt_3_1_.inventorySlots.add(this.field_147064_C);
        }
        else if (lvt_2_1_ == CreativeTabs.tabInventory.getTabIndex())
        {
            lvt_3_1_.inventorySlots = this.field_147063_B;
            this.field_147063_B = null;
        }

        if (this.searchField != null)
        {
            if (p_147050_1_ == CreativeTabs.tabAllSearch)
            {
                this.searchField.setVisible(true);
                this.searchField.setCanLoseFocus(false);
                this.searchField.setFocused(true);
                this.searchField.setText("");
                this.updateCreativeSearch();
            }
            else
            {
                this.searchField.setVisible(false);
                this.searchField.setCanLoseFocus(true);
                this.searchField.setFocused(false);
            }
        }

        this.currentScroll = 0.0F;
        lvt_3_1_.scrollTo(0.0F);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int lvt_1_1_ = Mouse.getEventDWheel();

        if (lvt_1_1_ != 0 && this.needsScrollBars())
        {
            int lvt_2_1_ = ((GuiContainerCreative.ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5;

            if (lvt_1_1_ > 0)
            {
                lvt_1_1_ = 1;
            }

            if (lvt_1_1_ < 0)
            {
                lvt_1_1_ = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)lvt_1_1_ / (double)lvt_2_1_);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        boolean lvt_4_1_ = Mouse.isButtonDown(0);
        int lvt_5_1_ = this.guiLeft;
        int lvt_6_1_ = this.guiTop;
        int lvt_7_1_ = lvt_5_1_ + 175;
        int lvt_8_1_ = lvt_6_1_ + 18;
        int lvt_9_1_ = lvt_7_1_ + 14;
        int lvt_10_1_ = lvt_8_1_ + 112;

        if (!this.wasClicking && lvt_4_1_ && mouseX >= lvt_7_1_ && mouseY >= lvt_8_1_ && mouseX < lvt_9_1_ && mouseY < lvt_10_1_)
        {
            this.isScrolling = this.needsScrollBars();
        }

        if (!lvt_4_1_)
        {
            this.isScrolling = false;
        }

        this.wasClicking = lvt_4_1_;

        if (this.isScrolling)
        {
            this.currentScroll = ((float)(mouseY - lvt_8_1_) - 7.5F) / ((float)(lvt_10_1_ - lvt_8_1_) - 15.0F);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (CreativeTabs lvt_14_1_ : CreativeTabs.creativeTabArray)
        {
            if (this.renderCreativeInventoryHoveringText(lvt_14_1_, mouseX, mouseY))
            {
                break;
            }
        }

        if (this.field_147064_C != null && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex() && this.isPointInRegion(this.field_147064_C.xDisplayPosition, this.field_147064_C.yDisplayPosition, 16, 16, mouseX, mouseY))
        {
            this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot", new Object[0]), mouseX, mouseY);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex())
        {
            List<String> lvt_4_1_ = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
            CreativeTabs lvt_5_1_ = stack.getItem().getCreativeTab();

            if (lvt_5_1_ == null && stack.getItem() == Items.enchanted_book)
            {
                Map<Integer, Integer> lvt_6_1_ = EnchantmentHelper.getEnchantments(stack);

                if (lvt_6_1_.size() == 1)
                {
                    Enchantment lvt_7_1_ = Enchantment.getEnchantmentById(((Integer)lvt_6_1_.keySet().iterator().next()).intValue());

                    for (CreativeTabs lvt_11_1_ : CreativeTabs.creativeTabArray)
                    {
                        if (lvt_11_1_.hasRelevantEnchantmentType(lvt_7_1_.type))
                        {
                            lvt_5_1_ = lvt_11_1_;
                            break;
                        }
                    }
                }
            }

            if (lvt_5_1_ != null)
            {
                lvt_4_1_.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.format(lvt_5_1_.getTranslatedTabLabel(), new Object[0]));
            }

            for (int lvt_6_2_ = 0; lvt_6_2_ < lvt_4_1_.size(); ++lvt_6_2_)
            {
                if (lvt_6_2_ == 0)
                {
                    lvt_4_1_.set(lvt_6_2_, stack.getRarity().rarityColor + (String)lvt_4_1_.get(lvt_6_2_));
                }
                else
                {
                    lvt_4_1_.set(lvt_6_2_, EnumChatFormatting.GRAY + (String)lvt_4_1_.get(lvt_6_2_));
                }
            }

            this.drawHoveringText(lvt_4_1_, x, y);
        }
        else
        {
            super.renderToolTip(stack, x, y);
        }
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        CreativeTabs lvt_4_1_ = CreativeTabs.creativeTabArray[selectedTabIndex];

        for (CreativeTabs lvt_8_1_ : CreativeTabs.creativeTabArray)
        {
            this.mc.getTextureManager().bindTexture(creativeInventoryTabs);

            if (lvt_8_1_.getTabIndex() != selectedTabIndex)
            {
                this.func_147051_a(lvt_8_1_);
            }
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + lvt_4_1_.getBackgroundImageName()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int lvt_5_2_ = this.guiLeft + 175;
        int lvt_6_2_ = this.guiTop + 18;
        int lvt_7_2_ = lvt_6_2_ + 112;
        this.mc.getTextureManager().bindTexture(creativeInventoryTabs);

        if (lvt_4_1_.shouldHidePlayerInventory())
        {
            this.drawTexturedModalRect(lvt_5_2_, lvt_6_2_ + (int)((float)(lvt_7_2_ - lvt_6_2_ - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }

        this.func_147051_a(lvt_4_1_);

        if (lvt_4_1_ == CreativeTabs.tabInventory)
        {
            GuiInventory.drawEntityOnScreen(this.guiLeft + 43, this.guiTop + 45, 20, (float)(this.guiLeft + 43 - mouseX), (float)(this.guiTop + 45 - 30 - mouseY), this.mc.thePlayer);
        }
    }

    protected boolean func_147049_a(CreativeTabs p_147049_1_, int p_147049_2_, int p_147049_3_)
    {
        int lvt_4_1_ = p_147049_1_.getTabColumn();
        int lvt_5_1_ = 28 * lvt_4_1_;
        int lvt_6_1_ = 0;

        if (lvt_4_1_ == 5)
        {
            lvt_5_1_ = this.xSize - 28 + 2;
        }
        else if (lvt_4_1_ > 0)
        {
            lvt_5_1_ += lvt_4_1_;
        }

        if (p_147049_1_.isTabInFirstRow())
        {
            lvt_6_1_ = lvt_6_1_ - 32;
        }
        else
        {
            lvt_6_1_ = lvt_6_1_ + this.ySize;
        }

        return p_147049_2_ >= lvt_5_1_ && p_147049_2_ <= lvt_5_1_ + 28 && p_147049_3_ >= lvt_6_1_ && p_147049_3_ <= lvt_6_1_ + 32;
    }

    /**
     * Renders the creative inventory hovering text if mouse is over it. Returns true if did render or false otherwise.
     * Params: current creative tab to be checked, current mouse x position, current mouse y position.
     */
    protected boolean renderCreativeInventoryHoveringText(CreativeTabs p_147052_1_, int p_147052_2_, int p_147052_3_)
    {
        int lvt_4_1_ = p_147052_1_.getTabColumn();
        int lvt_5_1_ = 28 * lvt_4_1_;
        int lvt_6_1_ = 0;

        if (lvt_4_1_ == 5)
        {
            lvt_5_1_ = this.xSize - 28 + 2;
        }
        else if (lvt_4_1_ > 0)
        {
            lvt_5_1_ += lvt_4_1_;
        }

        if (p_147052_1_.isTabInFirstRow())
        {
            lvt_6_1_ = lvt_6_1_ - 32;
        }
        else
        {
            lvt_6_1_ = lvt_6_1_ + this.ySize;
        }

        if (this.isPointInRegion(lvt_5_1_ + 3, lvt_6_1_ + 3, 23, 27, p_147052_2_, p_147052_3_))
        {
            this.drawCreativeTabHoveringText(I18n.format(p_147052_1_.getTranslatedTabLabel(), new Object[0]), p_147052_2_, p_147052_3_);
            return true;
        }
        else
        {
            return false;
        }
    }

    protected void func_147051_a(CreativeTabs p_147051_1_)
    {
        boolean lvt_2_1_ = p_147051_1_.getTabIndex() == selectedTabIndex;
        boolean lvt_3_1_ = p_147051_1_.isTabInFirstRow();
        int lvt_4_1_ = p_147051_1_.getTabColumn();
        int lvt_5_1_ = lvt_4_1_ * 28;
        int lvt_6_1_ = 0;
        int lvt_7_1_ = this.guiLeft + 28 * lvt_4_1_;
        int lvt_8_1_ = this.guiTop;
        int lvt_9_1_ = 32;

        if (lvt_2_1_)
        {
            lvt_6_1_ += 32;
        }

        if (lvt_4_1_ == 5)
        {
            lvt_7_1_ = this.guiLeft + this.xSize - 28;
        }
        else if (lvt_4_1_ > 0)
        {
            lvt_7_1_ += lvt_4_1_;
        }

        if (lvt_3_1_)
        {
            lvt_8_1_ = lvt_8_1_ - 28;
        }
        else
        {
            lvt_6_1_ += 64;
            lvt_8_1_ = lvt_8_1_ + (this.ySize - 4);
        }

        GlStateManager.disableLighting();
        this.drawTexturedModalRect(lvt_7_1_, lvt_8_1_, lvt_5_1_, lvt_6_1_, 28, lvt_9_1_);
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        lvt_7_1_ = lvt_7_1_ + 6;
        lvt_8_1_ = lvt_8_1_ + 8 + (lvt_3_1_ ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        ItemStack lvt_10_1_ = p_147051_1_.getIconItemStack();
        this.itemRender.renderItemAndEffectIntoGUI(lvt_10_1_, lvt_7_1_, lvt_8_1_);
        this.itemRender.renderItemOverlays(this.fontRendererObj, lvt_10_1_, lvt_7_1_, lvt_8_1_);
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
        }
    }

    public int getSelectedTabIndex()
    {
        return selectedTabIndex;
    }

    static class ContainerCreative extends Container
    {
        public List<ItemStack> itemList = Lists.newArrayList();

        public ContainerCreative(EntityPlayer p_i1086_1_)
        {
            InventoryPlayer lvt_2_1_ = p_i1086_1_.inventory;

            for (int lvt_3_1_ = 0; lvt_3_1_ < 5; ++lvt_3_1_)
            {
                for (int lvt_4_1_ = 0; lvt_4_1_ < 9; ++lvt_4_1_)
                {
                    this.addSlotToContainer(new Slot(GuiContainerCreative.field_147060_v, lvt_3_1_ * 9 + lvt_4_1_, 9 + lvt_4_1_ * 18, 18 + lvt_3_1_ * 18));
                }
            }

            for (int lvt_3_2_ = 0; lvt_3_2_ < 9; ++lvt_3_2_)
            {
                this.addSlotToContainer(new Slot(lvt_2_1_, lvt_3_2_, 9 + lvt_3_2_ * 18, 112));
            }

            this.scrollTo(0.0F);
        }

        public boolean canInteractWith(EntityPlayer playerIn)
        {
            return true;
        }

        public void scrollTo(float p_148329_1_)
        {
            int lvt_2_1_ = (this.itemList.size() + 9 - 1) / 9 - 5;
            int lvt_3_1_ = (int)((double)(p_148329_1_ * (float)lvt_2_1_) + 0.5D);

            if (lvt_3_1_ < 0)
            {
                lvt_3_1_ = 0;
            }

            for (int lvt_4_1_ = 0; lvt_4_1_ < 5; ++lvt_4_1_)
            {
                for (int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_)
                {
                    int lvt_6_1_ = lvt_5_1_ + (lvt_4_1_ + lvt_3_1_) * 9;

                    if (lvt_6_1_ >= 0 && lvt_6_1_ < this.itemList.size())
                    {
                        GuiContainerCreative.field_147060_v.setInventorySlotContents(lvt_5_1_ + lvt_4_1_ * 9, (ItemStack)this.itemList.get(lvt_6_1_));
                    }
                    else
                    {
                        GuiContainerCreative.field_147060_v.setInventorySlotContents(lvt_5_1_ + lvt_4_1_ * 9, (ItemStack)null);
                    }
                }
            }
        }

        public boolean func_148328_e()
        {
            return this.itemList.size() > 45;
        }

        protected void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn)
        {
        }

        public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
        {
            if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size())
            {
                Slot lvt_3_1_ = (Slot)this.inventorySlots.get(index);

                if (lvt_3_1_ != null && lvt_3_1_.getHasStack())
                {
                    lvt_3_1_.putStack((ItemStack)null);
                }
            }

            return null;
        }

        public boolean canMergeSlot(ItemStack stack, Slot slotIn)
        {
            return slotIn.yDisplayPosition > 90;
        }

        public boolean canDragIntoSlot(Slot p_94531_1_)
        {
            return p_94531_1_.inventory instanceof InventoryPlayer || p_94531_1_.yDisplayPosition > 90 && p_94531_1_.xDisplayPosition <= 162;
        }
    }

    class CreativeSlot extends Slot
    {
        private final Slot slot;

        public CreativeSlot(Slot p_i46313_2_, int p_i46313_3_)
        {
            super(p_i46313_2_.inventory, p_i46313_3_, 0, 0);
            this.slot = p_i46313_2_;
        }

        public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
        {
            this.slot.onPickupFromSlot(playerIn, stack);
        }

        public boolean isItemValid(ItemStack stack)
        {
            return this.slot.isItemValid(stack);
        }

        public ItemStack getStack()
        {
            return this.slot.getStack();
        }

        public boolean getHasStack()
        {
            return this.slot.getHasStack();
        }

        public void putStack(ItemStack stack)
        {
            this.slot.putStack(stack);
        }

        public void onSlotChanged()
        {
            this.slot.onSlotChanged();
        }

        public int getSlotStackLimit()
        {
            return this.slot.getSlotStackLimit();
        }

        public int getItemStackLimit(ItemStack stack)
        {
            return this.slot.getItemStackLimit(stack);
        }

        public String getSlotTexture()
        {
            return this.slot.getSlotTexture();
        }

        public ItemStack decrStackSize(int amount)
        {
            return this.slot.decrStackSize(amount);
        }

        public boolean isHere(IInventory inv, int slotIn)
        {
            return this.slot.isHere(inv, slotIn);
        }
    }
}
