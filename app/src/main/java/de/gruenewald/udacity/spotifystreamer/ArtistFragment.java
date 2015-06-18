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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import de.gruenewald.udacity.spotifystreamer.model.ArtistAdapter;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jan on 18.06.2015.
 */
public class ArtistFragment extends Fragment {
    static final String LOG_TAG = ArtistFragment.class.getSimpleName();
    static final String KEY_ARTLIST_ENTRIES = "existing_entries";
    static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    final SpotifyApi mSpotifyApi = new SpotifyApi();

    ArrayList<ArtistListEntry> mArtistListEntries;

    @InjectView(R.id.artist_fragment_listview) ListView mListView;
    @InjectView(R.id.artist_fragment_textview) TextView mTextView;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        ButterKnife.inject(this, rootView);
        // fetch existing artistlist entries from the saved instance state to prevent
        // and empty list after rotating the device
        if (savedInstanceState != null) {
            mArtistListEntries = savedInstanceState.getParcelableArrayList(KEY_ARTLIST_ENTRIES);
            repopulateListView(mArtistListEntries);
        } else {
            repopulateListView(null);
        }

        return rootView;
    }

    /**
     * Callback method when an item in {@link ListView} is clicked.
     *
     * @param parent   The parenting {@link AdapterView}. In this case the artists' Listview.
     * @param view     The view representation of the clicked list cell.
     * @param position The position inside the list (0-indexed).
     * @param id       The id of the list cell.
     */
    @OnItemClick(R.id.artist_fragment_listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position) != null && parent.getItemAtPosition(position) instanceof ArtistListEntry) {
            final ArtistListEntry myArtistEntry = (ArtistListEntry) parent.getItemAtPosition(position);
            Map<String, Object> myParameterMap = new HashMap<String, Object>();
            // TODO: make configurable over settings
            myParameterMap.put("country", "US");


            mSpotifyApi.getService().getArtistTopTrack(myArtistEntry.getArtistId(), myParameterMap, new Callback<Tracks>() {
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
                                }
                            }
                            myTrackListEntries.add(myCurrentEntry);
                        }

                        MAIN_THREAD.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent trackIntent = new Intent(getActivity(), TrackActivity.class);
                                trackIntent.putExtra(TrackActivity.EXTRA_TITLE, myArtistEntry.getArtistName());
                                trackIntent.putExtra(TrackActivity.EXTRA_NOFRESULTS, myTrackListEntries.size());
                                trackIntent.putParcelableArrayListExtra(TrackActivity.EXTRA_TRACKLIST, myTrackListEntries);
                                startActivity(trackIntent);
                            }
                        });


                    } else {
                        MAIN_THREAD.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.track_search_error_noresults, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.track_search_error_default, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else if (parent.getItemAtPosition(position) != null) {
            Log.v(LOG_TAG, "Entry at position " + position + ": " + parent.getItemAtPosition(position).toString());
        }
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
        outState.putParcelableArrayList(KEY_ARTLIST_ENTRIES, mArtistListEntries);
        super.onSaveInstanceState(outState);
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
        } else if(mListView != null) {
            mListView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }
}
