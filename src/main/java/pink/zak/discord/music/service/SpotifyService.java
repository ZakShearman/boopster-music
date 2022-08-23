package pink.zak.discord.music.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotifyService {
    private static final int MIN_SPOTIFY_URL_LENGTH = 53;
    private final SpotifyApi spotifyApi;

    public Optional<Track> parseTrackFromUrl(String url) {
        if (url.length() <= MIN_SPOTIFY_URL_LENGTH ||  !url.startsWith("https://open.spotify.com/track/")) // 31 chars long link (before the URL)
            return Optional.empty();

        String inputEnd = url.substring(31);
        String trackId = inputEnd.length() > 22 ? inputEnd.substring(0, 22) : inputEnd; // Spotify often puts ?si=xxxxxx at the end, this accounts for that
        if (trackId.length() != 22)
            return Optional.empty();

        return Optional.of(this.getSpotifyTrack(trackId));
    }

    public Image getLargestImage(Image[] images) {
        Image largestImage = images[0];
        for (int i = 1; i < images.length; i++) {
            Image image = images[i];
            if (image.getHeight() > largestImage.getHeight())
                largestImage = image;
        }
        return largestImage;
    }

    /**
     * Takes a track id and gets the Artist - Title for use searching youtube
     *
     * @param trackId The spotify ID of the track
     * @return String {artist} - {title}
     */
    @SneakyThrows
    public Track getSpotifyTrack(String trackId) {
        return this.spotifyApi
                .getTrack(trackId)
                .build().execute();
    }
}
