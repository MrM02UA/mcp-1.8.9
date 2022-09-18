package net.minecraft.init;

import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap
{
    private static final PrintStream SYSOUT = System.out;

    /** Whether the blocks, items, etc have already been registered */
    private static boolean alreadyRegistered = false;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Is Bootstrap registration already done?
     */
    public static boolean isRegistered()
    {
        return alreadyRegistered;
    }

    static void registerDispenserBehaviors()
    {
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense()
        {
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                EntityArrow lvt_3_1_ = new EntityArrow(worldIn, position.getX(), position.getY(), position.getZ());
                lvt_3_1_.canBePickedUp = 1;
                return lvt_3_1_;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense()
        {
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense()
        {
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense()
        {
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
            }
            protected float func_82498_a()
            {
                return super.func_82498_a() * 0.5F;
            }
            protected float func_82500_b()
            {
                return super.func_82500_b() * 1.25F;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potionitem, new IBehaviorDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();
            public ItemStack dispense(IBlockSource source, final ItemStack stack)
            {
                return ItemPotion.isSplash(stack.getMetadata()) ? (new BehaviorProjectileDispense()
                {
                    protected IProjectile getProjectileEntity(World worldIn, IPosition position)
                    {
                        return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack.copy());
                    }
                    protected float func_82498_a()
                    {
                        return super.func_82498_a() * 0.5F;
                    }
                    protected float func_82500_b()
                    {
                        return super.func_82500_b() * 1.25F;
                    }
                }).dispense(source, stack): this.field_150843_b.dispense(source, stack);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem()
        {
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing lvt_3_1_ = BlockDispenser.getFacing(source.getBlockMetadata());
                double lvt_4_1_ = source.getX() + (double)lvt_3_1_.getFrontOffsetX();
                double lvt_6_1_ = (double)((float)source.getBlockPos().getY() + 0.2F);
                double lvt_8_1_ = source.getZ() + (double)lvt_3_1_.getFrontOffsetZ();
                Entity lvt_10_1_ = ItemMonsterPlacer.spawnCreature(source.getWorld(), stack.getMetadata(), lvt_4_1_, lvt_6_1_, lvt_8_1_);

                if (lvt_10_1_ instanceof EntityLivingBase && stack.hasDisplayName())
                {
                    ((EntityLiving)lvt_10_1_).setCustomNameTag(stack.getDisplayName());
                }

                stack.splitStack(1);
                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem()
        {
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing lvt_3_1_ = BlockDispenser.getFacing(source.getBlockMetadata());
                double lvt_4_1_ = source.getX() + (double)lvt_3_1_.getFrontOffsetX();
                double lvt_6_1_ = (double)((float)source.getBlockPos().getY() + 0.2F);
                double lvt_8_1_ = source.getZ() + (double)lvt_3_1_.getFrontOffsetZ();
                EntityFireworkRocket lvt_10_1_ = new EntityFireworkRocket(source.getWorld(), lvt_4_1_, lvt_6_1_, lvt_8_1_, stack);
                source.getWorld().spawnEntityInWorld(lvt_10_1_);
                stack.splitStack(1);
                return stack;
            }
            protected void playDispenseSound(IBlockSource source)
            {
                source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem()
        {
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing lvt_3_1_ = BlockDispenser.getFacing(source.getBlockMetadata());
                IPosition lvt_4_1_ = BlockDispenser.getDispensePosition(source);
                double lvt_5_1_ = lvt_4_1_.getX() + (double)((float)lvt_3_1_.getFrontOffsetX() * 0.3F);
                double lvt_7_1_ = lvt_4_1_.getY() + (double)((float)lvt_3_1_.getFrontOffsetY() * 0.3F);
                double lvt_9_1_ = lvt_4_1_.getZ() + (double)((float)lvt_3_1_.getFrontOffsetZ() * 0.3F);
                World lvt_11_1_ = source.getWorld();
                Random lvt_12_1_ = lvt_11_1_.rand;
                double lvt_13_1_ = lvt_12_1_.nextGaussian() * 0.05D + (double)lvt_3_1_.getFrontOffsetX();
                double lvt_15_1_ = lvt_12_1_.nextGaussian() * 0.05D + (double)lvt_3_1_.getFrontOffsetY();
                double lvt_17_1_ = lvt_12_1_.nextGaussian() * 0.05D + (double)lvt_3_1_.getFrontOffsetZ();
                lvt_11_1_.spawnEntityInWorld(new EntitySmallFireball(lvt_11_1_, lvt_5_1_, lvt_7_1_, lvt_9_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_));
                stack.splitStack(1);
                return stack;
            }
            protected void playDispenseSound(IBlockSource source)
            {
                source.getWorld().playAuxSFX(1009, source.getBlockPos(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing lvt_3_1_ = BlockDispenser.getFacing(source.getBlockMetadata());
                World lvt_4_1_ = source.getWorld();
                double lvt_5_1_ = source.getX() + (double)((float)lvt_3_1_.getFrontOffsetX() * 1.125F);
                double lvt_7_1_ = source.getY() + (double)((float)lvt_3_1_.getFrontOffsetY() * 1.125F);
                double lvt_9_1_ = source.getZ() + (double)((float)lvt_3_1_.getFrontOffsetZ() * 1.125F);
                BlockPos lvt_11_1_ = source.getBlockPos().offset(lvt_3_1_);
                Material lvt_12_1_ = lvt_4_1_.getBlockState(lvt_11_1_).getBlock().getMaterial();
                double lvt_13_1_;

                if (Material.water.equals(lvt_12_1_))
                {
                    lvt_13_1_ = 1.0D;
                }
                else
                {
                    if (!Material.air.equals(lvt_12_1_) || !Material.water.equals(lvt_4_1_.getBlockState(lvt_11_1_.down()).getBlock().getMaterial()))
                    {
                        return this.field_150842_b.dispense(source, stack);
                    }

                    lvt_13_1_ = 0.0D;
                }

                EntityBoat lvt_15_1_ = new EntityBoat(lvt_4_1_, lvt_5_1_, lvt_7_1_ + lvt_13_1_, lvt_9_1_);
                lvt_4_1_.spawnEntityInWorld(lvt_15_1_);
                stack.splitStack(1);
                return stack;
            }
            protected void playDispenseSound(IBlockSource source)
            {
                source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            }
        });
        IBehaviorDispenseItem lvt_0_1_ = new BehaviorDefaultDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                ItemBucket lvt_3_1_ = (ItemBucket)stack.getItem();
                BlockPos lvt_4_1_ = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));

                if (lvt_3_1_.tryPlaceContainedLiquid(source.getWorld(), lvt_4_1_))
                {
                    stack.setItem(Items.bucket);
                    stack.stackSize = 1;
                    return stack;
                }
                else
                {
                    return this.field_150841_b.dispense(source, stack);
                }
            }
        };
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, lvt_0_1_);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, lvt_0_1_);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                World lvt_3_1_ = source.getWorld();
                BlockPos lvt_4_1_ = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
                IBlockState lvt_5_1_ = lvt_3_1_.getBlockState(lvt_4_1_);
                Block lvt_6_1_ = lvt_5_1_.getBlock();
                Material lvt_7_1_ = lvt_6_1_.getMaterial();
                Item lvt_8_1_;

                if (Material.water.equals(lvt_7_1_) && lvt_6_1_ instanceof BlockLiquid && ((Integer)lvt_5_1_.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                {
                    lvt_8_1_ = Items.water_bucket;
                }
                else
                {
                    if (!Material.lava.equals(lvt_7_1_) || !(lvt_6_1_ instanceof BlockLiquid) || ((Integer)lvt_5_1_.getValue(BlockLiquid.LEVEL)).intValue() != 0)
                    {
                        return super.dispenseStack(source, stack);
                    }

                    lvt_8_1_ = Items.lava_bucket;
                }

                lvt_3_1_.setBlockToAir(lvt_4_1_);

                if (--stack.stackSize == 0)
                {
                    stack.setItem(lvt_8_1_);
                    stack.stackSize = 1;
                }
                else if (((TileEntityDispenser)source.getBlockTileEntity()).addItemStack(new ItemStack(lvt_8_1_)) < 0)
                {
                    this.field_150840_b.dispense(source, new ItemStack(lvt_8_1_));
                }

                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem()
        {
            private boolean field_150839_b = true;
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                World lvt_3_1_ = source.getWorld();
                BlockPos lvt_4_1_ = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));

                if (lvt_3_1_.isAirBlock(lvt_4_1_))
                {
                    lvt_3_1_.setBlockState(lvt_4_1_, Blocks.fire.getDefaultState());

                    if (stack.attemptDamageItem(1, lvt_3_1_.rand))
                    {
                        stack.stackSize = 0;
                    }
                }
                else if (lvt_3_1_.getBlockState(lvt_4_1_).getBlock() == Blocks.tnt)
                {
                    Blocks.tnt.onBlockDestroyedByPlayer(lvt_3_1_, lvt_4_1_, Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
                    lvt_3_1_.setBlockToAir(lvt_4_1_);
                }
                else
                {
                    this.field_150839_b = false;
                }

                return stack;
            }
            protected void playDispenseSound(IBlockSource source)
            {
                if (this.field_150839_b)
                {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                }
                else
                {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem()
        {
            private boolean field_150838_b = true;
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata()))
                {
                    World lvt_3_1_ = source.getWorld();
                    BlockPos lvt_4_1_ = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));

                    if (ItemDye.applyBonemeal(stack, lvt_3_1_, lvt_4_1_))
                    {
                        if (!lvt_3_1_.isRemote)
                        {
                            lvt_3_1_.playAuxSFX(2005, lvt_4_1_, 0);
                        }
                    }
                    else
                    {
                        this.field_150838_b = false;
                    }

                    return stack;
                }
                else
                {
                    return super.dispenseStack(source, stack);
                }
            }
            protected void playDispenseSound(IBlockSource source)
            {
                if (this.field_150838_b)
                {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                }
                else
                {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem()
        {
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                World lvt_3_1_ = source.getWorld();
                BlockPos lvt_4_1_ = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
                EntityTNTPrimed lvt_5_1_ = new EntityTNTPrimed(lvt_3_1_, (double)lvt_4_1_.getX() + 0.5D, (double)lvt_4_1_.getY(), (double)lvt_4_1_.getZ() + 0.5D, (EntityLivingBase)null);
                lvt_3_1_.spawnEntityInWorld(lvt_5_1_);
                lvt_3_1_.playSoundAtEntity(lvt_5_1_, "game.tnt.primed", 1.0F, 1.0F);
                --stack.stackSize;
                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.skull, new BehaviorDefaultDispenseItem()
        {
            private boolean field_179240_b = true;
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                World lvt_3_1_ = source.getWorld();
                EnumFacing lvt_4_1_ = BlockDispenser.getFacing(source.getBlockMetadata());
                BlockPos lvt_5_1_ = source.getBlockPos().offset(lvt_4_1_);
                BlockSkull lvt_6_1_ = Blocks.skull;

                if (lvt_3_1_.isAirBlock(lvt_5_1_) && lvt_6_1_.canDispenserPlace(lvt_3_1_, lvt_5_1_, stack))
                {
                    if (!lvt_3_1_.isRemote)
                    {
                        lvt_3_1_.setBlockState(lvt_5_1_, lvt_6_1_.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP), 3);
                        TileEntity lvt_7_1_ = lvt_3_1_.getTileEntity(lvt_5_1_);

                        if (lvt_7_1_ instanceof TileEntitySkull)
                        {
                            if (stack.getMetadata() == 3)
                            {
                                GameProfile lvt_8_1_ = null;

                                if (stack.hasTagCompound())
                                {
                                    NBTTagCompound lvt_9_1_ = stack.getTagCompound();

                                    if (lvt_9_1_.hasKey("SkullOwner", 10))
                                    {
                                        lvt_8_1_ = NBTUtil.readGameProfileFromNBT(lvt_9_1_.getCompoundTag("SkullOwner"));
                                    }
                                    else if (lvt_9_1_.hasKey("SkullOwner", 8))
                                    {
                                        String lvt_10_1_ = lvt_9_1_.getString("SkullOwner");

                                        if (!StringUtils.isNullOrEmpty(lvt_10_1_))
                                        {
                                            lvt_8_1_ = new GameProfile((UUID)null, lvt_10_1_);
                                        }
                                    }
                                }

                                ((TileEntitySkull)lvt_7_1_).setPlayerProfile(lvt_8_1_);
                            }
                            else
                            {
                                ((TileEntitySkull)lvt_7_1_).setType(stack.getMetadata());
                            }

                            ((TileEntitySkull)lvt_7_1_).setSkullRotation(lvt_4_1_.getOpposite().getHorizontalIndex() * 4);
                            Blocks.skull.checkWitherSpawn(lvt_3_1_, lvt_5_1_, (TileEntitySkull)lvt_7_1_);
                        }

                        --stack.stackSize;
                    }
                }
                else
                {
                    this.field_179240_b = false;
                }

                return stack;
            }
            protected void playDispenseSound(IBlockSource source)
            {
                if (this.field_179240_b)
                {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                }
                else
                {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorDefaultDispenseItem()
        {
            private boolean field_179241_b = true;
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                World lvt_3_1_ = source.getWorld();
                BlockPos lvt_4_1_ = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
                BlockPumpkin lvt_5_1_ = (BlockPumpkin)Blocks.pumpkin;

                if (lvt_3_1_.isAirBlock(lvt_4_1_) && lvt_5_1_.canDispenserPlace(lvt_3_1_, lvt_4_1_))
                {
                    if (!lvt_3_1_.isRemote)
                    {
                        lvt_3_1_.setBlockState(lvt_4_1_, lvt_5_1_.getDefaultState(), 3);
                    }

                    --stack.stackSize;
                }
                else
                {
                    this.field_179241_b = false;
                }

                return stack;
            }
            protected void playDispenseSound(IBlockSource source)
            {
                if (this.field_179241_b)
                {
                    source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
                }
                else
                {
                    source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
                }
            }
        });
    }

    /**
     * Registers blocks, items, stats, etc.
     */
    public static void register()
    {
        if (!alreadyRegistered)
        {
            alreadyRegistered = true;

            if (LOGGER.isDebugEnabled())
            {
                redirectOutputToLog();
            }

            Block.registerBlocks();
            BlockFire.init();
            Item.registerItems();
            StatList.init();
            registerDispenserBehaviors();
        }
    }

    /**
     * redirect standard streams to logger
     */
    private static void redirectOutputToLog()
    {
        System.setErr(new LoggingPrintStream("STDERR", System.err));
        System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
    }

    public static void printToSYSOUT(String p_179870_0_)
    {
        SYSOUT.println(p_179870_0_);
    }
}
