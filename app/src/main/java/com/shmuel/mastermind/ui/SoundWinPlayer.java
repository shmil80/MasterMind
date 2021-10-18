package com.shmuel.mastermind.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.shmuel.mastermind.R;

public class SoundWinPlayer {

    private static final int MAX_SOUNDS_ALLOWED_AT_ONCE = 1;
    private static final int NOT_IN_USE = 0;
    private static final int PRIORITY = 1;
    private static final float VOLUME_LEFT = 1.0F;
    private static final float VOLUME_RIGHT = 1.0F;
    private static final int NO_REPEAT = 0;
    private static final float RATE_NORMAL = 1.0F;
    private SoundPool soundPool;
    private final int soundID;

    public SoundWinPlayer(Context context) {
        soundPool = new SoundPool(MAX_SOUNDS_ALLOWED_AT_ONCE, AudioManager.STREAM_MUSIC, NOT_IN_USE);
        soundID = soundPool.load(context, R.raw.win, PRIORITY);
    }



    public void playSound() {
        soundPool.play(soundID, VOLUME_LEFT, VOLUME_RIGHT, PRIORITY, NO_REPEAT, RATE_NORMAL);
    }

}
