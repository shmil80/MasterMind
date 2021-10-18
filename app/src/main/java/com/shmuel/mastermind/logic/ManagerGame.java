package com.shmuel.mastermind.logic;

import com.shmuel.mastermind.logic.guessers.guessCheckAllOptions.OptionFinderInList;
import com.shmuel.mastermind.logic.guessers.PlayerGuesserSeparated;
import com.shmuel.mastermind.shared.beans.Configuration;
import com.shmuel.mastermind.shared.interfaces.IPlayerGuesser;

@SuppressWarnings("WeakerAccess")
public class ManagerGame
{
    public Configuration configuration;
    public Matcher matcher;
    public OptionFinder optionFinder;
    public OptionFinderInList optionFinderInList;
    public AnswerManager answerManager;

    public ManagerGame(Configuration configuration)
    {
        this.configuration = configuration;
        matcher = new Matcher(this);
        optionFinder = new OptionFinder(this);
        answerManager = new AnswerManager(this);
        optionFinderInList=new OptionFinderInList(this);
    }

    public PlayerChooser createChooser()
    {
        return new PlayerChooser(this);
    }

    public IPlayerGuesser createGuesser()
    {
        return new PlayerGuesserSeparated(this);
    }


}
