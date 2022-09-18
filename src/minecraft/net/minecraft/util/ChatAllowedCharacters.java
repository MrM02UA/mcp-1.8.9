package net.minecraft.util;

public class ChatAllowedCharacters
{
    /**
     * Array of the special characters that are allowed in any text drawing of Minecraft.
     */
    public static final char[] allowedCharactersArray = new char[] {'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    public static boolean isAllowedCharacter(char character)
    {
        return character != 167 && character >= 32 && character != 127;
    }

    /**
     * Filter string by only keeping those characters for which isAllowedCharacter() returns true.
     */
    public static String filterAllowedCharacters(String input)
    {
        StringBuilder lvt_1_1_ = new StringBuilder();

        for (char lvt_5_1_ : input.toCharArray())
        {
            if (isAllowedCharacter(lvt_5_1_))
            {
                lvt_1_1_.append(lvt_5_1_);
            }
        }

        return lvt_1_1_.toString();
    }
}
