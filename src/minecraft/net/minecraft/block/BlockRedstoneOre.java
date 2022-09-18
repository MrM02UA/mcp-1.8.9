package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block
{
    private final boolean isOn;

    public BlockRedstoneOre(boolean isOn)
    {
        super(Material.rock);

        if (isOn)
        {
            this.setTickRandomly(true);
        }

        this.isOn = isOn;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 30;
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        this.activate(worldIn, pos);
        super.onBlockClicked(worldIn, pos, playerIn);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block)
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn)
    {
        this.activate(worldIn, pos);
        super.onEntityCollidedWithBlock(worldIn, pos, entityIn);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        this.activate(worldIn, pos);
        return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
    }

    private void activate(World worldIn, BlockPos pos)
    {
        this.spawnParticles(worldIn, pos);

        if (this == Blocks.redstone_ore)
        {
            worldIn.setBlockState(pos, Blocks.lit_redstone_ore.getDefaultState());
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this == Blocks.lit_redstone_ore)
        {
            worldIn.setBlockState(pos, Blocks.redstone_ore.getDefaultState());
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.redstone;
    }

    /**
     * Get the quantity dropped based on the given fortune level
     */
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return this.quantityDropped(random) + random.nextInt(fortune + 1);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 4 + random.nextInt(2);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);

        if (this.getItemDropped(state, worldIn.rand, fortune) != Item.getItemFromBlock(this))
        {
            int lvt_6_1_ = 1 + worldIn.rand.nextInt(5);
            this.dropXpOnBlockBreak(worldIn, pos, lvt_6_1_);
        }
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isOn)
        {
            this.spawnParticles(worldIn, pos);
        }
    }

    private void spawnParticles(World worldIn, BlockPos pos)
    {
        Random lvt_3_1_ = worldIn.rand;
        double lvt_4_1_ = 0.0625D;

        for (int lvt_6_1_ = 0; lvt_6_1_ < 6; ++lvt_6_1_)
        {
            double lvt_7_1_ = (double)((float)pos.getX() + lvt_3_1_.nextFloat());
            double lvt_9_1_ = (double)((float)pos.getY() + lvt_3_1_.nextFloat());
            double lvt_11_1_ = (double)((float)pos.getZ() + lvt_3_1_.nextFloat());

            if (lvt_6_1_ == 0 && !worldIn.getBlockState(pos.up()).getBlock().isOpaqueCube())
            {
                lvt_9_1_ = (double)pos.getY() + lvt_4_1_ + 1.0D;
            }

            if (lvt_6_1_ == 1 && !worldIn.getBlockState(pos.down()).getBlock().isOpaqueCube())
            {
                lvt_9_1_ = (double)pos.getY() - lvt_4_1_;
            }

            if (lvt_6_1_ == 2 && !worldIn.getBlockState(pos.south()).getBlock().isOpaqueCube())
            {
                lvt_11_1_ = (double)pos.getZ() + lvt_4_1_ + 1.0D;
            }

            if (lvt_6_1_ == 3 && !worldIn.getBlockState(pos.north()).getBlock().isOpaqueCube())
            {
                lvt_11_1_ = (double)pos.getZ() - lvt_4_1_;
            }

            if (lvt_6_1_ == 4 && !worldIn.getBlockState(pos.east()).getBlock().isOpaqueCube())
            {
                lvt_7_1_ = (double)pos.getX() + lvt_4_1_ + 1.0D;
            }

            if (lvt_6_1_ == 5 && !worldIn.getBlockState(pos.west()).getBlock().isOpaqueCube())
            {
                lvt_7_1_ = (double)pos.getX() - lvt_4_1_;
            }

            if (lvt_7_1_ < (double)pos.getX() || lvt_7_1_ > (double)(pos.getX() + 1) || lvt_9_1_ < 0.0D || lvt_9_1_ > (double)(pos.getY() + 1) || lvt_11_1_ < (double)pos.getZ() || lvt_11_1_ > (double)(pos.getZ() + 1))
            {
                worldIn.spawnParticle(EnumParticleTypes.REDSTONE, lvt_7_1_, lvt_9_1_, lvt_11_1_, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }

    protected ItemStack createStackedBlock(IBlockState state)
    {
        return new ItemStack(Blocks.redstone_ore);
    }
}
