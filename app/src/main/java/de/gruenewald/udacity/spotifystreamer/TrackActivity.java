/**
 * ****************************************************************************
 * Copyright (c) 2015 by Jan Grünewald.
 * jan.gruenewald84@googlemail.com
 * --------------------------------------------------------------------------------
 * This file is part of 'Spotify Streamer'. 'Spotify Streamer' was developed as
 * part of the Android Developer Nanodegree by Udacity. For further
 * information see:
 * https://www.udacity.com/course/android-developer-nanodegree--nd801
 * --------------------------------------------------------------------------------
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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnItemClick;
import de.gruenewald.udacity.spotifystreamer.controller.AppController;
import de.gruenewald.udacity.spotifystreamer.exception.MissingDependencyException;
import de.gruenewald.udacity.spotifystreamer.exception.ParameterException;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;


public class TrackActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    static final String LOG_TAG = TrackActivity.class.getSimpleName();

    static final String TRACK_FRAGMENT_TAG = "tag_fragment_track";


    static public final String EXTRA_ARTISTENTRY = "track_extra_artistentry";
    static public final String EXTRA_TRACKLIST = "track_extra_list";

    private ArtistListEntry mArtistListEntry;
    private TrackFragment mTrackFragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //constrain orientation to portrait on smartphones
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_track);

        AppController.getInstance().registerTrackActivity(this);

        if (savedInstanceState == null) {
            //If onCreate() is called we're in onePane-Mode. That means MainActivity should have
            //passed in the tracklist-data via Intent-Extras.
            mArtistListEntry = getIntent().getParcelableExtra(EXTRA_ARTISTENTRY);
            mTrackFragment = new TrackFragment();
            mTrackFragment.setTitle(mArtistListEntry.getArtistName());
            ArrayList<TrackListEntry> myEntries = getIntent().getParcelableArrayListExtra(EXTRA_TRACKLIST);
            mTrackFragment.setTrackListEntries(myEntries);
            mTrackFragment.setNofResults(myEntries.size());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_track_container, mTrackFragment, TRACK_FRAGMENT_TAG)
                    .commit();
        } else {
            mTrackFragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag(TRACK_FRAGMENT_TAG);
        }

        ActionBar myActionBar = getSupportActionBar();
        String mySubtitle = null;

        if (mTrackFragment.getNofResults() > 0) {
            mySubtitle = String.format(getString(R.string.track_subtitle), mTrackFragment.getNofResults());
        }

        if (myActionBar != null) {
            if (mTrackFragment.getTitle() != null) {
                myActionBar.setTitle(mTrackFragment.getTitle());
            }
            if (mySubtitle != null) {
                myActionBar.setSubtitle(mySubtitle);
            }
        }

        AppController.getInstance().registerTrackFragment(mTrackFragment);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mTrackFragment != null) {
            ButterKnife.inject(this, mTrackFragment.getView());
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // overwrite "Home"-Button (Navigation Button on left) to emulate the behaviour of
        // the hardware back-button so that the state of the previous activity is retained
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        AppController.getInstance().unregisterTrackActivity();
    }

    @OnItemClick(R.id.track_fragment_listview) public void onTrackItemClicked(int position) {
        //TODO: Create visual error-feedback for the user
        try {
            AppController.getInstance().handleOnTrackSelected(position, false);
        } catch (MissingDependencyException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (ParameterException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override public void onDismiss(DialogInterface dialog) {
        AppController.getInstance().unregisterPlaybackFragment();
        getSupportFragmentManager().popBackStack();
//        getSupportFragmentManager().beginTransaction()
//                .remove(getSupportFragmentManager().findFragmentByTag(TRACK_PLAYBACK_TAG))
//                .commit();
    }
}
