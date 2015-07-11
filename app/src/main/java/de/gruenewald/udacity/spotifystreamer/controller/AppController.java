/**
 * ****************************************************************************
 * Copyright (c) 2015 by Jan Grünewald.
 * jan.gruenewald84@googlemail.com
 * <p>
 * This file is part of 'Spotify Streamer'. 'Spotify Streamer' was developed as
 * part of the Android Developer Nanodegree by Udacity.
 * <p>
 * 'Spotify Streamer' is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * 'Spotify Streamer' is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with 'Spotify Streamer'.  If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */

package de.gruenewald.udacity.spotifystreamer.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.gruenewald.udacity.spotifystreamer.ArtistFragment;
import de.gruenewald.udacity.spotifystreamer.MainActivity;
import de.gruenewald.udacity.spotifystreamer.PlaybackFragment;
import de.gruenewald.udacity.spotifystreamer.R;
import de.gruenewald.udacity.spotifystreamer.TrackActivity;
import de.gruenewald.udacity.spotifystreamer.TrackFragment;
import de.gruenewald.udacity.spotifystreamer.exception.MissingDependencyException;
import de.gruenewald.udacity.spotifystreamer.exception.ParameterException;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;
import de.gruenewald.udacity.spotifystreamer.model.PlaybackEntry;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jan on 27.06.2015.
 */
public class AppController {
    private static final String LOG_TAG = AppController.class.getSimpleName();
    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    public static final SpotifyApi SPOTIFY_API = new SpotifyApi();
    public static final String TRACK_PLAYBACK_TAG = "tag_fragment_playback";

    private static AppController instance = new AppController();

    private MainActivity mMainActivity;
    private TrackActivity mTrackActivity;

    private ArtistFragment mArtistFragment;
    private TrackFragment mTrackFragment;
    private PlaybackFragment mPlaybackFragment;

    private int mArtistListPosition;
    private int mTrackListPosition;

    private AppController() {
        Log.v(LOG_TAG, "Creating new instance of AppController.");

        unregisterMainActivity();
        unregisterTrackActivity();

        unregisterArtistFragment();
        unregisterTrackFragment();
        unregisterPlaybackFragment();
    }

