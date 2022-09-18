package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource
{
    private Entity indirectEntity;

    public EntityDamageSourceIndirect(String damageTypeIn, Entity source, Entity indirectEntityIn)
    {
        super(damageTypeIn, source);
        this.indirectEntity = indirectEntityIn;
    }

    public Entity getSourceOfDamage()
    {
        return this.damageSourceEntity;
    }

    public Entity getEntity()
    {
        return this.indirectEntity;
    }

    /**
     * Gets the death message that is displayed when the player dies
     *  
     * @param entityLivingBaseIn The EntityLivingBase that died
     */
    public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn)
    {
        IChatComponent lvt_2_1_ = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
        ItemStack lvt_3_1_ = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
        String lvt_4_1_ = "death.attack." + this.damageType;
        String lvt_5_1_ = lvt_4_1_ + ".item";
        return lvt_3_1_ != null && lvt_3_1_.hasDisplayName() && StatCollector.canTranslate(lvt_5_1_) ? new ChatComponentTranslation(lvt_5_1_, new Object[] {entityLivingBaseIn.getDisplayName(), lvt_2_1_, lvt_3_1_.getChatComponent()}): new ChatComponentTranslation(lvt_4_1_, new Object[] {entityLivingBaseIn.getDisplayName(), lvt_2_1_});
    }
}
