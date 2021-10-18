package com.shmuel.mastermind;

import com.shmuel.workWait.logic.RunnableWaiter;
import com.shmuel.workWait.logic.WorkWaiter;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.interfaces.IPlayerGuesser;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GuesserThread implements Closeable
{
    private final IPlayerGuesser playerGuesser;
    private Option nextGuess;
    private final WorkWaiter workWaiter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Set<Future<?>> tasks = new HashSet<>();

    public void cancel()
    {
        if (playerGuesser != null)
        {
            playerGuesser.cancel();
        }
        for (Future<?> task : tasks)
        {
            if (!task.isDone())
                task.cancel(true);
        }
        tasks.clear();
        workWaiter.interrupt();
    }

    public GuesserThread(WorkWaiter workWaiter,final IPlayerGuesser playerGuesser, final boolean firstTime)
    {
        this.playerGuesser = playerGuesser;
        this.workWaiter = workWaiter;
        Future<?> task = executor.submit(new RunnableWaiter(workWaiter)
        {
            @Override
            protected void doWork() throws IOException
            {
                if (firstTime)
                {
                    playerGuesser.startGame();
                }
                nextGuess = playerGuesser.guessSmart(firstTime);
            }

            @Override
            protected void onError(Exception e)
            {
                nextGuess = null;
                e.printStackTrace();
            }
        });
        tasks.add(task);
    }

    public void newResult(final OptionResult result)
    {
        executor.submit(new RunnableWaiter(workWaiter)
        {
            @Override
            protected void doWork() throws Exception
            {
                playerGuesser.calcuateAnswer(result);
                nextGuess = playerGuesser.guessSmart(false);
            }

            @Override
            protected void onError(Exception e)
            {
                nextGuess = null;
                e.printStackTrace();
            }
        });
    }

    public Option getNextGuess()
    {
        if (!workWaiter.waitForWork())
            return null;

        if (nextGuess == null)
            return null;

        return nextGuess.clone();
    }

    @Override
    public void close()
    {
        cancel();
    }
}
