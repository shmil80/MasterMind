package com.shmuel.mastermind.logic.guessers.guessCheckAllOptions;

import com.shmuel.mastermind.shared.beans.OptionResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AnswerCalcThread
{
    private final PlayerGuesserAllOptions playerGuesserAllOptions;
    private final ExecutorService executorPool = Executors.newCachedThreadPool();


    public AnswerCalcThread(final PlayerGuesserAllOptions playerGuesserAllOptions)
    {
        this.playerGuesserAllOptions = playerGuesserAllOptions;
    }


    public boolean calcuateAnswer(OptionResult result) throws IOException
    {
        List<Callable> tasks = playerGuesserAllOptions.calculateAnswer(result.getOption(), result.getResult());
        switch (tasks.size())
        {
            case 0:
                return false;
            case 1:
                try
                {
                    tasks.get(0).call();
                    break;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
            default:
                List<Future> futures = new ArrayList<>();
                for (Callable task : tasks)
                {
                    futures.add(executorPool.submit(task));
                }
                wait(futures);
                break;
        }
        return playerGuesserAllOptions.HasMoreOptions();
    }


    private void wait(List<Future> futures) throws IOException
    {
        for (Future future : futures)
            try
            {
                future.get();
            }
            catch (InterruptedException ignored)
            {

            }
            catch (ExecutionException e)
            {
                if (e.getCause() instanceof IOException)
                    throw (IOException) e.getCause();
            }
    }


}
