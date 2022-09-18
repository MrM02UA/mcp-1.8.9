package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil
{
    public static final ListeningExecutorService field_180193_a = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Downloader %d").build()));

    /** The number of download threads that we have started so far. */
    private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();

    /**
     * Builds an encoded HTTP POST content string from a string map
     */
    public static String buildPostString(Map<String, Object> data)
    {
        StringBuilder lvt_1_1_ = new StringBuilder();

        for (Entry<String, Object> lvt_3_1_ : data.entrySet())
        {
            if (lvt_1_1_.length() > 0)
            {
                lvt_1_1_.append('&');
            }

            try
            {
                lvt_1_1_.append(URLEncoder.encode((String)lvt_3_1_.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException var6)
            {
                var6.printStackTrace();
            }

            if (lvt_3_1_.getValue() != null)
            {
                lvt_1_1_.append('=');

                try
                {
                    lvt_1_1_.append(URLEncoder.encode(lvt_3_1_.getValue().toString(), "UTF-8"));
                }
                catch (UnsupportedEncodingException var5)
                {
                    var5.printStackTrace();
                }
            }
        }

        return lvt_1_1_.toString();
    }

    /**
     * Sends a POST to the given URL using the map as the POST args
     */
    public static String postMap(URL url, Map<String, Object> data, boolean skipLoggingErrors)
    {
        return post(url, buildPostString(data), skipLoggingErrors);
    }

    /**
     * Sends a POST to the given URL
     */
    private static String post(URL url, String content, boolean skipLoggingErrors)
    {
        try
        {
            Proxy lvt_3_1_ = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();

            if (lvt_3_1_ == null)
            {
                lvt_3_1_ = Proxy.NO_PROXY;
            }

            HttpURLConnection lvt_4_1_ = (HttpURLConnection)url.openConnection(lvt_3_1_);
            lvt_4_1_.setRequestMethod("POST");
            lvt_4_1_.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            lvt_4_1_.setRequestProperty("Content-Length", "" + content.getBytes().length);
            lvt_4_1_.setRequestProperty("Content-Language", "en-US");
            lvt_4_1_.setUseCaches(false);
            lvt_4_1_.setDoInput(true);
            lvt_4_1_.setDoOutput(true);
            DataOutputStream lvt_5_1_ = new DataOutputStream(lvt_4_1_.getOutputStream());
            lvt_5_1_.writeBytes(content);
            lvt_5_1_.flush();
            lvt_5_1_.close();
            BufferedReader lvt_6_1_ = new BufferedReader(new InputStreamReader(lvt_4_1_.getInputStream()));
            StringBuffer lvt_8_1_ = new StringBuffer();
            String lvt_7_1_;

            while ((lvt_7_1_ = lvt_6_1_.readLine()) != null)
            {
                lvt_8_1_.append(lvt_7_1_);
                lvt_8_1_.append('\r');
            }

            lvt_6_1_.close();
            return lvt_8_1_.toString();
        }
        catch (Exception var9)
        {
            if (!skipLoggingErrors)
            {
                logger.error("Could not post to " + url, var9);
            }

            return "";
        }
    }

    public static ListenableFuture<Object> downloadResourcePack(final File saveFile, final String packUrl, final Map<String, String> p_180192_2_, final int maxSize, final IProgressUpdate p_180192_4_, final Proxy p_180192_5_)
    {
        ListenableFuture<?> lvt_6_1_ = field_180193_a.submit(new Runnable()
        {
            public void run()
            {
                HttpURLConnection lvt_1_1_ = null;
                InputStream lvt_2_1_ = null;
                OutputStream lvt_3_1_ = null;

                if (p_180192_4_ != null)
                {
                    p_180192_4_.resetProgressAndMessage("Downloading Resource Pack");
                    p_180192_4_.displayLoadingString("Making Request...");
                }

                try
                {
                    try
                    {
                        byte[] lvt_4_1_ = new byte[4096];
                        URL lvt_5_1_ = new URL(packUrl);
                        lvt_1_1_ = (HttpURLConnection)lvt_5_1_.openConnection(p_180192_5_);
                        float lvt_6_1_ = 0.0F;
                        float lvt_7_1_ = (float)p_180192_2_.entrySet().size();

                        for (Entry<String, String> lvt_9_1_ : p_180192_2_.entrySet())
                        {
                            lvt_1_1_.setRequestProperty((String)lvt_9_1_.getKey(), (String)lvt_9_1_.getValue());

                            if (p_180192_4_ != null)
                            {
                                p_180192_4_.setLoadingProgress((int)(++lvt_6_1_ / lvt_7_1_ * 100.0F));
                            }
                        }

                        lvt_2_1_ = lvt_1_1_.getInputStream();
                        lvt_7_1_ = (float)lvt_1_1_.getContentLength();
                        int lvt_8_2_ = lvt_1_1_.getContentLength();

                        if (p_180192_4_ != null)
                        {
                            p_180192_4_.displayLoadingString(String.format("Downloading file (%.2f MB)...", new Object[] {Float.valueOf(lvt_7_1_ / 1000.0F / 1000.0F)}));
                        }

                        if (saveFile.exists())
                        {
                            long lvt_9_2_ = saveFile.length();

                            if (lvt_9_2_ == (long)lvt_8_2_)
                            {
                                if (p_180192_4_ != null)
                                {
                                    p_180192_4_.setDoneWorking();
                                }

                                return;
                            }

                            HttpUtil.logger.warn("Deleting " + saveFile + " as it does not match what we currently have (" + lvt_8_2_ + " vs our " + lvt_9_2_ + ").");
                            FileUtils.deleteQuietly(saveFile);
                        }
                        else if (saveFile.getParentFile() != null)
                        {
                            saveFile.getParentFile().mkdirs();
                        }

                        lvt_3_1_ = new DataOutputStream(new FileOutputStream(saveFile));

                        if (maxSize > 0 && lvt_7_1_ > (float)maxSize)
                        {
                            if (p_180192_4_ != null)
                            {
                                p_180192_4_.setDoneWorking();
                            }

                            throw new IOException("Filesize is bigger than maximum allowed (file is " + lvt_6_1_ + ", limit is " + maxSize + ")");
                        }

                        int lvt_9_3_ = 0;

                        while ((lvt_9_3_ = lvt_2_1_.read(lvt_4_1_)) >= 0)
                        {
                            lvt_6_1_ += (float)lvt_9_3_;

                            if (p_180192_4_ != null)
                            {
                                p_180192_4_.setLoadingProgress((int)(lvt_6_1_ / lvt_7_1_ * 100.0F));
                            }

                            if (maxSize > 0 && lvt_6_1_ > (float)maxSize)
                            {
                                if (p_180192_4_ != null)
                                {
                                    p_180192_4_.setDoneWorking();
                                }

                                throw new IOException("Filesize was bigger than maximum allowed (got >= " + lvt_6_1_ + ", limit was " + maxSize + ")");
                            }

                            if (Thread.interrupted())
                            {
                                HttpUtil.logger.error("INTERRUPTED");

                                if (p_180192_4_ != null)
                                {
                                    p_180192_4_.setDoneWorking();
                                }

                                return;
                            }

                            lvt_3_1_.write(lvt_4_1_, 0, lvt_9_3_);
                        }

                        if (p_180192_4_ != null)
                        {
                            p_180192_4_.setDoneWorking();
                            return;
                        }
                    }
                    catch (Throwable var16)
                    {
                        var16.printStackTrace();

                        if (lvt_1_1_ != null)
                        {
                            InputStream lvt_5_2_ = lvt_1_1_.getErrorStream();

                            try
                            {
                                HttpUtil.logger.error(IOUtils.toString(lvt_5_2_));
                            }
                            catch (IOException var15)
                            {
                                var15.printStackTrace();
                            }
                        }

                        if (p_180192_4_ != null)
                        {
                            p_180192_4_.setDoneWorking();
                            return;
                        }
                    }
                }
                finally
                {
                    IOUtils.closeQuietly(lvt_2_1_);
                    IOUtils.closeQuietly(lvt_3_1_);
                }
            }
        });
        return lvt_6_1_;
    }

    public static int getSuitableLanPort() throws IOException
    {
        ServerSocket lvt_0_1_ = null;
        int lvt_1_1_ = -1;

        try
        {
            lvt_0_1_ = new ServerSocket(0);
            lvt_1_1_ = lvt_0_1_.getLocalPort();
        }
        finally
        {
            try
            {
                if (lvt_0_1_ != null)
                {
                    lvt_0_1_.close();
                }
            }
            catch (IOException var8)
            {
                ;
            }
        }

        return lvt_1_1_;
    }

    /**
     * Send a GET request to the given URL.
     */
    public static String get(URL url) throws IOException
    {
        HttpURLConnection lvt_1_1_ = (HttpURLConnection)url.openConnection();
        lvt_1_1_.setRequestMethod("GET");
        BufferedReader lvt_2_1_ = new BufferedReader(new InputStreamReader(lvt_1_1_.getInputStream()));
        StringBuilder lvt_4_1_ = new StringBuilder();
        String lvt_3_1_;

        while ((lvt_3_1_ = lvt_2_1_.readLine()) != null)
        {
            lvt_4_1_.append(lvt_3_1_);
            lvt_4_1_.append('\r');
        }

        lvt_2_1_.close();
        return lvt_4_1_.toString();
    }
}
