package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT
{
    private static final Logger logger = LogManager.getLogger();
    private static final Pattern field_179273_b = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

    public static NBTTagCompound getTagFromJson(String jsonString) throws NBTException
    {
        jsonString = jsonString.trim();

        if (!jsonString.startsWith("{"))
        {
            throw new NBTException("Invalid tag encountered, expected \'{\' as first char.");
        }
        else if (func_150310_b(jsonString) != 1)
        {
            throw new NBTException("Encountered multiple top tags, only one expected");
        }
        else
        {
            return (NBTTagCompound)func_150316_a("tag", jsonString).parse();
        }
    }

    static int func_150310_b(String p_150310_0_) throws NBTException
    {
        int lvt_1_1_ = 0;
        boolean lvt_2_1_ = false;
        Stack<Character> lvt_3_1_ = new Stack();

        for (int lvt_4_1_ = 0; lvt_4_1_ < p_150310_0_.length(); ++lvt_4_1_)
        {
            char lvt_5_1_ = p_150310_0_.charAt(lvt_4_1_);

            if (lvt_5_1_ == 34)
            {
                if (func_179271_b(p_150310_0_, lvt_4_1_))
                {
                    if (!lvt_2_1_)
                    {
                        throw new NBTException("Illegal use of \\\": " + p_150310_0_);
                    }
                }
                else
                {
                    lvt_2_1_ = !lvt_2_1_;
                }
            }
            else if (!lvt_2_1_)
            {
                if (lvt_5_1_ != 123 && lvt_5_1_ != 91)
                {
                    if (lvt_5_1_ == 125 && (lvt_3_1_.isEmpty() || ((Character)lvt_3_1_.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + p_150310_0_);
                    }

                    if (lvt_5_1_ == 93 && (lvt_3_1_.isEmpty() || ((Character)lvt_3_1_.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + p_150310_0_);
                    }
                }
                else
                {
                    if (lvt_3_1_.isEmpty())
                    {
                        ++lvt_1_1_;
                    }

                    lvt_3_1_.push(Character.valueOf(lvt_5_1_));
                }
            }
        }

        if (lvt_2_1_)
        {
            throw new NBTException("Unbalanced quotation: " + p_150310_0_);
        }
        else if (!lvt_3_1_.isEmpty())
        {
            throw new NBTException("Unbalanced brackets: " + p_150310_0_);
        }
        else
        {
            if (lvt_1_1_ == 0 && !p_150310_0_.isEmpty())
            {
                lvt_1_1_ = 1;
            }

            return lvt_1_1_;
        }
    }

    static JsonToNBT.Any func_179272_a(String... p_179272_0_) throws NBTException
    {
        return func_150316_a(p_179272_0_[0], p_179272_0_[1]);
    }

    static JsonToNBT.Any func_150316_a(String p_150316_0_, String p_150316_1_) throws NBTException
    {
        p_150316_1_ = p_150316_1_.trim();

        if (p_150316_1_.startsWith("{"))
        {
            p_150316_1_ = p_150316_1_.substring(1, p_150316_1_.length() - 1);
            JsonToNBT.Compound lvt_2_1_;
            String lvt_3_1_;

            for (lvt_2_1_ = new JsonToNBT.Compound(p_150316_0_); p_150316_1_.length() > 0; p_150316_1_ = p_150316_1_.substring(lvt_3_1_.length() + 1))
            {
                lvt_3_1_ = func_150314_a(p_150316_1_, true);

                if (lvt_3_1_.length() > 0)
                {
                    boolean lvt_4_1_ = false;
                    lvt_2_1_.field_150491_b.add(func_179270_a(lvt_3_1_, lvt_4_1_));
                }

                if (p_150316_1_.length() < lvt_3_1_.length() + 1)
                {
                    break;
                }

                char lvt_4_2_ = p_150316_1_.charAt(lvt_3_1_.length());

                if (lvt_4_2_ != 44 && lvt_4_2_ != 123 && lvt_4_2_ != 125 && lvt_4_2_ != 91 && lvt_4_2_ != 93)
                {
                    throw new NBTException("Unexpected token \'" + lvt_4_2_ + "\' at: " + p_150316_1_.substring(lvt_3_1_.length()));
                }
            }

            return lvt_2_1_;
        }
        else if (p_150316_1_.startsWith("[") && !field_179273_b.matcher(p_150316_1_).matches())
        {
            p_150316_1_ = p_150316_1_.substring(1, p_150316_1_.length() - 1);
            JsonToNBT.List lvt_2_2_;
            String lvt_3_2_;

            for (lvt_2_2_ = new JsonToNBT.List(p_150316_0_); p_150316_1_.length() > 0; p_150316_1_ = p_150316_1_.substring(lvt_3_2_.length() + 1))
            {
                lvt_3_2_ = func_150314_a(p_150316_1_, false);

                if (lvt_3_2_.length() > 0)
                {
                    boolean lvt_4_3_ = true;
                    lvt_2_2_.field_150492_b.add(func_179270_a(lvt_3_2_, lvt_4_3_));
                }

                if (p_150316_1_.length() < lvt_3_2_.length() + 1)
                {
                    break;
                }

                char lvt_4_4_ = p_150316_1_.charAt(lvt_3_2_.length());

                if (lvt_4_4_ != 44 && lvt_4_4_ != 123 && lvt_4_4_ != 125 && lvt_4_4_ != 91 && lvt_4_4_ != 93)
                {
                    throw new NBTException("Unexpected token \'" + lvt_4_4_ + "\' at: " + p_150316_1_.substring(lvt_3_2_.length()));
                }
            }

            return lvt_2_2_;
        }
        else
        {
            return new JsonToNBT.Primitive(p_150316_0_, p_150316_1_);
        }
    }

    private static JsonToNBT.Any func_179270_a(String p_179270_0_, boolean p_179270_1_) throws NBTException
    {
        String lvt_2_1_ = func_150313_b(p_179270_0_, p_179270_1_);
        String lvt_3_1_ = func_150311_c(p_179270_0_, p_179270_1_);
        return func_179272_a(new String[] {lvt_2_1_, lvt_3_1_});
    }

    private static String func_150314_a(String p_150314_0_, boolean p_150314_1_) throws NBTException
    {
        int lvt_2_1_ = func_150312_a(p_150314_0_, ':');
        int lvt_3_1_ = func_150312_a(p_150314_0_, ',');

        if (p_150314_1_)
        {
            if (lvt_2_1_ == -1)
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150314_0_);
            }

            if (lvt_3_1_ != -1 && lvt_3_1_ < lvt_2_1_)
            {
                throw new NBTException("Name error at: " + p_150314_0_);
            }
        }
        else if (lvt_2_1_ == -1 || lvt_2_1_ > lvt_3_1_)
        {
            lvt_2_1_ = -1;
        }

        return func_179269_a(p_150314_0_, lvt_2_1_);
    }

    private static String func_179269_a(String p_179269_0_, int p_179269_1_) throws NBTException
    {
        Stack<Character> lvt_2_1_ = new Stack();
        int lvt_3_1_ = p_179269_1_ + 1;
        boolean lvt_4_1_ = false;
        boolean lvt_5_1_ = false;
        boolean lvt_6_1_ = false;

        for (int lvt_7_1_ = 0; lvt_3_1_ < p_179269_0_.length(); ++lvt_3_1_)
        {
            char lvt_8_1_ = p_179269_0_.charAt(lvt_3_1_);

            if (lvt_8_1_ == 34)
            {
                if (func_179271_b(p_179269_0_, lvt_3_1_))
                {
                    if (!lvt_4_1_)
                    {
                        throw new NBTException("Illegal use of \\\": " + p_179269_0_);
                    }
                }
                else
                {
                    lvt_4_1_ = !lvt_4_1_;

                    if (lvt_4_1_ && !lvt_6_1_)
                    {
                        lvt_5_1_ = true;
                    }

                    if (!lvt_4_1_)
                    {
                        lvt_7_1_ = lvt_3_1_;
                    }
                }
            }
            else if (!lvt_4_1_)
            {
                if (lvt_8_1_ != 123 && lvt_8_1_ != 91)
                {
                    if (lvt_8_1_ == 125 && (lvt_2_1_.isEmpty() || ((Character)lvt_2_1_.pop()).charValue() != 123))
                    {
                        throw new NBTException("Unbalanced curly brackets {}: " + p_179269_0_);
                    }

                    if (lvt_8_1_ == 93 && (lvt_2_1_.isEmpty() || ((Character)lvt_2_1_.pop()).charValue() != 91))
                    {
                        throw new NBTException("Unbalanced square brackets []: " + p_179269_0_);
                    }

                    if (lvt_8_1_ == 44 && lvt_2_1_.isEmpty())
                    {
                        return p_179269_0_.substring(0, lvt_3_1_);
                    }
                }
                else
                {
                    lvt_2_1_.push(Character.valueOf(lvt_8_1_));
                }
            }

            if (!Character.isWhitespace(lvt_8_1_))
            {
                if (!lvt_4_1_ && lvt_5_1_ && lvt_7_1_ != lvt_3_1_)
                {
                    return p_179269_0_.substring(0, lvt_7_1_ + 1);
                }

                lvt_6_1_ = true;
            }
        }

        return p_179269_0_.substring(0, lvt_3_1_);
    }

    private static String func_150313_b(String p_150313_0_, boolean p_150313_1_) throws NBTException
    {
        if (p_150313_1_)
        {
            p_150313_0_ = p_150313_0_.trim();

            if (p_150313_0_.startsWith("{") || p_150313_0_.startsWith("["))
            {
                return "";
            }
        }

        int lvt_2_1_ = func_150312_a(p_150313_0_, ':');

        if (lvt_2_1_ == -1)
        {
            if (p_150313_1_)
            {
                return "";
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150313_0_);
            }
        }
        else
        {
            return p_150313_0_.substring(0, lvt_2_1_).trim();
        }
    }

    private static String func_150311_c(String p_150311_0_, boolean p_150311_1_) throws NBTException
    {
        if (p_150311_1_)
        {
            p_150311_0_ = p_150311_0_.trim();

            if (p_150311_0_.startsWith("{") || p_150311_0_.startsWith("["))
            {
                return p_150311_0_;
            }
        }

        int lvt_2_1_ = func_150312_a(p_150311_0_, ':');

        if (lvt_2_1_ == -1)
        {
            if (p_150311_1_)
            {
                return p_150311_0_;
            }
            else
            {
                throw new NBTException("Unable to locate name/value separator for string: " + p_150311_0_);
            }
        }
        else
        {
            return p_150311_0_.substring(lvt_2_1_ + 1).trim();
        }
    }

    private static int func_150312_a(String p_150312_0_, char p_150312_1_)
    {
        int lvt_2_1_ = 0;

        for (boolean lvt_3_1_ = true; lvt_2_1_ < p_150312_0_.length(); ++lvt_2_1_)
        {
            char lvt_4_1_ = p_150312_0_.charAt(lvt_2_1_);

            if (lvt_4_1_ == 34)
            {
                if (!func_179271_b(p_150312_0_, lvt_2_1_))
                {
                    lvt_3_1_ = !lvt_3_1_;
                }
            }
            else if (lvt_3_1_)
            {
                if (lvt_4_1_ == p_150312_1_)
                {
                    return lvt_2_1_;
                }

                if (lvt_4_1_ == 123 || lvt_4_1_ == 91)
                {
                    return -1;
                }
            }
        }

        return -1;
    }

    private static boolean func_179271_b(String p_179271_0_, int p_179271_1_)
    {
        return p_179271_1_ > 0 && p_179271_0_.charAt(p_179271_1_ - 1) == 92 && !func_179271_b(p_179271_0_, p_179271_1_ - 1);
    }

    abstract static class Any
    {
        protected String json;

        public abstract NBTBase parse() throws NBTException;
    }

    static class Compound extends JsonToNBT.Any
    {
        protected java.util.List<JsonToNBT.Any> field_150491_b = Lists.newArrayList();

        public Compound(String p_i45137_1_)
        {
            this.json = p_i45137_1_;
        }

        public NBTBase parse() throws NBTException
        {
            NBTTagCompound lvt_1_1_ = new NBTTagCompound();

            for (JsonToNBT.Any lvt_3_1_ : this.field_150491_b)
            {
                lvt_1_1_.setTag(lvt_3_1_.json, lvt_3_1_.parse());
            }

            return lvt_1_1_;
        }
    }

    static class List extends JsonToNBT.Any
    {
        protected java.util.List<JsonToNBT.Any> field_150492_b = Lists.newArrayList();

        public List(String json)
        {
            this.json = json;
        }

        public NBTBase parse() throws NBTException
        {
            NBTTagList lvt_1_1_ = new NBTTagList();

            for (JsonToNBT.Any lvt_3_1_ : this.field_150492_b)
            {
                lvt_1_1_.appendTag(lvt_3_1_.parse());
            }

            return lvt_1_1_;
        }
    }

    static class Primitive extends JsonToNBT.Any
    {
        private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
        private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
        private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
        private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
        private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
        private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
        private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
        protected String jsonValue;

        public Primitive(String p_i45139_1_, String p_i45139_2_)
        {
            this.json = p_i45139_1_;
            this.jsonValue = p_i45139_2_;
        }

        public NBTBase parse() throws NBTException
        {
            try
            {
                if (DOUBLE.matcher(this.jsonValue).matches())
                {
                    return new NBTTagDouble(Double.parseDouble(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (FLOAT.matcher(this.jsonValue).matches())
                {
                    return new NBTTagFloat(Float.parseFloat(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (BYTE.matcher(this.jsonValue).matches())
                {
                    return new NBTTagByte(Byte.parseByte(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (LONG.matcher(this.jsonValue).matches())
                {
                    return new NBTTagLong(Long.parseLong(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (SHORT.matcher(this.jsonValue).matches())
                {
                    return new NBTTagShort(Short.parseShort(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
                }

                if (INTEGER.matcher(this.jsonValue).matches())
                {
                    return new NBTTagInt(Integer.parseInt(this.jsonValue));
                }

                if (DOUBLE_UNTYPED.matcher(this.jsonValue).matches())
                {
                    return new NBTTagDouble(Double.parseDouble(this.jsonValue));
                }

                if (this.jsonValue.equalsIgnoreCase("true") || this.jsonValue.equalsIgnoreCase("false"))
                {
                    return new NBTTagByte((byte)(Boolean.parseBoolean(this.jsonValue) ? 1 : 0));
                }
            }
            catch (NumberFormatException var6)
            {
                this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                return new NBTTagString(this.jsonValue);
            }

            if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]"))
            {
                String lvt_1_2_ = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                String[] lvt_2_1_ = (String[])Iterables.toArray(SPLITTER.split(lvt_1_2_), String.class);

                try
                {
                    int[] lvt_3_1_ = new int[lvt_2_1_.length];

                    for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_2_1_.length; ++lvt_4_1_)
                    {
                        lvt_3_1_[lvt_4_1_] = Integer.parseInt(lvt_2_1_[lvt_4_1_].trim());
                    }

                    return new NBTTagIntArray(lvt_3_1_);
                }
                catch (NumberFormatException var5)
                {
                    return new NBTTagString(this.jsonValue);
                }
            }
            else
            {
                if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\""))
                {
                    this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
                }

                this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
                StringBuilder lvt_1_3_ = new StringBuilder();

                for (int lvt_2_2_ = 0; lvt_2_2_ < this.jsonValue.length(); ++lvt_2_2_)
                {
                    if (lvt_2_2_ < this.jsonValue.length() - 1 && this.jsonValue.charAt(lvt_2_2_) == 92 && this.jsonValue.charAt(lvt_2_2_ + 1) == 92)
                    {
                        lvt_1_3_.append('\\');
                        ++lvt_2_2_;
                    }
                    else
                    {
                        lvt_1_3_.append(this.jsonValue.charAt(lvt_2_2_));
                    }
                }

                return new NBTTagString(lvt_1_3_.toString());
            }
        }
    }
}
