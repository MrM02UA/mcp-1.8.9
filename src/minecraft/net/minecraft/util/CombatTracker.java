package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CombatTracker
{
    private final List<CombatEntry> combatEntries = Lists.newArrayList();

    /** The entity tracked. */
    private final EntityLivingBase fighter;
    private int field_94555_c;
    private int field_152775_d;
    private int field_152776_e;
    private boolean field_94552_d;
    private boolean field_94553_e;
    private String field_94551_f;

    public CombatTracker(EntityLivingBase fighterIn)
    {
        this.fighter = fighterIn;
    }

    public void func_94545_a()
    {
        this.func_94542_g();

        if (this.fighter.isOnLadder())
        {
            Block lvt_1_1_ = this.fighter.worldObj.getBlockState(new BlockPos(this.fighter.posX, this.fighter.getEntityBoundingBox().minY, this.fighter.posZ)).getBlock();

            if (lvt_1_1_ == Blocks.ladder)
            {
                this.field_94551_f = "ladder";
            }
            else if (lvt_1_1_ == Blocks.vine)
            {
                this.field_94551_f = "vines";
            }
        }
        else if (this.fighter.isInWater())
        {
            this.field_94551_f = "water";
        }
    }

    /**
     * Adds an entry for the combat tracker
     */
    public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount)
    {
        this.reset();
        this.func_94545_a();
        CombatEntry lvt_4_1_ = new CombatEntry(damageSrc, this.fighter.ticksExisted, healthIn, damageAmount, this.field_94551_f, this.fighter.fallDistance);
        this.combatEntries.add(lvt_4_1_);
        this.field_94555_c = this.fighter.ticksExisted;
        this.field_94553_e = true;

        if (lvt_4_1_.isLivingDamageSrc() && !this.field_94552_d && this.fighter.isEntityAlive())
        {
            this.field_94552_d = true;
            this.field_152775_d = this.fighter.ticksExisted;
            this.field_152776_e = this.field_152775_d;
            this.fighter.sendEnterCombat();
        }
    }

    public IChatComponent getDeathMessage()
    {
        if (this.combatEntries.size() == 0)
        {
            return new ChatComponentTranslation("death.attack.generic", new Object[] {this.fighter.getDisplayName()});
        }
        else
        {
            CombatEntry lvt_1_1_ = this.func_94544_f();
            CombatEntry lvt_2_1_ = (CombatEntry)this.combatEntries.get(this.combatEntries.size() - 1);
            IChatComponent lvt_4_1_ = lvt_2_1_.getDamageSrcDisplayName();
            Entity lvt_5_1_ = lvt_2_1_.getDamageSrc().getEntity();
            IChatComponent lvt_3_2_;

            if (lvt_1_1_ != null && lvt_2_1_.getDamageSrc() == DamageSource.fall)
            {
                IChatComponent lvt_6_1_ = lvt_1_1_.getDamageSrcDisplayName();

                if (lvt_1_1_.getDamageSrc() != DamageSource.fall && lvt_1_1_.getDamageSrc() != DamageSource.outOfWorld)
                {
                    if (lvt_6_1_ != null && (lvt_4_1_ == null || !lvt_6_1_.equals(lvt_4_1_)))
                    {
                        Entity lvt_7_1_ = lvt_1_1_.getDamageSrc().getEntity();
                        ItemStack lvt_8_1_ = lvt_7_1_ instanceof EntityLivingBase ? ((EntityLivingBase)lvt_7_1_).getHeldItem() : null;

                        if (lvt_8_1_ != null && lvt_8_1_.hasDisplayName())
                        {
                            lvt_3_2_ = new ChatComponentTranslation("death.fell.assist.item", new Object[] {this.fighter.getDisplayName(), lvt_6_1_, lvt_8_1_.getChatComponent()});
                        }
                        else
                        {
                            lvt_3_2_ = new ChatComponentTranslation("death.fell.assist", new Object[] {this.fighter.getDisplayName(), lvt_6_1_});
                        }
                    }
                    else if (lvt_4_1_ != null)
                    {
                        ItemStack lvt_7_2_ = lvt_5_1_ instanceof EntityLivingBase ? ((EntityLivingBase)lvt_5_1_).getHeldItem() : null;

                        if (lvt_7_2_ != null && lvt_7_2_.hasDisplayName())
                        {
                            lvt_3_2_ = new ChatComponentTranslation("death.fell.finish.item", new Object[] {this.fighter.getDisplayName(), lvt_4_1_, lvt_7_2_.getChatComponent()});
                        }
                        else
                        {
                            lvt_3_2_ = new ChatComponentTranslation("death.fell.finish", new Object[] {this.fighter.getDisplayName(), lvt_4_1_});
                        }
                    }
                    else
                    {
                        lvt_3_2_ = new ChatComponentTranslation("death.fell.killer", new Object[] {this.fighter.getDisplayName()});
                    }
                }
                else
                {
                    lvt_3_2_ = new ChatComponentTranslation("death.fell.accident." + this.func_94548_b(lvt_1_1_), new Object[] {this.fighter.getDisplayName()});
                }
            }
            else
            {
                lvt_3_2_ = lvt_2_1_.getDamageSrc().getDeathMessage(this.fighter);
            }

            return lvt_3_2_;
        }
    }

    public EntityLivingBase func_94550_c()
    {
        EntityLivingBase lvt_1_1_ = null;
        EntityPlayer lvt_2_1_ = null;
        float lvt_3_1_ = 0.0F;
        float lvt_4_1_ = 0.0F;

        for (CombatEntry lvt_6_1_ : this.combatEntries)
        {
            if (lvt_6_1_.getDamageSrc().getEntity() instanceof EntityPlayer && (lvt_2_1_ == null || lvt_6_1_.func_94563_c() > lvt_4_1_))
            {
                lvt_4_1_ = lvt_6_1_.func_94563_c();
                lvt_2_1_ = (EntityPlayer)lvt_6_1_.getDamageSrc().getEntity();
            }

            if (lvt_6_1_.getDamageSrc().getEntity() instanceof EntityLivingBase && (lvt_1_1_ == null || lvt_6_1_.func_94563_c() > lvt_3_1_))
            {
                lvt_3_1_ = lvt_6_1_.func_94563_c();
                lvt_1_1_ = (EntityLivingBase)lvt_6_1_.getDamageSrc().getEntity();
            }
        }

        if (lvt_2_1_ != null && lvt_4_1_ >= lvt_3_1_ / 3.0F)
        {
            return lvt_2_1_;
        }
        else
        {
            return lvt_1_1_;
        }
    }

    private CombatEntry func_94544_f()
    {
        CombatEntry lvt_1_1_ = null;
        CombatEntry lvt_2_1_ = null;
        int lvt_3_1_ = 0;
        float lvt_4_1_ = 0.0F;

        for (int lvt_5_1_ = 0; lvt_5_1_ < this.combatEntries.size(); ++lvt_5_1_)
        {
            CombatEntry lvt_6_1_ = (CombatEntry)this.combatEntries.get(lvt_5_1_);
            CombatEntry lvt_7_1_ = lvt_5_1_ > 0 ? (CombatEntry)this.combatEntries.get(lvt_5_1_ - 1) : null;

            if ((lvt_6_1_.getDamageSrc() == DamageSource.fall || lvt_6_1_.getDamageSrc() == DamageSource.outOfWorld) && lvt_6_1_.getDamageAmount() > 0.0F && (lvt_1_1_ == null || lvt_6_1_.getDamageAmount() > lvt_4_1_))
            {
                if (lvt_5_1_ > 0)
                {
                    lvt_1_1_ = lvt_7_1_;
                }
                else
                {
                    lvt_1_1_ = lvt_6_1_;
                }

                lvt_4_1_ = lvt_6_1_.getDamageAmount();
            }

            if (lvt_6_1_.func_94562_g() != null && (lvt_2_1_ == null || lvt_6_1_.func_94563_c() > (float)lvt_3_1_))
            {
                lvt_2_1_ = lvt_6_1_;
            }
        }

        if (lvt_4_1_ > 5.0F && lvt_1_1_ != null)
        {
            return lvt_1_1_;
        }
        else if (lvt_3_1_ > 5 && lvt_2_1_ != null)
        {
            return lvt_2_1_;
        }
        else
        {
            return null;
        }
    }

    private String func_94548_b(CombatEntry p_94548_1_)
    {
        return p_94548_1_.func_94562_g() == null ? "generic" : p_94548_1_.func_94562_g();
    }

    public int func_180134_f()
    {
        return this.field_94552_d ? this.fighter.ticksExisted - this.field_152775_d : this.field_152776_e - this.field_152775_d;
    }

    private void func_94542_g()
    {
        this.field_94551_f = null;
    }

    /**
     * Resets this trackers list of combat entries
     */
    public void reset()
    {
        int lvt_1_1_ = this.field_94552_d ? 300 : 100;

        if (this.field_94553_e && (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.field_94555_c > lvt_1_1_))
        {
            boolean lvt_2_1_ = this.field_94552_d;
            this.field_94553_e = false;
            this.field_94552_d = false;
            this.field_152776_e = this.fighter.ticksExisted;

            if (lvt_2_1_)
            {
                this.fighter.sendEndCombat();
            }

            this.combatEntries.clear();
        }
    }

    /**
     * Returns EntityLivingBase assigned for this CombatTracker
     */
    public EntityLivingBase getFighter()
    {
        return this.fighter;
    }
}
