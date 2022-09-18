package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities
{
    /** Disables player damage. */
    public boolean disableDamage;

    /** Sets/indicates whether the player is flying. */
    public boolean isFlying;

    /** whether or not to allow the player to fly when they double jump. */
    public boolean allowFlying;

    /**
     * Used to determine if creative mode is enabled, and therefore if items should be depleted on usage
     */
    public boolean isCreativeMode;

    /** Indicates whether the player is allowed to modify the surroundings */
    public boolean allowEdit = true;
    private float flySpeed = 0.05F;
    private float walkSpeed = 0.1F;

    public void writeCapabilitiesToNBT(NBTTagCompound tagCompound)
    {
        NBTTagCompound lvt_2_1_ = new NBTTagCompound();
        lvt_2_1_.setBoolean("invulnerable", this.disableDamage);
        lvt_2_1_.setBoolean("flying", this.isFlying);
        lvt_2_1_.setBoolean("mayfly", this.allowFlying);
        lvt_2_1_.setBoolean("instabuild", this.isCreativeMode);
        lvt_2_1_.setBoolean("mayBuild", this.allowEdit);
        lvt_2_1_.setFloat("flySpeed", this.flySpeed);
        lvt_2_1_.setFloat("walkSpeed", this.walkSpeed);
        tagCompound.setTag("abilities", lvt_2_1_);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound tagCompound)
    {
        if (tagCompound.hasKey("abilities", 10))
        {
            NBTTagCompound lvt_2_1_ = tagCompound.getCompoundTag("abilities");
            this.disableDamage = lvt_2_1_.getBoolean("invulnerable");
            this.isFlying = lvt_2_1_.getBoolean("flying");
            this.allowFlying = lvt_2_1_.getBoolean("mayfly");
            this.isCreativeMode = lvt_2_1_.getBoolean("instabuild");

            if (lvt_2_1_.hasKey("flySpeed", 99))
            {
                this.flySpeed = lvt_2_1_.getFloat("flySpeed");
                this.walkSpeed = lvt_2_1_.getFloat("walkSpeed");
            }

            if (lvt_2_1_.hasKey("mayBuild", 1))
            {
                this.allowEdit = lvt_2_1_.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed()
    {
        return this.flySpeed;
    }

    public void setFlySpeed(float speed)
    {
        this.flySpeed = speed;
    }

    public float getWalkSpeed()
    {
        return this.walkSpeed;
    }

    public void setPlayerWalkSpeed(float speed)
    {
        this.walkSpeed = speed;
    }
}
