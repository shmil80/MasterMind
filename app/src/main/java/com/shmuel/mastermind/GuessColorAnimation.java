package com.shmuel.mastermind;

import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.ui.views.CircleColorButton;
import com.shmuel.mastermind.ui.views.LinearLineOption;
import com.shmuel.mastermind.ui.views.LinearPallete;

public class GuessColorAnimation
{
    private final ChooseColorAnimation chooseColorAnimation;
    private final LinearPallete source;

    public GuessColorAnimation(ChooseColorAnimation chooseColorAnimation, LinearPallete source)
    {
        this.chooseColorAnimation = chooseColorAnimation;
        this.source = source;
    }

    public void animate(LinearLineOption target, Option optionGuess, final Runnable afterAnim)
    {
        animateOne(0,optionGuess,target,afterAnim);

    }
    private void animateOne(final int index, final Option optionGuess, final LinearLineOption target, final Runnable afterAnim)
    {
        if(optionGuess.getColors()[index]==-1)
        {
            nextStep(index,optionGuess,target,afterAnim);
            return;
        }
        CircleColorButton sourceButton =  source.getChild(optionGuess.getColors()[index]);
        CircleColorButton targeButton=target.getChild(index);
        Runnable callBack=new Runnable() {
            public void run()
            {
                target.getOption().getColors()[index]=optionGuess.getColors()[index];
                target.invalidate();
                nextStep(index, optionGuess, target, afterAnim);
            }
        };
        chooseColorAnimation.animate(sourceButton, targeButton, callBack);
    }

    private void nextStep(int index, Option optionGuess, LinearLineOption target, Runnable afterAnim)
    {
        if(index<optionGuess.getColors().length-1)
        {
            animateOne(index+1,optionGuess,target,afterAnim);
        }
        else
        {
            afterAnim.run();
        }
    }
}
