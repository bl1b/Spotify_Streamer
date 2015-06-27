/*******************************************************************************
 Copyright (c) 2015 by Jan Grünewald.
 jan.gruenewald84@googlemail.com

 This file is part of 'Spotify Streamer'. 'Spotify Streamer' was developed as
 part of the Android Developer Nanodegree by Udacity.

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

package de.gruenewald.udacity.spotifystreamer.exception;

/**
 * Created by Jan on 27.06.2015.
 */
public class MissingDependencyException extends Exception {
    public MissingDependencyException(String detailMessage) {
        super(detailMessage);
    }
}
