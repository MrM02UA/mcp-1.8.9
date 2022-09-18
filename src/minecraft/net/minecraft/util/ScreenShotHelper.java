package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ScreenShotHelper
{
    private static final Logger logger = LogManager.getLogger();
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /** A buffer to hold pixel values returned by OpenGL. */
    private static IntBuffer pixelBuffer;

    /**
     * The built-up array that contains all the pixel values returned by OpenGL.
     */
    private static int[] pixelValues;

    /**
     * Saves a screenshot in the game directory with a time-stamped filename.  Args: gameDirectory,
     * requestedWidthInPixels, requestedHeightInPixels, frameBuffer
     */
    public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer)
    {
        return saveScreenshot(gameDirectory, (String)null, width, height, buffer);
    }

    /**
     * Saves a screenshot in the game directory with the given file name (or null to generate a time-stamped name).
     * Args: gameDirectory, fileName, requestedWidthInPixels, requestedHeightInPixels, frameBuffer
     */
    public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer)
    {
        try
        {
            File lvt_5_1_ = new File(gameDirectory, "screenshots");
            lvt_5_1_.mkdir();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                width = buffer.framebufferTextureWidth;
                height = buffer.framebufferTextureHeight;
            }

            int lvt_6_1_ = width * height;

            if (pixelBuffer == null || pixelBuffer.capacity() < lvt_6_1_)
            {
                pixelBuffer = BufferUtils.createIntBuffer(lvt_6_1_);
                pixelValues = new int[lvt_6_1_];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                GlStateManager.bindTexture(buffer.framebufferTexture);
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            }
            else
            {
                GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            }

            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            BufferedImage lvt_7_1_ = null;

            if (OpenGlHelper.isFramebufferEnabled())
            {
                lvt_7_1_ = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
                int lvt_8_1_ = buffer.framebufferTextureHeight - buffer.framebufferHeight;

                for (int lvt_9_1_ = lvt_8_1_; lvt_9_1_ < buffer.framebufferTextureHeight; ++lvt_9_1_)
                {
                    for (int lvt_10_1_ = 0; lvt_10_1_ < buffer.framebufferWidth; ++lvt_10_1_)
                    {
                        lvt_7_1_.setRGB(lvt_10_1_, lvt_9_1_ - lvt_8_1_, pixelValues[lvt_9_1_ * buffer.framebufferTextureWidth + lvt_10_1_]);
                    }
                }
            }
            else
            {
                lvt_7_1_ = new BufferedImage(width, height, 1);
                lvt_7_1_.setRGB(0, 0, width, height, pixelValues, 0, width);
            }

            File lvt_8_2_;

            if (screenshotName == null)
            {
                lvt_8_2_ = getTimestampedPNGFileForDirectory(lvt_5_1_);
            }
            else
            {
                lvt_8_2_ = new File(lvt_5_1_, screenshotName);
            }

            ImageIO.write(lvt_7_1_, "png", lvt_8_2_);
            IChatComponent lvt_9_2_ = new ChatComponentText(lvt_8_2_.getName());
            lvt_9_2_.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, lvt_8_2_.getAbsolutePath()));
            lvt_9_2_.getChatStyle().setUnderlined(Boolean.valueOf(true));
            return new ChatComponentTranslation("screenshot.success", new Object[] {lvt_9_2_});
        }
        catch (Exception var11)
        {
            logger.warn("Couldn\'t save screenshot", var11);
            return new ChatComponentTranslation("screenshot.failure", new Object[] {var11.getMessage()});
        }
    }

    /**
     * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone
     * is not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where
     * the filename was unique when this method was called, but another process or thread created a file at the same
     * path immediately after this method returned.
     */
    private static File getTimestampedPNGFileForDirectory(File gameDirectory)
    {
        String lvt_2_1_ = dateFormat.format(new Date()).toString();
        int lvt_3_1_ = 1;

        while (true)
        {
            File lvt_1_1_ = new File(gameDirectory, lvt_2_1_ + (lvt_3_1_ == 1 ? "" : "_" + lvt_3_1_) + ".png");

            if (!lvt_1_1_.exists())
            {
                return lvt_1_1_;
            }

            ++lvt_3_1_;
        }
    }
}
