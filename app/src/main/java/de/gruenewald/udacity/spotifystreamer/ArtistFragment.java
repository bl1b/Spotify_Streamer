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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.gruenewald.udacity.spotifystreamer.controller.AppController;
import de.gruenewald.udacity.spotifystreamer.model.ArtistAdapter;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;

/**
 * Created by Jan on 18.06.2015.
 */
public class ArtistFragment extends Fragment {
    static final String LOG_TAG = ArtistFragment.class.getSimpleName();

    static final String KEY_ARTLIST_POSITION = "artlist_position";
    static final String KEY_ARTLIST_ENTRIES = "existing_entries";

    private int mArtlistPosition;
    private ArrayList<ArtistListEntry> mArtistListEntries;

    @InjectView(R.id.artist_fragment_listview) ListView mListView;
    @InjectView(R.id.artist_fragment_textview) TextView mTextView;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getInstance().registerArtistFragment(this);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        ButterKnife.inject(this, rootView);
        // fetch existing artistlist entries from the saved instance state to prevent
        // and empty list after rotating the device
        if (savedInstanceState != null) {
            mArtlistPosition = savedInstanceState.getInt(KEY_ARTLIST_POSITION);
            mArtistListEntries = savedInstanceState.getParcelableArrayList(KEY_ARTLIST_ENTRIES);
            repopulateListView(mArtistListEntries);
        } else {
            mArtlistPosition = -1;
            repopulateListView(null);
        }

        return rootView;
    }

    /**
     * This method is called when the activity is paused/restarted (e.g. on rotation).
     * It can be used to persist data in a {@link Bundle} which is then passed to the onCreate()
     * method.
     *
     * @param outState The {@link Bundle} object to store the information.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save the existing list so that it can be restored
        outState.putInt(KEY_ARTLIST_POSITION, mArtlistPosition);
        outState.putParcelableArrayList(KEY_ARTLIST_ENTRIES, mArtistListEntries);
        super.onSaveInstanceState(outState);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        AppController.getInstance().unregisterArtistFragment();
    }

    /**
     * This is a helper method to repopulate the artist list.
     *
     * @param pArtistListEntries A list of {@link ArtistListEntry}'s to populate the artist list.
     */
    void repopulateListView(ArrayList<ArtistListEntry> pArtistListEntries) {
        if (mListView != null && pArtistListEntries != null && pArtistListEntries.size() > 0) {
            mTextView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            ArtistAdapter myAdapter = new ArtistAdapter(getActivity(), R.layout.view_artist_listentry, R.id.artist_listentry_text, pArtistListEntries);
            mArtistListEntries = pArtistListEntries;
            mListView.setAdapter(myAdapter);
        } else if (mListView != null) {
            mListView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<ArtistListEntry> getArtistListEntries() {
        return mArtistListEntries;
    }

    public int getArtlistPosition() {
        return mArtlistPosition;
    }

    public void setArtlistPosition(int pArtlistPosition) {
        mArtlistPosition = pArtlistPosition;
    }
}
