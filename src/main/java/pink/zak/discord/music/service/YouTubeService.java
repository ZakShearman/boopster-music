package pink.zak.discord.music.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YouTubeService {
    private static final List<String> PARTS = List.of("snippet");

    private final @NotNull YouTube youTube;

    @SneakyThrows
    public String getThumbnail(@NotNull String videoId) {
        VideoListResponse response = this.youTube.videos()
            .list(PARTS)
            .setId(List.of(videoId))
            .execute();

        return response.getItems().get(0).getSnippet().getThumbnails().getStandard().getUrl();
    }
}
