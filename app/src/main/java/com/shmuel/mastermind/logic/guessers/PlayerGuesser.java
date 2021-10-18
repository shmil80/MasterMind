package com.shmuel.mastermind.logic.guessers;

import android.os.Parcel;

import com.shmuel.mastermind.logic.ManagerGame;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.interfaces.IPlayerGuesser;

import java.io.IOException;
import java.util.ArrayList;


public class PlayerGuesser implements IPlayerGuesser
{
    private ManagerGame managerGame;
    private boolean cancelled;
    private Option next;
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

    public PlayerGuesser(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }

    public void setManagerGame(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }


    public void startGame() throws IOException
    {
        initFirstOption();
    }


    public Option guessSmart(boolean firstTime) throws IOException
    {
        if (cancelled)
            return null;
        return next;
    }

    public boolean calcuateAnswer(OptionResult result) throws IOException
    {
        answers.add(result);
        while (true)
        {
            next = managerGame.optionFinder.nextOptionValid(next,allowOccurenceNumber, allowEmpty,answers);
            if(next!=null)
                return true;
            if(allowEmpty)
                return false;


            if(managerGame.configuration.isAllowDuplicate()&&allowOccurenceNumber<managerGame.configuration.getOptionLength())
            {
                allowOccurenceNumber++;
                initFirstOption();
                continue;
            }
            if(managerGame.configuration.isAllowEmpty())
            {
                allowEmpty=true;
                initFirstOption();
                continue;
            }
            return false;
        }
    }

    private void initFirstOption()
    {
        next=new Option(managerGame.optionFinder.firstOption(allowOccurenceNumber, allowEmpty));
    }

    @Override
    public boolean HasMoreOptions()
    {
        return next!=null;
    }



    protected PlayerGuesser(Parcel in)
    {
        in.readTypedList(answers,OptionResult.CREATOR);
        cancelled=in.readByte()==1;
        next = in.readParcelable(Option.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeTypedList(answers);
        dest.writeByte((byte) (cancelled?1:0));
        dest.writeParcelable(next,flags);
    }

    public static final Creator<PlayerGuesser> CREATOR = new Creator<PlayerGuesser>()
    {
        @Override
        public PlayerGuesser createFromParcel(Parcel in)
        {
            return new PlayerGuesser(in);
        }

        @Override
        public PlayerGuesser[] newArray(int size)
        {
            return new PlayerGuesser[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

}
