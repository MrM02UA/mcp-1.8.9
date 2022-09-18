package net.minecraft.util;

import java.util.Random;

public class EnchantmentNameParts
{
    private static final EnchantmentNameParts instance = new EnchantmentNameParts();
    private Random rand = new Random();
    private String[] namePartsArray = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale ".split(" ");

    public static EnchantmentNameParts getInstance()
    {
        return instance;
    }

    /**
     * Randomly generates a new name built up of 3 or 4 randomly selected words.
     */
    public String generateNewRandomName()
    {
        int lvt_1_1_ = this.rand.nextInt(2) + 3;
        String lvt_2_1_ = "";

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_1_1_; ++lvt_3_1_)
        {
            if (lvt_3_1_ > 0)
            {
                lvt_2_1_ = lvt_2_1_ + " ";
            }

            lvt_2_1_ = lvt_2_1_ + this.namePartsArray[this.rand.nextInt(this.namePartsArray.length)];
        }

        return lvt_2_1_;
    }

    /**
     * Resets the underlying random number generator using a given seed.
     */
    public void reseedRandomGenerator(long seed)
    {
        this.rand.setSeed(seed);
    }
}
