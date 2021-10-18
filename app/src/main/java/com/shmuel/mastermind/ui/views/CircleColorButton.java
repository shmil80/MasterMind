package com.shmuel.mastermind.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

public abstract class CircleColorButton extends AppCompatImageButton
{
    private byte value=-2;

    public CircleColorButton(Context context)
    {
        super(context);
    }

    public CircleColorButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CircleColorButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int curSquareDim = w == 0 || w > h ? h : w;
        setMeasuredDimension(curSquareDim, curSquareDim);
    }
    public void setValue(byte value)
    {
        if(value!=this.value)
        {
            this.value=value;
            invalidate();
        }
    }
    public void invalidate()
    {
        int color=getColor(value);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        shape.setStroke(getStrokeWidth(), Color.BLACK);
        assignShape(shape);
        super.invalidate();
    }

    protected  int getStrokeWidth()
    {
        return 1;
    };

    protected void assignShape(GradientDrawable shape)
    {
        this.setBackgroundDrawable(shape);
    }

    protected abstract int getColor(byte value);

    public byte getValue()
    {
        return value;
    }
}
