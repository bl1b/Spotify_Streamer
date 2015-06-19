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

package de.gruenewald.udacity.spotifystreamer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

    @InjectView(R.id.track_fragment_listview) ListView mListView;
    @InjectView(R.id.track_fragment_textview) TextView mTextView;

    private String mTitle;
    private int mNofResults;
    private ArrayList<TrackListEntry> mTrackListEntries;
    private View mRootView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(TrackActivity.EXTRA_TITLE);
            mNofResults = savedInstanceState.getInt(TrackActivity.EXTRA_NOFRESULTS);
            mTrackListEntries = savedInstanceState.getParcelableArrayList(TrackActivity.EXTRA_TRACKLIST);
        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_track, container, false);

        ButterKnife.inject(this, mRootView);
        //after creation of view (re)-populate the list
        populateListView(mTrackListEntries);

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TrackActivity.EXTRA_TITLE, mTitle);
        outState.putInt(TrackActivity.EXTRA_NOFRESULTS, mNofResults);
        outState.putParcelableArrayList(TrackActivity.EXTRA_TRACKLIST, mTrackListEntries);

        super.onSaveInstanceState(outState);
    }

    public void populateListView(ArrayList<TrackListEntry> pTrackListEntries) {

        setTrackListEntries(pTrackListEntries);

        if (pTrackListEntries != null && pTrackListEntries.size() > 0 && mListView != null) {
            mListView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            mListView.setAdapter(new TrackAdapter(getActivity(), R.layout.view_track_listentry, R.id.track_listentry_track, mTrackListEntries));
        } else if (mListView != null && mTextView != null) {
            mListView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public int getNofResults() {
        return mNofResults;
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
