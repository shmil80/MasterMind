package com.shmuel.mastermind.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;

import com.shmuel.mastermind.R;

public class ColorButtonChoose extends CircleColorButton
{

    private boolean selected;
    public ColorButtonChoose(Context context)
    {
        super(context);
    }

    public ColorButtonChoose(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ColorButtonChoose(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getColor(byte value)
    {
        if(value!=-1)
        {
            Resources resources = getContext().getResources();
            int id = resources.getIdentifier("colorOption" + value, "color", getContext().getPackageName());
            return resources.getColor(id);
        }
        else
        {
            return Color.TRANSPARENT;
        }
    }
    @Override
    protected int getStrokeWidth()
    {
        Resources resources = getContext().getResources();
        return  selected?resources.getInteger(R.integer.choose_color_select_stroke):1;

    }

//    @Override
//    protected void assignShape(GradientDrawable shape)
//    {
//        if(!selected)
//        super.assignShape(shape);
//        else {
//            Resources resources = getContext().getResources();
//            GradientDrawable selectedShape=new GradientDrawable();
//            selectedShape.setColor(resources.getColor(R.color.colorBackgroundSelected));
//            selectedShape.setShape(GradientDrawable.RECTANGLE);
//            Drawable[] layers = {selectedShape, shape};
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            this.setBackgroundDrawable(layerDrawable);
//        }
//    }

    public boolean isSelectedColor()
    {
        return selected;
    }

    public void setSelectedColor(boolean selected)
    {
        if(this.selected!=selected)
        {
            this.selected = selected;
            invalidate();
        }

    }
}
