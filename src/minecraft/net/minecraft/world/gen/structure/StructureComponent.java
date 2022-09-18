package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public abstract class StructureComponent
{
    protected StructureBoundingBox boundingBox;

    /** switches the Coordinate System base off the Bounding Box */
    protected EnumFacing coordBaseMode;

    /** The type ID of this component. */
    protected int componentType;

    public StructureComponent()
    {
    }

    protected StructureComponent(int type)
    {
        this.componentType = type;
    }

    /**
     * Writes structure base data (id, boundingbox, {@link
     * net.minecraft.world.gen.structure.StructureComponent#coordBaseMode coordBase} and {@link
     * net.minecraft.world.gen.structure.StructureComponent#componentType componentType}) to new NBTTagCompound and
     * returns it.
     */
    public NBTTagCompound createStructureBaseNBT()
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();
        lvt_1_1_.setString("id", MapGenStructureIO.getStructureComponentName(this));
        lvt_1_1_.setTag("BB", this.boundingBox.toNBTTagIntArray());
        lvt_1_1_.setInteger("O", this.coordBaseMode == null ? -1 : this.coordBaseMode.getHorizontalIndex());
        lvt_1_1_.setInteger("GD", this.componentType);
        this.writeStructureToNBT(lvt_1_1_);
        return lvt_1_1_;
    }

    /**
     * (abstract) Helper method to write subclass data to NBT
     */
    protected abstract void writeStructureToNBT(NBTTagCompound tagCompound);

    /**
     * Reads and sets structure base data (boundingbox, {@link
     * net.minecraft.world.gen.structure.StructureComponent#coordBaseMode coordBase} and {@link
     * net.minecraft.world.gen.structure.StructureComponent#componentType componentType})
     */
    public void readStructureBaseNBT(World worldIn, NBTTagCompound tagCompound)
    {
        if (tagCompound.hasKey("BB"))
        {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }

        int lvt_3_1_ = tagCompound.getInteger("O");
        this.coordBaseMode = lvt_3_1_ == -1 ? null : EnumFacing.getHorizontal(lvt_3_1_);
        this.componentType = tagCompound.getInteger("GD");
        this.readStructureFromNBT(tagCompound);
    }

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected abstract void readStructureFromNBT(NBTTagCompound tagCompound);

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
    {
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public abstract boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn);

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    /**
     * Returns the component type ID of this component.
     */
    public int getComponentType()
    {
        return this.componentType;
    }

    /**
     * Discover if bounding box can fit within the current bounding box object.
     */
    public static StructureComponent findIntersecting(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn)
    {
        for (StructureComponent lvt_3_1_ : listIn)
        {
            if (lvt_3_1_.getBoundingBox() != null && lvt_3_1_.getBoundingBox().intersectsWith(boundingboxIn))
            {
                return lvt_3_1_;
            }
        }

        return null;
    }

    public BlockPos getBoundingBoxCenter()
    {
        return new BlockPos(this.boundingBox.getCenter());
    }

    /**
     * checks the entire StructureBoundingBox for Liquids
     */
    protected boolean isLiquidInStructureBoundingBox(World worldIn, StructureBoundingBox boundingboxIn)
    {
        int lvt_3_1_ = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
        int lvt_4_1_ = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
        int lvt_5_1_ = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
        int lvt_6_1_ = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
        int lvt_7_1_ = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
        int lvt_8_1_ = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
        BlockPos.MutableBlockPos lvt_9_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_10_1_ = lvt_3_1_; lvt_10_1_ <= lvt_6_1_; ++lvt_10_1_)
        {
            for (int lvt_11_1_ = lvt_5_1_; lvt_11_1_ <= lvt_8_1_; ++lvt_11_1_)
            {
                if (worldIn.getBlockState(lvt_9_1_.set(lvt_10_1_, lvt_4_1_, lvt_11_1_)).getBlock().getMaterial().isLiquid())
                {
                    return true;
                }

                if (worldIn.getBlockState(lvt_9_1_.set(lvt_10_1_, lvt_7_1_, lvt_11_1_)).getBlock().getMaterial().isLiquid())
                {
                    return true;
                }
            }
        }

        for (int lvt_10_2_ = lvt_3_1_; lvt_10_2_ <= lvt_6_1_; ++lvt_10_2_)
        {
            for (int lvt_11_2_ = lvt_4_1_; lvt_11_2_ <= lvt_7_1_; ++lvt_11_2_)
            {
                if (worldIn.getBlockState(lvt_9_1_.set(lvt_10_2_, lvt_11_2_, lvt_5_1_)).getBlock().getMaterial().isLiquid())
                {
                    return true;
                }

                if (worldIn.getBlockState(lvt_9_1_.set(lvt_10_2_, lvt_11_2_, lvt_8_1_)).getBlock().getMaterial().isLiquid())
                {
                    return true;
                }
            }
        }

        for (int lvt_10_3_ = lvt_5_1_; lvt_10_3_ <= lvt_8_1_; ++lvt_10_3_)
        {
            for (int lvt_11_3_ = lvt_4_1_; lvt_11_3_ <= lvt_7_1_; ++lvt_11_3_)
            {
                if (worldIn.getBlockState(lvt_9_1_.set(lvt_3_1_, lvt_11_3_, lvt_10_3_)).getBlock().getMaterial().isLiquid())
                {
                    return true;
                }

                if (worldIn.getBlockState(lvt_9_1_.set(lvt_6_1_, lvt_11_3_, lvt_10_3_)).getBlock().getMaterial().isLiquid())
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected int getXWithOffset(int x, int z)
    {
        if (this.coordBaseMode == null)
        {
            return x;
        }
        else
        {
            switch (this.coordBaseMode)
            {
                case NORTH:
                case SOUTH:
                    return this.boundingBox.minX + x;

                case WEST:
                    return this.boundingBox.maxX - z;

                case EAST:
                    return this.boundingBox.minX + z;

                default:
                    return x;
            }
        }
    }

    protected int getYWithOffset(int y)
    {
        return this.coordBaseMode == null ? y : y + this.boundingBox.minY;
    }

    protected int getZWithOffset(int x, int z)
    {
        if (this.coordBaseMode == null)
        {
            return z;
        }
        else
        {
            switch (this.coordBaseMode)
            {
                case NORTH:
                    return this.boundingBox.maxZ - z;

                case SOUTH:
                    return this.boundingBox.minZ + z;

                case WEST:
                case EAST:
                    return this.boundingBox.minZ + x;

                default:
                    return z;
            }
        }
    }

    /**
     * Returns the direction-shifted metadata for blocks that require orientation, e.g. doors, stairs, ladders.
     */
    protected int getMetadataWithOffset(Block blockIn, int meta)
    {
        if (blockIn == Blocks.rail)
        {
            if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.EAST)
            {
                if (meta == 1)
                {
                    return 0;
                }

                return 1;
            }
        }
        else if (blockIn instanceof BlockDoor)
        {
            if (this.coordBaseMode == EnumFacing.SOUTH)
            {
                if (meta == 0)
                {
                    return 2;
                }

                if (meta == 2)
                {
                    return 0;
                }
            }
            else
            {
                if (this.coordBaseMode == EnumFacing.WEST)
                {
                    return meta + 1 & 3;
                }

                if (this.coordBaseMode == EnumFacing.EAST)
                {
                    return meta + 3 & 3;
                }
            }
        }
        else if (blockIn != Blocks.stone_stairs && blockIn != Blocks.oak_stairs && blockIn != Blocks.nether_brick_stairs && blockIn != Blocks.stone_brick_stairs && blockIn != Blocks.sandstone_stairs)
        {
            if (blockIn == Blocks.ladder)
            {
                if (this.coordBaseMode == EnumFacing.SOUTH)
                {
                    if (meta == EnumFacing.NORTH.getIndex())
                    {
                        return EnumFacing.SOUTH.getIndex();
                    }

                    if (meta == EnumFacing.SOUTH.getIndex())
                    {
                        return EnumFacing.NORTH.getIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.WEST)
                {
                    if (meta == EnumFacing.NORTH.getIndex())
                    {
                        return EnumFacing.WEST.getIndex();
                    }

                    if (meta == EnumFacing.SOUTH.getIndex())
                    {
                        return EnumFacing.EAST.getIndex();
                    }

                    if (meta == EnumFacing.WEST.getIndex())
                    {
                        return EnumFacing.NORTH.getIndex();
                    }

                    if (meta == EnumFacing.EAST.getIndex())
                    {
                        return EnumFacing.SOUTH.getIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.EAST)
                {
                    if (meta == EnumFacing.NORTH.getIndex())
                    {
                        return EnumFacing.EAST.getIndex();
                    }

                    if (meta == EnumFacing.SOUTH.getIndex())
                    {
                        return EnumFacing.WEST.getIndex();
                    }

                    if (meta == EnumFacing.WEST.getIndex())
                    {
                        return EnumFacing.NORTH.getIndex();
                    }

                    if (meta == EnumFacing.EAST.getIndex())
                    {
                        return EnumFacing.SOUTH.getIndex();
                    }
                }
            }
            else if (blockIn == Blocks.stone_button)
            {
                if (this.coordBaseMode == EnumFacing.SOUTH)
                {
                    if (meta == 3)
                    {
                        return 4;
                    }

                    if (meta == 4)
                    {
                        return 3;
                    }
                }
                else if (this.coordBaseMode == EnumFacing.WEST)
                {
                    if (meta == 3)
                    {
                        return 1;
                    }

                    if (meta == 4)
                    {
                        return 2;
                    }

                    if (meta == 2)
                    {
                        return 3;
                    }

                    if (meta == 1)
                    {
                        return 4;
                    }
                }
                else if (this.coordBaseMode == EnumFacing.EAST)
                {
                    if (meta == 3)
                    {
                        return 2;
                    }

                    if (meta == 4)
                    {
                        return 1;
                    }

                    if (meta == 2)
                    {
                        return 3;
                    }

                    if (meta == 1)
                    {
                        return 4;
                    }
                }
            }
            else if (blockIn != Blocks.tripwire_hook && !(blockIn instanceof BlockDirectional))
            {
                if (blockIn == Blocks.piston || blockIn == Blocks.sticky_piston || blockIn == Blocks.lever || blockIn == Blocks.dispenser)
                {
                    if (this.coordBaseMode == EnumFacing.SOUTH)
                    {
                        if (meta == EnumFacing.NORTH.getIndex() || meta == EnumFacing.SOUTH.getIndex())
                        {
                            return EnumFacing.getFront(meta).getOpposite().getIndex();
                        }
                    }
                    else if (this.coordBaseMode == EnumFacing.WEST)
                    {
                        if (meta == EnumFacing.NORTH.getIndex())
                        {
                            return EnumFacing.WEST.getIndex();
                        }

                        if (meta == EnumFacing.SOUTH.getIndex())
                        {
                            return EnumFacing.EAST.getIndex();
                        }

                        if (meta == EnumFacing.WEST.getIndex())
                        {
                            return EnumFacing.NORTH.getIndex();
                        }

                        if (meta == EnumFacing.EAST.getIndex())
                        {
                            return EnumFacing.SOUTH.getIndex();
                        }
                    }
                    else if (this.coordBaseMode == EnumFacing.EAST)
                    {
                        if (meta == EnumFacing.NORTH.getIndex())
                        {
                            return EnumFacing.EAST.getIndex();
                        }

                        if (meta == EnumFacing.SOUTH.getIndex())
                        {
                            return EnumFacing.WEST.getIndex();
                        }

                        if (meta == EnumFacing.WEST.getIndex())
                        {
                            return EnumFacing.NORTH.getIndex();
                        }

                        if (meta == EnumFacing.EAST.getIndex())
                        {
                            return EnumFacing.SOUTH.getIndex();
                        }
                    }
                }
            }
            else
            {
                EnumFacing lvt_3_1_ = EnumFacing.getHorizontal(meta);

                if (this.coordBaseMode == EnumFacing.SOUTH)
                {
                    if (lvt_3_1_ == EnumFacing.SOUTH || lvt_3_1_ == EnumFacing.NORTH)
                    {
                        return lvt_3_1_.getOpposite().getHorizontalIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.WEST)
                {
                    if (lvt_3_1_ == EnumFacing.NORTH)
                    {
                        return EnumFacing.WEST.getHorizontalIndex();
                    }

                    if (lvt_3_1_ == EnumFacing.SOUTH)
                    {
                        return EnumFacing.EAST.getHorizontalIndex();
                    }

                    if (lvt_3_1_ == EnumFacing.WEST)
                    {
                        return EnumFacing.NORTH.getHorizontalIndex();
                    }

                    if (lvt_3_1_ == EnumFacing.EAST)
                    {
                        return EnumFacing.SOUTH.getHorizontalIndex();
                    }
                }
                else if (this.coordBaseMode == EnumFacing.EAST)
                {
                    if (lvt_3_1_ == EnumFacing.NORTH)
                    {
                        return EnumFacing.EAST.getHorizontalIndex();
                    }

                    if (lvt_3_1_ == EnumFacing.SOUTH)
                    {
                        return EnumFacing.WEST.getHorizontalIndex();
                    }

                    if (lvt_3_1_ == EnumFacing.WEST)
                    {
                        return EnumFacing.NORTH.getHorizontalIndex();
                    }

                    if (lvt_3_1_ == EnumFacing.EAST)
                    {
                        return EnumFacing.SOUTH.getHorizontalIndex();
                    }
                }
            }
        }
        else if (this.coordBaseMode == EnumFacing.SOUTH)
        {
            if (meta == 2)
            {
                return 3;
            }

            if (meta == 3)
            {
                return 2;
            }
        }
        else if (this.coordBaseMode == EnumFacing.WEST)
        {
            if (meta == 0)
            {
                return 2;
            }

            if (meta == 1)
            {
                return 3;
            }

            if (meta == 2)
            {
                return 0;
            }

            if (meta == 3)
            {
                return 1;
            }
        }
        else if (this.coordBaseMode == EnumFacing.EAST)
        {
            if (meta == 0)
            {
                return 2;
            }

            if (meta == 1)
            {
                return 3;
            }

            if (meta == 2)
            {
                return 1;
            }

            if (meta == 3)
            {
                return 0;
            }
        }

        return meta;
    }

    protected void setBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
    {
        BlockPos lvt_7_1_ = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingboxIn.isVecInside(lvt_7_1_))
        {
            worldIn.setBlockState(lvt_7_1_, blockstateIn, 2);
        }
    }

    protected IBlockState getBlockStateFromPos(World worldIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
    {
        int lvt_6_1_ = this.getXWithOffset(x, z);
        int lvt_7_1_ = this.getYWithOffset(y);
        int lvt_8_1_ = this.getZWithOffset(x, z);
        BlockPos lvt_9_1_ = new BlockPos(lvt_6_1_, lvt_7_1_, lvt_8_1_);
        return !boundingboxIn.isVecInside(lvt_9_1_) ? Blocks.air.getDefaultState() : worldIn.getBlockState(lvt_9_1_);
    }

    /**
     * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
     * maxZ)
     */
    protected void fillWithAir(World worldIn, StructureBoundingBox structurebb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        for (int lvt_9_1_ = minY; lvt_9_1_ <= maxY; ++lvt_9_1_)
        {
            for (int lvt_10_1_ = minX; lvt_10_1_ <= maxX; ++lvt_10_1_)
            {
                for (int lvt_11_1_ = minZ; lvt_11_1_ <= maxZ; ++lvt_11_1_)
                {
                    this.setBlockState(worldIn, Blocks.air.getDefaultState(), lvt_10_1_, lvt_9_1_, lvt_11_1_, structurebb);
                }
            }
        }
    }

    /**
     * Fill the given area with the selected blocks
     */
    protected void fillWithBlocks(World worldIn, StructureBoundingBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, IBlockState boundaryBlockState, IBlockState insideBlockState, boolean existingOnly)
    {
        for (int lvt_12_1_ = yMin; lvt_12_1_ <= yMax; ++lvt_12_1_)
        {
            for (int lvt_13_1_ = xMin; lvt_13_1_ <= xMax; ++lvt_13_1_)
            {
                for (int lvt_14_1_ = zMin; lvt_14_1_ <= zMax; ++lvt_14_1_)
                {
                    if (!existingOnly || this.getBlockStateFromPos(worldIn, lvt_13_1_, lvt_12_1_, lvt_14_1_, boundingboxIn).getBlock().getMaterial() != Material.air)
                    {
                        if (lvt_12_1_ != yMin && lvt_12_1_ != yMax && lvt_13_1_ != xMin && lvt_13_1_ != xMax && lvt_14_1_ != zMin && lvt_14_1_ != zMax)
                        {
                            this.setBlockState(worldIn, insideBlockState, lvt_13_1_, lvt_12_1_, lvt_14_1_, boundingboxIn);
                        }
                        else
                        {
                            this.setBlockState(worldIn, boundaryBlockState, lvt_13_1_, lvt_12_1_, lvt_14_1_, boundingboxIn);
                        }
                    }
                }
            }
        }
    }

    /**
     * arguments: World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
     * maxZ, boolean alwaysreplace, Random rand, StructurePieceBlockSelector blockselector
     */
    protected void fillWithRandomizedBlocks(World worldIn, StructureBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean alwaysReplace, Random rand, StructureComponent.BlockSelector blockselector)
    {
        for (int lvt_12_1_ = minY; lvt_12_1_ <= maxY; ++lvt_12_1_)
        {
            for (int lvt_13_1_ = minX; lvt_13_1_ <= maxX; ++lvt_13_1_)
            {
                for (int lvt_14_1_ = minZ; lvt_14_1_ <= maxZ; ++lvt_14_1_)
                {
                    if (!alwaysReplace || this.getBlockStateFromPos(worldIn, lvt_13_1_, lvt_12_1_, lvt_14_1_, boundingboxIn).getBlock().getMaterial() != Material.air)
                    {
                        blockselector.selectBlocks(rand, lvt_13_1_, lvt_12_1_, lvt_14_1_, lvt_12_1_ == minY || lvt_12_1_ == maxY || lvt_13_1_ == minX || lvt_13_1_ == maxX || lvt_14_1_ == minZ || lvt_14_1_ == maxZ);
                        this.setBlockState(worldIn, blockselector.getBlockState(), lvt_13_1_, lvt_12_1_, lvt_14_1_, boundingboxIn);
                    }
                }
            }
        }
    }

    protected void func_175805_a(World worldIn, StructureBoundingBox boundingboxIn, Random rand, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockstate1, IBlockState blockstate2, boolean p_175805_13_)
    {
        for (int lvt_14_1_ = minY; lvt_14_1_ <= maxY; ++lvt_14_1_)
        {
            for (int lvt_15_1_ = minX; lvt_15_1_ <= maxX; ++lvt_15_1_)
            {
                for (int lvt_16_1_ = minZ; lvt_16_1_ <= maxZ; ++lvt_16_1_)
                {
                    if (rand.nextFloat() <= chance && (!p_175805_13_ || this.getBlockStateFromPos(worldIn, lvt_15_1_, lvt_14_1_, lvt_16_1_, boundingboxIn).getBlock().getMaterial() != Material.air))
                    {
                        if (lvt_14_1_ != minY && lvt_14_1_ != maxY && lvt_15_1_ != minX && lvt_15_1_ != maxX && lvt_16_1_ != minZ && lvt_16_1_ != maxZ)
                        {
                            this.setBlockState(worldIn, blockstate2, lvt_15_1_, lvt_14_1_, lvt_16_1_, boundingboxIn);
                        }
                        else
                        {
                            this.setBlockState(worldIn, blockstate1, lvt_15_1_, lvt_14_1_, lvt_16_1_, boundingboxIn);
                        }
                    }
                }
            }
        }
    }

    protected void randomlyPlaceBlock(World worldIn, StructureBoundingBox boundingboxIn, Random rand, float chance, int x, int y, int z, IBlockState blockstateIn)
    {
        if (rand.nextFloat() < chance)
        {
            this.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
        }
    }

    protected void randomlyRareFillWithBlocks(World worldIn, StructureBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockstateIn, boolean p_180777_10_)
    {
        float lvt_11_1_ = (float)(maxX - minX + 1);
        float lvt_12_1_ = (float)(maxY - minY + 1);
        float lvt_13_1_ = (float)(maxZ - minZ + 1);
        float lvt_14_1_ = (float)minX + lvt_11_1_ / 2.0F;
        float lvt_15_1_ = (float)minZ + lvt_13_1_ / 2.0F;

        for (int lvt_16_1_ = minY; lvt_16_1_ <= maxY; ++lvt_16_1_)
        {
            float lvt_17_1_ = (float)(lvt_16_1_ - minY) / lvt_12_1_;

            for (int lvt_18_1_ = minX; lvt_18_1_ <= maxX; ++lvt_18_1_)
            {
                float lvt_19_1_ = ((float)lvt_18_1_ - lvt_14_1_) / (lvt_11_1_ * 0.5F);

                for (int lvt_20_1_ = minZ; lvt_20_1_ <= maxZ; ++lvt_20_1_)
                {
                    float lvt_21_1_ = ((float)lvt_20_1_ - lvt_15_1_) / (lvt_13_1_ * 0.5F);

                    if (!p_180777_10_ || this.getBlockStateFromPos(worldIn, lvt_18_1_, lvt_16_1_, lvt_20_1_, boundingboxIn).getBlock().getMaterial() != Material.air)
                    {
                        float lvt_22_1_ = lvt_19_1_ * lvt_19_1_ + lvt_17_1_ * lvt_17_1_ + lvt_21_1_ * lvt_21_1_;

                        if (lvt_22_1_ <= 1.05F)
                        {
                            this.setBlockState(worldIn, blockstateIn, lvt_18_1_, lvt_16_1_, lvt_20_1_, boundingboxIn);
                        }
                    }
                }
            }
        }
    }

    /**
     * Deletes all continuous blocks from selected position upwards. Stops at hitting air.
     */
    protected void clearCurrentPositionBlocksUpwards(World worldIn, int x, int y, int z, StructureBoundingBox structurebb)
    {
        BlockPos lvt_6_1_ = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (structurebb.isVecInside(lvt_6_1_))
        {
            while (!worldIn.isAirBlock(lvt_6_1_) && lvt_6_1_.getY() < 255)
            {
                worldIn.setBlockState(lvt_6_1_, Blocks.air.getDefaultState(), 2);
                lvt_6_1_ = lvt_6_1_.up();
            }
        }
    }

    /**
     * Replaces air and liquid from given position downwards. Stops when hitting anything else than air or liquid
     */
    protected void replaceAirAndLiquidDownwards(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn)
    {
        int lvt_7_1_ = this.getXWithOffset(x, z);
        int lvt_8_1_ = this.getYWithOffset(y);
        int lvt_9_1_ = this.getZWithOffset(x, z);

        if (boundingboxIn.isVecInside(new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_)))
        {
            while ((worldIn.isAirBlock(new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_)) || worldIn.getBlockState(new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_)).getBlock().getMaterial().isLiquid()) && lvt_8_1_ > 1)
            {
                worldIn.setBlockState(new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_), blockstateIn, 2);
                --lvt_8_1_;
            }
        }
    }

    protected boolean generateChestContents(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int max)
    {
        BlockPos lvt_9_1_ = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingBoxIn.isVecInside(lvt_9_1_) && worldIn.getBlockState(lvt_9_1_).getBlock() != Blocks.chest)
        {
            IBlockState lvt_10_1_ = Blocks.chest.getDefaultState();
            worldIn.setBlockState(lvt_9_1_, Blocks.chest.correctFacing(worldIn, lvt_9_1_, lvt_10_1_), 2);
            TileEntity lvt_11_1_ = worldIn.getTileEntity(lvt_9_1_);

            if (lvt_11_1_ instanceof TileEntityChest)
            {
                WeightedRandomChestContent.generateChestContents(rand, listIn, (TileEntityChest)lvt_11_1_, max);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean generateDispenserContents(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, int meta, List<WeightedRandomChestContent> listIn, int max)
    {
        BlockPos lvt_10_1_ = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingBoxIn.isVecInside(lvt_10_1_) && worldIn.getBlockState(lvt_10_1_).getBlock() != Blocks.dispenser)
        {
            worldIn.setBlockState(lvt_10_1_, Blocks.dispenser.getStateFromMeta(this.getMetadataWithOffset(Blocks.dispenser, meta)), 2);
            TileEntity lvt_11_1_ = worldIn.getTileEntity(lvt_10_1_);

            if (lvt_11_1_ instanceof TileEntityDispenser)
            {
                WeightedRandomChestContent.generateDispenserContents(rand, listIn, (TileEntityDispenser)lvt_11_1_, max);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Places door on given position
     */
    protected void placeDoorCurrentPosition(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, EnumFacing facing)
    {
        BlockPos lvt_8_1_ = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

        if (boundingBoxIn.isVecInside(lvt_8_1_))
        {
            ItemDoor.placeDoor(worldIn, lvt_8_1_, facing.rotateYCCW(), Blocks.oak_door);
        }
    }

    public void func_181138_a(int p_181138_1_, int p_181138_2_, int p_181138_3_)
    {
        this.boundingBox.offset(p_181138_1_, p_181138_2_, p_181138_3_);
    }

    public abstract static class BlockSelector
    {
        protected IBlockState blockstate = Blocks.air.getDefaultState();

        public abstract void selectBlocks(Random rand, int x, int y, int z, boolean p_75062_5_);

        public IBlockState getBlockState()
        {
            return this.blockstate;
        }
    }
}
