package com.shmuel.mastermind.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;

import java.util.HashMap;
import java.util.Map;


public abstract class LinearCircles extends LinearLayout
{
    protected CircleColorButton[] items;

    public LinearCircles(Context context)
    {
        super(context);
    }

    public LinearCircles(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LinearCircles(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinearCircles(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setLength(byte length) {
        if(items!=null&&items.length==length)
            return;
        removeAllViews();
        items=new CircleColorButton[length];
        Resources resources = getContext().getResources();
        byte maxColorsInRow=getMaxColorsInRow(resources);
        byte rows= (byte) ((length-1)/maxColorsInRow+1);
        byte colorsInRow= (byte) ((length-1)/rows+1);
        byte colorsInLastRow= (byte) (length-(rows-1)*colorsInRow);
        boolean addSpaceInLastRow=(colorsInRow-colorsInLastRow)%2>0;
        if(rows>1)
        {
            setOrientation(VERTICAL);
            for (byte r = 0; r < rows; r++)
            {
                LinearLayout nested = new LinearLayout(getContext());
                boolean lastRow=r==rows-1;
                byte colorsInThisRow=lastRow?colorsInLastRow:colorsInRow;
                fillRow(nested,lastRow&& addSpaceInLastRow, colorsInThisRow, (byte) (colorsInRow*r), resources);
                this.addView(nested, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1F));
            }
        }
        else
        {
            fillRow(this, addSpaceInLastRow, colorsInLastRow, (byte) 0,resources);
        }
        invalidate();
    }

    protected abstract byte getMaxColorsInRow(Resources resources);

    private void fillRow(LinearLayout rowLayout, boolean addSpace,byte  colorsInThisRow,byte startIndex, Resources resources)
    {
        rowLayout.setOrientation(HORIZONTAL);
        int margin = (int) getPaddingItem(resources);
        if(addSpace)
            addSpace(rowLayout,margin);
        for (byte c = 0; c <colorsInThisRow; c++)
        {
            byte index= (byte) (startIndex+c);
            CircleColorButton colorButton=createNewCircle(index);
            items[index]=colorButton;
            FrameLayout frameLayout=new FrameLayout(getContext());
            FrameLayout.LayoutParams flp=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            flp.gravity=Gravity.CENTER;
            frameLayout.addView(colorButton,flp);

            LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1F);
            params.setMargins(margin,margin,margin,margin);
            params.gravity= Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
            rowLayout.addView(frameLayout,params);
        }
        if(addSpace)
            addSpace(rowLayout,margin);
    }

    protected abstract float getPaddingItem(Resources resources);

    protected abstract CircleColorButton createNewCircle(final byte value);

    public CircleColorButton getChild(int index)
    {
        return items[index];
    }

    private void addSpace(LinearLayout nested, int margin)
    {
        Space space=new Space(getContext());
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5F);
        params.setMargins(margin/2,0,margin/2,0);
        nested.addView(space,params);
    }
}
