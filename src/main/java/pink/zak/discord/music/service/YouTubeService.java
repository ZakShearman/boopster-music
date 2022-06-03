package pink.zak.discord.music.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YouTubeService {
    private static final List<String> SNIPPET_PART = List.of("snippet");

    private final @NotNull YouTube youTube;

    @SneakyThrows
    public @Nullable VideoSnippet getSnippet(@NotNull String videoId) {
        VideoListResponse response = this.youTube.videos().list(SNIPPET_PART)
                .setId(List.of(videoId))
                .execute();

        if (response.getItems().size() == 0) return null;

        return response
                .getItems()
                .get(0)
                .getSnippet();
    }
}
