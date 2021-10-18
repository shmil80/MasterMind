package com.shmuel.mastermind.logic;

import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerManager
{
    private ManagerGame _managerGame;

    AnswerManager(ManagerGame managerGame)
    {
        _managerGame = managerGame;
    }

    @SuppressWarnings("WeakerAccess")
    public Map<Result,Integer> GetResultPropability(List<Option> options, Option guess)
    {
        Map<Result,Integer> results = new HashMap<>();
        for (Option option : options)
        {
            Result result=_managerGame.matcher.check(option,guess);
            if(!results.containsKey(result))
                results.put(result,1);
            else
                results.put(result,results.get(result)+1);
        }
        return results;
    }


}
