/*******************************************************************************
 Copyright (c) 2015 by Jan Grünewald.
 jan.gruenewald84@googlemail.com
 --------------------------------------------------------------------------------
 This file is part of 'Spotify Streamer'. 'Spotify Streamer' was developed as
 part of the Android Developer Nanodegree by Udacity. For further
 information see:
 https://www.udacity.com/course/android-developer-nanodegree--nd801
 --------------------------------------------------------------------------------
 'Spotify Streamer' is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 'Spotify Streamer' is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with 'Spotify Streamer'.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.gruenewald.udacity.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistListEntry implements Parcelable {
    private String mArtistName;
    private String mCoverUrl;
    private final String mArtistId;

    public static final Creator<ArtistListEntry> CREATOR = new Creator<ArtistListEntry>() {
        @Override
        public ArtistListEntry createFromParcel(Parcel source) {
            return new ArtistListEntry(source);
        }

        @Override
        public ArtistListEntry[] newArray(int size) {
            return new ArtistListEntry[size];
        }
    };

    public ArtistListEntry(String pArtistId) {
        mArtistName = null;
        mCoverUrl = null;
        mArtistId = pArtistId;
    }

    private ArtistListEntry(Parcel in) {
        mArtistId = in.readString();
        mArtistName = in.readString();
        mCoverUrl = in.readString();
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String pArtistName) {
        mArtistName = pArtistName;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String pCoverUrl) {
        mCoverUrl = pCoverUrl;
    }

    public String getArtistId() {
        return mArtistId;
    }

    @Override
    public String toString() {
        return "ArtistListEntry{" +
                "mArtistName='" + mArtistName + '\'' +
                ", mCoverUrl='" + mCoverUrl + '\'' +
                ", mArtistId='" + mArtistId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mArtistId);
        dest.writeString(mArtistName);
        dest.writeString(mCoverUrl);
    }
}
