package com.shmuel.workWait.logic;

public abstract class RunnableWaiter implements Runnable
{
    private final WorkWaiter workWaiter;

    protected RunnableWaiter(WorkWaiter workWaiter)
    {
        this.workWaiter = workWaiter;
    }

    @Override
    public void run()
    {
        try
        {
            workWaiter.workStarted();
            doWork();
        }
        catch (Exception e)
        {
            onError(e);
        }
        finally
        {
            workWaiter.workFinished();
        }
    }

    protected void onError(Exception e){}

    protected abstract void doWork() throws Exception;
}
