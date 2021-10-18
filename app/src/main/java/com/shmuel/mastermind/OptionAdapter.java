package com.shmuel.mastermind;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;

import com.shmuel.mastermind.shared.beans.OptionResult;
import com.shmuel.mastermind.shared.beans.Result;
import com.shmuel.mastermind.ui.views.LinearAnswerView;
import com.shmuel.mastermind.ui.views.LinearLineOption;

import java.util.List;

public class OptionAdapter extends ArrayAdapter<OptionResult>
{
    private final LayoutInflater inflater;
    private LinearLineOption currentLine;
    private boolean needAnimtaion;

    public void setNeedAnimtaion(boolean needAnimtaion)
    {
        this.needAnimtaion = needAnimtaion;
    }

    public OptionAdapter(Activity context, List<OptionResult> objects)
    {
        super(context, 0, objects);
        this.inflater = context.getLayoutInflater();
    }


    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {

        final OptionResult option = getItem(position);
        final ViewGroup layout;
        layout = (ViewGroup) inflater.inflate(R.layout.option_line, null); // null = not in use.
        LinearLineOption lineOption = layout.findViewById(R.id.lineOption);
        lineOption.setOption(option.getOption());
        LinearAnswerView answerView =layout.findViewById(R.id.lineAnswer);
        answerView.setLength((byte) option.getOption().getColors().length);
        Result result = option.getResult();
        Result oldResulr =answerView.getAnswer();
        if(result==null? oldResulr !=null: oldResulr ==null||!result.equals(oldResulr))
        {
            answerView.setAnswer(result);
            answerView.invalidate();
        }
        Byte newSelected;
        //if(position == getCount() - 1)
        if(position == 0)
        {
            currentLine=lineOption;
            newSelected = LinearLineOption.selectedGlobal;
            if(needAnimtaion)
            {
                Animation animation = AnimationUtils
                        .loadAnimation(getContext(), R.anim.slide_add_list_view);
                layout.startAnimation(animation);
                needAnimtaion=false;
            }
        }
        else
        {
            newSelected =null;
        }
        Byte oldselected = lineOption.getSelected();
        if(newSelected==null? oldselected !=null: oldselected ==null||!newSelected.equals(oldselected))
        {
            lineOption.setSelected(newSelected);
            lineOption.invalidate();
        }

        return layout;
    }

    public LinearLineOption getCurrentLine() {
        return currentLine;
    }
}
