package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem
{
    /**
     * Dispenses the specified ItemStack from a dispenser.
     */
    public final ItemStack dispense(IBlockSource source, ItemStack stack)
    {
        ItemStack lvt_3_1_ = this.dispenseStack(source, stack);
        this.playDispenseSound(source);
        this.spawnDispenseParticles(source, BlockDispenser.getFacing(source.getBlockMetadata()));
        return lvt_3_1_;
    }

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        EnumFacing lvt_3_1_ = BlockDispenser.getFacing(source.getBlockMetadata());
        IPosition lvt_4_1_ = BlockDispenser.getDispensePosition(source);
        ItemStack lvt_5_1_ = stack.splitStack(1);
        doDispense(source.getWorld(), lvt_5_1_, 6, lvt_3_1_, lvt_4_1_);
        return stack;
    }

    public static void doDispense(World worldIn, ItemStack stack, int speed, EnumFacing facing, IPosition position)
    {
        double lvt_5_1_ = position.getX();
        double lvt_7_1_ = position.getY();
        double lvt_9_1_ = position.getZ();

        if (facing.getAxis() == EnumFacing.Axis.Y)
        {
            lvt_7_1_ = lvt_7_1_ - 0.125D;
        }
        else
        {
            lvt_7_1_ = lvt_7_1_ - 0.15625D;
        }

        EntityItem lvt_11_1_ = new EntityItem(worldIn, lvt_5_1_, lvt_7_1_, lvt_9_1_, stack);
        double lvt_12_1_ = worldIn.rand.nextDouble() * 0.1D + 0.2D;
        lvt_11_1_.motionX = (double)facing.getFrontOffsetX() * lvt_12_1_;
        lvt_11_1_.motionY = 0.20000000298023224D;
        lvt_11_1_.motionZ = (double)facing.getFrontOffsetZ() * lvt_12_1_;
        lvt_11_1_.motionX += worldIn.rand.nextGaussian() * 0.007499999832361937D * (double)speed;
        lvt_11_1_.motionY += worldIn.rand.nextGaussian() * 0.007499999832361937D * (double)speed;
        lvt_11_1_.motionZ += worldIn.rand.nextGaussian() * 0.007499999832361937D * (double)speed;
        worldIn.spawnEntityInWorld(lvt_11_1_);
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(IBlockSource source)
    {
        source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
    }

    /**
     * Order clients to display dispense particles from the specified block and facing.
     */
    protected void spawnDispenseParticles(IBlockSource source, EnumFacing facingIn)
    {
        source.getWorld().playAuxSFX(2000, source.getBlockPos(), this.func_82488_a(facingIn));
    }

    private int func_82488_a(EnumFacing facingIn)
    {
        return facingIn.getFrontOffsetX() + 1 + (facingIn.getFrontOffsetZ() + 1) * 3;
    }
}
