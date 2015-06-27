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

package de.gruenewald.udacity.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jan on 19.06.2015.
 */
public class PlaybackEntry implements Parcelable {
    private ArtistListEntry mArtistListEntry;
    private TrackListEntry mTrackListEntry;

    public static final Creator<PlaybackEntry> CREATOR = new Creator<PlaybackEntry>() {
        @Override
        public PlaybackEntry createFromParcel(Parcel source) {
            return new PlaybackEntry(source);
        }

        @Override
        public PlaybackEntry[] newArray(int size) {
            return new PlaybackEntry[size];
        }
    };

    public PlaybackEntry() {
        mArtistListEntry = null;
        mTrackListEntry = null;
    }

    private PlaybackEntry(Parcel in) {
        mArtistListEntry = in.readParcelable(ArtistListEntry.class.getClassLoader());
        mTrackListEntry = in.readParcelable(TrackListEntry.class.getClassLoader());
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mArtistListEntry, 0);
        dest.writeParcelable(mTrackListEntry, 0);
    }

    public ArtistListEntry getArtistListEntry() {
        return mArtistListEntry;
    }

    public void setArtistListEntry(ArtistListEntry pArtistListEntry) {
        mArtistListEntry = pArtistListEntry;
    }

    public TrackListEntry getTrackListEntry() {
        return mTrackListEntry;
    }

    public void setTrackListEntry(TrackListEntry pTrackListEntry) {
        mTrackListEntry = pTrackListEntry;
    }
}
