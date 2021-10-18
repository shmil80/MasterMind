package com.shmuel.mastermind;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.shmuel.mastermind.ui.SoundTickPlayer;
import com.shmuel.mastermind.ui.views.CircleColorButton;

public class ChooseColorAnimation
{
    private final CircleColorButton viewAnim;
    private final float speed;
    private final SoundTickPlayer soundTickPlayer;
    private final View parentView;
    private boolean sound;
    public void setSound(boolean sound)
    {
        this.sound = sound;
    }

    public ChooseColorAnimation(CircleColorButton viewAnim)
    {
        this.viewAnim = viewAnim;
        this.parentView= (View) viewAnim.getParent();
        Context context = viewAnim.getContext();
        this.soundTickPlayer = new SoundTickPlayer(context);
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(R.dimen.anim_speed, outValue, true);
        this.speed=outValue.getFloat();
    }

    private Interpolator getInterpolator()
    {
         return new AccelerateDecelerateInterpolator();
    }

    public void animate(final CircleColorButton source, CircleColorButton target,final Runnable afterAnim)
    {
        viewAnim.setValue(source.getValue());

        final int sourceWidth = source.getMeasuredWidth();
        final int sourceHeight = source.getMeasuredHeight();
        final int targetWidth=target.getMeasuredWidth();
        final int targetHeight=target.getMeasuredHeight();

        final int[] sourceLoc=new int[2],targetLoc=new int[2],parentLoc=new int[2];

        source.getLocationOnScreen(sourceLoc);
        target.getLocationOnScreen(targetLoc);
        parentView.getLocationOnScreen(parentLoc);

        final int startMarginLeft=sourceLoc[0]-parentLoc[0];
        final int diffMarginLeft=targetLoc[0]-sourceLoc[0];
        final int startMarginTop=sourceLoc[1]-parentLoc[1];
        final int diffMarginTop=targetLoc[1]-sourceLoc[1];

        double travel=Math.sqrt(diffMarginLeft*diffMarginLeft+startMarginTop*startMarginTop);
        long duration=(long) (travel/speed);

        final FrameLayout.LayoutParams viewAnimLayoutParams = (FrameLayout.LayoutParams) viewAnim.getLayoutParams();

        ValueAnimator animator = ValueAnimator.ofFloat(0,1);
        animator.setDuration(duration);
        animator.setInterpolator(getInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float percent = (float) animation.getAnimatedValue();
                viewAnimLayoutParams.width = (int) (sourceWidth+ percent*(targetWidth-sourceWidth));
                viewAnimLayoutParams.height = (int) (sourceHeight+ percent*(targetHeight-sourceHeight));
                viewAnimLayoutParams.setMargins((int) (startMarginLeft+percent*diffMarginLeft), (int) (startMarginTop+percent*diffMarginTop), 0, 0);
                viewAnim.setLayoutParams(viewAnimLayoutParams);
                viewAnim.invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation)
            {
                viewAnim.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                viewAnim.setVisibility(View.GONE);
                if(sound){
                    soundTickPlayer.playSound();
                }
                afterAnim.run();
            }
        });
        animator.start();

    }

}
