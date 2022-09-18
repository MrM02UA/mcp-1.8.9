package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

@SuppressWarnings("incomplete-switch")
public class StructureMineshaftPieces
{
    private static final List<WeightedRandomChestContent> CHEST_CONTENT_WEIGHT_LIST = Lists.newArrayList(new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5), new WeightedRandomChestContent(Items.dye, EnumDyeColor.BLUE.getDyeDamage(), 4, 9, 5), new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3), new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1), new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1)});

    public static void registerStructurePieces()
    {
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
    }

    private static StructureComponent func_175892_a(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int type)
    {
        int lvt_7_1_ = rand.nextInt(100);

        if (lvt_7_1_ >= 80)
        {
            StructureBoundingBox lvt_8_1_ = StructureMineshaftPieces.Cross.func_175813_a(listIn, rand, x, y, z, facing);

            if (lvt_8_1_ != null)
            {
                return new StructureMineshaftPieces.Cross(type, rand, lvt_8_1_, facing);
            }
        }
        else if (lvt_7_1_ >= 70)
        {
            StructureBoundingBox lvt_8_2_ = StructureMineshaftPieces.Stairs.func_175812_a(listIn, rand, x, y, z, facing);

            if (lvt_8_2_ != null)
            {
                return new StructureMineshaftPieces.Stairs(type, rand, lvt_8_2_, facing);
            }
        }
        else
        {
            StructureBoundingBox lvt_8_3_ = StructureMineshaftPieces.Corridor.func_175814_a(listIn, rand, x, y, z, facing);

            if (lvt_8_3_ != null)
            {
                return new StructureMineshaftPieces.Corridor(type, rand, lvt_8_3_, facing);
            }
        }

        return null;
    }

    private static StructureComponent func_175890_b(StructureComponent componentIn, List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int type)
    {
        if (type > 8)
        {
            return null;
        }
        else if (Math.abs(x - componentIn.getBoundingBox().minX) <= 80 && Math.abs(z - componentIn.getBoundingBox().minZ) <= 80)
        {
            StructureComponent lvt_8_1_ = func_175892_a(listIn, rand, x, y, z, facing, type + 1);

            if (lvt_8_1_ != null)
            {
                listIn.add(lvt_8_1_);
                lvt_8_1_.buildComponent(componentIn, listIn, rand);
            }

            return lvt_8_1_;
        }
        else
        {
            return null;
        }
    }

    public static class Corridor extends StructureComponent
    {
        private boolean hasRails;
        private boolean hasSpiders;
        private boolean spawnerPlaced;
        private int sectionCount;

        public Corridor()
        {
        }

        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            tagCompound.setBoolean("hr", this.hasRails);
            tagCompound.setBoolean("sc", this.hasSpiders);
            tagCompound.setBoolean("hps", this.spawnerPlaced);
            tagCompound.setInteger("Num", this.sectionCount);
        }

        protected void readStructureFromNBT(NBTTagCompound tagCompound)
        {
            this.hasRails = tagCompound.getBoolean("hr");
            this.hasSpiders = tagCompound.getBoolean("sc");
            this.spawnerPlaced = tagCompound.getBoolean("hps");
            this.sectionCount = tagCompound.getInteger("Num");
        }

        public Corridor(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing)
        {
            super(type);
            this.coordBaseMode = facing;
            this.boundingBox = structurebb;
            this.hasRails = rand.nextInt(3) == 0;
            this.hasSpiders = !this.hasRails && rand.nextInt(23) == 0;

            if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH)
            {
                this.sectionCount = structurebb.getXSize() / 5;
            }
            else
            {
                this.sectionCount = structurebb.getZSize() / 5;
            }
        }

        public static StructureBoundingBox func_175814_a(List<StructureComponent> p_175814_0_, Random rand, int x, int y, int z, EnumFacing facing)
        {
            StructureBoundingBox lvt_6_1_ = new StructureBoundingBox(x, y, z, x, y + 2, z);
            int lvt_7_1_;

            for (lvt_7_1_ = rand.nextInt(3) + 2; lvt_7_1_ > 0; --lvt_7_1_)
            {
                int lvt_8_1_ = lvt_7_1_ * 5;

                switch (facing)
                {
                    case NORTH:
                        lvt_6_1_.maxX = x + 2;
                        lvt_6_1_.minZ = z - (lvt_8_1_ - 1);
                        break;

                    case SOUTH:
                        lvt_6_1_.maxX = x + 2;
                        lvt_6_1_.maxZ = z + (lvt_8_1_ - 1);
                        break;

                    case WEST:
                        lvt_6_1_.minX = x - (lvt_8_1_ - 1);
                        lvt_6_1_.maxZ = z + 2;
                        break;

                    case EAST:
                        lvt_6_1_.maxX = x + (lvt_8_1_ - 1);
                        lvt_6_1_.maxZ = z + 2;
                }

                if (StructureComponent.findIntersecting(p_175814_0_, lvt_6_1_) == null)
                {
                    break;
                }
            }

            return lvt_7_1_ > 0 ? lvt_6_1_ : null;
        }

        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
        {
            int lvt_4_1_ = this.getComponentType();
            int lvt_5_1_ = rand.nextInt(4);

            if (this.coordBaseMode != null)
            {
                switch (this.coordBaseMode)
                {
                    case NORTH:
                        if (lvt_5_1_ <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, this.coordBaseMode, lvt_4_1_);
                        }
                        else if (lvt_5_1_ == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.WEST, lvt_4_1_);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.EAST, lvt_4_1_);
                        }

                        break;

                    case SOUTH:
                        if (lvt_5_1_ <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, this.coordBaseMode, lvt_4_1_);
                        }
                        else if (lvt_5_1_ == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.WEST, lvt_4_1_);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.EAST, lvt_4_1_);
                        }

                        break;

                    case WEST:
                        if (lvt_5_1_ <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, lvt_4_1_);
                        }
                        else if (lvt_5_1_ == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);
                        }

                        break;

                    case EAST:
                        if (lvt_5_1_ <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, lvt_4_1_);
                        }
                        else if (lvt_5_1_ == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);
                        }
                }
            }

            if (lvt_4_1_ < 8)
            {
                if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH)
                {
                    for (int lvt_6_2_ = this.boundingBox.minX + 3; lvt_6_2_ + 3 <= this.boundingBox.maxX; lvt_6_2_ += 5)
                    {
                        int lvt_7_2_ = rand.nextInt(5);

                        if (lvt_7_2_ == 0)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, lvt_6_2_, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_ + 1);
                        }
                        else if (lvt_7_2_ == 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, lvt_6_2_, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_ + 1);
                        }
                    }
                }
                else
                {
                    for (int lvt_6_1_ = this.boundingBox.minZ + 3; lvt_6_1_ + 3 <= this.boundingBox.maxZ; lvt_6_1_ += 5)
                    {
                        int lvt_7_1_ = rand.nextInt(5);

                        if (lvt_7_1_ == 0)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, lvt_6_1_, EnumFacing.WEST, lvt_4_1_ + 1);
                        }
                        else if (lvt_7_1_ == 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, lvt_6_1_, EnumFacing.EAST, lvt_4_1_ + 1);
                        }
                    }
                }
            }
        }

        protected boolean generateChestContents(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int max)
        {
            BlockPos lvt_9_1_ = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

            if (boundingBoxIn.isVecInside(lvt_9_1_) && worldIn.getBlockState(lvt_9_1_).getBlock().getMaterial() == Material.air)
            {
                int lvt_10_1_ = rand.nextBoolean() ? 1 : 0;
                worldIn.setBlockState(lvt_9_1_, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, lvt_10_1_)), 2);
                EntityMinecartChest lvt_11_1_ = new EntityMinecartChest(worldIn, (double)((float)lvt_9_1_.getX() + 0.5F), (double)((float)lvt_9_1_.getY() + 0.5F), (double)((float)lvt_9_1_.getZ() + 0.5F));
                WeightedRandomChestContent.generateChestContents(rand, listIn, lvt_11_1_, max);
                worldIn.spawnEntityInWorld(lvt_11_1_);
                return true;
            }
            else
            {
                return false;
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                int lvt_4_1_ = 0;
                int lvt_5_1_ = 2;
                int lvt_6_1_ = 0;
                int lvt_7_1_ = 2;
                int lvt_8_1_ = this.sectionCount * 5 - 1;
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, lvt_8_1_, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.func_175805_a(worldIn, structureBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, lvt_8_1_, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

                if (this.hasSpiders)
                {
                    this.func_175805_a(worldIn, structureBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, lvt_8_1_, Blocks.web.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                for (int lvt_9_1_ = 0; lvt_9_1_ < this.sectionCount; ++lvt_9_1_)
                {
                    int lvt_10_1_ = 2 + lvt_9_1_ * 5;
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, lvt_10_1_, 0, 1, lvt_10_1_, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, lvt_10_1_, 2, 1, lvt_10_1_, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);

                    if (randomIn.nextInt(4) == 0)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, lvt_10_1_, 0, 2, lvt_10_1_, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, lvt_10_1_, 2, 2, lvt_10_1_, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                    }
                    else
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, lvt_10_1_, 2, 2, lvt_10_1_, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                    }

                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, lvt_10_1_ - 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, lvt_10_1_ - 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, lvt_10_1_ + 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, lvt_10_1_ + 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, lvt_10_1_ - 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, lvt_10_1_ - 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, lvt_10_1_ + 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, lvt_10_1_ + 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, lvt_10_1_ - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, lvt_10_1_ + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));

                    if (randomIn.nextInt(100) == 0)
                    {
                        this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 2, 0, lvt_10_1_ - 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, new WeightedRandomChestContent[] {Items.enchanted_book.getRandom(randomIn)}), 3 + randomIn.nextInt(4));
                    }

                    if (randomIn.nextInt(100) == 0)
                    {
                        this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 0, 0, lvt_10_1_ + 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, new WeightedRandomChestContent[] {Items.enchanted_book.getRandom(randomIn)}), 3 + randomIn.nextInt(4));
                    }

                    if (this.hasSpiders && !this.spawnerPlaced)
                    {
                        int lvt_11_1_ = this.getYWithOffset(0);
                        int lvt_12_1_ = lvt_10_1_ - 1 + randomIn.nextInt(3);
                        int lvt_13_1_ = this.getXWithOffset(1, lvt_12_1_);
                        lvt_12_1_ = this.getZWithOffset(1, lvt_12_1_);
                        BlockPos lvt_14_1_ = new BlockPos(lvt_13_1_, lvt_11_1_, lvt_12_1_);

                        if (structureBoundingBoxIn.isVecInside(lvt_14_1_))
                        {
                            this.spawnerPlaced = true;
                            worldIn.setBlockState(lvt_14_1_, Blocks.mob_spawner.getDefaultState(), 2);
                            TileEntity lvt_15_1_ = worldIn.getTileEntity(lvt_14_1_);

                            if (lvt_15_1_ instanceof TileEntityMobSpawner)
                            {
                                ((TileEntityMobSpawner)lvt_15_1_).getSpawnerBaseLogic().setEntityName("CaveSpider");
                            }
                        }
                    }
                }

                for (int lvt_9_2_ = 0; lvt_9_2_ <= 2; ++lvt_9_2_)
                {
                    for (int lvt_10_2_ = 0; lvt_10_2_ <= lvt_8_1_; ++lvt_10_2_)
                    {
                        int lvt_11_2_ = -1;
                        IBlockState lvt_12_2_ = this.getBlockStateFromPos(worldIn, lvt_9_2_, lvt_11_2_, lvt_10_2_, structureBoundingBoxIn);

                        if (lvt_12_2_.getBlock().getMaterial() == Material.air)
                        {
                            int lvt_13_2_ = -1;
                            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), lvt_9_2_, lvt_13_2_, lvt_10_2_, structureBoundingBoxIn);
                        }
                    }
                }

                if (this.hasRails)
                {
                    for (int lvt_9_3_ = 0; lvt_9_3_ <= lvt_8_1_; ++lvt_9_3_)
                    {
                        IBlockState lvt_10_3_ = this.getBlockStateFromPos(worldIn, 1, -1, lvt_9_3_, structureBoundingBoxIn);

                        if (lvt_10_3_.getBlock().getMaterial() != Material.air && lvt_10_3_.getBlock().isFullBlock())
                        {
                            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.7F, 1, 0, lvt_9_3_, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, 0)));
                        }
                    }
                }

                return true;
            }
        }
    }

    public static class Cross extends StructureComponent
    {
        private EnumFacing corridorDirection;
        private boolean isMultipleFloors;

        public Cross()
        {
        }

        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            tagCompound.setBoolean("tf", this.isMultipleFloors);
            tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
        }

        protected void readStructureFromNBT(NBTTagCompound tagCompound)
        {
            this.isMultipleFloors = tagCompound.getBoolean("tf");
            this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
        }

        public Cross(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing)
        {
            super(type);
            this.corridorDirection = facing;
            this.boundingBox = structurebb;
            this.isMultipleFloors = structurebb.getYSize() > 3;
        }

        public static StructureBoundingBox func_175813_a(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing)
        {
            StructureBoundingBox lvt_6_1_ = new StructureBoundingBox(x, y, z, x, y + 2, z);

            if (rand.nextInt(4) == 0)
            {
                lvt_6_1_.maxY += 4;
            }

            switch (facing)
            {
                case NORTH:
                    lvt_6_1_.minX = x - 1;
                    lvt_6_1_.maxX = x + 3;
                    lvt_6_1_.minZ = z - 4;
                    break;

                case SOUTH:
                    lvt_6_1_.minX = x - 1;
                    lvt_6_1_.maxX = x + 3;
                    lvt_6_1_.maxZ = z + 4;
                    break;

                case WEST:
                    lvt_6_1_.minX = x - 4;
                    lvt_6_1_.minZ = z - 1;
                    lvt_6_1_.maxZ = z + 3;
                    break;

                case EAST:
                    lvt_6_1_.maxX = x + 4;
                    lvt_6_1_.minZ = z - 1;
                    lvt_6_1_.maxZ = z + 3;
            }

            return StructureComponent.findIntersecting(listIn, lvt_6_1_) != null ? null : lvt_6_1_;
        }

        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
        {
            int lvt_4_1_ = this.getComponentType();

            switch (this.corridorDirection)
            {
                case NORTH:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, lvt_4_1_);
                    break;

                case SOUTH:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, lvt_4_1_);
                    break;

                case WEST:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, lvt_4_1_);
                    break;

                case EAST:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, lvt_4_1_);
            }

            if (this.isMultipleFloors)
            {
                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);
                }

                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, lvt_4_1_);
                }

                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, lvt_4_1_);
                }

                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);
                }
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                if (this.isMultipleFloors)
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }
                else
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);

                for (int lvt_4_1_ = this.boundingBox.minX; lvt_4_1_ <= this.boundingBox.maxX; ++lvt_4_1_)
                {
                    for (int lvt_5_1_ = this.boundingBox.minZ; lvt_5_1_ <= this.boundingBox.maxZ; ++lvt_5_1_)
                    {
                        if (this.getBlockStateFromPos(worldIn, lvt_4_1_, this.boundingBox.minY - 1, lvt_5_1_, structureBoundingBoxIn).getBlock().getMaterial() == Material.air)
                        {
                            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), lvt_4_1_, this.boundingBox.minY - 1, lvt_5_1_, structureBoundingBoxIn);
                        }
                    }
                }

                return true;
            }
        }
    }

    public static class Room extends StructureComponent
    {
        private List<StructureBoundingBox> roomsLinkedToTheRoom = Lists.newLinkedList();

        public Room()
        {
        }

        public Room(int type, Random rand, int x, int z)
        {
            super(type);
            this.boundingBox = new StructureBoundingBox(x, 50, z, x + 7 + rand.nextInt(6), 54 + rand.nextInt(6), z + 7 + rand.nextInt(6));
        }

        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
        {
            int lvt_4_1_ = this.getComponentType();
            int lvt_6_1_ = this.boundingBox.getYSize() - 3 - 1;

            if (lvt_6_1_ <= 0)
            {
                lvt_6_1_ = 1;
            }

            int var9;

            for (lvt_5_1_ = 0; var9 < this.boundingBox.getXSize(); var9 = var9 + 4)
            {
                var9 = var9 + rand.nextInt(this.boundingBox.getXSize());

                if (var9 + 3 > this.boundingBox.getXSize())
                {
                    break;
                }

                StructureComponent lvt_7_1_ = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + var9, this.boundingBox.minY + rand.nextInt(lvt_6_1_) + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);

                if (lvt_7_1_ != null)
                {
                    StructureBoundingBox lvt_8_1_ = lvt_7_1_.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(lvt_8_1_.minX, lvt_8_1_.minY, this.boundingBox.minZ, lvt_8_1_.maxX, lvt_8_1_.maxY, this.boundingBox.minZ + 1));
                }
            }

            for (var9 = 0; var9 < this.boundingBox.getXSize(); var9 = var9 + 4)
            {
                var9 = var9 + rand.nextInt(this.boundingBox.getXSize());

                if (var9 + 3 > this.boundingBox.getXSize())
                {
                    break;
                }

                StructureComponent lvt_7_2_ = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + var9, this.boundingBox.minY + rand.nextInt(lvt_6_1_) + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);

                if (lvt_7_2_ != null)
                {
                    StructureBoundingBox lvt_8_2_ = lvt_7_2_.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(lvt_8_2_.minX, lvt_8_2_.minY, this.boundingBox.maxZ - 1, lvt_8_2_.maxX, lvt_8_2_.maxY, this.boundingBox.maxZ));
                }
            }

            for (var9 = 0; var9 < this.boundingBox.getZSize(); var9 = var9 + 4)
            {
                var9 = var9 + rand.nextInt(this.boundingBox.getZSize());

                if (var9 + 3 > this.boundingBox.getZSize())
                {
                    break;
                }

                StructureComponent lvt_7_3_ = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(lvt_6_1_) + 1, this.boundingBox.minZ + var9, EnumFacing.WEST, lvt_4_1_);

                if (lvt_7_3_ != null)
                {
                    StructureBoundingBox lvt_8_3_ = lvt_7_3_.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.minX, lvt_8_3_.minY, lvt_8_3_.minZ, this.boundingBox.minX + 1, lvt_8_3_.maxY, lvt_8_3_.maxZ));
                }
            }

            for (var9 = 0; var9 < this.boundingBox.getZSize(); var9 = var9 + 4)
            {
                var9 = var9 + rand.nextInt(this.boundingBox.getZSize());

                if (var9 + 3 > this.boundingBox.getZSize())
                {
                    break;
                }

                StructureComponent lvt_7_4_ = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(lvt_6_1_) + 1, this.boundingBox.minZ + var9, EnumFacing.EAST, lvt_4_1_);

                if (lvt_7_4_ != null)
                {
                    StructureBoundingBox lvt_8_4_ = lvt_7_4_.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.maxX - 1, lvt_8_4_.minY, lvt_8_4_.minZ, this.boundingBox.maxX, lvt_8_4_.maxY, lvt_8_4_.maxZ));
                }
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.dirt.getDefaultState(), Blocks.air.getDefaultState(), true);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

                for (StructureBoundingBox lvt_5_1_ : this.roomsLinkedToTheRoom)
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, lvt_5_1_.minX, lvt_5_1_.maxY - 2, lvt_5_1_.minZ, lvt_5_1_.maxX, lvt_5_1_.maxY, lvt_5_1_.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                this.randomlyRareFillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), false);
                return true;
            }
        }

        public void func_181138_a(int p_181138_1_, int p_181138_2_, int p_181138_3_)
        {
            super.func_181138_a(p_181138_1_, p_181138_2_, p_181138_3_);

            for (StructureBoundingBox lvt_5_1_ : this.roomsLinkedToTheRoom)
            {
                lvt_5_1_.offset(p_181138_1_, p_181138_2_, p_181138_3_);
            }
        }

        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            NBTTagList lvt_2_1_ = new NBTTagList();

            for (StructureBoundingBox lvt_4_1_ : this.roomsLinkedToTheRoom)
            {
                lvt_2_1_.appendTag(lvt_4_1_.toNBTTagIntArray());
            }

            tagCompound.setTag("Entrances", lvt_2_1_);
        }

        protected void readStructureFromNBT(NBTTagCompound tagCompound)
        {
            NBTTagList lvt_2_1_ = tagCompound.getTagList("Entrances", 11);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
            {
                this.roomsLinkedToTheRoom.add(new StructureBoundingBox(lvt_2_1_.getIntArrayAt(lvt_3_1_)));
            }
        }
    }

    public static class Stairs extends StructureComponent
    {
        public Stairs()
        {
        }

        public Stairs(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing)
        {
            super(type);
            this.coordBaseMode = facing;
            this.boundingBox = structurebb;
        }

        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
        }

        protected void readStructureFromNBT(NBTTagCompound tagCompound)
        {
        }

        public static StructureBoundingBox func_175812_a(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing)
        {
            StructureBoundingBox lvt_6_1_ = new StructureBoundingBox(x, y - 5, z, x, y + 2, z);

            switch (facing)
            {
                case NORTH:
                    lvt_6_1_.maxX = x + 2;
                    lvt_6_1_.minZ = z - 8;
                    break;

                case SOUTH:
                    lvt_6_1_.maxX = x + 2;
                    lvt_6_1_.maxZ = z + 8;
                    break;

                case WEST:
                    lvt_6_1_.minX = x - 8;
                    lvt_6_1_.maxZ = z + 2;
                    break;

                case EAST:
                    lvt_6_1_.maxX = x + 8;
                    lvt_6_1_.maxZ = z + 2;
            }

            return StructureComponent.findIntersecting(listIn, lvt_6_1_) != null ? null : lvt_6_1_;
        }

        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand)
        {
            int lvt_4_1_ = this.getComponentType();

            if (this.coordBaseMode != null)
            {
                switch (this.coordBaseMode)
                {
                    case NORTH:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, lvt_4_1_);
                        break;

                    case SOUTH:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, lvt_4_1_);
                        break;

                    case WEST:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, lvt_4_1_);
                        break;

                    case EAST:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, lvt_4_1_);
                }
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

                for (int lvt_4_1_ = 0; lvt_4_1_ < 5; ++lvt_4_1_)
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5 - lvt_4_1_ - (lvt_4_1_ < 4 ? 1 : 0), 2 + lvt_4_1_, 2, 7 - lvt_4_1_, 2 + lvt_4_1_, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                return true;
            }
        }
    }
}
