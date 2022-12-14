package net.minecraft.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity
{
    private static final Logger logger = LogManager.getLogger();
    private static Map < String, Class <? extends TileEntity >> nameToClassMap = Maps.newHashMap();
    private static Map < Class <? extends TileEntity > , String > classToNameMap = Maps.newHashMap();

    /** the instance of the world the tile entity is in. */
    protected World worldObj;
    protected BlockPos pos = BlockPos.ORIGIN;
    protected boolean tileEntityInvalid;
    private int blockMetadata = -1;

    /** the Block type that this TileEntity is contained within */
    protected Block blockType;

    /**
     * Adds a new two-way mapping between the class and its string name in both hashmaps.
     */
    private static void addMapping(Class <? extends TileEntity > cl, String id)
    {
        if (nameToClassMap.containsKey(id))
        {
            throw new IllegalArgumentException("Duplicate id: " + id);
        }
        else
        {
            nameToClassMap.put(id, cl);
            classToNameMap.put(cl, id);
        }
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorld()
    {
        return this.worldObj;
    }

    /**
     * Sets the worldObj for this tileEntity.
     */
    public void setWorldObj(World worldIn)
    {
        this.worldObj = worldIn;
    }

    /**
     * Returns true if the worldObj isn't null.
     */
    public boolean hasWorldObj()
    {
        return this.worldObj != null;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        this.pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        String lvt_2_1_ = (String)classToNameMap.get(this.getClass());

        if (lvt_2_1_ == null)
        {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        else
        {
            compound.setString("id", lvt_2_1_);
            compound.setInteger("x", this.pos.getX());
            compound.setInteger("y", this.pos.getY());
            compound.setInteger("z", this.pos.getZ());
        }
    }

    /**
     * Creates a new entity and loads its data from the specified NBT.
     */
    public static TileEntity createAndLoadEntity(NBTTagCompound nbt)
    {
        TileEntity lvt_1_1_ = null;

        try
        {
            Class <? extends TileEntity > lvt_2_1_ = (Class)nameToClassMap.get(nbt.getString("id"));

            if (lvt_2_1_ != null)
            {
                lvt_1_1_ = (TileEntity)lvt_2_1_.newInstance();
            }
        }
        catch (Exception var3)
        {
            var3.printStackTrace();
        }

        if (lvt_1_1_ != null)
        {
            lvt_1_1_.readFromNBT(nbt);
        }
        else
        {
            logger.warn("Skipping BlockEntity with id " + nbt.getString("id"));
        }

        return lvt_1_1_;
    }

    public int getBlockMetadata()
    {
        if (this.blockMetadata == -1)
        {
            IBlockState lvt_1_1_ = this.worldObj.getBlockState(this.pos);
            this.blockMetadata = lvt_1_1_.getBlock().getMetaFromState(lvt_1_1_);
        }

        return this.blockMetadata;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        if (this.worldObj != null)
        {
            IBlockState lvt_1_1_ = this.worldObj.getBlockState(this.pos);
            this.blockMetadata = lvt_1_1_.getBlock().getMetaFromState(lvt_1_1_);
            this.worldObj.markChunkDirty(this.pos, this);

            if (this.getBlockType() != Blocks.air)
            {
                this.worldObj.updateComparatorOutputLevel(this.pos, this.getBlockType());
            }
        }
    }

    /**
     * Returns the square of the distance between this entity and the passed in coordinates.
     */
    public double getDistanceSq(double x, double y, double z)
    {
        double lvt_7_1_ = (double)this.pos.getX() + 0.5D - x;
        double lvt_9_1_ = (double)this.pos.getY() + 0.5D - y;
        double lvt_11_1_ = (double)this.pos.getZ() + 0.5D - z;
        return lvt_7_1_ * lvt_7_1_ + lvt_9_1_ * lvt_9_1_ + lvt_11_1_ * lvt_11_1_;
    }

    public double getMaxRenderDistanceSquared()
    {
        return 4096.0D;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    /**
     * Gets the block type at the location of this entity (client-only).
     */
    public Block getBlockType()
    {
        if (this.blockType == null)
        {
            this.blockType = this.worldObj.getBlockState(this.pos).getBlock();
        }

        return this.blockType;
    }

    /**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket()
    {
        return null;
    }

    public boolean isInvalid()
    {
        return this.tileEntityInvalid;
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        this.tileEntityInvalid = true;
    }

    /**
     * validates a tile entity
     */
    public void validate()
    {
        this.tileEntityInvalid = false;
    }

    public boolean receiveClientEvent(int id, int type)
    {
        return false;
    }

    public void updateContainingBlockInfo()
    {
        this.blockType = null;
        this.blockMetadata = -1;
    }

    public void addInfoToCrashReport(CrashReportCategory reportCategory)
    {
        reportCategory.addCrashSectionCallable("Name", new Callable<String>()
        {
            public String call() throws Exception
            {
                return (String)TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });

        if (this.worldObj != null)
        {
            CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockType(), this.getBlockMetadata());
            reportCategory.addCrashSectionCallable("Actual block type", new Callable<String>()
            {
                public String call() throws Exception
                {
                    int lvt_1_1_ = Block.getIdFromBlock(TileEntity.this.worldObj.getBlockState(TileEntity.this.pos).getBlock());

                    try
                    {
                        return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(lvt_1_1_), Block.getBlockById(lvt_1_1_).getUnlocalizedName(), Block.getBlockById(lvt_1_1_).getClass().getCanonicalName()});
                    }
                    catch (Throwable var3)
                    {
                        return "ID #" + lvt_1_1_;
                    }
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            reportCategory.addCrashSectionCallable("Actual block data value", new Callable<String>()
            {
                public String call() throws Exception
                {
                    IBlockState lvt_1_1_ = TileEntity.this.worldObj.getBlockState(TileEntity.this.pos);
                    int lvt_2_1_ = lvt_1_1_.getBlock().getMetaFromState(lvt_1_1_);

                    if (lvt_2_1_ < 0)
                    {
                        return "Unknown? (Got " + lvt_2_1_ + ")";
                    }
                    else
                    {
                        String lvt_3_1_ = String.format("%4s", new Object[] {Integer.toBinaryString(lvt_2_1_)}).replace(" ", "0");
                        return String.format("%1$d / 0x%1$X / 0b%2$s", new Object[] {Integer.valueOf(lvt_2_1_), lvt_3_1_});
                    }
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
        }
    }

    public void setPos(BlockPos posIn)
    {
        this.pos = posIn;
    }

    public boolean func_183000_F()
    {
        return false;
    }

    static
    {
        addMapping(TileEntityFurnace.class, "Furnace");
        addMapping(TileEntityChest.class, "Chest");
        addMapping(TileEntityEnderChest.class, "EnderChest");
        addMapping(BlockJukebox.TileEntityJukebox.class, "RecordPlayer");
        addMapping(TileEntityDispenser.class, "Trap");
        addMapping(TileEntityDropper.class, "Dropper");
        addMapping(TileEntitySign.class, "Sign");
        addMapping(TileEntityMobSpawner.class, "MobSpawner");
        addMapping(TileEntityNote.class, "Music");
        addMapping(TileEntityPiston.class, "Piston");
        addMapping(TileEntityBrewingStand.class, "Cauldron");
        addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
        addMapping(TileEntityEndPortal.class, "Airportal");
        addMapping(TileEntityCommandBlock.class, "Control");
        addMapping(TileEntityBeacon.class, "Beacon");
        addMapping(TileEntitySkull.class, "Skull");
        addMapping(TileEntityDaylightDetector.class, "DLDetector");
        addMapping(TileEntityHopper.class, "Hopper");
        addMapping(TileEntityComparator.class, "Comparator");
        addMapping(TileEntityFlowerPot.class, "FlowerPot");
        addMapping(TileEntityBanner.class, "Banner");
    }
}
