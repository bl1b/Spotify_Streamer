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
package de.gruenewald.udacity.spotifystreamer.util;

/**
 * Created by jan on 11.07.2015.
 */
public class NumberUtil {

    /**
     * This method ensures that the provided pNewIndex is within the bounds of 0 and pMaxIndex.
     *
     * @param pNewIndex The new index after modification.
     * @param pMaxIndex The maximum index pNewIndex can reach.
     * @return The secured index.
     */
    public static int secureIndex(int pNewIndex, int pMaxIndex) {
        int mySecuredIndex = pNewIndex;

        if (mySecuredIndex < 0) {
            mySecuredIndex += pMaxIndex;
        }

        if (mySecuredIndex >= pMaxIndex) {
            mySecuredIndex -= pMaxIndex;
        }

        return mySecuredIndex;
    }
}
