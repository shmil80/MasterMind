package com.shmuel.mastermind.logic.guessers.guessCheckAllOptions;

import com.shmuel.helpers.beans.Wrapper;
import com.shmuel.helpers.interfaces.Cancelable;
import com.shmuel.iterables.logic.IteratorClosableImpl;
import com.shmuel.iterables.logic.IteratorWork;
import com.shmuel.mastermind.logic.ManagerGame;
import com.shmuel.mastermind.shared.MasterMindApplication;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.beans.Result;
import com.shmuel.mastermind.shared.interfaces.IOptionProvider;
import com.shmuel.mastermind.shared.interfaces.ManagerDataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class OptionFinderInList
{
    private final ManagerDataProvider dataProvider = MasterMindApplication.dataProvider;
    private ManagerGame managerGame;
    private static Random rand = new Random(System.currentTimeMillis());

    public OptionFinderInList(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }

    public int findRemainOptionsCount(Cancelable cancelable, Iterable<Option> currentOptions, Option checkedOption, Result checkedResult)
    {
        int count = 0;
        for (Option o : currentOptions)
        {
            if (cancelable.isCancelled())
                return Integer.MAX_VALUE;
            if (managerGame.matcher.check(o, checkedOption).equals(checkedResult))
                count++;
        }

        return count;
    }

    public IOptionProvider findRemainOptions(final Cancelable cancelable, IOptionProvider currentOptions, final Option checkedOption,
                                             final Result checkedResult) throws IOException
    {
        final IOptionProvider result = dataProvider.getOptionProviderFactory().create();

        IteratorClosableImpl.Iterate(currentOptions, new IteratorWork<Option>()
        {
            @Override
            public void beforeWork() throws IOException
            {
                result.startWrite();
            }

            @Override
            public boolean doWork(Option item) throws IOException
            {
                if (cancelable.isCancelled())
                    return true;
                if (managerGame.matcher.check(item, checkedOption).equals(checkedResult))
                    result.add(item);
                return false;
            }

            @Override
            public void afterWorkFinally() throws IOException
            {
                result.endWrite();
            }
        });
        return result;
    }

    private void assign(IOptionProvider[] results, byte[] posibilitiesUse, byte position, byte[] currentOption) throws IOException
    {
        boolean allowEmpty = managerGame.configuration.isAllowEmpty();
        boolean allowDuplicate = managerGame.configuration.isAllowDuplicate();
        if (position == currentOption.length)
        {
            boolean cantBeFirst;
            int maxPosibility = posibilitiesUse[0];
            cantBeFirst = allowEmpty && maxPosibility > 0;
            for (int i = 1; i < posibilitiesUse.length; i++)
            {
                if (maxPosibility < posibilitiesUse[i])
                    maxPosibility = posibilitiesUse[i];
            }
            byte[] toEnter = new byte[currentOption.length];
            for (int i = 0; i < currentOption.length; i++)
            {
                toEnter[i] = currentOption[i];
            }
            Option option = new Option(toEnter);
            results[maxPosibility == 1 && cantBeFirst ? 1 : maxPosibility - 1].add(option);

        } else
        {
            for (byte i = 0; i < posibilitiesUse.length; i++)
            {
                if (posibilitiesUse[i] == 0 || (allowDuplicate && (!allowEmpty || i != 0)))
                {
                    currentOption[position] = allowEmpty ? (byte) (i - 1) : i;
                    posibilitiesUse[i]++;
                    assign(results, posibilitiesUse, (byte) (position + 1), currentOption);
                    posibilitiesUse[i]--;
                }
            }
        }

    }


    private boolean nextOptionWithoutDuplicate(byte position, byte[] currentOption) throws IOException
    {
        if (position == currentOption.length)
        {
            return true;

        } else
        {
            outer:
            for (byte i = 0; i < this.managerGame.configuration.getOptionPosibilities(); i++)
            {
                for (byte j = 0; j < position; j++)
                {
                    if (currentOption[j] == i)
                        continue outer;
                }
                currentOption[position] = i;
                if (nextOptionWithoutDuplicate((byte) (position + 1), currentOption))
                    return true;
            }
            return false;
        }

    }

    public IOptionProvider[] getInitialOptions() throws IOException
    {
        int optionLength = managerGame.configuration.getOptionLength();
        int providersCount = managerGame.configuration.isAllowDuplicate() || managerGame.configuration.isAllowEmpty() ? managerGame.configuration.getOptionLength() : 1;
        if (dataProvider.isCacheAllOptions() && dataProvider.getOptionProviderCache().isValid(providersCount))
        {
            return dataProvider.getOptionProviderCache().getAllOptions(providersCount, optionLength);
        } else
        {
            return createInitialOptions(providersCount);
        }
    }

    public void createOptionsCacheIfNeeded() throws IOException
    {
        if (dataProvider.isCacheAllOptions())
        {
            int providersCount = managerGame.configuration.isAllowDuplicate() || managerGame.configuration.isAllowEmpty() ? managerGame.configuration.getOptionLength() : 1;
            if (!dataProvider.getOptionProviderCache().isValid(providersCount))
            {
                createInitialOptions(providersCount);
            }
        }
    }

    private IOptionProvider[] createInitialOptions(int providersCount) throws IOException
    {
        IOptionProvider[] results = new IOptionProvider[providersCount];
        for (int i = 0; i < results.length; i++)
        {
            results[i] = dataProvider.getOptionProviderFactory().createForCache(i);
        }
        assignInitialOptions(results);
        return results;
    }

    private void assignInitialOptions(IOptionProvider[] results) throws IOException
    {
        byte[] currentOption = new byte[this.managerGame.configuration.getOptionLength()];
        byte[] posibilitiesUse = new byte[managerGame.configuration.getOptionPosibilities() + (managerGame.configuration.isAllowEmpty() ? 1 : 0)];
        try
        {
            for (IOptionProvider result : results)
            {
                result.startWrite();
            }
            assign(results, posibilitiesUse, (byte) 0, currentOption);
        }
        finally
        {
            for (IOptionProvider result : results)
            {
                result.endWrite();
            }
        }

    }


    public boolean nextOptionValid(byte[] currentOption, boolean allowDuplicate)
    {
        byte position = 0;
        byte max = (byte) (this.managerGame.configuration.getOptionPosibilities() - 1);
        outer:
        while (true)
        {
            if (currentOption[position] != max)
            {
                currentOption[position]++;
            } else
            {
                do
                {
                    position++;
                    if (position == currentOption.length)
                        return false;
                } while (currentOption[position] == max);
                currentOption[position]++;
                if (allowDuplicate)
                    for (byte i = 0; i < position; i++)
                        currentOption[i] = 0;
                else
                    for (byte i = 0; i < position; i++)
                        currentOption[i] = (byte) (position - i - 1);
                position = 0;
            }
            if (!allowDuplicate)
            {
                for (int i = 0; i < currentOption.length; i++)
                {
                    for (int j = i + 1; j < currentOption.length; j++)
                    {
                        if (currentOption[j] == currentOption[i])
                        {
                            continue outer;
                        }
                    }
                }
            }
            return true;
        }

    }

    private boolean nextOptionValid(Wrapper<byte[]> current, Wrapper<Byte> position)
    {
        while (true)
        {
            if (!nextOption(current, position))
                return false;
            if (isValid(current.getItem()))
                return true;
        }
    }

    private boolean isValid(byte[] next)
    {
        if (managerGame.configuration.isAllowDuplicate())
            return true;

        Set<Byte> hashset = new HashSet<>();
        for (byte i : next)
            hashset.add(i);

        return hashset.size() == next.length;
    }

    private boolean nextOption(Wrapper<byte[]> current, Wrapper<Byte> position)
    {
        byte[] currentArray = current.getItem();
        if (currentArray[position.getItem()] != this.managerGame.configuration.getOptionPosibilities() - 1)
        {
            currentArray[position.getItem()]++;
        } else
        {
            do
            {
                position.setItem((byte) (position.getItem() + 1));
                if (position.getItem() == currentArray.length)
                    return false;
            }
            while (currentArray[position.getItem()] == this.managerGame.configuration.getOptionPosibilities() - 1);
            currentArray[position.getItem()]++;
            for (byte i = 0; i < position.getItem(); i++)
                currentArray[i] = (byte) (this.managerGame.configuration.isAllowDuplicate() ? 0 : (position.getItem() - i - 1));
            position.setItem((byte) 0);
        }

        return true;
    }

}
