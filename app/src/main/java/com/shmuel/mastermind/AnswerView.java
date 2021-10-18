package com.shmuel.mastermind;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shmuel.mastermind.shared.beans.Result;

public class AnswerView extends View {
    private byte optionsLength;
    private Paint paint=new Paint();
    private byte maxItemsInRow;
    private byte rows;
    private byte itemsInRow;
    private byte itemsInLastRow;
    private float heightRow;
    private float widthItem;
    private float addLeftInLastRow;
    private Result answer;
    private final static int EMPTY=0;
    private final static int RIGHT=1;
    private final static int EXACT=2;

    public void setOptionsLength(byte optionsLength)
    {
        if(optionsLength!=this.optionsLength)
        {
            this.optionsLength = optionsLength;
            rows = (byte) ((this.optionsLength - 1) / maxItemsInRow + 1);
            itemsInRow = (byte) ((this.optionsLength - 1) / rows + 1);
            itemsInLastRow = (byte) (this.optionsLength - (rows - 1) * itemsInRow);
            addLeftInLastRow=(itemsInRow-itemsInLastRow)*0.5F;
        }
    }

    public Result getAnswer()
    {
        return answer;
    }

    public void setAnswer(Result answer)
    {
        this.answer = answer;
    }

    private void init()
    {
        Resources resources = getContext().getResources();
        maxItemsInRow= (byte) resources.getInteger(R.integer.answer_view_max_items_in_row);

    }

    public AnswerView(Context context) {
        super(context);
        init();
    }

    public AnswerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnswerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnswerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    private void drawColor(Canvas c, Paint p, float x,float y, int value, float radius)
    {
        Resources resources = getContext().getResources();

        if (value != EMPTY)
        {
            if (value == RIGHT)
                p.setColor(resources.getColor(R.color.colorRightAnswer));
            else if (value == EXACT)
                p.setColor(resources.getColor(R.color.colorExactAnswer));
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            c.drawCircle(x, y, radius, p);
        }
        p.setColor(resources.getColor(R.color.colorStroke));
        p.setStyle(Paint.Style.STROKE);
        c.drawCircle(x,y,radius,p);


    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(rows==0)return;
        heightRow=(getHeight()-getPaddingTop()-getPaddingBottom())/rows;

        int allWidth = getWidth()-getPaddingLeft()-getPaddingRight();
        widthItem= allWidth /(float) itemsInRow;

        Resources resources = getContext().getResources();
        float padding=resources.getDimension(R.dimen.answer_view_item_padding);
        float radius=Math.min(heightRow,widthItem)/2-padding;

        int rights=answer==null?0:answer.right;
        int exacts=answer==null?0:answer.exacts;
        for (int i = 0; i < rows; i++)
        {
            int columns;
            float addLeft;
            if (i == rows - 1)
            {
                columns = itemsInLastRow;
                addLeft=addLeftInLastRow+0.5F;
            }
            else
            {
                columns = itemsInRow;
                addLeft=0.5F;
            }
            for (int j = 0; j < columns; j++)
            {
                float x=widthItem*(j+addLeft)+getPaddingLeft();
                float y=heightRow*(i+0.5f)+getPaddingTop();
                int status;
                if(exacts>0)
                {
                    exacts--;
                    status=EXACT;
                }
                else if(rights>0)
                {
                    rights--;
                    status=RIGHT;
                }
                else
                {
                    status=EMPTY;
                }
                this.drawColor(canvas,paint,x,y, status,radius);
            }
        }
    }

}
