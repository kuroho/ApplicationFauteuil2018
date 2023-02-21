package com.example.guedira.swcontol;

import android.media.MediaPlayer;

/**
 * Created by guedira on 20/04/2016.
 */
public class PrepListener implements MediaPlayer.OnPreparedListener {
    public void onPrepared(MediaPlayer mp) {
        // Do something. For example: playButton.setEnabled(true);
        mp.start();
    }
}
