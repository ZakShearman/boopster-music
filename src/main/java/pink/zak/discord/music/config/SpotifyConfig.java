package pink.zak.discord.music.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("spotify")
//@ConstructorBinding todo remove
@RequiredArgsConstructor
public class SpotifyConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyConfig.class);

    private final String clientId;
    private final String clientSecret;

    private SpotifyApi spotifyApi;

    @Bean
    public SpotifyApi spotifyApi(ThreadPoolTaskScheduler scheduler) {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(this.clientId)
                .setClientSecret(this.clientSecret)
                .build();

        this.renewCredentials(scheduler);

        return this.spotifyApi;
    }

    @SneakyThrows
    private ClientCredentials renewCredentials(ThreadPoolTaskScheduler scheduler) {
        ClientCredentials credentials = this.spotifyApi.clientCredentials().build().execute();
        this.spotifyApi.setAccessToken(credentials.getAccessToken());

        LOGGER.info("Retrieved spotify access token. Resets in {} seconds", credentials.getExpiresIn());

        // give us 1 minute spare. We should check if this fails in the future
        scheduler.schedule(() -> this.renewCredentials(scheduler), Instant.now().plus(credentials.getExpiresIn() - 60, ChronoUnit.SECONDS));

        return credentials;
    }
}
