package com.innotech.imap_taxi.utile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.preference.PreferenceManager;

import android.util.Log;
import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.helpers.ContextHelper;

public class PlaySound {

    private static PlaySound instance;
    private static AudioManager am;
    MediaPlayer mp;

    public static PlaySound getInstance() {

        if (instance == null) {
            instance = new PlaySound();
        }
        if (am == null) {
            am = (AudioManager) ContextHelper.getInstance().getCurrentContext().getSystemService(Context.AUDIO_SERVICE);
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        return instance;
    }

    public void play(int raw) {
        /*if (mp!=null)
			mp.reset();*/
        try {
            mp = MediaPlayer.create(ContextHelper.getInstance().getCurrentContext(), raw);
            if (mp != null) {
                mp.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        mp.release();
                    }

                });
                //mp.setOnPreparedListener(listener);
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext());
                int temp = sharedPrefs.getInt(UserSettingActivity.KEY_VOLUME, 50);
                float volume = temp / 100f;
                mp.setVolume(volume, volume);
                mp.start();
            }
        } catch (Exception e) {
            Log.d("Play Exception", "!!!!!!");
            e.printStackTrace();
        }
    }

}
