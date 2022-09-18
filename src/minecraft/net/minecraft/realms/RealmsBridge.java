package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends RealmsScreen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private GuiScreen previousScreen;

    public void switchToRealms(GuiScreen p_switchToRealms_1_)
    {
        this.previousScreen = p_switchToRealms_1_;

        try
        {
            Class<?> lvt_2_1_ = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
            Constructor<?> lvt_3_1_ = lvt_2_1_.getDeclaredConstructor(new Class[] {RealmsScreen.class});
            lvt_3_1_.setAccessible(true);
            Object lvt_4_1_ = lvt_3_1_.newInstance(new Object[] {this});
            Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen)lvt_4_1_).getProxy());
        }
        catch (Exception var5)
        {
            LOGGER.error("Realms module missing", var5);
        }
    }

    public GuiScreenRealmsProxy getNotificationScreen(GuiScreen p_getNotificationScreen_1_)
    {
        try
        {
            this.previousScreen = p_getNotificationScreen_1_;
            Class<?> lvt_2_1_ = Class.forName("com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen");
            Constructor<?> lvt_3_1_ = lvt_2_1_.getDeclaredConstructor(new Class[] {RealmsScreen.class});
            lvt_3_1_.setAccessible(true);
            Object lvt_4_1_ = lvt_3_1_.newInstance(new Object[] {this});
            return ((RealmsScreen)lvt_4_1_).getProxy();
        }
        catch (Exception var5)
        {
            LOGGER.error("Realms module missing", var5);
            return null;
        }
    }

    public void init()
    {
        Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
    }
}
