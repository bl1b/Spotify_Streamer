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
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    SearchView mSearchView;
    ArtistFragment mArtistFragment;

    boolean mTwoPane;

    @Optional @InjectView(R.id.fragment_track_container) FrameLayout mTrackContainer;

    final SpotifyApi mSpotifyApi = new SpotifyApi();
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mArtistFragment = (ArtistFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_artist);

        TrackFragment myTrackFragment = null;
        //if savedInstanceState != null we are re-creating a previously existing MainActivity; thus
        //there should be a TrackFragment already.
        if (savedInstanceState != null && mTrackContainer != null) {
            myTrackFragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag(TrackActivity.TRACK_FRAGMENT_TAG);
        } else if (mTrackContainer != null) {
            myTrackFragment = new TrackFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(mTrackContainer.getId(), myTrackFragment, TrackActivity.TRACK_FRAGMENT_TAG)
                    .commit();
        }

        mArtistFragment.setTrackFragment(myTrackFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // setup the searchview-item and set the querylistener to be this activity
        mSearchMenuItem = menu.findItem(R.id.artist_search_actionbar_search);
        if (mSearchMenuItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
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

        final MainActivity ref = this;
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
                        //on successful search collapse the actionview
                        MenuItemCompat.collapseActionView(mSearchMenuItem);
                        // Handling the corner case: no artist found.
                        if (t.artists.total == 0) {
                            Toast.makeText(ref, R.string.artist_search_error_noresults, Toast.LENGTH_SHORT).show();
                        } else {
                            mArtistFragment.repopulateListView(myList);
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
        return false;
    }
}
