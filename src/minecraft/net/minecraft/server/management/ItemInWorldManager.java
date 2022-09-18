package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public class ItemInWorldManager
{
    /** The world object that this object is connected to. */
    public World theWorld;

    /** The EntityPlayerMP object that this object is connected to. */
    public EntityPlayerMP thisPlayerMP;
    private WorldSettings.GameType gameType = WorldSettings.GameType.NOT_SET;

    /** True if the player is destroying a block */
    private boolean isDestroyingBlock;
    private int initialDamage;
    private BlockPos field_180240_f = BlockPos.ORIGIN;
    private int curblockDamage;

    /**
     * Set to true when the "finished destroying block" packet is received but the block wasn't fully damaged yet. The
     * block will not be destroyed while this is false.
     */
    private boolean receivedFinishDiggingPacket;
    private BlockPos field_180241_i = BlockPos.ORIGIN;
    private int initialBlockDamage;
    private int durabilityRemainingOnBlock = -1;

    public ItemInWorldManager(World worldIn)
    {
        this.theWorld = worldIn;
    }

    public void setGameType(WorldSettings.GameType type)
    {
        this.gameType = type;
        type.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
        this.thisPlayerMP.sendPlayerAbilities();
        this.thisPlayerMP.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_GAME_MODE, new EntityPlayerMP[] {this.thisPlayerMP}));
    }

    public WorldSettings.GameType getGameType()
    {
        return this.gameType;
    }

    public boolean survivalOrAdventure()
    {
        return this.gameType.isSurvivalOrAdventure();
    }

    /**
     * Get if we are in creative game mode.
     */
    public boolean isCreative()
    {
        return this.gameType.isCreative();
    }

    /**
     * if the gameType is currently NOT_SET then change it to par1
     */
    public void initializeGameType(WorldSettings.GameType type)
    {
        if (this.gameType == WorldSettings.GameType.NOT_SET)
        {
            this.gameType = type;
        }

        this.setGameType(this.gameType);
    }

    public void updateBlockRemoving()
    {
        ++this.curblockDamage;

        if (this.receivedFinishDiggingPacket)
        {
            int lvt_1_1_ = this.curblockDamage - this.initialBlockDamage;
            Block lvt_2_1_ = this.theWorld.getBlockState(this.field_180241_i).getBlock();

            if (lvt_2_1_.getMaterial() == Material.air)
            {
                this.receivedFinishDiggingPacket = false;
            }
            else
            {
                float lvt_3_1_ = lvt_2_1_.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.field_180241_i) * (float)(lvt_1_1_ + 1);
                int lvt_4_1_ = (int)(lvt_3_1_ * 10.0F);

                if (lvt_4_1_ != this.durabilityRemainingOnBlock)
                {
                    this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180241_i, lvt_4_1_);
                    this.durabilityRemainingOnBlock = lvt_4_1_;
                }

                if (lvt_3_1_ >= 1.0F)
                {
                    this.receivedFinishDiggingPacket = false;
                    this.tryHarvestBlock(this.field_180241_i);
                }
            }
        }
        else if (this.isDestroyingBlock)
        {
            Block lvt_1_2_ = this.theWorld.getBlockState(this.field_180240_f).getBlock();

            if (lvt_1_2_.getMaterial() == Material.air)
            {
                this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, -1);
                this.durabilityRemainingOnBlock = -1;
                this.isDestroyingBlock = false;
            }
            else
            {
                int lvt_2_2_ = this.curblockDamage - this.initialDamage;
                float lvt_3_2_ = lvt_1_2_.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.field_180241_i) * (float)(lvt_2_2_ + 1);
                int lvt_4_2_ = (int)(lvt_3_2_ * 10.0F);

                if (lvt_4_2_ != this.durabilityRemainingOnBlock)
                {
                    this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, lvt_4_2_);
                    this.durabilityRemainingOnBlock = lvt_4_2_;
                }
            }
        }
    }

    /**
     * If not creative, it calls sendBlockBreakProgress until the block is broken first. tryHarvestBlock can also be the
     * result of this call.
     */
    public void onBlockClicked(BlockPos pos, EnumFacing side)
    {
        if (this.isCreative())
        {
            if (!this.theWorld.extinguishFire((EntityPlayer)null, pos, side))
            {
                this.tryHarvestBlock(pos);
            }
        }
        else
        {
            Block lvt_3_1_ = this.theWorld.getBlockState(pos).getBlock();

            if (this.gameType.isAdventure())
            {
                if (this.gameType == WorldSettings.GameType.SPECTATOR)
                {
                    return;
                }

                if (!this.thisPlayerMP.isAllowEdit())
                {
                    ItemStack lvt_4_1_ = this.thisPlayerMP.getCurrentEquippedItem();

                    if (lvt_4_1_ == null)
                    {
                        return;
                    }

                    if (!lvt_4_1_.canDestroy(lvt_3_1_))
                    {
                        return;
                    }
                }
            }

            this.theWorld.extinguishFire((EntityPlayer)null, pos, side);
            this.initialDamage = this.curblockDamage;
            float lvt_4_2_ = 1.0F;

            if (lvt_3_1_.getMaterial() != Material.air)
            {
                lvt_3_1_.onBlockClicked(this.theWorld, pos, this.thisPlayerMP);
                lvt_4_2_ = lvt_3_1_.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos);
            }

            if (lvt_3_1_.getMaterial() != Material.air && lvt_4_2_ >= 1.0F)
            {
                this.tryHarvestBlock(pos);
            }
            else
            {
                this.isDestroyingBlock = true;
                this.field_180240_f = pos;
                int lvt_5_1_ = (int)(lvt_4_2_ * 10.0F);
                this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, lvt_5_1_);
                this.durabilityRemainingOnBlock = lvt_5_1_;
            }
        }
    }

    public void blockRemoving(BlockPos pos)
    {
        if (pos.equals(this.field_180240_f))
        {
            int lvt_2_1_ = this.curblockDamage - this.initialDamage;
            Block lvt_3_1_ = this.theWorld.getBlockState(pos).getBlock();

            if (lvt_3_1_.getMaterial() != Material.air)
            {
                float lvt_4_1_ = lvt_3_1_.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos) * (float)(lvt_2_1_ + 1);

                if (lvt_4_1_ >= 0.7F)
                {
                    this.isDestroyingBlock = false;
                    this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, -1);
                    this.tryHarvestBlock(pos);
                }
                else if (!this.receivedFinishDiggingPacket)
                {
                    this.isDestroyingBlock = false;
                    this.receivedFinishDiggingPacket = true;
                    this.field_180241_i = pos;
                    this.initialBlockDamage = this.initialDamage;
                }
            }
        }
    }

    /**
     * Stops the block breaking process
     */
    public void cancelDestroyingBlock()
    {
        this.isDestroyingBlock = false;
        this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, -1);
    }

    /**
     * Removes a block and triggers the appropriate events
     */
    private boolean removeBlock(BlockPos pos)
    {
        IBlockState lvt_2_1_ = this.theWorld.getBlockState(pos);
        lvt_2_1_.getBlock().onBlockHarvested(this.theWorld, pos, lvt_2_1_, this.thisPlayerMP);
        boolean lvt_3_1_ = this.theWorld.setBlockToAir(pos);

        if (lvt_3_1_)
        {
            lvt_2_1_.getBlock().onBlockDestroyedByPlayer(this.theWorld, pos, lvt_2_1_);
        }

        return lvt_3_1_;
    }

    /**
     * Attempts to harvest a block
     */
    public boolean tryHarvestBlock(BlockPos pos)
    {
        if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItem() != null && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemSword)
        {
            return false;
        }
        else
        {
            IBlockState lvt_2_1_ = this.theWorld.getBlockState(pos);
            TileEntity lvt_3_1_ = this.theWorld.getTileEntity(pos);

            if (this.gameType.isAdventure())
            {
                if (this.gameType == WorldSettings.GameType.SPECTATOR)
                {
                    return false;
                }

                if (!this.thisPlayerMP.isAllowEdit())
                {
                    ItemStack lvt_4_1_ = this.thisPlayerMP.getCurrentEquippedItem();

                    if (lvt_4_1_ == null)
                    {
                        return false;
                    }

                    if (!lvt_4_1_.canDestroy(lvt_2_1_.getBlock()))
                    {
                        return false;
                    }
                }
            }

            this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, pos, Block.getStateId(lvt_2_1_));
            boolean lvt_4_2_ = this.removeBlock(pos);

            if (this.isCreative())
            {
                this.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(this.theWorld, pos));
            }
            else
            {
                ItemStack lvt_5_1_ = this.thisPlayerMP.getCurrentEquippedItem();
                boolean lvt_6_1_ = this.thisPlayerMP.canHarvestBlock(lvt_2_1_.getBlock());

                if (lvt_5_1_ != null)
                {
                    lvt_5_1_.onBlockDestroyed(this.theWorld, lvt_2_1_.getBlock(), pos, this.thisPlayerMP);

                    if (lvt_5_1_.stackSize == 0)
                    {
                        this.thisPlayerMP.destroyCurrentEquippedItem();
                    }
                }

                if (lvt_4_2_ && lvt_6_1_)
                {
                    lvt_2_1_.getBlock().harvestBlock(this.theWorld, this.thisPlayerMP, pos, lvt_2_1_, lvt_3_1_);
                }
            }

            return lvt_4_2_;
        }
    }

    /**
     * Attempts to right-click use an item by the given EntityPlayer in the given World
     */
    public boolean tryUseItem(EntityPlayer player, World worldIn, ItemStack stack)
    {
        if (this.gameType == WorldSettings.GameType.SPECTATOR)
        {
            return false;
        }
        else
        {
            int lvt_4_1_ = stack.stackSize;
            int lvt_5_1_ = stack.getMetadata();
            ItemStack lvt_6_1_ = stack.useItemRightClick(worldIn, player);

            if (lvt_6_1_ != stack || lvt_6_1_ != null && (lvt_6_1_.stackSize != lvt_4_1_ || lvt_6_1_.getMaxItemUseDuration() > 0 || lvt_6_1_.getMetadata() != lvt_5_1_))
            {
                player.inventory.mainInventory[player.inventory.currentItem] = lvt_6_1_;

                if (this.isCreative())
                {
                    lvt_6_1_.stackSize = lvt_4_1_;

                    if (lvt_6_1_.isItemStackDamageable())
                    {
                        lvt_6_1_.setItemDamage(lvt_5_1_);
                    }
                }

                if (lvt_6_1_.stackSize == 0)
                {
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                }

                if (!player.isUsingItem())
                {
                    ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Activate the clicked on block, otherwise use the held item.
     */
    public boolean activateBlockOrUseItem(EntityPlayer player, World worldIn, ItemStack stack, BlockPos pos, EnumFacing side, float offsetX, float offsetY, float offsetZ)
    {
        if (this.gameType == WorldSettings.GameType.SPECTATOR)
        {
            TileEntity lvt_9_1_ = worldIn.getTileEntity(pos);

            if (lvt_9_1_ instanceof ILockableContainer)
            {
                Block lvt_10_1_ = worldIn.getBlockState(pos).getBlock();
                ILockableContainer lvt_11_1_ = (ILockableContainer)lvt_9_1_;

                if (lvt_11_1_ instanceof TileEntityChest && lvt_10_1_ instanceof BlockChest)
                {
                    lvt_11_1_ = ((BlockChest)lvt_10_1_).getLockableContainer(worldIn, pos);
                }

                if (lvt_11_1_ != null)
                {
                    player.displayGUIChest(lvt_11_1_);
                    return true;
                }
            }
            else if (lvt_9_1_ instanceof IInventory)
            {
                player.displayGUIChest((IInventory)lvt_9_1_);
                return true;
            }

            return false;
        }
        else
        {
            if (!player.isSneaking() || player.getHeldItem() == null)
            {
                IBlockState lvt_9_2_ = worldIn.getBlockState(pos);

                if (lvt_9_2_.getBlock().onBlockActivated(worldIn, pos, lvt_9_2_, player, side, offsetX, offsetY, offsetZ))
                {
                    return true;
                }
            }

            if (stack == null)
            {
                return false;
            }
            else if (this.isCreative())
            {
                int lvt_9_3_ = stack.getMetadata();
                int lvt_10_2_ = stack.stackSize;
                boolean lvt_11_2_ = stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
                stack.setItemDamage(lvt_9_3_);
                stack.stackSize = lvt_10_2_;
                return lvt_11_2_;
            }
            else
            {
                return stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
            }
        }
    }

    /**
     * Sets the world instance.
     */
    public void setWorld(WorldServer serverWorld)
    {
        this.theWorld = serverWorld;
    }
}
