package com.shmuel.mastermind.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.shmuel.mastermind.R;

public class ColorButtonAnswer extends CircleColorButton
{
    public final static byte EMPTY=0;
    public final static byte RIGHT=1;
    public final static byte EXACT=2;
    public ColorButtonAnswer(Context context)
    {
        super(context);
    }

    public ColorButtonAnswer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ColorButtonAnswer(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getColor(byte value)
    {
        Resources resources = getContext().getResources();
        switch (value)
        {
            case EMPTY:
                return Color.TRANSPARENT;
            case RIGHT:
                return resources.getColor(R.color.colorRightAnswer);
            case EXACT:
                return resources.getColor(R.color.colorExactAnswer);
            default:
                throw new ArrayIndexOutOfBoundsException(value);
        }
    }
}
