package com.shmuel.mastermind.shared.interfaces;

import android.os.Parcelable;

import com.shmuel.helpers.interfaces.Cancelable;
import com.shmuel.mastermind.logic.ManagerGame;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.beans.Result;

import java.io.IOException;

public interface IPlayerGuesser extends Parcelable,Cancelable
{

    void startGame() throws IOException;

    Option guessSmart(boolean firstTime) throws IOException;

    boolean calcuateAnswer(OptionResult result) throws IOException;

    boolean HasMoreOptions();

    void setManagerGame(ManagerGame managerGame);
}
