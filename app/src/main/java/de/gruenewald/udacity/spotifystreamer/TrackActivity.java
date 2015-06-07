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

package de.gruenewald.udacity.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ListViewCompat;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import de.gruenewald.udacity.spotifystreamer.model.TrackAdapter;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;


public class TrackActivity extends AppCompatActivity {
    static final String LOG_TAG = TrackActivity.class.getSimpleName();

    static public final String EXTRA_TITLE = "track_extra_title";
    static public final String EXTRA_NOFRESULTS = "track_extra_nofresults";
    static public final String EXTRA_TRACKLIST = "track_extra_list";

    String mTitle;
    int mNofResults;
    ListViewCompat mListView;
    ArrayList<TrackListEntry> mTrackListEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        if (savedInstanceState == null) {
            mTitle = getIntent().getStringExtra(EXTRA_TITLE);
            mNofResults = getIntent().getIntExtra(EXTRA_NOFRESULTS, -1);
            mTrackListEntries = getIntent().getParcelableArrayListExtra(EXTRA_TRACKLIST);
        } else {
            mTitle = savedInstanceState.getString(EXTRA_TITLE);
            mNofResults = savedInstanceState.getInt(EXTRA_NOFRESULTS);
            mTrackListEntries = savedInstanceState.getParcelableArrayList(EXTRA_TRACKLIST);
        }

        String mySubtitle = null;
        ActionBar myActionBar = getSupportActionBar();

        if (mNofResults > 0) {
            mySubtitle = String.format(getString(R.string.track_subtitle), mNofResults);
        }

        if (myActionBar != null) {
            if (mTitle != null) {
                myActionBar.setTitle(mTitle);
            }
            if (mySubtitle != null) {
                myActionBar.setSubtitle(mySubtitle);
            }
        }

        mListView = (ListViewCompat) findViewById(R.id.track_listview);
        if (mTrackListEntries != null) {
            mListView.setAdapter(new TrackAdapter(this, R.layout.view_track_listentry, R.id.track_listentry_track, mTrackListEntries));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_TITLE, mTitle);
        outState.putInt(EXTRA_NOFRESULTS, mNofResults);
        outState.putParcelableArrayList(EXTRA_TRACKLIST, mTrackListEntries);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
