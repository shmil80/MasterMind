package com.shmuel.mastermind.logic;

import android.os.Parcel;
import android.os.Parcelable;

import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.Result;
import com.shmuel.mastermind.shared.interfaces.IOptionProvider;

import java.io.IOException;

public class PlayerChooser implements Parcelable
{
    private ManagerGame managerGame;
    private Option chosenOption;

    PlayerChooser(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }

    public void setManagerGame(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }

    public void startGame() throws IOException
    {
        this.chosenOption = managerGame.optionFinder.chooseRandom(managerGame.configuration.isAllowDuplicate(), managerGame.configuration.isAllowEmpty());
    }

    public void startGame(Option choosed)
    {
        this.chosenOption = choosed;
    }

    public Result answerGuess(Option guess)
    {
        Result result = managerGame.matcher.check(this.chosenOption, guess);
        result.rightAnswer = managerGame.matcher.checkRightResult(this.chosenOption, guess);
        return result;
    }

    protected PlayerChooser(Parcel in)
    {
        this.chosenOption = in.readParcelable(Option.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(chosenOption, 0);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<PlayerChooser> CREATOR = new Creator<PlayerChooser>()
    {
        @Override
        public PlayerChooser createFromParcel(Parcel in)
        {
            return new PlayerChooser(in);
        }

        @Override
        public PlayerChooser[] newArray(int size)
        {
            return new PlayerChooser[size];
        }
    };

}
