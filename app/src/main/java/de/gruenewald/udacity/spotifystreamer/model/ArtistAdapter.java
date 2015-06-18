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

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.gruenewald.udacity.spotifystreamer.R;

public class ArtistAdapter extends ArrayAdapter<ArtistListEntry> {

    public ArtistAdapter(Context context, int resource, int textViewResourceId, List<ArtistListEntry> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null && convertView.getTag() == null) {
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            LayoutInflater myLayoutInflater = LayoutInflater.from(getContext());
            convertView = myLayoutInflater.inflate(R.layout.view_artist_listentry, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (holder == null) {
            holder = (ViewHolder) convertView.getTag();
        }

        if (getItem(position).getCoverUrl() != null) {
            Picasso.with(getContext()).load(getItem(position).getCoverUrl()).into(holder.icon);
        }

        if (getItem(position).getArtistName() != null) {
            holder.text.setText(getItem(position).getArtistName());
        }

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.artist_listentry_text) TextView text;
        @InjectView(R.id.artist_listentry_icon) ImageView icon;

        public ViewHolder(View pReferenceView) {
            ButterKnife.inject(this, pReferenceView);
        }
    }
}