    public static AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }

        return instance;
    }


    public void handleOnArtistSelected(final int pArtistPosition, final boolean pIsTabletMode) throws MissingDependencyException, ParameterException {
        registerArtistListPosition(pArtistPosition);

        if (mArtistListPosition <= -1) {
            throw new ParameterException("Can't execute 'handleOnArtistSelected'. Invalid artist's position.");
        }

        if (mMainActivity == null) {
            throw new MissingDependencyException("Can't execute 'handleOnArtistSelected'. Missing dependency: 'MainActivity'.");
        }

        if (pIsTabletMode && mTrackFragment == null) {
            throw new MissingDependencyException("Can't execute 'handleOnArtistSelected'. Missing dependency: 'TrackFragment'.");
        }

        if (mArtistFragment == null) {
            throw new MissingDependencyException("Can't execute: 'handleOnArtistSelected'. Missing dependency: 'ArtistFragment'.");
        }

        final ArtistListEntry myArtistEntry = mArtistFragment.getArtistListEntries().get(mArtistListPosition);

        //setup the request object for the top-track query
        Map<String, Object> myParameterMap = new HashMap<String, Object>();
        // TODO: Read the country from the settings
        myParameterMap.put("country", "US");

        SPOTIFY_API.getService().getArtistTopTrack(myArtistEntry.getArtistId(), myParameterMap, new Callback<Tracks>() {
            @Override
            public void success(Tracks t, Response response) {
                if (t != null && t.tracks != null && t.tracks.size() > 0) {
                    final ArrayList<TrackListEntry> myTrackListEntries = new ArrayList<TrackListEntry>();
                    for (Track myTrack : t.tracks) {
                        TrackListEntry myCurrentEntry = new TrackListEntry(myTrack.id);
                        myCurrentEntry.setTrackName(myTrack.name);
                        if (myTrack.album != null) {
                            myCurrentEntry.setAlbumName(myTrack.album.name);
                            if (myTrack.album.images != null && myTrack.album.images.size() > 0) {
                                myCurrentEntry.setAlbumCover(myTrack.album.images.get(myTrack.album.images.size() - 1).url);
                                myCurrentEntry.setAlbumCoverLarge(myTrack.album.images.get(0).url);
                            }

                            myCurrentEntry.setPreviewUrl(myTrack.preview_url);
                            myCurrentEntry.setDuration(myTrack.duration_ms);
                        }
                        myTrackListEntries.add(myCurrentEntry);
                    }

                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            //Check if mTrackFragment is set. If not we're on a smartphone
                            //and start a new TrackActivity as an intent. Else we re-use
                            //the fragment's instance and update the view.
                            if (mTrackFragment == null) {
                                Intent trackIntent = new Intent(mMainActivity, TrackActivity.class);
                                trackIntent.putExtra(TrackActivity.EXTRA_ARTISTENTRY, myArtistEntry);
                                trackIntent.putParcelableArrayListExtra(TrackActivity.EXTRA_TRACKLIST, myTrackListEntries);
                                mMainActivity.startActivity(trackIntent);
                            } else {
                                mTrackFragment.setTitle(myArtistEntry.getArtistName());
                                mTrackFragment.setNofResults(myTrackListEntries.size());
                                mTrackFragment.populateListView(myTrackListEntries);
                            }
                        }
                    });
                } else {
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mMainActivity, R.string.track_search_error_noresults, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MAIN_THREAD.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mMainActivity, R.string.track_search_error_default, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * This function will be called when a track from the track list is separated.
     * It will then launch a {@link PlaybackFragment} either as fragment or as dialog
     * depending on the device specification (tablet / phone).
     *
     * @param pTrackPosition The position of the Item which was selected.
     * @param pIsTabletMode  A boolean indicating whether the device is a tablet or not.
     * @throws MissingDependencyException Will be thrown if a required dependency is missing.
     * @throws ParameterException         Will be thrown if a provided parameter is invalid.
     */
    public void handleOnTrackSelected(final int pTrackPosition, final boolean pIsTabletMode) throws MissingDependencyException, ParameterException {

        registerTrackListPosition(pTrackPosition);

        if (mTrackListPosition <= -1) {
            throw new ParameterException("Can't execute 'handleOnTrackSelected'. Invalid track's position.");
        }

        if (mArtistListPosition <= -1) {
            throw new MissingDependencyException("Can't execute 'handleOnTrackSelected'. Missing dependency: 'ArtistListPosition'.");
        }

        //we'll use this dependency to fetch the currently displayed tracklist which will then be
        //passed to the PlaybackFragment
        if (mTrackFragment == null) {
            throw new MissingDependencyException("Can't execute: 'handleOnTrackSelected'. Missing dependency: 'TrackFragment'.");
        }

        //we'll use this dependency to fetch the currently selected artist (which links to the
        //tracklist) and which will be passed to the PlaybackFragment
        if (mArtistFragment == null) {
            throw new MissingDependencyException("Can't execute: 'handleOnTrackSelected'. Missing dependency: 'ArtistFragment'.");
        }

        //if we are in table-mode we need the MainActivity to register the PlaybackFragment on.
        if (pIsTabletMode && mMainActivity == null) {
            throw new MissingDependencyException("Can't execute: 'handleOnTrackSelected'. Missing dependency: 'MainActivity'.");
        }

        //if we are in phone-mode we need the TrackActivity to register the PlaybackFragment on.
        if (!pIsTabletMode && mTrackActivity == null) {
            throw new MissingDependencyException("Can't execute 'handleOnTrackSelected.'. Missing dependency: 'TrackActivity'.");
        }

        ArrayList<PlaybackEntry> myPlaybackEntries = new ArrayList<PlaybackEntry>();

        //fetch the playback-entries from the currently displayed track-list
        for (TrackListEntry myEntry : mTrackFragment.getTrackListEntries()) {
            PlaybackEntry myNewPlaybackEntry = new PlaybackEntry();
            myNewPlaybackEntry.setArtistListEntry(mArtistFragment.getArtistListEntries().get(mArtistListPosition));
            myNewPlaybackEntry.setTrackListEntry(myEntry);
            myPlaybackEntries.add(myNewPlaybackEntry);
        }

        //prepare the data bundle for the playback fragment
        Bundle myDataBundle = new Bundle();
        myDataBundle.putInt(PlaybackFragment.ARG_PLAYBACK_TRACKINDEX, pTrackPosition);
        myDataBundle.putParcelableArrayList(PlaybackFragment.ARG_PLAYBACK_DATALIST, myPlaybackEntries);

        //register the PlaybackFragment
        registerPlaybackFragment(PlaybackFragment.newInstance(myDataBundle));

        //launch the PlaybackFragment depending on whether we are running on tablet or phone
        //remove any existing PlaybackFragment's which might have been registered previously
        //(that should not happen)
        if (!pIsTabletMode) {
            FragmentManager myFM = mTrackActivity.getSupportFragmentManager();
            FragmentTransaction myFT = myFM.beginTransaction();

            Fragment myExisting = myFM.findFragmentByTag(TRACK_PLAYBACK_TAG);
            if (myExisting != null) {
                Log.w(LOG_TAG, "Had to remove a previously existing PlaybackFragment.");
                myFT.remove(myExisting);
            }

            myFT.replace(R.id.fragment_playback_container, mPlaybackFragment, TRACK_PLAYBACK_TAG)
                    .addToBackStack(null)
                    .commit();
        } else {
            FragmentManager myFM = mMainActivity.getSupportFragmentManager();
            FragmentTransaction myFT = myFM.beginTransaction();

            Fragment myExisting = myFM.findFragmentByTag(TRACK_PLAYBACK_TAG);
            if (myExisting != null) {
                Log.w(LOG_TAG, "Had to remove a previously existing PlaybackFragment.");
                myFT.remove(myExisting);
            }

            myFT.addToBackStack(null);
            mPlaybackFragment.show(myFT, TRACK_PLAYBACK_TAG);
        }
    }

    public void registerMainActivity(MainActivity pMainActivity) {
        mMainActivity = pMainActivity;
    }

    public void unregisterMainActivity() {
        mMainActivity = null;
        unregisterArtistFragment();
        unregisterTrackFragment();
    }

    public void registerTrackActivity(TrackActivity pTrackActivity) {
        mTrackActivity = pTrackActivity;
    }

    public void unregisterTrackActivity() {
        mTrackActivity = null;
        unregisterTrackFragment();
    }

    public void registerArtistFragment(ArtistFragment pArtistFragment) {
        mArtistFragment = pArtistFragment;

        if (mArtistFragment != null) {
            registerArtistListPosition(mArtistFragment.getArtlistPosition());
        }
    }

    public void unregisterArtistFragment() {
        mArtistFragment = null;
        unregisterArtistListPosition();
    }

    public void registerTrackFragment(TrackFragment pTrackFragment) {
        mTrackFragment = pTrackFragment;

        if (mTrackFragment != null) {
            registerTrackListPosition(mTrackFragment.getTrackListPosition());
        }
    }

    public void unregisterTrackFragment() {
        mTrackFragment = null;
        unregisterTrackListPosition();
    }

    public void registerPlaybackFragment(PlaybackFragment pPlaybackFragment) {
        mPlaybackFragment = pPlaybackFragment;
    }

    public void unregisterPlaybackFragment() {
        mPlaybackFragment = null;
    }

    public void registerArtistListPosition(int pArtistListPosition) {
        int myResult = -1;

        if (mArtistFragment != null && mArtistFragment.getArtistListEntries() != null && pArtistListPosition >= 0 && mArtistFragment.getArtistListEntries().size() > pArtistListPosition) {
            myResult = pArtistListPosition;
        }

        mArtistListPosition = myResult;
    }

    public void unregisterArtistListPosition() {
        mArtistListPosition = -1;
    }

    public void registerTrackListPosition(int pTrackListPosition) {
        int myResult = -1;

        if (mTrackFragment != null && mTrackFragment.getTrackListEntries() != null && pTrackListPosition >= 0 && mTrackFragment.getTrackListEntries().size() > pTrackListPosition) {
            myResult = pTrackListPosition;
        }

        mTrackListPosition = myResult;
    }

    public int getTrackListPosition() {
        return mTrackListPosition;
    }

    public void unregisterTrackListPosition() {
        mTrackListPosition = -1;
    }
}
