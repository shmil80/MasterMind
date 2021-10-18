package com.shmuel.mastermind.logic;

import com.shmuel.helpers.beans.Wrapper;
import com.shmuel.helpers.interfaces.Cancelable;
import com.shmuel.mastermind.shared.beans.Colors;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.beans.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class OptionFinder
{
    private ManagerGame managerGame;
    private static Random rand = new Random(System.currentTimeMillis());

    public OptionFinder(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }

    public Option emptyOption()
    {
        byte[] colors = new byte[managerGame.configuration.getOptionLength()];
        for (byte i = 0; i < colors.length; i++)
            colors[i] = -1;

        return new Option(colors);
    }

    public byte[] firstOption(boolean allowDuplicate)
    {
        byte[] option = new byte[this.managerGame.configuration.getOptionLength()];
        if (!allowDuplicate)
            for (byte i = 1; i < option.length; i++)
                option[i] = (byte) (option.length - 1 - i);

        return option;
    }

    public Option chooseRandom(boolean allowDuplicate, boolean allowEmpty)
    {
        byte length = managerGame.configuration.getOptionLength();
        byte posibilities = managerGame.configuration.getOptionPosibilities();
        byte[] colors = new byte[length];
        if (allowDuplicate)
        {

            for (byte i = 0; i < length; i++)
            {
                int randInt = rand.nextInt(posibilities);
                colors[i] = (byte) randInt;
            }
        } else
        {
            Set<Byte> options = new HashSet<>();
            for (byte i = 0; i < posibilities; i++)
                options.add(i);

            for (byte i = 0; i < length; i++)
            {
                if(options.size()>0)
                {
                    int randValue = rand.nextInt(options.size());
                    Iterator<Byte> iterator = options.iterator();
                    for (int j = 0; j < randValue; j++)
                    {
                        iterator.next();
                    }
                    colors[i] = iterator.next();
                    options.remove(colors[i]);
                }
                else if(allowEmpty)
                {
                    colors[i]=-1;
                    int randValue = rand.nextInt(colors.length);
                    byte v=colors[randValue];
                    colors[randValue] = -1;
                    colors[i]=v;
                    return new Option(colors);
                }
                else
                {
                    throw new RuntimeException("not sufisant options");
                }
            }
        }
        if (allowEmpty && rand.nextInt(3) == 0)
        {
            colors[rand.nextInt(colors.length)] = -1;

        }
        return new Option(colors);
    }


    public Option nextOptionValid(Option current,byte allowOccurenceNumber,boolean allowEmpty, ArrayList<OptionResult> answers)
    {
        Wrapper<Byte> position=new Wrapper<>((byte)0);
        while (true)
        {
            if (isValid(current,answers,allowOccurenceNumber, allowEmpty))
                return current;
            if (!nextOption(current, position,allowOccurenceNumber>1, allowEmpty))
                return null;
        }

    }
    private boolean nextOption(Option current, Wrapper<Byte> position,boolean allowDupliacte, boolean allowEmpty)
    {
        byte[] currentArray = current.getColors();
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
                if (allowDupliacte)
                {
                    if (allowEmpty)
                    {
                        allowEmpty = false;
                        currentArray[i] = (byte) -1;
                    }
                    else
                    {
                        currentArray[i] = (byte) 0;
                    }
                }
                else
                {
                    currentArray[i] = (byte) (position.getItem() - i - (allowEmpty?2:1));
                }
            position.setItem((byte) 0);
        }

        return true;
    }

    private boolean isValid(Option current, ArrayList<OptionResult> answers,byte allowOccurenceNumber,boolean allowEmpty)
    {
        byte maxOccur=0;
        boolean emptyFound=false;
        byte[] colors = current.getColors();

        byte[] taken=new byte[managerGame.configuration.getOptionPosibilities()];
        for (int i = 0; i < colors.length; i++)
        {
            byte color = colors[i];
            if (color < 0)
            {
                if(!allowEmpty||emptyFound)
                    return false;
                emptyFound = true;
            } else
            {
                if (++taken[color] > maxOccur)
                    maxOccur = taken[color];
            }
        }
        if(!allowEmpty)
        {

            if (maxOccur != allowOccurenceNumber)
                return false;
        }
        else
        {
            if (!emptyFound)
                return false;
            if (maxOccur>1&&!managerGame.configuration.isAllowDuplicate())
                return false;

        }

        for (OptionResult answer : answers)
        {
            if(!managerGame.matcher.check(answer.getOption(),current).equals(answer.getResult()))
                return false;
        }
        return true;
    }
    public byte[] firstOption(byte allowOccurenceNumber,boolean allowEmpty)
    {
        byte[] option = new byte[this.managerGame.configuration.getOptionLength()];
        int start;
        if(allowEmpty)
        {
            option[option.length-1]=-1;
            start=1;
        }
        else
        {
            start = 0;
        }
        int numOccur = 0;
        byte value = 0;
        for (int i = start; i < option.length; i++)
        {
            option[option.length-1- i] = value;
            if (++numOccur >= allowOccurenceNumber)//&&option.length-i<=this.managerGame.configuration.getOptionPosibilities())
            {
                value++;
                numOccur = 0;
            }

        }
        return option;
    }

    public Option nextOptionValid(Colors nextColors, Option current, byte allowOccurenceNumber, boolean allowEmpty, ArrayList<OptionResult> answers)
    {
        if (!isValidColors(nextColors,answers,allowOccurenceNumber))
        {
            nextColors=nextColorValid(nextColors,allowOccurenceNumber,allowEmpty,answers);
            if(nextColors==null)
                return null;
            current = new Option(nextColors.getColors().clone());
        }
        while (true)
        {
            if (isValid(current,answers,allowOccurenceNumber, allowEmpty))
                return current;
            if (!nextOptionInColors(current))
            {
                nextColors=nextColorValid(nextColors,allowOccurenceNumber,allowEmpty,answers);
                if(nextColors==null)
                    return null;
                current = new Option(nextColors.getColors().clone());
            }
        }
    }

    private boolean nextOptionInColors(Option current)
    {
        byte[] currentColor=current.getColors();
        byte[] orig=currentColor.clone();
        byte[] toReorder=new byte[currentColor.length];
        byte lastColor=Byte.MIN_VALUE;
        for (int i = 0; i < currentColor.length; i++)
        {
            if(currentColor[i]>=lastColor)
            {
                toReorder[i]=lastColor=currentColor[i];
            }
            else
            {
                byte toReplace=currentColor[i];
                byte newValue=Byte.MAX_VALUE;
                int index=-1;
                for ( int j = 0; j < i; j++)
                {
                    if(toReorder[j]<newValue&&toReorder[j]>toReplace)
                    {
                        newValue=toReorder[j];
                        index=j;
                    }
                }
                toReorder[index]=toReplace;
                currentColor[i]=newValue;
                Arrays.sort(toReorder,0,i);
                byte[] re=toReorder.clone();
                for (int j = 0; j < i; j++)
                {
                    byte value=toReorder[j];
                    currentColor[i-j-1]=value;
                }
                return true;
            }
        }
        return false;
    }

    public Colors nextColorValid(Colors nextColors, byte allowOccurenceNumber, boolean allowEmpty, ArrayList<OptionResult> answers)
    {
        Wrapper<Byte> position=new Wrapper<>((byte)0);
        while (true)
        {
            if (!getNextColors(nextColors, position,allowOccurenceNumber>1, allowEmpty))
                return null;
            if (isValidColors(nextColors,answers,allowOccurenceNumber))
                return nextColors;
        }
    }

    private boolean isValidColors(Colors colors, ArrayList<OptionResult> answers, byte allowOccurenceNumber)
    {
        byte[] colorsArray=colors.getColors();
        if(allowOccurenceNumber>1)
        {
            byte maxOccur=0;
            byte[] taken = new byte[managerGame.configuration.getOptionPosibilities()];
            for (int i = 0; i < colorsArray.length; i++)
            {
                byte color = colorsArray[i];
                if (color>=0&&++taken[color] > maxOccur)
                    maxOccur = taken[color];
            }
            if (maxOccur != allowOccurenceNumber)
                return false;
        }
        for (OptionResult answer : answers)
        {
            if(!managerGame.matcher.checkColors(answer,colors))
                return false;
        }
        return true;

    }

    private boolean getNextColors(Colors currentColors, Wrapper<Byte> position, boolean allowDupliacte, boolean allowEmpty)
    {
        byte[] currentArray=currentColors.getColors();
        byte lentghToTouch= (byte) (currentArray.length-(allowEmpty?1:0));
        if (currentArray[position.getItem()] != this.managerGame.configuration.getOptionPosibilities() - 1)
        {
            currentArray[position.getItem()]++;
        } else
        {
            byte newPosition=position.getItem();
            do
            {
                newPosition++;
                if (newPosition == lentghToTouch)
                    return false;
            }
            while (currentArray[newPosition] == this.managerGame.configuration.getOptionPosibilities() - (allowDupliacte?1:1+newPosition));
            byte minValue= ++currentArray[newPosition];
            for (byte i = 0; i < newPosition; i++)
                if (allowDupliacte)
                {
                    currentArray[i] = minValue;
                }
                else
                {
                    currentArray[i] = (byte) (newPosition - i +minValue);
                }
            position.setItem((byte) 0);
        }

        return true;
    }
}
