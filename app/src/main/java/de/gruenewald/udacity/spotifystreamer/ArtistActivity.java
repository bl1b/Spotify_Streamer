/**
 * ****************************************************************************
 * Copyright (c) 2015 by Jan Grünewald.
 * jan.gruenewald84@googlemail.com
 * -------------------------------------------------------------------------------
 * This file is part of 'Spotify Streamer'. 'Spotify Streamer' was developed as
 * part of the Android Developer Nanodegree by Udacity. For further
 * information see:
 * https://www.udacity.com/course/android-developer-nanodegree--nd801
 * -------------------------------------------------------------------------------
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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.gruenewald.udacity.spotifystreamer.model.ArtistAdapter;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;
import de.gruenewald.udacity.spotifystreamer.model.TrackListEntry;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    static final String LOG_TAG = ArtistActivity.class.getSimpleName();
    static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    public static final String KEY_ARTLIST_ENTRIES = "existing_entries";

    SearchView mSearchView;
    ListView mListView;
    ArrayList<ArtistListEntry> mArtistListEntries;

    final SpotifyApi mSpotifyApi = new SpotifyApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_artist);
        // setup the listview
        mListView = (ListView) findViewById(R.id.artist_listview);
        mListView.setOnItemClickListener(this);
        // fetch existing artistlist entries from the saved instance state to prevent
        // and empty list after rotating the device
        if (savedInstanceState != null) {
            mArtistListEntries = savedInstanceState.getParcelableArrayList("existing_entries");
            repopulateListView(mArtistListEntries);
        } else {
            String[] dummyEntries = new String[]{
                    "Dummy 1",
                    "Dummy 2",
                    "Dummy 3",
                    "Dummy 4",
                    "Dummy 5",
                    "Dummy 6",
                    "Dummy 7",
                    "Dummy 8",
                    "Dummy 9",
                    "Dummy 10"
            };
            mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.view_artist_listentry, R.id.artist_listentry_text, dummyEntries));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // setup the searchview-item and set the querylistener to be this activity
        MenuItem searchItem = menu.findItem(R.id.artist_search_actionbar_search);
        if (searchItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            mSearchView.setOnQueryTextListener(this);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method when an item in {@link ListView} is clicked.
     *
     * @param parent   The parenting {@link AdapterView}. In this case the artists' Listview.
     * @param view     The view representation of the clicked list cell.
     * @param position The position inside the list (0-indexed).
     * @param id       The id of the list cell.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position) != null && parent.getItemAtPosition(position) instanceof ArtistListEntry) {
            final ArtistListEntry myArtistEntry = (ArtistListEntry) parent.getItemAtPosition(position);
            Map<String, Object> myParameterMap = new HashMap<String, Object>();
            // TODO: make configurable over settings
            myParameterMap.put("country", "US");

            final ArtistActivity ref = this;
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
                                Intent trackIntent = new Intent(ref, TrackActivity.class);
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
                                Toast.makeText(ref, R.string.track_search_error_noresults, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ref, R.string.track_search_error_default, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else if (parent.getItemAtPosition(position) != null) {
            Log.v(LOG_TAG, "Entry at position " + position + ": " + parent.getItemAtPosition(position).toString());
        }
    }

    /**
     * This callback method is used by the {@link #mSearchView} and called when
     * the search-query is submitted (by enter button)
     *
     * @param query The text entered in searview
     * @return Returns true if this method is the final handler for the query else false
     */
    @Override
    public boolean onQueryTextSubmit(final String query) {

        final SpotifyService mySpotifyService = mSpotifyApi.getService();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        // TODO: Read the limit from the settings
        queryMap.put("limit", 20);

        final ArtistActivity ref = this;
        mySpotifyService.searchArtists(query, queryMap, new Callback<ArtistsPager>() {
            /**
             * Callback Method used be retrofit when the REST-Request completes.
             *
             * @param t        The result-object containing the artists information
             * @param response The raw response object.
             */
            @Override
            public void success(final ArtistsPager t, final Response response) {
                // translate the artistlist from REST-Request into ArtistListEntry objects.
                final ArrayList<ArtistListEntry> myList = new ArrayList<ArtistListEntry>();
                for (Artist myArtist : t.artists.items) {
                    ArtistListEntry myNewEntry = new ArtistListEntry(myArtist.id);
                    myNewEntry.setArtistName(myArtist.name);
                    // seems the last image in the list is the smallest; so pick that.
                    if (myArtist.images.size() > 0) {
                        myNewEntry.setCoverUrl(myArtist.images.get(myArtist.images.size() - 1).url);
                    }
                    myList.add(myNewEntry);
                }

                // Seems when this method is called by retrofit we are not
                // on the MAIN_THREAD. Since we want to manipulate the UI we have
                // to post to the MAIN_THREAD (I guess runOnUiThread would also do the job but this way
                // should be preferred for more flexibility).
                MAIN_THREAD.post(new Runnable() {
                    @Override
                    public void run() {
                        // Handling the corner case: no artist found.
                        if (t.artists.total == 0) {
                            Toast.makeText(ref, R.string.artist_search_error_noresults, Toast.LENGTH_SHORT).show();
                        } else if (mListView != null) {
                            repopulateListView(myList);
                        }
                    }
                });
            }

            /**
             * Callback-Method used by retrofit to handle errors on failed
             * REST-Requests.
             *
             * @param error The error object.
             */
            @Override
            public void failure(final RetrofitError error) {
                // See success()-method why we post to MAIN_THREAD.
                MAIN_THREAD.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(LOG_TAG, "RetrofitError: " + error.getMessage());
                        Toast.makeText(ref, R.string.artist_search_error_default, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // close the keyboard after search by taking focus from the searchview
        mSearchView.clearFocus();

        return true;
    }

    /**
     * Currently unused. Callback method used by {@link #mSearchView} which is called
     * as soon as the input in the searchview changes.
     * #
     *
     * @param newText The new text-value.
     * @return true if this method is the final handler else false.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO: Consider updating the listview as soon as the text changes.
        return false;
    }


    /**
     * This method is called when the activity is paused/restarted (e.g. on rotation).
     * It can be used to persist data in a {@link Bundle} which is then passed to the onCreate()
     * method.
     *
     * @param outState The {@link Bundle} object to store the information.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save the existing list so that it can be restored
        outState.putParcelableArrayList(KEY_ARTLIST_ENTRIES, mArtistListEntries);
        super.onSaveInstanceState(outState);
    }

    /**
     * This is a helper method to repopulate the artist list.
     *
     * @param pArtistListEntries A list of {@link ArtistListEntry}'s to populate the artist list.
     */
    private void repopulateListView(ArrayList<ArtistListEntry> pArtistListEntries) {
        ArtistAdapter myAdapter = new ArtistAdapter(this, R.layout.view_artist_listentry, R.id.artist_listentry_text, pArtistListEntries);
        if (mListView != null) {
            mArtistListEntries = pArtistListEntries;
            mListView.setAdapter(myAdapter);
        }
    }
}
