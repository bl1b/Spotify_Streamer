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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.gruenewald.udacity.spotifystreamer.model.TrackAdapter;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;

/**
 * Created by Jan on 18.06.2015.
 */
public class TrackFragment extends Fragment {
    static final String LOG_TAG = TrackFragment.class.getSimpleName();

    static public final String EXTRA_TITLE = "track_extra_title";
    static public final String EXTRA_NOFRESULTS = "track_extra_nofresults";
    static public final String EXTRA_TRACKLIST = "track_extra_list";


    @InjectView(R.id.track_fragment_listview) ListView mListView;


    private String mTitle;
    private int mNofResults;
    private ArrayList<TrackListEntry> mTrackListEntries;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(EXTRA_TITLE);
            mNofResults = savedInstanceState.getInt(EXTRA_NOFRESULTS);
            mTrackListEntries = savedInstanceState.getParcelableArrayList(EXTRA_TRACKLIST);
        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        ButterKnife.inject(this, rootView);
        String mySubtitle = null;
//        ActionBar myActionBar = getActivity().getSupportActionBar();
//
//        if (mNofResults > 0) {
//            mySubtitle = String.format(getString(R.string.track_subtitle), mNofResults);
//        }
//
//        if (myActionBar != null) {
//            if (mTitle != null) {
//                myActionBar.setTitle(mTitle);
//            }
//            if (mySubtitle != null) {
//                myActionBar.setSubtitle(mySubtitle);
//            }
//        }

        if (mTrackListEntries != null && mListView != null) {
            mListView.setAdapter(new TrackAdapter(getActivity(), R.layout.view_track_listentry, R.id.track_listentry_track, mTrackListEntries));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_TITLE, mTitle);
        outState.putInt(EXTRA_NOFRESULTS, mNofResults);
        outState.putParcelableArrayList(EXTRA_TRACKLIST, mTrackListEntries);

        super.onSaveInstanceState(outState);
    }

    public void setTitle(String pTitle) {
        mTitle = pTitle;
    }

    public void setNofResults(int pNofResults) {
        mNofResults = pNofResults;
    }

    public void setTrackListEntries(ArrayList<TrackListEntry> pTrackListEntries) {
        mTrackListEntries = pTrackListEntries;
    }
}
