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

package de.gruenewald.udacity.spotifystreamer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.gruenewald.udacity.spotifystreamer.controller.AppController;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;
import de.gruenewald.udacity.spotifystreamer.model.PlaybackEntry;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;
import de.gruenewald.udacity.spotifystreamer.util.StringUtil;

/**
 * Created by Jan on 19.06.2015.
 */
public class PlaybackFragment extends DialogFragment {

    static final String LOG_TAG = PlaybackFragment.class.getSimpleName();

    static public final String ARG_PLAYBACK_TRACKINDEX = "arg_playback_trackindex";
    static public final String ARG_PLAYBACK_DATALIST = "arg_playback_datalist";

    private int mTrackIndex;
    private View mRootView;
    private ArrayList<PlaybackEntry> mPlaybackEntries;
    private final List<DialogInterface.OnDismissListener> mOnDismissListeners = new ArrayList<DialogInterface.OnDismissListener>();

    @InjectView(R.id.fragment_playback_text_artist) TextView mArtistTextview;
    @InjectView(R.id.fragment_playback_text_album) TextView mAlbumTextview;
    @InjectView(R.id.fragment_playback_image_cover) ImageView mCoverImageview;
    @InjectView(R.id.fragment_playback_text_track) TextView mTrackTextview;
    @InjectView(R.id.fragment_playback_scrub_track) SeekBar mScrubSeekbar;
    @InjectView(R.id.fragment_playback_button_prev) ImageButton mPreviousButton;
    @InjectView(R.id.fragment_playback_button_play) ImageButton mPlayButton;
    @InjectView(R.id.fragment_playback_button_next) ImageButton mNextButton;

    //static creator for a new PlaybackFragment-Instance
    //best practice since Fragments do not support typed constructors
    public static PlaybackFragment newInstance(Bundle pDataBundle) {
        PlaybackFragment myNewFragment = new PlaybackFragment();
        myNewFragment.setArguments(pDataBundle);
        return myNewFragment;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_playback, container, false);
            ButterKnife.inject(this, mRootView);

            //repopulate the views with the provided arguments; if no external arguments are
            //found try to use the savedInstanceState
            Bundle myBundle = getArguments();
            if (myBundle == null && savedInstanceState != null) {
                myBundle = getArguments();
            }

            if (myBundle != null) {
                mTrackIndex = myBundle.getInt(ARG_PLAYBACK_TRACKINDEX);
                mPlaybackEntries = myBundle.getParcelableArrayList(ARG_PLAYBACK_DATALIST);
            }
        }
        return mRootView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            updateViews();
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_PLAYBACK_TRACKINDEX, mTrackIndex);
        outState.putParcelableArrayList(ARG_PLAYBACK_DATALIST, mPlaybackEntries);

        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.fragment_playback_button_prev)
    public void onPrevButtonClick(View pView) {
        prevTrack();
    }

    @OnClick(R.id.fragment_playback_button_play)
    public void onPlayButtonClick(View pView) {
        playTrack();
    }

    @OnClick(R.id.fragment_playback_button_next)
    public void onNextButtonClick(View pView) {
        nextTrack();
    }

    private void updateViews() {
        if (mPlaybackEntries != null && mTrackIndex >= 0 && mTrackIndex < mPlaybackEntries.size()) {

            AppController.getInstance().registerTrackListPosition(mTrackIndex);
            PlaybackEntry myEntry = mPlaybackEntries.get(mTrackIndex);

            if (myEntry != null) {

                ArtistListEntry myArtist = myEntry.getArtistListEntry();
                TrackListEntry myTrack = myEntry.getTrackListEntry();

                if (mArtistTextview != null && myArtist != null && !StringUtil.isEmpty(myArtist.getArtistName())) {
                    mArtistTextview.setText(myArtist.getArtistName());
                }

                if (mAlbumTextview != null && myTrack != null && !StringUtil.isEmpty(myTrack.getAlbumName())) {
                    mAlbumTextview.setText(myTrack.getAlbumName());
                }

                if (mCoverImageview != null && myTrack != null && !StringUtil.isEmpty(myTrack.getAlbumCoverLarge())) {
                    Picasso.with(getActivity()).load(myTrack.getAlbumCoverLarge()).into(mCoverImageview);
                }

                if (mTrackTextview != null && myTrack != null && !StringUtil.isEmpty(myTrack.getTrackName())) {
                    mTrackTextview.setText(myTrack.getTrackName());
                }
            } else {

                Log.w(LOG_TAG, "Entry in Playback-List is null!");

            }

        }
    }

    public void prevTrack() {
        mTrackIndex--;
        if (mTrackIndex < 0) {
            mTrackIndex = mPlaybackEntries.size() - 1;
        }

        updateViews();
    }

    public void playTrack() {

    }

    public void nextTrack() {
        mTrackIndex++;
        if (mTrackIndex > mPlaybackEntries.size() - 1) {
            mTrackIndex = 0;
        }

        updateViews();
    }

    public boolean addOnDismissListener(DialogInterface.OnDismissListener pOnDismissListener) {
        boolean result = false;

        if (mOnDismissListeners != null && !mOnDismissListeners.contains(pOnDismissListener)) {
            result = mOnDismissListeners.add(pOnDismissListener);
        }

        return result;
    }

    public boolean removeOnDismissListener(DialogInterface.OnDismissListener pOnDismissListener) {
        boolean result = false;

        if (mOnDismissListeners != null && mOnDismissListeners.contains(pOnDismissListener)) {
            result = mOnDismissListeners.remove(pOnDismissListener);
        }

        return result;
    }
}