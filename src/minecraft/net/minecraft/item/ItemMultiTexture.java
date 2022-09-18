package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class ItemMultiTexture extends ItemBlock
{
    protected final Block theBlock;
    protected final Function<ItemStack, String> nameFunction;

    public ItemMultiTexture(Block block, Block block2, Function<ItemStack, String> nameFunction)
    {
        super(block);
        this.theBlock = block2;
        this.nameFunction = nameFunction;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public ItemMultiTexture(Block block, Block block2, final String[] namesByMeta)
    {
        this(block, block2, new Function<ItemStack, String>()
        {
            public String apply(ItemStack p_apply_1_)
            {
                int lvt_2_1_ = p_apply_1_.getMetadata();

                if (lvt_2_1_ < 0 || lvt_2_1_ >= namesByMeta.length)
                {
                    lvt_2_1_ = 0;
                }

                return namesByMeta[lvt_2_1_];
            }
            public Object apply(Object p_apply_1_)
            {
                return this.apply((ItemStack)p_apply_1_);
            }
        });
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName() + "." + (String)this.nameFunction.apply(stack);
    }
}
