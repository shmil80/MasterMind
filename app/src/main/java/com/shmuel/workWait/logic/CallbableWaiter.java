package com.shmuel.workWait.logic;

import java.util.concurrent.Callable;

public abstract class CallbableWaiter<T> implements Callable<T>
{
    private final WorkWaiter workWaiter;

    protected CallbableWaiter(WorkWaiter workWaiter)
    {
        this.workWaiter = workWaiter;
    }

    protected void onError(Exception e)
    {
    }

    protected abstract T doWorkAndReturn() throws Exception;

    @Override
    public T call() throws Exception
    {
        try
        {
            workWaiter.workStarted();
            return doWorkAndReturn();
        }
        catch (Exception e)
        {
            onError(e);
            return null;
        }
        finally
        {
            workWaiter.workFinished();
        }
    }
}

