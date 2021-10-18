package com.shmuel.mastermind.logic.guessers.guessCheckAllOptions;

import android.os.Parcel;
import android.util.SparseIntArray;

import com.shmuel.iterables.logic.IteratorClosableImpl;
import com.shmuel.iterables.logic.IteratorWork;
import com.shmuel.mastermind.logic.ManagerGame;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.beans.Result;
import com.shmuel.mastermind.shared.interfaces.IOptionProvider;
import com.shmuel.mastermind.shared.interfaces.IPlayerGuesser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class PlayerGuesserAllOptions implements IPlayerGuesser
{
private final AnswerCalcThread answerCalcThread;
    private ManagerGame managerGame;
    private final static int VALUE_IF_KEY_NOT_FOUND = -1000;
    private IOptionProvider[] optionProviders=new IOptionProvider[0];
    private int totalSize;
    private boolean cancelled;

    public boolean isCancelled()
    {
        return cancelled;
    }

    public void cancel()
    {
        this.cancelled = true;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public PlayerGuesserAllOptions(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
        this.answerCalcThread=new AnswerCalcThread(this);
    }

    public void setManagerGame(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }


    public void startGame() throws IOException
    {
        this.optionProviders = managerGame.optionFinderInList.getInitialOptions();
        totalSize = 0;
        for (IOptionProvider option : this.optionProviders)
        {
            totalSize += option.size();
        }
    }

    public Option guessRandom() throws IOException
    {
        if (cancelled)
            return null;
        for (IOptionProvider optionprovider : optionProviders)
        {
            if (optionprovider.size() > 0)
                return optionprovider.get(0);
        }
        throw new ArrayIndexOutOfBoundsException("no options");

    }

    public Option guessSmart(boolean firstTime) throws IOException
    {
        if (cancelled)
            return null;
        if (firstTime)
        {
            return managerGame.optionFinder.chooseRandom(false,false);
        }

        if (totalSize == 1 || totalSize > 100)
        {
            return guessRandom();

        }
        List<Option> theBests = new ArrayList<>();
        int bestScore = totalSize * totalSize * totalSize;
        final List<Option> options = new ArrayList<>();
        for (IOptionProvider optionGroup : this.optionProviders)
        {
            IteratorClosableImpl.Iterate(optionGroup, new IteratorWork<Option>()
            {
                @Override
                public boolean doWork(Option item)
                {
                    options.add(item);
                    return false;
                }
            });
        }
        long start = System.currentTimeMillis();
        SparseIntArray scoresOfResult = new SparseIntArray();

        outer:
        for (Option option : options)
        {
            scoresOfResult.clear();
            int score = 0;
            for (Option actual : options)
            {

                Result checkedResult = managerGame.matcher.check(option, actual);
                int checkedResultKey = checkedResult.hashCode();
                int scoreExist = scoresOfResult.get(checkedResultKey, VALUE_IF_KEY_NOT_FOUND);
                if (scoreExist == VALUE_IF_KEY_NOT_FOUND)
                {

                    int count = managerGame.optionFinderInList.findRemainOptionsCount(this, options, option, checkedResult);
                    scoresOfResult.put(checkedResultKey, count);
                    score += count;
                }
                else
                {
                    score += scoreExist;
                }
                if (score > bestScore)
                    continue outer;
            }
            if (score == bestScore)
            {
                theBests.add(option);
            } else if (score < bestScore)
            {
                theBests.clear();
                theBests.add(option);
                bestScore = score;
            }
        }
        if (cancelled)
            return null;
        System.out.println("----------------- in size " + totalSize + " calculate best took " + (System.currentTimeMillis() - start) + " ms");
        if (theBests.size() == 0)
            throw new ArrayIndexOutOfBoundsException("no options in bests. it shouldn't happend, cancelled-"+cancelled);
        return theBests.get(0);
    }

    public boolean calcuateAnswer(OptionResult result) throws IOException
    {
        return answerCalcThread.calcuateAnswer(result);
    }

    @Override
    public boolean HasMoreOptions()
    {
        return totalSize > 0;
    }

    public List<Callable> calculateAnswer(final Option guess, final Result resultOfguess) throws IOException
    {
        totalSize = 0;
        List<Callable> tasks = new ArrayList<>();
        if (cancelled)
            return tasks;
        for (int i = 0; i < this.optionProviders.length; i++)
        {
            tasks.add(new CalculateAnswerTask(i, guess, resultOfguess));
        }
        return tasks;
    }


    private class CalculateAnswerTask implements Callable<IOptionProvider>
    {
        private final int index;
        private final Option guess;
        private final Result resultOfguess;

        public CalculateAnswerTask(int index, Option guess, Result resultOfguess)
        {
            this.index = index;
            this.guess = guess;
            this.resultOfguess = resultOfguess;
        }

        @Override
        public IOptionProvider call() throws IOException
        {
            if (cancelled)
                return null;
            IOptionProvider newOptions = managerGame.optionFinderInList.findRemainOptions(PlayerGuesserAllOptions.this, optionProviders[index], guess, resultOfguess);
            optionProviders[index].clear();
            optionProviders[index] = newOptions;
            totalSize += newOptions.size();
            return newOptions;
        }
    }

    protected PlayerGuesserAllOptions(Parcel in)
    {
        optionProviders = (IOptionProvider[]) in.readParcelableArray(IOptionProvider.class.getClassLoader());
        answerCalcThread = new AnswerCalcThread(this);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelableArray(optionProviders, flags);
    }

    public static final Creator<PlayerGuesserAllOptions> CREATOR = new Creator<PlayerGuesserAllOptions>()
    {
        @Override
        public PlayerGuesserAllOptions createFromParcel(Parcel in)
        {
            return new PlayerGuesserAllOptions(in);
        }

        @Override
        public PlayerGuesserAllOptions[] newArray(int size)
        {
            return new PlayerGuesserAllOptions[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

}
