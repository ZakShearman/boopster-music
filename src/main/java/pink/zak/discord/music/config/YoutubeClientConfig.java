package pink.zak.discord.music.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties("youtube")
@ConstructorBinding
public record YoutubeClientConfig(@NotNull String key) {

    @SneakyThrows
    @Bean
    public YouTube youTube() {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = new GsonFactory();
        return new YouTube.Builder(httpTransport, jsonFactory, null)
            .setYouTubeRequestInitializer(new YouTubeRequestInitializer(this.key))
            .setApplicationName("Music")
            .build();
    }
}
