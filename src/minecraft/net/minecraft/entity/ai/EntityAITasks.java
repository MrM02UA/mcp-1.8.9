package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks
{
    private static final Logger logger = LogManager.getLogger();
    private List<EntityAITasks.EntityAITaskEntry> taskEntries = Lists.newArrayList();
    private List<EntityAITasks.EntityAITaskEntry> executingTaskEntries = Lists.newArrayList();

    /** Instance of Profiler. */
    private final Profiler theProfiler;
    private int tickCount;
    private int tickRate = 3;

    public EntityAITasks(Profiler profilerIn)
    {
        this.theProfiler = profilerIn;
    }

    /**
     * Add a now AITask. Args : priority, task
     */
    public void addTask(int priority, EntityAIBase task)
    {
        this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(priority, task));
    }

    /**
     * removes the indicated task from the entity's AI tasks.
     */
    public void removeTask(EntityAIBase task)
    {
        Iterator<EntityAITasks.EntityAITaskEntry> lvt_2_1_ = this.taskEntries.iterator();

        while (lvt_2_1_.hasNext())
        {
            EntityAITasks.EntityAITaskEntry lvt_3_1_ = (EntityAITasks.EntityAITaskEntry)lvt_2_1_.next();
            EntityAIBase lvt_4_1_ = lvt_3_1_.action;

            if (lvt_4_1_ == task)
            {
                if (this.executingTaskEntries.contains(lvt_3_1_))
                {
                    lvt_4_1_.resetTask();
                    this.executingTaskEntries.remove(lvt_3_1_);
                }

                lvt_2_1_.remove();
            }
        }
    }

    public void onUpdateTasks()
    {
        this.theProfiler.startSection("goalSetup");

        if (this.tickCount++ % this.tickRate == 0)
        {
            Iterator lvt_1_1_ = this.taskEntries.iterator();
            label38:

            while (true)
            {
                EntityAITasks.EntityAITaskEntry lvt_2_1_;

                while (true)
                {
                    if (!lvt_1_1_.hasNext())
                    {
                        break label38;
                    }

                    lvt_2_1_ = (EntityAITasks.EntityAITaskEntry)lvt_1_1_.next();
                    boolean lvt_3_1_ = this.executingTaskEntries.contains(lvt_2_1_);

                    if (!lvt_3_1_)
                    {
                        break;
                    }

                    if (!this.canUse(lvt_2_1_) || !this.canContinue(lvt_2_1_))
                    {
                        lvt_2_1_.action.resetTask();
                        this.executingTaskEntries.remove(lvt_2_1_);
                        break;
                    }
                }

                if (this.canUse(lvt_2_1_) && lvt_2_1_.action.shouldExecute())
                {
                    lvt_2_1_.action.startExecuting();
                    this.executingTaskEntries.add(lvt_2_1_);
                }
            }
        }
        else
        {
            Iterator<EntityAITasks.EntityAITaskEntry> lvt_1_2_ = this.executingTaskEntries.iterator();

            while (lvt_1_2_.hasNext())
            {
                EntityAITasks.EntityAITaskEntry lvt_2_2_ = (EntityAITasks.EntityAITaskEntry)lvt_1_2_.next();

                if (!this.canContinue(lvt_2_2_))
                {
                    lvt_2_2_.action.resetTask();
                    lvt_1_2_.remove();
                }
            }
        }

        this.theProfiler.endSection();
        this.theProfiler.startSection("goalTick");

        for (EntityAITasks.EntityAITaskEntry lvt_2_3_ : this.executingTaskEntries)
        {
            lvt_2_3_.action.updateTask();
        }

        this.theProfiler.endSection();
    }

    /**
     * Determine if a specific AI Task should continue being executed.
     */
    private boolean canContinue(EntityAITasks.EntityAITaskEntry taskEntry)
    {
        boolean lvt_2_1_ = taskEntry.action.continueExecuting();
        return lvt_2_1_;
    }

    /**
     * Determine if a specific AI Task can be executed, which means that all running higher (= lower int value) priority
     * tasks are compatible with it or all lower priority tasks can be interrupted.
     */
    private boolean canUse(EntityAITasks.EntityAITaskEntry taskEntry)
    {
        for (EntityAITasks.EntityAITaskEntry lvt_3_1_ : this.taskEntries)
        {
            if (lvt_3_1_ != taskEntry)
            {
                if (taskEntry.priority >= lvt_3_1_.priority)
                {
                    if (!this.areTasksCompatible(taskEntry, lvt_3_1_) && this.executingTaskEntries.contains(lvt_3_1_))
                    {
                        return false;
                    }
                }
                else if (!lvt_3_1_.action.isInterruptible() && this.executingTaskEntries.contains(lvt_3_1_))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry taskEntry1, EntityAITasks.EntityAITaskEntry taskEntry2)
    {
        return (taskEntry1.action.getMutexBits() & taskEntry2.action.getMutexBits()) == 0;
    }

    class EntityAITaskEntry
    {
        public EntityAIBase action;
        public int priority;

        public EntityAITaskEntry(int priorityIn, EntityAIBase task)
        {
            this.priority = priorityIn;
            this.action = task;
        }
    }
}
