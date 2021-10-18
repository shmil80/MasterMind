package com.shmuel.mastermind.logic.guessers;

import android.os.Parcel;

import com.shmuel.mastermind.logic.ManagerGame;
import com.shmuel.mastermind.shared.beans.Colors;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.interfaces.IPlayerGuesser;

import java.io.IOException;
import java.util.ArrayList;


public class PlayerGuesserSeparated implements IPlayerGuesser
{
    private ManagerGame managerGame;
    private boolean cancelled;
    private Option nextOption;
    private Colors nextColors;
    private final ArrayList<OptionResult> answers=new ArrayList<>();
    private byte allowOccurenceNumber=1;
    private boolean allowEmpty;

    public boolean isCancelled()
    {
        return cancelled;
    }

    public void cancel()
    {
        this.cancelled = true;
    }

    public PlayerGuesserSeparated(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }

    public void setManagerGame(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }


    public void startGame() throws IOException
    {
        initOccurenceNumber();
        initFirstOption();
    }

    private void initOccurenceNumber()
    {
        int optionIsBigger=managerGame.configuration.getOptionLength()- managerGame.configuration.getOptionPosibilities();
        if(optionIsBigger==1&&!managerGame.configuration.isAllowDuplicate()&&managerGame.configuration.isAllowEmpty())
            allowEmpty=true;
        if(allowEmpty)
            optionIsBigger--;
        allowOccurenceNumber= (byte) (optionIsBigger>=0?Math.ceil(optionIsBigger*1.0/managerGame.configuration.getOptionPosibilities())+1:1);
    }


    public Option guessSmart(boolean firstTime) throws IOException
    {
        if (cancelled)
            return null;
        return nextOption;
    }

    public boolean calcuateAnswer(OptionResult result) throws IOException
    {
        answers.add(result);
        while (true)
        {
            nextOption = managerGame.optionFinder.nextOptionValid(nextColors, nextOption,allowOccurenceNumber, allowEmpty,answers);
            if(nextOption !=null)
                return true;

            if(managerGame.configuration.isAllowDuplicate()&&allowOccurenceNumber<managerGame.configuration.getOptionLength())
            {
                allowOccurenceNumber++;
                initFirstOption();
                continue;
            }
            if(managerGame.configuration.isAllowEmpty()&&!allowEmpty)
            {
                allowEmpty=true;
                initOccurenceNumber();
                initFirstOption();
                continue;
            }
            return false;
        }
    }

    private void initFirstOption()
    {
        byte[] first=managerGame.optionFinder.firstOption(allowOccurenceNumber, allowEmpty);
        nextOption =new Option(first);
        nextColors=new Colors(first.clone());
    }

    @Override
    public boolean HasMoreOptions()
    {
        return nextOption !=null;
    }



    protected PlayerGuesserSeparated(Parcel in)
    {
        in.readTypedList(answers,OptionResult.CREATOR);
        cancelled=in.readByte()==1;
        nextOption = in.readParcelable(Option.class.getClassLoader());
        nextColors=in.readParcelable(Colors.class.getClassLoader());
        allowOccurenceNumber=in.readByte();
        allowEmpty=in.readByte()==1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedList(answers);
        dest.writeByte((byte) (cancelled?1:0));
        dest.writeParcelable(nextOption,flags);
        dest.writeParcelable(nextColors,flags);
        dest.writeByte(allowOccurenceNumber);
        dest.writeByte((byte) (allowEmpty?1:0));
    }

    public static final Creator<PlayerGuesserSeparated> CREATOR = new Creator<PlayerGuesserSeparated>()
    {
        @Override
        public PlayerGuesserSeparated createFromParcel(Parcel in)
        {
            return new PlayerGuesserSeparated(in);
        }

        @Override
        public PlayerGuesserSeparated[] newArray(int size)
        {
            return new PlayerGuesserSeparated[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

}
