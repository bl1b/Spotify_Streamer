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

import android.os.Parcel;
import android.os.Parcelable;

public class TrackListEntry implements Parcelable {

    private final String mTrackId;
    private String mTrackName;
    private String mAlbumName;
    private String mAlbumCover;
    private String mAlbumCoverLarge;

    public static final Creator<TrackListEntry> CREATOR = new Creator<TrackListEntry>() {
        @Override
        public TrackListEntry createFromParcel(Parcel source) {
            return new TrackListEntry(source);
        }

        @Override
        public TrackListEntry[] newArray(int size) {
            return new TrackListEntry[size];
        }
    };

    public TrackListEntry(String pTrackId) {
        mTrackId = pTrackId;
        mTrackName = null;
        mAlbumName = null;
        mAlbumCover = null;
        mAlbumCoverLarge = null;
    }

    private TrackListEntry(Parcel in) {
        mTrackId = in.readString();
        mTrackName = in.readString();
        mAlbumName = in.readString();
        mAlbumCover = in.readString();
        mAlbumCoverLarge = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTrackId);
        dest.writeString(mTrackName);
        dest.writeString(mAlbumName);
        dest.writeString(mAlbumCover);
        dest.writeString(mAlbumCoverLarge);
    }

    @Override public String toString() {
        return "TrackListEntry{" +
                "mTrackId='" + mTrackId + '\'' +
                ", mTrackName='" + mTrackName + '\'' +
                ", mAlbumName='" + mAlbumName + '\'' +
                ", mAlbumCover='" + mAlbumCover + '\'' +
                ", mAlbumCoverLarge='" + mAlbumCoverLarge + '\'' +
                '}';
    }

    public String getTrackId() {
        return mTrackId;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public void setTrackName(String pTrackName) {
        mTrackName = pTrackName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String pAlbumName) {
        mAlbumName = pAlbumName;
    }

    public String getAlbumCover() {
        return mAlbumCover;
    }

    public void setAlbumCover(String pAlbumCover) {
        mAlbumCover = pAlbumCover;
    }

    public String getAlbumCoverLarge() {
        return mAlbumCoverLarge;
    }

    public void setAlbumCoverLarge(String pAlbumCoverLarge) {
        mAlbumCoverLarge = pAlbumCoverLarge;
    }
}
