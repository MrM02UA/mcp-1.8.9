package net.minecraft.village;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

public class VillageSiege
{
    private World worldObj;
    private boolean field_75535_b;
    private int field_75536_c = -1;
    private int field_75533_d;
    private int field_75534_e;

    /** Instance of Village. */
    private Village theVillage;
    private int field_75532_g;
    private int field_75538_h;
    private int field_75539_i;

    public VillageSiege(World worldIn)
    {
        this.worldObj = worldIn;
    }

    /**
     * Runs a single tick for the village siege
     */
    public void tick()
    {
        if (this.worldObj.isDaytime())
        {
            this.field_75536_c = 0;
        }
        else if (this.field_75536_c != 2)
        {
            if (this.field_75536_c == 0)
            {
                float lvt_1_1_ = this.worldObj.getCelestialAngle(0.0F);

                if ((double)lvt_1_1_ < 0.5D || (double)lvt_1_1_ > 0.501D)
                {
                    return;
                }

                this.field_75536_c = this.worldObj.rand.nextInt(10) == 0 ? 1 : 2;
                this.field_75535_b = false;

                if (this.field_75536_c == 2)
                {
                    return;
                }
            }

            if (this.field_75536_c != -1)
            {
                if (!this.field_75535_b)
                {
                    if (!this.func_75529_b())
                    {
                        return;
                    }

                    this.field_75535_b = true;
                }

                if (this.field_75534_e > 0)
                {
                    --this.field_75534_e;
                }
                else
                {
                    this.field_75534_e = 2;

                    if (this.field_75533_d > 0)
                    {
                        this.spawnZombie();
                        --this.field_75533_d;
                    }
                    else
                    {
                        this.field_75536_c = 2;
                    }
                }
            }
        }
    }

    private boolean func_75529_b()
    {
        List<EntityPlayer> lvt_1_1_ = this.worldObj.playerEntities;
        Iterator lvt_2_1_ = lvt_1_1_.iterator();

        while (true)
        {
            if (!lvt_2_1_.hasNext())
            {
                return false;
            }

            EntityPlayer lvt_3_1_ = (EntityPlayer)lvt_2_1_.next();

            if (!lvt_3_1_.isSpectator())
            {
                this.theVillage = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(lvt_3_1_), 1);

                if (this.theVillage != null && this.theVillage.getNumVillageDoors() >= 10 && this.theVillage.getTicksSinceLastDoorAdding() >= 20 && this.theVillage.getNumVillagers() >= 20)
                {
                    BlockPos lvt_4_1_ = this.theVillage.getCenter();
                    float lvt_5_1_ = (float)this.theVillage.getVillageRadius();
                    boolean lvt_6_1_ = false;

                    for (int lvt_7_1_ = 0; lvt_7_1_ < 10; ++lvt_7_1_)
                    {
                        float lvt_8_1_ = this.worldObj.rand.nextFloat() * (float)Math.PI * 2.0F;
                        this.field_75532_g = lvt_4_1_.getX() + (int)((double)(MathHelper.cos(lvt_8_1_) * lvt_5_1_) * 0.9D);
                        this.field_75538_h = lvt_4_1_.getY();
                        this.field_75539_i = lvt_4_1_.getZ() + (int)((double)(MathHelper.sin(lvt_8_1_) * lvt_5_1_) * 0.9D);
                        lvt_6_1_ = false;

                        for (Village lvt_10_1_ : this.worldObj.getVillageCollection().getVillageList())
                        {
                            if (lvt_10_1_ != this.theVillage && lvt_10_1_.func_179866_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i)))
                            {
                                lvt_6_1_ = true;
                                break;
                            }
                        }

                        if (!lvt_6_1_)
                        {
                            break;
                        }
                    }

                    if (lvt_6_1_)
                    {
                        return false;
                    }

                    Vec3 lvt_7_2_ = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));

                    if (lvt_7_2_ != null)
                    {
                        break;
                    }
                }
            }
        }

        this.field_75534_e = 0;
        this.field_75533_d = 20;
        return true;
    }

    private boolean spawnZombie()
    {
        Vec3 lvt_1_1_ = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));

        if (lvt_1_1_ == null)
        {
            return false;
        }
        else
        {
            EntityZombie lvt_2_1_;

            try
            {
                lvt_2_1_ = new EntityZombie(this.worldObj);
                lvt_2_1_.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(lvt_2_1_)), (IEntityLivingData)null);
                lvt_2_1_.setVillager(false);
            }
            catch (Exception var4)
            {
                var4.printStackTrace();
                return false;
            }

            lvt_2_1_.setLocationAndAngles(lvt_1_1_.xCoord, lvt_1_1_.yCoord, lvt_1_1_.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
            this.worldObj.spawnEntityInWorld(lvt_2_1_);
            BlockPos lvt_3_2_ = this.theVillage.getCenter();
            lvt_2_1_.setHomePosAndDistance(lvt_3_2_, this.theVillage.getVillageRadius());
            return true;
        }
    }

    private Vec3 func_179867_a(BlockPos p_179867_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < 10; ++lvt_2_1_)
        {
            BlockPos lvt_3_1_ = p_179867_1_.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);

            if (this.theVillage.func_179866_a(lvt_3_1_) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, this.worldObj, lvt_3_1_))
            {
                return new Vec3((double)lvt_3_1_.getX(), (double)lvt_3_1_.getY(), (double)lvt_3_1_.getZ());
            }
        }

        return null;
    }
}
