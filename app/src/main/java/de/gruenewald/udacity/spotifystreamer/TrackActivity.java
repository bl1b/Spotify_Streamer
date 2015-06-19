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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;


public class TrackActivity extends AppCompatActivity {
    static final String LOG_TAG = TrackActivity.class.getSimpleName();
    static final String TRACK_FRAGMENT_TAG = "tag_fragment_track";

    static public final String EXTRA_TITLE = "track_extra_title";
    static public final String EXTRA_NOFRESULTS = "track_extra_nofresults";
    static public final String EXTRA_TRACKLIST = "track_extra_list";

    private TrackFragment mTrackFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        if (savedInstanceState == null) {
            //If onCreate() is called we're in onePane-Mode. That means MainActivity should have
            //passed in the tracklist-data via Intent-Extras.
            mTrackFragment = new TrackFragment();
            mTrackFragment.setTitle(getIntent().getStringExtra(EXTRA_TITLE));
            mTrackFragment.setNofResults(getIntent().getIntExtra(EXTRA_NOFRESULTS, -1));
            ArrayList<TrackListEntry> myEntries = getIntent().getParcelableArrayListExtra(EXTRA_TRACKLIST);
            mTrackFragment.setTrackListEntries(myEntries);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // overwrite "Home"-Button (Navigation Button on left) to emulate the behaviour of
        // the hardware back-button so that the state of the previous activity is retained
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
