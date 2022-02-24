package pink.zak.discord.music.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("spotify")
@ConstructorBinding
@RequiredArgsConstructor
public class SpotifyConfig {
    private final String clientId;
    private final String clientSecret;

    private SpotifyApi spotifyApi;

    @Bean
    public SpotifyApi spotifyApi(ThreadPoolTaskScheduler scheduler) {
        this.spotifyApi = new SpotifyApi.Builder()
            .setClientId(this.clientId)
            .setClientSecret(this.clientSecret)
            .build();

        this.startRenewCredentialsFlow(scheduler);
        return this.spotifyApi;
    }

    private void startRenewCredentialsFlow(ThreadPoolTaskScheduler scheduler) {
        ClientCredentials credentials = this.renewCredentials();

        scheduler.schedule(this::renewCredentials, Instant.now().plus(credentials.getExpiresIn() - 10, ChronoUnit.MINUTES)); // give us 10 mins spare. We should check if this fails in the future
    }

    @SneakyThrows
    private ClientCredentials renewCredentials() {
        ClientCredentials credentials = this.spotifyApi.clientCredentials().build().execute();
        this.spotifyApi.setAccessToken(credentials.getAccessToken());

        return credentials;
    }
}
