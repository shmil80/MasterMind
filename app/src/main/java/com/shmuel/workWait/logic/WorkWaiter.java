package com.shmuel.workWait.logic;

import java.util.HashSet;
import java.util.Set;

public class WorkWaiter
{
    private boolean ready;
    private final Set<Thread> waitingThreads=new HashSet<>();
    private final Object locker = new Object();

    public boolean isReady()
    {
        return ready;
    }

    public void workStarted()
    {
        ready = false;
    }

    public void workFinished()
    {
        ready = true;
        synchronized (locker)
        {
            locker.notify();
        }
    }

    public void interrupt()
    {
        for (Thread waitingThread : waitingThreads)
        {
            System.out.println("-----------interrupting... thread:" + waitingThread.getName());
            waitingThread.interrupt();
        }
        waitingThreads.clear();
    }

    public boolean waitForWork()
    {
        Thread currentThread=Thread.currentThread();
        this.waitingThreads.add(currentThread);
        synchronized (locker)
        {
            try
            {
                while (!ready)
                {
                    System.out.println("-----------start wait. thread:" + currentThread.getName());
                    locker.wait();
                    System.out.println("-----------end wait. thread:" + currentThread.getName());
                    locker.notify();
                    System.out.println("-----------notify. thread:" + currentThread.getName());
                }
            }
            catch (InterruptedException ignored)
            {
                System.out.println("-----------interupted. thread:" + currentThread.getName());
            }
        }
        this.waitingThreads.clear();
        return true;
    }
}
