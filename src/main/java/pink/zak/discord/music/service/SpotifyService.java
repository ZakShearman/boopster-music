package pink.zak.discord.music.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

@Service
@RequiredArgsConstructor
public class SpotifyService {
    private final SpotifyApi spotifyApi;

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
