package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener
{
    private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];

    /** Array of width of all the characters in default.png */
    private int[] charWidth = new int[256];

    /** the height in pixels of default text */
    public int FONT_HEIGHT = 9;
    public Random fontRandom = new Random();

    /**
     * Array of the start/end column (in upper/lower nibble) for every glyph in the /font directory.
     */
    private byte[] glyphWidth = new byte[65536];

    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
     * drop shadows.
     */
    private int[] colorCode = new int[32];
    private final ResourceLocation locationFontTexture;

    /** The RenderEngine used to load and setup glyph textures. */
    private final TextureManager renderEngine;

    /** Current X coordinate at which to draw the next character. */
    private float posX;

    /** Current Y coordinate at which to draw the next character. */
    private float posY;

    /**
     * If true, strings should be rendered with Unicode fonts instead of the default.png font
     */
    private boolean unicodeFlag;

    /**
     * If true, the Unicode Bidirectional Algorithm should be run before rendering any string.
     */
    private boolean bidiFlag;

    /** Used to specify new red value for the current color. */
    private float red;

    /** Used to specify new blue value for the current color. */
    private float blue;

    /** Used to specify new green value for the current color. */
    private float green;

    /** Used to speify new alpha value for the current color. */
    private float alpha;

    /** Text color of the currently rendering string. */
    private int textColor;

    /** Set if the "k" style (random) is active in currently rendering string */
    private boolean randomStyle;

    /** Set if the "l" style (bold) is active in currently rendering string */
    private boolean boldStyle;

    /** Set if the "o" style (italic) is active in currently rendering string */
    private boolean italicStyle;

    /**
     * Set if the "n" style (underlined) is active in currently rendering string
     */
    private boolean underlineStyle;

    /**
     * Set if the "m" style (strikethrough) is active in currently rendering string
     */
    private boolean strikethroughStyle;

    public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode)
    {
        this.locationFontTexture = location;
        this.renderEngine = textureManagerIn;
        this.unicodeFlag = unicode;
        textureManagerIn.bindTexture(this.locationFontTexture);

        for (int lvt_5_1_ = 0; lvt_5_1_ < 32; ++lvt_5_1_)
        {
            int lvt_6_1_ = (lvt_5_1_ >> 3 & 1) * 85;
            int lvt_7_1_ = (lvt_5_1_ >> 2 & 1) * 170 + lvt_6_1_;
            int lvt_8_1_ = (lvt_5_1_ >> 1 & 1) * 170 + lvt_6_1_;
            int lvt_9_1_ = (lvt_5_1_ >> 0 & 1) * 170 + lvt_6_1_;

            if (lvt_5_1_ == 6)
            {
                lvt_7_1_ += 85;
            }

            if (gameSettingsIn.anaglyph)
            {
                int lvt_10_1_ = (lvt_7_1_ * 30 + lvt_8_1_ * 59 + lvt_9_1_ * 11) / 100;
                int lvt_11_1_ = (lvt_7_1_ * 30 + lvt_8_1_ * 70) / 100;
                int lvt_12_1_ = (lvt_7_1_ * 30 + lvt_9_1_ * 70) / 100;
                lvt_7_1_ = lvt_10_1_;
                lvt_8_1_ = lvt_11_1_;
                lvt_9_1_ = lvt_12_1_;
            }

            if (lvt_5_1_ >= 16)
            {
                lvt_7_1_ /= 4;
                lvt_8_1_ /= 4;
                lvt_9_1_ /= 4;
            }

            this.colorCode[lvt_5_1_] = (lvt_7_1_ & 255) << 16 | (lvt_8_1_ & 255) << 8 | lvt_9_1_ & 255;
        }

        this.readGlyphSizes();
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.readFontTexture();
    }

    private void readFontTexture()
    {
        BufferedImage lvt_1_1_;

        try
        {
            lvt_1_1_ = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(this.locationFontTexture).getInputStream());
        }
        catch (IOException var17)
        {
            throw new RuntimeException(var17);
        }

        int lvt_2_2_ = lvt_1_1_.getWidth();
        int lvt_3_1_ = lvt_1_1_.getHeight();
        int[] lvt_4_1_ = new int[lvt_2_2_ * lvt_3_1_];
        lvt_1_1_.getRGB(0, 0, lvt_2_2_, lvt_3_1_, lvt_4_1_, 0, lvt_2_2_);
        int lvt_5_1_ = lvt_3_1_ / 16;
        int lvt_6_1_ = lvt_2_2_ / 16;
        int lvt_7_1_ = 1;
        float lvt_8_1_ = 8.0F / (float)lvt_6_1_;

        for (int lvt_9_1_ = 0; lvt_9_1_ < 256; ++lvt_9_1_)
        {
            int lvt_10_1_ = lvt_9_1_ % 16;
            int lvt_11_1_ = lvt_9_1_ / 16;

            if (lvt_9_1_ == 32)
            {
                this.charWidth[lvt_9_1_] = 3 + lvt_7_1_;
            }

            int lvt_12_1_;

            for (lvt_12_1_ = lvt_6_1_ - 1; lvt_12_1_ >= 0; --lvt_12_1_)
            {
                int lvt_13_1_ = lvt_10_1_ * lvt_6_1_ + lvt_12_1_;
                boolean lvt_14_1_ = true;

                for (int lvt_15_1_ = 0; lvt_15_1_ < lvt_5_1_ && lvt_14_1_; ++lvt_15_1_)
                {
                    int lvt_16_1_ = (lvt_11_1_ * lvt_6_1_ + lvt_15_1_) * lvt_2_2_;

                    if ((lvt_4_1_[lvt_13_1_ + lvt_16_1_] >> 24 & 255) != 0)
                    {
                        lvt_14_1_ = false;
                    }
                }

                if (!lvt_14_1_)
                {
                    break;
                }
            }

            ++lvt_12_1_;
            this.charWidth[lvt_9_1_] = (int)(0.5D + (double)((float)lvt_12_1_ * lvt_8_1_)) + lvt_7_1_;
        }
    }

    private void readGlyphSizes()
    {
        InputStream lvt_1_1_ = null;

        try
        {
            lvt_1_1_ = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
            lvt_1_1_.read(this.glyphWidth);
        }
        catch (IOException var6)
        {
            throw new RuntimeException(var6);
        }
        finally
        {
            IOUtils.closeQuietly(lvt_1_1_);
        }
    }

    /**
     * Render the given char
     */
    private float renderChar(char ch, boolean italic)
    {
        if (ch == 32)
        {
            return 4.0F;
        }
        else
        {
            int lvt_3_1_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch);
            return lvt_3_1_ != -1 && !this.unicodeFlag ? this.renderDefaultChar(lvt_3_1_, italic) : this.renderUnicodeChar(ch, italic);
        }
    }

    /**
     * Render a single character with the default.png font at current (posX,posY) location...
     */
    private float renderDefaultChar(int ch, boolean italic)
    {
        int lvt_3_1_ = ch % 16 * 8;
        int lvt_4_1_ = ch / 16 * 8;
        int lvt_5_1_ = italic ? 1 : 0;
        this.renderEngine.bindTexture(this.locationFontTexture);
        int lvt_6_1_ = this.charWidth[ch];
        float lvt_7_1_ = (float)lvt_6_1_ - 0.01F;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f((float)lvt_3_1_ / 128.0F, (float)lvt_4_1_ / 128.0F);
        GL11.glVertex3f(this.posX + (float)lvt_5_1_, this.posY, 0.0F);
        GL11.glTexCoord2f((float)lvt_3_1_ / 128.0F, ((float)lvt_4_1_ + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX - (float)lvt_5_1_, this.posY + 7.99F, 0.0F);
        GL11.glTexCoord2f(((float)lvt_3_1_ + lvt_7_1_ - 1.0F) / 128.0F, (float)lvt_4_1_ / 128.0F);
        GL11.glVertex3f(this.posX + lvt_7_1_ - 1.0F + (float)lvt_5_1_, this.posY, 0.0F);
        GL11.glTexCoord2f(((float)lvt_3_1_ + lvt_7_1_ - 1.0F) / 128.0F, ((float)lvt_4_1_ + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX + lvt_7_1_ - 1.0F - (float)lvt_5_1_, this.posY + 7.99F, 0.0F);
        GL11.glEnd();
        return (float)lvt_6_1_;
    }

    private ResourceLocation getUnicodePageLocation(int page)
    {
        if (unicodePageLocations[page] == null)
        {
            unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", new Object[] {Integer.valueOf(page)}));
        }

        return unicodePageLocations[page];
    }

    /**
     * Load one of the /font/glyph_XX.png into a new GL texture and store the texture ID in glyphTextureName array.
     */
    private void loadGlyphTexture(int page)
    {
        this.renderEngine.bindTexture(this.getUnicodePageLocation(page));
    }

    /**
     * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files...
     */
    private float renderUnicodeChar(char ch, boolean italic)
    {
        if (this.glyphWidth[ch] == 0)
        {
            return 0.0F;
        }
        else
        {
            int lvt_3_1_ = ch / 256;
            this.loadGlyphTexture(lvt_3_1_);
            int lvt_4_1_ = this.glyphWidth[ch] >>> 4;
            int lvt_5_1_ = this.glyphWidth[ch] & 15;
            float lvt_6_1_ = (float)lvt_4_1_;
            float lvt_7_1_ = (float)(lvt_5_1_ + 1);
            float lvt_8_1_ = (float)(ch % 16 * 16) + lvt_6_1_;
            float lvt_9_1_ = (float)((ch & 255) / 16 * 16);
            float lvt_10_1_ = lvt_7_1_ - lvt_6_1_ - 0.02F;
            float lvt_11_1_ = italic ? 1.0F : 0.0F;
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            GL11.glTexCoord2f(lvt_8_1_ / 256.0F, lvt_9_1_ / 256.0F);
            GL11.glVertex3f(this.posX + lvt_11_1_, this.posY, 0.0F);
            GL11.glTexCoord2f(lvt_8_1_ / 256.0F, (lvt_9_1_ + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX - lvt_11_1_, this.posY + 7.99F, 0.0F);
            GL11.glTexCoord2f((lvt_8_1_ + lvt_10_1_) / 256.0F, lvt_9_1_ / 256.0F);
            GL11.glVertex3f(this.posX + lvt_10_1_ / 2.0F + lvt_11_1_, this.posY, 0.0F);
            GL11.glTexCoord2f((lvt_8_1_ + lvt_10_1_) / 256.0F, (lvt_9_1_ + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX + lvt_10_1_ / 2.0F - lvt_11_1_, this.posY + 7.99F, 0.0F);
            GL11.glEnd();
            return (lvt_7_1_ - lvt_6_1_) / 2.0F + 1.0F;
        }
    }

    /**
     * Draws the specified string with a shadow.
     */
    public int drawStringWithShadow(String text, float x, float y, int color)
    {
        return this.drawString(text, x, y, color, true);
    }

    /**
     * Draws the specified string.
     */
    public int drawString(String text, int x, int y, int color)
    {
        return this.drawString(text, (float)x, (float)y, color, false);
    }

    /**
     * Draws the specified string.
     */
    public int drawString(String text, float x, float y, int color, boolean dropShadow)
    {
        GlStateManager.enableAlpha();
        this.resetStyles();
        int lvt_6_2_;

        if (dropShadow)
        {
            lvt_6_2_ = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
            lvt_6_2_ = Math.max(lvt_6_2_, this.renderString(text, x, y, color, false));
        }
        else
        {
            lvt_6_2_ = this.renderString(text, x, y, color, false);
        }

        return lvt_6_2_;
    }

    /**
     * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
     */
    private String bidiReorder(String text)
    {
        try
        {
            Bidi lvt_2_1_ = new Bidi((new ArabicShaping(8)).shape(text), 127);
            lvt_2_1_.setReorderingMode(0);
            return lvt_2_1_.writeReordered(2);
        }
        catch (ArabicShapingException var3)
        {
            return text;
        }
    }

    /**
     * Reset all style flag fields in the class to false; called at the start of string rendering
     */
    private void resetStyles()
    {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     */
    private void renderStringAtPos(String text, boolean shadow)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < text.length(); ++lvt_3_1_)
        {
            char lvt_4_1_ = text.charAt(lvt_3_1_);

            if (lvt_4_1_ == 167 && lvt_3_1_ + 1 < text.length())
            {
                int lvt_5_1_ = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(lvt_3_1_ + 1));

                if (lvt_5_1_ < 16)
                {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (lvt_5_1_ < 0 || lvt_5_1_ > 15)
                    {
                        lvt_5_1_ = 15;
                    }

                    if (shadow)
                    {
                        lvt_5_1_ += 16;
                    }

                    int lvt_6_1_ = this.colorCode[lvt_5_1_];
                    this.textColor = lvt_6_1_;
                    GlStateManager.color((float)(lvt_6_1_ >> 16) / 255.0F, (float)(lvt_6_1_ >> 8 & 255) / 255.0F, (float)(lvt_6_1_ & 255) / 255.0F, this.alpha);
                }
                else if (lvt_5_1_ == 16)
                {
                    this.randomStyle = true;
                }
                else if (lvt_5_1_ == 17)
                {
                    this.boldStyle = true;
                }
                else if (lvt_5_1_ == 18)
                {
                    this.strikethroughStyle = true;
                }
                else if (lvt_5_1_ == 19)
                {
                    this.underlineStyle = true;
                }
                else if (lvt_5_1_ == 20)
                {
                    this.italicStyle = true;
                }
                else if (lvt_5_1_ == 21)
                {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    GlStateManager.color(this.red, this.blue, this.green, this.alpha);
                }

                ++lvt_3_1_;
            }
            else
            {
                int lvt_5_2_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(lvt_4_1_);

                if (this.randomStyle && lvt_5_2_ != -1)
                {
                    int lvt_6_2_ = this.getCharWidth(lvt_4_1_);
                    char lvt_7_1_;

                    while (true)
                    {
                        lvt_5_2_ = this.fontRandom.nextInt("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".length());
                        lvt_7_1_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".charAt(lvt_5_2_);

                        if (lvt_6_2_ == this.getCharWidth(lvt_7_1_))
                        {
                            break;
                        }
                    }

                    lvt_4_1_ = lvt_7_1_;
                }

                float lvt_6_3_ = this.unicodeFlag ? 0.5F : 1.0F;
                boolean lvt_7_2_ = (lvt_4_1_ == 0 || lvt_5_2_ == -1 || this.unicodeFlag) && shadow;

                if (lvt_7_2_)
                {
                    this.posX -= lvt_6_3_;
                    this.posY -= lvt_6_3_;
                }

                float lvt_8_1_ = this.renderChar(lvt_4_1_, this.italicStyle);

                if (lvt_7_2_)
                {
                    this.posX += lvt_6_3_;
                    this.posY += lvt_6_3_;
                }

                if (this.boldStyle)
                {
                    this.posX += lvt_6_3_;

                    if (lvt_7_2_)
                    {
                        this.posX -= lvt_6_3_;
                        this.posY -= lvt_6_3_;
                    }

                    this.renderChar(lvt_4_1_, this.italicStyle);
                    this.posX -= lvt_6_3_;

                    if (lvt_7_2_)
                    {
                        this.posX += lvt_6_3_;
                        this.posY += lvt_6_3_;
                    }

                    ++lvt_8_1_;
                }

                if (this.strikethroughStyle)
                {
                    Tessellator lvt_9_1_ = Tessellator.getInstance();
                    WorldRenderer lvt_10_1_ = lvt_9_1_.getWorldRenderer();
                    GlStateManager.disableTexture2D();
                    lvt_10_1_.begin(7, DefaultVertexFormats.POSITION);
                    lvt_10_1_.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
                    lvt_10_1_.pos((double)(this.posX + lvt_8_1_), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
                    lvt_10_1_.pos((double)(this.posX + lvt_8_1_), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
                    lvt_10_1_.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
                    lvt_9_1_.draw();
                    GlStateManager.enableTexture2D();
                }

                if (this.underlineStyle)
                {
                    Tessellator lvt_9_2_ = Tessellator.getInstance();
                    WorldRenderer lvt_10_2_ = lvt_9_2_.getWorldRenderer();
                    GlStateManager.disableTexture2D();
                    lvt_10_2_.begin(7, DefaultVertexFormats.POSITION);
                    int lvt_11_1_ = this.underlineStyle ? -1 : 0;
                    lvt_10_2_.pos((double)(this.posX + (float)lvt_11_1_), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
                    lvt_10_2_.pos((double)(this.posX + lvt_8_1_), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
                    lvt_10_2_.pos((double)(this.posX + lvt_8_1_), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
                    lvt_10_2_.pos((double)(this.posX + (float)lvt_11_1_), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
                    lvt_9_2_.draw();
                    GlStateManager.enableTexture2D();
                }

                this.posX += (float)((int)lvt_8_1_);
            }
        }
    }

    /**
     * Render string either left or right aligned depending on bidiFlag
     */
    private int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow)
    {
        if (this.bidiFlag)
        {
            int lvt_7_1_ = this.getStringWidth(this.bidiReorder(text));
            x = x + width - lvt_7_1_;
        }

        return this.renderString(text, (float)x, (float)y, color, dropShadow);
    }

    /**
     * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
     */
    private int renderString(String text, float x, float y, int color, boolean dropShadow)
    {
        if (text == null)
        {
            return 0;
        }
        else
        {
            if (this.bidiFlag)
            {
                text = this.bidiReorder(text);
            }

            if ((color & -67108864) == 0)
            {
                color |= -16777216;
            }

            if (dropShadow)
            {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            this.red = (float)(color >> 16 & 255) / 255.0F;
            this.blue = (float)(color >> 8 & 255) / 255.0F;
            this.green = (float)(color & 255) / 255.0F;
            this.alpha = (float)(color >> 24 & 255) / 255.0F;
            GlStateManager.color(this.red, this.blue, this.green, this.alpha);
            this.posX = x;
            this.posY = y;
            this.renderStringAtPos(text, dropShadow);
            return (int)this.posX;
        }
    }

    /**
     * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
     */
    public int getStringWidth(String text)
    {
        if (text == null)
        {
            return 0;
        }
        else
        {
            int lvt_2_1_ = 0;
            boolean lvt_3_1_ = false;

            for (int lvt_4_1_ = 0; lvt_4_1_ < text.length(); ++lvt_4_1_)
            {
                char lvt_5_1_ = text.charAt(lvt_4_1_);
                int lvt_6_1_ = this.getCharWidth(lvt_5_1_);

                if (lvt_6_1_ < 0 && lvt_4_1_ < text.length() - 1)
                {
                    ++lvt_4_1_;
                    lvt_5_1_ = text.charAt(lvt_4_1_);

                    if (lvt_5_1_ != 108 && lvt_5_1_ != 76)
                    {
                        if (lvt_5_1_ == 114 || lvt_5_1_ == 82)
                        {
                            lvt_3_1_ = false;
                        }
                    }
                    else
                    {
                        lvt_3_1_ = true;
                    }

                    lvt_6_1_ = 0;
                }

                lvt_2_1_ += lvt_6_1_;

                if (lvt_3_1_ && lvt_6_1_ > 0)
                {
                    ++lvt_2_1_;
                }
            }

            return lvt_2_1_;
        }
    }

    /**
     * Returns the width of this character as rendered.
     */
    public int getCharWidth(char character)
    {
        if (character == 167)
        {
            return -1;
        }
        else if (character == 32)
        {
            return 4;
        }
        else
        {
            int lvt_2_1_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(character);

            if (character > 0 && lvt_2_1_ != -1 && !this.unicodeFlag)
            {
                return this.charWidth[lvt_2_1_];
            }
            else if (this.glyphWidth[character] != 0)
            {
                int lvt_3_1_ = this.glyphWidth[character] >>> 4;
                int lvt_4_1_ = this.glyphWidth[character] & 15;

                if (lvt_4_1_ > 7)
                {
                    lvt_4_1_ = 15;
                    lvt_3_1_ = 0;
                }

                ++lvt_4_1_;
                return (lvt_4_1_ - lvt_3_1_) / 2 + 1;
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Trims a string to fit a specified Width.
     */
    public String trimStringToWidth(String text, int width)
    {
        return this.trimStringToWidth(text, width, false);
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    public String trimStringToWidth(String text, int width, boolean reverse)
    {
        StringBuilder lvt_4_1_ = new StringBuilder();
        int lvt_5_1_ = 0;
        int lvt_6_1_ = reverse ? text.length() - 1 : 0;
        int lvt_7_1_ = reverse ? -1 : 1;
        boolean lvt_8_1_ = false;
        boolean lvt_9_1_ = false;

        for (int lvt_10_1_ = lvt_6_1_; lvt_10_1_ >= 0 && lvt_10_1_ < text.length() && lvt_5_1_ < width; lvt_10_1_ += lvt_7_1_)
        {
            char lvt_11_1_ = text.charAt(lvt_10_1_);
            int lvt_12_1_ = this.getCharWidth(lvt_11_1_);

            if (lvt_8_1_)
            {
                lvt_8_1_ = false;

                if (lvt_11_1_ != 108 && lvt_11_1_ != 76)
                {
                    if (lvt_11_1_ == 114 || lvt_11_1_ == 82)
                    {
                        lvt_9_1_ = false;
                    }
                }
                else
                {
                    lvt_9_1_ = true;
                }
            }
            else if (lvt_12_1_ < 0)
            {
                lvt_8_1_ = true;
            }
            else
            {
                lvt_5_1_ += lvt_12_1_;

                if (lvt_9_1_)
                {
                    ++lvt_5_1_;
                }
            }

            if (lvt_5_1_ > width)
            {
                break;
            }

            if (reverse)
            {
                lvt_4_1_.insert(0, lvt_11_1_);
            }
            else
            {
                lvt_4_1_.append(lvt_11_1_);
            }
        }

        return lvt_4_1_.toString();
    }

    /**
     * Remove all newline characters from the end of the string
     */
    private String trimStringNewline(String text)
    {
        while (text != null && text.endsWith("\n"))
        {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k)
     */
    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor)
    {
        this.resetStyles();
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x, y, wrapWidth, false);
    }

    /**
     * Perform actual work of rendering a multi-line string with wordwrap and with darker drop shadow color if flag is
     * set
     */
    private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow)
    {
        for (String lvt_8_1_ : this.listFormattedStringToWidth(str, wrapWidth))
        {
            this.renderStringAligned(lvt_8_1_, x, y, wrapWidth, this.textColor, addShadow);
            y += this.FONT_HEIGHT;
        }
    }

    /**
     * Returns the width of the wordwrapped String (maximum length is parameter k)
     *  
     * @param str The string to split
     * @param maxLength The maximum length of a word
     */
    public int splitStringWidth(String str, int maxLength)
    {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
    }

    /**
     * Set unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
     * font.
     */
    public void setUnicodeFlag(boolean unicodeFlagIn)
    {
        this.unicodeFlag = unicodeFlagIn;
    }

    /**
     * Get unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
     * font.
     */
    public boolean getUnicodeFlag()
    {
        return this.unicodeFlag;
    }

    /**
     * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
     */
    public void setBidiFlag(boolean bidiFlagIn)
    {
        this.bidiFlag = bidiFlagIn;
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth)
    {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     */
    String wrapFormattedStringToWidth(String str, int wrapWidth)
    {
        int lvt_3_1_ = this.sizeStringToWidth(str, wrapWidth);

        if (str.length() <= lvt_3_1_)
        {
            return str;
        }
        else
        {
            String lvt_4_1_ = str.substring(0, lvt_3_1_);
            char lvt_5_1_ = str.charAt(lvt_3_1_);
            boolean lvt_6_1_ = lvt_5_1_ == 32 || lvt_5_1_ == 10;
            String lvt_7_1_ = getFormatFromString(lvt_4_1_) + str.substring(lvt_3_1_ + (lvt_6_1_ ? 1 : 0));
            return lvt_4_1_ + "\n" + this.wrapFormattedStringToWidth(lvt_7_1_, wrapWidth);
        }
    }

    /**
     * Determines how many characters from the string will fit into the specified width.
     */
    private int sizeStringToWidth(String str, int wrapWidth)
    {
        int lvt_3_1_ = str.length();
        int lvt_4_1_ = 0;
        int lvt_5_1_ = 0;
        int lvt_6_1_ = -1;

        for (boolean lvt_7_1_ = false; lvt_5_1_ < lvt_3_1_; ++lvt_5_1_)
        {
            char lvt_8_1_ = str.charAt(lvt_5_1_);

            switch (lvt_8_1_)
            {
                case '\n':
                    --lvt_5_1_;
                    break;

                case ' ':
                    lvt_6_1_ = lvt_5_1_;

                default:
                    lvt_4_1_ += this.getCharWidth(lvt_8_1_);

                    if (lvt_7_1_)
                    {
                        ++lvt_4_1_;
                    }

                    break;

                case '\u00a7':
                    if (lvt_5_1_ < lvt_3_1_ - 1)
                    {
                        ++lvt_5_1_;
                        char lvt_9_1_ = str.charAt(lvt_5_1_);

                        if (lvt_9_1_ != 108 && lvt_9_1_ != 76)
                        {
                            if (lvt_9_1_ == 114 || lvt_9_1_ == 82 || isFormatColor(lvt_9_1_))
                            {
                                lvt_7_1_ = false;
                            }
                        }
                        else
                        {
                            lvt_7_1_ = true;
                        }
                    }
            }

            if (lvt_8_1_ == 10)
            {
                ++lvt_5_1_;
                lvt_6_1_ = lvt_5_1_;
                break;
            }

            if (lvt_4_1_ > wrapWidth)
            {
                break;
            }
        }

        return lvt_5_1_ != lvt_3_1_ && lvt_6_1_ != -1 && lvt_6_1_ < lvt_5_1_ ? lvt_6_1_ : lvt_5_1_;
    }

    /**
     * Checks if the char code is a hexadecimal character, used to set colour.
     */
    private static boolean isFormatColor(char colorChar)
    {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
    }

    /**
     * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
     */
    private static boolean isFormatSpecial(char formatChar)
    {
        return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114 || formatChar == 82;
    }

    /**
     * Digests a string for nonprinting formatting characters then returns a string containing only that formatting.
     */
    public static String getFormatFromString(String text)
    {
        String lvt_1_1_ = "";
        int lvt_2_1_ = -1;
        int lvt_3_1_ = text.length();

        while ((lvt_2_1_ = text.indexOf(167, lvt_2_1_ + 1)) != -1)
        {
            if (lvt_2_1_ < lvt_3_1_ - 1)
            {
                char lvt_4_1_ = text.charAt(lvt_2_1_ + 1);

                if (isFormatColor(lvt_4_1_))
                {
                    lvt_1_1_ = "\u00a7" + lvt_4_1_;
                }
                else if (isFormatSpecial(lvt_4_1_))
                {
                    lvt_1_1_ = lvt_1_1_ + "\u00a7" + lvt_4_1_;
                }
            }
        }

        return lvt_1_1_;
    }

    /**
     * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
     */
    public boolean getBidiFlag()
    {
        return this.bidiFlag;
    }

    public int getColorCode(char character)
    {
        return this.colorCode["0123456789abcdef".indexOf(character)];
    }
}
