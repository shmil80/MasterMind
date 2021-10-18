package com.shmuel.mastermind;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shmuel.mastermind.shared.beans.Configuration;
import com.shmuel.mastermind.ui.views.LinearLineOption;

public class Pallete extends View {
    private byte optionPosibilities;
    private Paint paint=new Paint();
    private byte maxColorsInRow;
    private byte rows;
    private byte colorsInRow;
    private byte colorsInLastRow;
    private float heightRow;
    private float widthColor;
    private float addLeftInLastRow;
    private Configuration config;
    private OptionAdapter optionAdapter;
    private boolean inAnimation;

    public void setConfig(Configuration config) {
        this.config = config;
        this.optionPosibilities = (byte) (config.getOptionPosibilities());
        rows= (byte) ((this.optionPosibilities-1)/maxColorsInRow+1);
        colorsInRow= (byte) ((this.optionPosibilities-1)/rows+1);
        colorsInLastRow= (byte) (this.optionPosibilities-(rows-1)*colorsInRow);
        addLeftInLastRow=(colorsInRow-colorsInLastRow)*0.5F;
    }
    public OptionAdapter getOptionAdapter() {
        return optionAdapter;
    }

    public void setOptionAdapter(OptionAdapter optionAdapter) {
        this.optionAdapter = optionAdapter;
    }

    private void init()
    {
        Resources resources = getContext().getResources();
        maxColorsInRow= (byte) resources.getInteger(R.integer.pallete_max_color_in_row);

    }

    public Pallete(Context context) {
        super(context);
        init();
    }

    public Pallete(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Pallete(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(LinearLineOption.selectedGlobal!=null&&!inAnimation)
        {
            inAnimation=true;
            int rowNumber=(int)(event.getY()/heightRow);
            int colorsFromFirstRows=rowNumber*colorsInRow;
            int colorsFromCurrentRow=(int)(event.getX()/widthColor-(rowNumber==rows-1?addLeftInLastRow:0));
            byte newValue= (byte) Math.min(colorsFromFirstRows+colorsFromCurrentRow,config.getOptionPosibilities()-1);
            LinearLineOption currentLine = optionAdapter.getCurrentLine();
            currentLine.chooseColor(newValue);
        }
        return super.onTouchEvent(event);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Pallete(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    protected void drawRect(Canvas canvas, Paint p, double start, double end, float margin)
    {
        float width=canvas.getWidth();
        canvas.drawRect((float)(width*(1-end)),canvas.getHeight()*margin,(float)(width*(1-start)),canvas.getHeight()*(1-margin),p);
    }
    protected void drawText(Canvas canvas, Paint p,String text, double position)
    {
        float width=canvas.getWidth();
        float widthText= p.measureText(text);
        float left= (float) Math.max(width-widthText,position-widthText/2);

        canvas.drawText(text,left,(canvas.getHeight()+p.getTextSize())*0.5F-2,p);
    }
    protected void drawPict(Canvas canvas, Paint p, double position, Bitmap image)
    {
        float width=canvas.getWidth();
        Rect src=new Rect(0,0,image.getWidth(),image.getHeight());
        float size=canvas.getHeight();
        float left=Math.min(width-size,Math.max((float)(width*(1-position))-size/2F,0F));
        float top=Math.max((canvas.getHeight()-size)/2F,0F);
        RectF dst=new RectF(left,top,left+size,top+size);
        canvas.drawBitmap(image,src,dst,p);
    }

    private void drawColor(Canvas c, Paint p, float x,float y, byte value, float radius)
    {
        Resources resources = getContext().getResources();
        if(value!=-1)
        {
            int id = resources.getIdentifier("colorOption" + value, "color", getContext().getPackageName());
            p.setColor(resources.getColor(id));
        }
        else
        {
            p.setColor(Color.WHITE);
        }
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        c.drawCircle(x,y,radius,p);


        p.setColor(resources.getColor(R.color.colorStroke));
        p.setStyle(Paint.Style.STROKE);
        c.drawCircle(x,y,radius,p);


    }
    @Override
    protected void onDraw(Canvas canvas) {
if(rows==0)return;
        heightRow=(getHeight()-getPaddingBottom()-getPaddingTop())/rows;

        int allWidth = getWidth()-getPaddingRight()-getPaddingLeft();
        widthColor= allWidth /(float)colorsInRow;

        Resources resources = getContext().getResources();
        float padding=resources.getDimension(R.dimen.pallete_item_padding);
        float radius=Math.min(heightRow,widthColor)/2-padding;


        for (int i = 0; i < rows; i++)
        {
            int columns;
            float addLeft;
            if (i == rows - 1)
            {
                columns = colorsInLastRow;
                addLeft=addLeftInLastRow+0.5F;
            }
            else
            {
                columns = colorsInRow;
                addLeft=0.5F;
            }
            for (int j = 0; j < columns; j++)
            {
                float x=widthColor*(j+addLeft)+getPaddingLeft();
                float y=heightRow*(i+0.5f)+getPaddingTop();
                this.drawColor(canvas,paint,x,y, (byte) (i*colorsInRow+j),radius);
            }
        }
    }

}
