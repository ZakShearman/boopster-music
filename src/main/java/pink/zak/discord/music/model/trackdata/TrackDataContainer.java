package pink.zak.discord.music.model.trackdata;

import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.model.trackdata.TrackDataUser;

import java.time.Instant;

public record TrackDataContainer(@NotNull String imageUrl, @NotNull TrackDataUser user,
                                 @NotNull Instant queueTime) {

    public TrackDataContainer(@NotNull String imageUrl, @NotNull TrackDataUser user) {
        this(imageUrl, user, Instant.now());
    }
}
