package com.shmuel.mastermind.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.shmuel.mastermind.ChooseColorAnimation;
import com.shmuel.mastermind.GuessColorAnimation;
import com.shmuel.mastermind.LineOption;
import com.shmuel.mastermind.OptionAdapter;
import com.shmuel.mastermind.R;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.ui.SoundTickPlayer;


public class LinearPallete extends LinearCircles
{
    private OptionAdapter optionAdapter;
    private ChooseColorAnimation chooseAnimation;
    private GuessColorAnimation guessColorAnimation;

    public void init(ChooseColorAnimation chooseAnimation)
    {
        this.chooseAnimation = chooseAnimation;
        this.guessColorAnimation=new GuessColorAnimation(chooseAnimation,this);
    }

    public LinearPallete(Context context)
    {
        super(context);
    }

    public LinearPallete(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LinearPallete(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinearPallete(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected byte getMaxColorsInRow(Resources resources)
    {
        return (byte) resources.getInteger(R.integer.pallete_max_color_in_row);
    }

    @Override
    protected float getPaddingItem(Resources resources)
    {
        return (int) resources.getDimension(R.dimen.pallete_item_padding);
    }

    private boolean inAnimation;
    @Override
    protected CircleColorButton createNewCircle(final byte index)
    {
        final ColorButtonChoose colorButton=new ColorButtonChoose(getContext());
        colorButton.setValue(index);
        colorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(LinearLineOption.selectedGlobal!=null&&!inAnimation)
                {
                    inAnimation=true;
                    final LinearLineOption currentLine = optionAdapter.getCurrentLine();
                    chooseAnimation.animate(colorButton,currentLine.getTarget(),new Runnable(){

                        @Override
                        public void run()
                        {
                            currentLine.chooseColor(index);
                            inAnimation=false;
                        }
                    });


                }
            }
        });
        colorButton.invalidate();
        return colorButton;
    }
    public void animate(Option optionGuess, final Runnable afterAnim)
    {
        inAnimation=true;
        LinearLineOption target=optionAdapter.getCurrentLine();
        guessColorAnimation.animate(target, optionGuess, new Runnable() {
            @Override
            public void run()
            {
                afterAnim.run();
                inAnimation=false;
            }
        });
    }


    public OptionAdapter getOptionAdapter() {
        return optionAdapter;
    }

    public void setOptionAdapter(OptionAdapter optionAdapter) {
        this.optionAdapter = optionAdapter;
    }


}
