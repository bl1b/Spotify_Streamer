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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.gruenewald.udacity.spotifystreamer.model.ArtistAdapter;
import de.gruenewald.udacity.spotifystreamer.model.ArtistListEntry;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, Callback<ArtistsPager> {

    static final String LOG_TAG = ArtistSearchActivity.class.getSimpleName();
    static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    SearchView mSearchView;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artistsearch);

        // setup the listview
        // TODO: maybe setup the listview with some dummy-data to prevent a blank screen
        mListView = (ListView) findViewById(R.id.artist_search_listview);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.artistsearch, menu);

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
     * This callback method is used by the {@link #mSearchView} and called when
     * the search-query is submitted (by enter button)
     *
     * @param query The text entered in searview
     * @return Returns true if this method is the final handler for the query else false
     */
    @Override
    public boolean onQueryTextSubmit(final String query) {
        final SpotifyApi mySpotifyApi = new SpotifyApi();
        final SpotifyService mySpotifyService = mySpotifyApi.getService();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        // TODO: Read the limit from the settings
        queryMap.put("limit", 20);
        mySpotifyService.searchArtists(query, queryMap, this);

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
     * Callback Method used be retrofit when the REST-Request completes.
     *
     * @param t        The result-object containing the artists information
     * @param response The raw response object.
     */
    @Override
    public void success(final ArtistsPager t, final Response response) {
        // Seems when this method is called by retrofit we are not
        // on the MAIN_THREAD. Since we want to manipulate the UI we have
        // to post to the MAIN_THREAD.
        final ArtistSearchActivity ref = this;

        // translate the artistlist from REST-Request into ArtistListEntry objects.
        final List<ArtistListEntry> myList = new ArrayList<ArtistListEntry>();
        for (Artist myArtist : t.artists.items) {
            ArtistListEntry myNewEntry = new ArtistListEntry(myArtist.id);
            myNewEntry.setArtistName(myArtist.name);
            // seems the last image in the list is the smallest; so pick that.
            if (myArtist.images.size() > 0) {
                myNewEntry.setCoverUrl(myArtist.images.get(myArtist.images.size() - 1).url);
            }
            myList.add(myNewEntry);
        }

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
        final ArtistSearchActivity ref = this;
        MAIN_THREAD.post(new Runnable() {
            @Override
            public void run() {
                Log.e(LOG_TAG, "RetrofitError: " + error.getMessage());
                Toast.makeText(ref, R.string.artist_search_error_default, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void repopulateListView(List<ArtistListEntry> pArtistListEntries) {
        ArtistAdapter myAdapter = new ArtistAdapter(this, R.layout.view_artist_search_listentry, R.id.artist_search_listentry_text, pArtistListEntries);
        if (mListView != null) {
            mListView.setAdapter(myAdapter);
        }
    }
}
