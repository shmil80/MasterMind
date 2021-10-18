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

import com.shmuel.mastermind.shared.beans.Option;

public class LineOption extends View {
    public static Byte selectedGlobal=0;
    private Option option;
    private Byte selected;
    private Paint paint=new Paint();;

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }
    public Byte getSelected() {
        return selected;
    }

    public void setSelected(Byte selected) {
        this.selected = selected;
    }

    public LineOption(Context context) {
        super(context);
    }

    public LineOption(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LineOption(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(selected!=null)
        {
            byte newSelected=(byte)(event.getX()*option.getColors().length/getWidth());
                selected=newSelected;
                option.getColors()[selected]=-1;
                selectedGlobal=newSelected;
                this.invalidate();
            return super.onTouchEvent(event);
        }
return true;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineOption(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    protected void drawRect(Canvas canvas, Paint p, double start, double end, float margin)
    {
        float width=canvas.getWidth();
        canvas.drawRect((float)(width*(1-end)),canvas.getHeight()*margin,(float)(width*(1-start)),canvas.getHeight()*(1-margin),p);
    }
    protected void drawRound(Canvas canvas, Paint p, double position, float radius)
    {
        float left= (float) (position);
        canvas.drawCircle(left,getPaddingTop()+(getHeight()-getPaddingBottom()-getPaddingTop())/2,radius,p);
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

    private void drawColor(Canvas c, Paint p, double position, byte value, float width, float padding, boolean isSelected)
    {
        Resources resources = getContext().getResources();
        float radius=Math.min(getHeight(),width)/2-padding;
        if(isSelected) {
            p.setStyle(Paint.Style.FILL);
            p.setColor(resources.getColor(R.color.colorBackgroundSelected));
            c.drawRect((float)(position-width/2),getPaddingTop(),(float)(position+width/2),getHeight()-getPaddingTop(),p);

        }
        if(value!=-1) {
                int id = resources.getIdentifier("colorOption" + value, "color", getContext().getPackageName());
                p.setColor(resources.getColor(id));
                p.setStyle(Paint.Style.FILL_AND_STROKE);
                this.drawRound(c,p,position,radius);
            }


        p.setColor(resources.getColor(R.color.colorStroke));
        p.setStyle(Paint.Style.STROKE);
        this.drawRound(c,p,position,radius);


    }
    @Override
    protected void onDraw(Canvas canvas) {

        Resources resources = getContext().getResources();
        float padding=resources.getDimension(R.dimen.line_padding);
        float allWidth=this.getWidth()-getPaddingLeft()-getPaddingRight();
        byte[] colors = this.option.getColors();
        float width=allWidth/ colors.length;
        for (int i = 0; i < colors.length; i++) {
            this.drawColor(canvas,paint,width*(i+0.5)+getPaddingLeft(),colors[i],width,padding,selected!=null&&selected==i);
        }
    }

    public void chooseColor(byte newValue)
    {
        byte[] colors = option.getColors();
        colors[this.selected]=newValue;
        moveSelectedNext();
        invalidate();

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
        LineOption.selectedGlobal=this.selected;
    }
}
