package com.shmuel.mastermind.logic;

import com.shmuel.mastermind.shared.beans.Colors;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.beans.Result;

public class Matcher
{
    private ManagerGame managerGame;

    Matcher(ManagerGame managerGame)
    {
        this.managerGame = managerGame;
    }


    public Result check(Option actual, Option toCheck)
    {
        Result result = new Result();
        byte[] toCheckColors = toCheck.getColors();
        byte[] actualColors = actual.getColors();
//        if (this.managerGame.configuration.isAllowDuplicate()) {
        byte[] taken = new byte[this.managerGame.configuration.getOptionPosibilities()];
        for (byte i = 0; i < toCheckColors.length; i++)
        {
            if (toCheckColors[i] < 0)
                continue;
            if (toCheckColors[i] == actualColors[i])
            {
                result.exacts++;
            } else
            {
                for (byte j = taken[toCheckColors[i]]; j < actualColors.length; j++)
                {
                    if (actualColors[j] != toCheckColors[j] && actualColors[j] == toCheckColors[i])
                    {
                        result.right++;
                        taken[actualColors[j]] = (byte) (j + 1);
                        break;
                    }
                }
            }
        }
//        } else {
//            for (byte i = 0; i < toCheckColors.length; i++) {
//                if(toCheckColors[i]<0)
//                    continue;
//                if (toCheckColors[i] == actualColors[i]) {
//                    result.exacts++;
//                } else {
//                    for (byte actualColor : actualColors) {
//                        if (actualColor == toCheckColors[i]) {
//                            result.right++;
//                        }
//                    }
//                }
//            }
//        }

        return result;
    }

    public boolean checkRightResult(Option chosenOption, Option toCheck)
    {
        byte[] chosenOptionColors = chosenOption.getColors();
        byte[] toCheckColors = toCheck.getColors();
        for (int i = 0; i < chosenOptionColors.length; i++)
        {
            if (toCheckColors[i] != chosenOptionColors[i])
                return false;
        }
        return true;
    }

    public boolean checkColors(OptionResult answer, Colors rightColors)
    {
        byte[] rightColorsArray=rightColors.getColors();
        byte expectedRights = (byte) (answer.getResult().exacts+answer.getResult().right);
        byte  actualRights=0;
        byte[] toCheckColors = answer.getOption().getColors();
        byte[] taken = new byte[this.managerGame.configuration.getOptionPosibilities()];
        for (byte i = 0; i < toCheckColors.length; i++)
        {
            byte colorToCheck=toCheckColors[i];
            if (colorToCheck< 0)
                continue;
            for (byte j = taken[colorToCheck]; j < rightColorsArray.length; j++)
            {
                if (colorToCheck == rightColorsArray[rightColorsArray.length-1-j])
                {
                    actualRights++;
                    taken[colorToCheck] = (byte) (j + 1);
                    break;
                }
            }
        }
        return expectedRights==actualRights;
    }
}
