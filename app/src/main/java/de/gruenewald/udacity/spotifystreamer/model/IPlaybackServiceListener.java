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

package de.gruenewald.udacity.spotifystreamer.model;

/**
 * Created by Jan on 06.07.2015.
 */
public interface IPlaybackServiceListener {
    /**
     * This callback method will be called when the current track will be changed (either on
     * user input or automatically after the previous track finished)
     *
     * @param pPlaybackEntry The new playback entry.
     */
    public void OnPlaybackChanged(PlaybackEntry pPlaybackEntry);

    /**
     * This callback method will be called when the a track starts playing; either after it was
     * unpaused or started after a previous track finished (this event will be called after
     * {@link #OnPlaybackChanged(PlaybackEntry)}).
     *
     * @param pPlaybackEntry The playback entry that has just been started.
     */
    public void OnPlaybackStarted(PlaybackEntry pPlaybackEntry);

    /**
     * This callback method will be called when a track stops.
     *
     * @param pPlaybackEntry The playback entry thas has just stopped.
     */
    public void OnPlaybackStopped(PlaybackEntry pPlaybackEntry);
    
    public void OnPlaybackUpdated(PlaybackEntry pPlaybackEntry, int pCurrentPosition, int pTotalPosition);
}
