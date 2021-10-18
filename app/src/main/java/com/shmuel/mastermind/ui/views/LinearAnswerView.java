package com.shmuel.mastermind.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.shmuel.mastermind.R;
import com.shmuel.mastermind.shared.beans.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shmuel.mastermind.ui.views.ColorButtonAnswer.EMPTY;
import static com.shmuel.mastermind.ui.views.ColorButtonAnswer.EXACT;
import static com.shmuel.mastermind.ui.views.ColorButtonAnswer.RIGHT;

public class LinearAnswerView extends LinearCircles {
    private Result answer;


    public Result getAnswer()
    {
        return answer;
    }

    public void setAnswer(Result answer)
    {
        this.answer = answer;
        invalidate();
    }


    public LinearAnswerView(Context context) {
        super(context);
    }

    public LinearAnswerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearAnswerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LinearAnswerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected byte getMaxColorsInRow(Resources resources)
    {
        return (byte) resources.getInteger(R.integer.answer_view_max_items_in_row);
    }

    @Override
    protected float getPaddingItem(Resources resources)
    {
        return resources.getDimension(R.dimen.answer_view_item_padding);
    }

    @Override
    protected CircleColorButton createNewCircle(byte index)
    {
        ColorButtonAnswer colorButton=new ColorButtonAnswer(getContext());

        colorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {

            }
        });
        return colorButton;
    }

    public void         invalidate()
    {
        if(items!=null)
        {
            for (byte index = 0; index < items.length; index++)
            {
                CircleColorButton colorButton = items[index];
                if (answer == null || index >= answer.exacts + answer.right)
                    colorButton.setValue(EMPTY);
                else if (index >= answer.exacts)
                    colorButton.setValue(RIGHT);
                else
                    colorButton.setValue(EXACT);
                colorButton.invalidate();
            }
        }
        super.invalidate();
    }


}
