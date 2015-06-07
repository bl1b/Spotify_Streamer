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

package de.gruenewald.udacity.spotifystreamer.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.gruenewald.udacity.spotifystreamer.R;

public class TrackAdapter extends ArrayAdapter<TrackListEntry> {

    public TrackAdapter(Context context, int resource, int textViewResourceId, List<TrackListEntry> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView = convertView;

        if (myView == null) {
            LayoutInflater myLayoutInflater = LayoutInflater.from(getContext());
            myView = myLayoutInflater.inflate(R.layout.view_track_listentry, parent, false);
        }

        TrackListEntry myEntry = getItem(position);

        TextView myTrackNameView = (TextView) myView.findViewById(R.id.track_listentry_track);
        TextView myAlbumNameView = (TextView) myView.findViewById(R.id.track_listentry_album);
        ImageView myImageView = (ImageView) myView.findViewById(R.id.track_listentry_icon);

        if (myImageView != null && myEntry.getAlbumCover() != null) {
            Picasso.with(getContext()).load(myEntry.getAlbumCover()).into(myImageView);
        }

        if (myTrackNameView != null && myEntry.getTrackName() != null) {
            myTrackNameView.setText(myEntry.getTrackName());
        }

        if (myAlbumNameView != null && myEntry.getAlbumName() != null) {
            myAlbumNameView.setText(myEntry.getAlbumName());
        }

        return myView;
    }
}
