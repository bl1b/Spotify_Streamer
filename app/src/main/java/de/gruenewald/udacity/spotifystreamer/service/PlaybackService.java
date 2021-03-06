/**
 * ****************************************************************************
 * Copyright (c) 2015 by Jan Grünewald.
 * jan.gruenewald84@googlemail.com
 * <p/>
 * This file is part of 'Spotify Streamer'. 'Spotify Streamer' was developed as
 * part of the Android Developer Nanodegree by Udacity.
 * <p/>
 * 'Spotify Streamer' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * 'Spotify Streamer' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with 'Spotify Streamer'.  If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */

package de.gruenewald.udacity.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.gruenewald.udacity.spotifystreamer.model.IPlaybackServiceListener;
import de.gruenewald.udacity.spotifystreamer.model.PlaybackEntry;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;
import de.gruenewald.udacity.spotifystreamer.util.NumberUtil;

/**
 * Created by Jan on 19.06.2015.
 */
public class PlaybackService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String LOG_TAG = PlaybackService.class.getSimpleName();

    private final IBinder mBinder = new PlaybackBinder();

    private MediaPlayer mMediaPlayer;
    private ArrayList<PlaybackEntry> mPlaybackEntries;
    private int mTrackIndex;
    private final List<IPlaybackServiceListener> mPlaybackSeriveListeners = new ArrayList<IPlaybackServiceListener>();

    private final static int UPDATER_INTERVAL = 1000;

    private Thread updaterThread;

    private void startUpdater() {
        updaterThread = new Thread(new Runnable() {
            @Override public void run() {
                Thread myCurrent = Thread.currentThread();
                while (updaterThread == myCurrent) {
                    notifyUpdate();
                    try {
                        Thread.sleep(UPDATER_INTERVAL);
                    } catch (InterruptedException e) {
                        Log.e(LOG_TAG, "Error pausing update thread: " + e.getMessage());
                        break;
                    }
                }

            }
        });
        updaterThread.start();
    }

    private void stopUpdater() {
        updaterThread = null;
    }

    @Override public void onCreate() {
        super.onCreate();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    private int switchTrack(int pAmount) {
        int myNewIndex = mTrackIndex;

        if (myNewIndex >= 0 && mPlaybackEntries != null) {
            myNewIndex = NumberUtil.secureIndex((myNewIndex + pAmount), mPlaybackEntries.size());
        }

        return myNewIndex;
    }

    public void prevTrack() {
        //local variable to store info if track was playing (track will be stopped on reset())
        boolean myWasPlaying = isPlaying();
        //only switch to previous track if the current track is no longer playing than 5 seconds
        //if it's playing for more than 5 seconds seek to the start of the track
        if (mMediaPlayer != null && (mMediaPlayer.getCurrentPosition() <= 5000)) {
            mTrackIndex = switchTrack(-1);
            mMediaPlayer.reset();
            notifyStop();
            notifyChanged();
        } else if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(0);
            notifyChanged();
        }

        if (myWasPlaying) {
            playTrack();
        }
    }

    public void nextTrack() {
        //local variable to store info if track was playing (will be false as soon as reset() is called)
        boolean myWasPlaying = isPlaying();

        mTrackIndex = switchTrack(1);
        //always reset the mediaplayer (to cause playTrack() to play the new track at new index)
        mMediaPlayer.reset();
        notifyStop();
        notifyChanged();

        if (myWasPlaying) {
            playTrack();
        }
    }

    public void playTrack() {
        int myCurrentPosition;

        try {
            myCurrentPosition = mMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            Log.w(LOG_TAG, "Can't fetch current position from mediaplayer: " + e.getMessage());
            myCurrentPosition = 0;
        }

        // only reset MediaPlayer and load a new track if the play
        if (mMediaPlayer != null && !isPlaying() && myCurrentPosition == mMediaPlayer.getDuration()) {
            mMediaPlayer.reset();
            if (mPlaybackEntries != null && mPlaybackEntries.get(mTrackIndex) != null) {
                TrackListEntry myEntry = mPlaybackEntries.get(mTrackIndex).getTrackListEntry();
                if (myEntry.getPreviewUrl() != null) {
                    try {
                        mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(myEntry.getPreviewUrl()));
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Unable to set data-source: '" + myEntry.getPreviewUrl() + "' -> " + e.getMessage());
                    }
                    mMediaPlayer.prepareAsync();
                }
            }
        } else if (mMediaPlayer != null) {
            mMediaPlayer.start();
            notifyStart();
        }
    }

    public void jumpToPosition(int pPosition) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.seekTo(pPosition);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error seeking to position: " + e.getMessage());
        }
    }

    public void pauseTrack() {
        mMediaPlayer.pause();
        notifyStop();
    }

    @Override public void onCompletion(MediaPlayer mp) {
        mTrackIndex = switchTrack(1);
        //always reset the mediaplayer (to cause playTrack() to play the new track at new index)
        mMediaPlayer.reset();
        notifyStop();
        notifyChanged();
        playTrack();
    }

    @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        stopUpdater();
        if (mp != null) {
            mp.reset();
            notifyStop();
        }
        return false;
    }

    @Override public void onPrepared(MediaPlayer mp) {
        mp.start();
        notifyStart();
    }

    public boolean isPlaying() {
        boolean myResult = false;

        if (mMediaPlayer != null) {
            try {
                myResult = mMediaPlayer.isPlaying();
            } catch (Exception e) {
                Log.w(LOG_TAG, "Can't determine whether the player is playing: " + e.getMessage());
                myResult = false;
            }
        }

        return myResult;
    }

    @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        notifyStop();

        //when the service is destory release all references to listener-objects (so they can be
        //gced)
        for (IPlaybackServiceListener listener : mPlaybackSeriveListeners) {
            listener = null;
        }

        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override public boolean onUnbind(Intent intent) {
        return false;
    }

    public class PlaybackBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    public void setPlaybackEntries(ArrayList<PlaybackEntry> pPlaybackEntries) {
        mPlaybackEntries = pPlaybackEntries;
    }

    public void setTrackIndex(int pTrackIndex) {
        mTrackIndex = pTrackIndex;
    }

    public boolean addPlaybackServiceListener(IPlaybackServiceListener pNewPlaybackServiceListener) {
        boolean myResult = false;
        if (mPlaybackSeriveListeners != null && !mPlaybackSeriveListeners.contains(pNewPlaybackServiceListener)) {
            myResult = mPlaybackSeriveListeners.add(pNewPlaybackServiceListener);
        }
        return myResult;
    }

    public boolean removePlaybackServiceListener(IPlaybackServiceListener pOldPlaybackServiceListener) {
        boolean myResult = false;
        if (mPlaybackSeriveListeners != null && mPlaybackSeriveListeners.contains(pOldPlaybackServiceListener)) {
            myResult = mPlaybackSeriveListeners.remove(pOldPlaybackServiceListener);
        }
        return myResult;
    }


    private void notifyStart() {
        startUpdater();
        for (IPlaybackServiceListener listener : mPlaybackSeriveListeners) {
            listener.OnPlaybackStarted(mPlaybackEntries.get(mTrackIndex));
        }
    }

    private void notifyUpdate() {
        for (IPlaybackServiceListener listener : mPlaybackSeriveListeners) {
            int myCurPos = -1;
            int myEndPos = -1;

            if (mMediaPlayer != null) {
                myCurPos = mMediaPlayer.getCurrentPosition();
                myEndPos = mMediaPlayer.getDuration();
            }

            listener.OnPlaybackUpdated(mPlaybackEntries.get(mTrackIndex), myCurPos, myEndPos);
        }
    }

    private void notifyChanged() {
        for (IPlaybackServiceListener listener : mPlaybackSeriveListeners) {
            listener.OnPlaybackChanged(mPlaybackEntries.get(mTrackIndex), mTrackIndex);
        }
    }

    private void notifyStop() {
        stopUpdater();
        for (IPlaybackServiceListener listener : mPlaybackSeriveListeners) {
            listener.OnPlaybackStopped(mPlaybackEntries.get(mTrackIndex));
        }
    }
}
