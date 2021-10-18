package com.shmuel.mastermind.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.shmuel.mastermind.ChooseColorAnimation;
import com.shmuel.mastermind.R;
import com.shmuel.mastermind.shared.beans.Option;


public class LinearLineOption extends LinearCircles
{
    public static Byte selectedGlobal=0;
    private Option option;
    private Byte selected;
    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
        setLength((byte) option.getColors().length);
    }
    public Byte getSelected() {
        return selected;
    }

    public void setSelected(Byte selected) {
        this.selected = selected;
    }

    public LinearLineOption(Context context)
    {
        super(context);
    }

    public LinearLineOption(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LinearLineOption(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinearLineOption(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected byte getMaxColorsInRow(Resources resources)
    {
        return Byte.MAX_VALUE;
    }

    @Override
    protected float getPaddingItem(Resources resources)
    {
        return (int) resources.getDimension(R.dimen.pallete_item_padding);
    }

    @Override
    protected CircleColorButton createNewCircle(final byte index)
    {
        ColorButtonChoose colorButton=new ColorButtonChoose(getContext());
        colorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(selected!=null)
                {
                    selected=index;
                    option.getColors()[selected]=-1;
                    selectedGlobal=index;
                    invalidate();

                }
            }
        });
        return colorButton;
    }

    @Override
    public void invalidate()
    {
        if(items!=null)
        {
            for (byte index = 0; index < items.length; index++)
            {
                ColorButtonChoose colorButton = (ColorButtonChoose) items[index];
                if (option == null)
                    colorButton.setValue((byte) -1);
                else
                    colorButton.setValue(option.getColors()[index]);
                colorButton.setSelectedColor(selected != null && index == selected);
            }
        }
        super.invalidate();
    }
    public void chooseColor(byte newValue)
    {
            byte[] colors = option.getColors();
            colors[selected]=newValue;
            moveSelectedNext();
            invalidate();
    }
    public CircleColorButton getTarget()
    {
        return items[this.selected];
    }

    private void moveSelectedNext()
    {
        byte[] colors = option.getColors();
        selected=(byte)(selected==colors.length-1?0:selected+1);
        if(!option.isFull())
        {
            while (colors[selected]!=-1)
            {
                selected=(byte)(selected==colors.length-1?0:selected+1);
            }
        };
        LinearLineOption.selectedGlobal=this.selected;
    }

}



