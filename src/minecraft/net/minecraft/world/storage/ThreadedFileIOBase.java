package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class ThreadedFileIOBase implements Runnable
{
    /** Instance of ThreadedFileIOBase */
    private static final ThreadedFileIOBase threadedIOInstance = new ThreadedFileIOBase();
    private List<IThreadedFileIO> threadedIOQueue = Collections.synchronizedList(Lists.newArrayList());
    private volatile long writeQueuedCounter;
    private volatile long savedIOCounter;
    private volatile boolean isThreadWaiting;

    private ThreadedFileIOBase()
    {
        Thread lvt_1_1_ = new Thread(this, "File IO Thread");
        lvt_1_1_.setPriority(1);
        lvt_1_1_.start();
    }

    /**
     * Retrieves an instance of the threadedFileIOBase.
     */
    public static ThreadedFileIOBase getThreadedIOInstance()
    {
        return threadedIOInstance;
    }

    public void run()
    {
        while (true)
        {
            this.processQueue();
        }
    }

    /**
     * Process the items that are in the queue
     */
    private void processQueue()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.threadedIOQueue.size(); ++lvt_1_1_)
        {
            IThreadedFileIO lvt_2_1_ = (IThreadedFileIO)this.threadedIOQueue.get(lvt_1_1_);
            boolean lvt_3_1_ = lvt_2_1_.writeNextIO();

            if (!lvt_3_1_)
            {
                this.threadedIOQueue.remove(lvt_1_1_--);
                ++this.savedIOCounter;
            }

            try
            {
                Thread.sleep(this.isThreadWaiting ? 0L : 10L);
            }
            catch (InterruptedException var6)
            {
                var6.printStackTrace();
            }
        }

        if (this.threadedIOQueue.isEmpty())
        {
            try
            {
                Thread.sleep(25L);
            }
            catch (InterruptedException var5)
            {
                var5.printStackTrace();
            }
        }
    }

    /**
     * threaded io
     */
    public void queueIO(IThreadedFileIO p_75735_1_)
    {
        if (!this.threadedIOQueue.contains(p_75735_1_))
        {
            ++this.writeQueuedCounter;
            this.threadedIOQueue.add(p_75735_1_);
        }
    }

    public void waitForFinish() throws InterruptedException
    {
        this.isThreadWaiting = true;

        while (this.writeQueuedCounter != this.savedIOCounter)
        {
            Thread.sleep(10L);
        }

        this.isThreadWaiting = false;
    }
}
